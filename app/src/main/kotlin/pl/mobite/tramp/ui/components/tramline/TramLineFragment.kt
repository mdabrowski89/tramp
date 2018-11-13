package pl.mobite.tramp.ui.components.tramline

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import pl.mobite.tramp.R
import pl.mobite.tramp.ViewModelFactory
import pl.mobite.tramp.data.repositories.models.TramLineDesc
import pl.mobite.tramp.ui.base.BaseFragment
import pl.mobite.tramp.ui.components.MainActivity
import pl.mobite.tramp.ui.components.tramline.TramLineIntent.FilterCurrentStopsIntent
import pl.mobite.tramp.ui.components.tramline.TramLineIntent.GetTramLineIntent
import pl.mobite.tramp.ui.models.TimeTableDetails
import pl.mobite.tramp.ui.models.TramLineDetails
import pl.mobite.tramp.ui.models.TramStopDetails
import pl.mobite.tramp.utils.dpToPx
import pl.mobite.tramp.utils.getBitmap
import java.util.concurrent.TimeUnit


class TramLineFragment: BaseFragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private var mapBottomPadding: Int? = null
    private var mapTopPadding: Int? = null

    private val intentsRelay = PublishRelay.create<TramLineIntent>()
    private var lastViewState: TramLineViewState? = null
    private lateinit var viewModel: TramLineViewModel
    private lateinit var disposable: CompositeDisposable
    private var filteringDisposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initialViewState: TramLineViewState? = savedInstanceState?.getParcelable(TramLineViewState.PARCEL_KEY)
        viewModel = ViewModelProviders
            .of(this, ViewModelFactory.getInstance(initialViewState))
            .get(TramLineViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tram_line, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (googleMap == null) {
            (childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment).getMapAsync(this)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireActivity() as MainActivity).showLightSystemUI()
    }

    override fun onStart() {
        super.onStart()
        disposable = CompositeDisposable()
        disposable.add(viewModel.states.subscribe(this::render))
        viewModel.processIntents(intentsRelay)
    }

    override fun onStop() {
        filteringDisposable?.dispose()
        disposable.dispose()
        viewModel.dispose()
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        lastViewState?.let { viewState ->
            outState.putParcelable(TramLineViewState.PARCEL_KEY, viewState)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onApplyInsets(v: View, insets: WindowInsets): WindowInsets {
        if (!insets.isConsumed) {
            mapTopPadding = insets.systemWindowInsetTop
            mapBottomPadding = insets.systemWindowInsetBottom
            tryUpdateGoogleMapPadding()
        }
        return insets
    }

    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map
        map.setOnMarkerClickListener { marker ->
            (marker.tag as? TimeTableDetails?)?.let { timeTableDesc ->
                openTimeTable(timeTableDesc)
            }
            true
        }
        tryUpdateGoogleMapPadding()
        Handler().post { intentsRelay.accept(GetTramLineIntent(defaultTramLineDesc)) }
    }

    private fun render(state: TramLineViewState) {
        with(state) {
            googleMap?.let { map ->
                if (tramLineDetails != null && tramLineStops != null) {
                    runStopsWithTramsFiltering(tramLineDetails.name, tramLineStops)

                    map.clear()
                    if (tramLineStops.isEmpty()) {
                        val msg = getString(R.string.tram_line_no_stops, tramLineDetails.name, tramLineDetails.direction)
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    } else {
                        renderTramLineStops(map, tramLineDetails, tramLineStops, markedTramStopIds.orEmpty())
                    }
                }

                if (getTramLineError?.shouldBeDisplayed() == true) {
                    Toast.makeText(requireContext(), R.string.tram_line_fetch_error, Toast.LENGTH_LONG).show()
                }

                saveViewState(this)
            }
        }
    }

    private fun runStopsWithTramsFiltering(tramLineName: String, tramLineStops: List<TramStopDetails>) {
        if (filteringDisposable == null || filteringDisposable?.isDisposed == true) {
            intentsRelay.accept(FilterCurrentStopsIntent(tramLineName, tramLineStops))
            filteringDisposable = CompositeDisposable()
            filteringDisposable?.add(Observable.interval(30, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    intentsRelay.accept(FilterCurrentStopsIntent(tramLineName, tramLineStops))
                }
            )
        }
    }

    private fun renderTramLineStops(
        map: GoogleMap,
        tramLine: TramLineDetails,
        tramLineStops: List<TramStopDetails>,
        markedTramStopIds: List<String>
    ) {
        val blueDotBitmap = getBitmap(R.drawable.ic_blue_dot)
        val redDotBitmap = getBitmap(R.drawable.ic_red_dot)
        val boundsBuilder by lazy { LatLngBounds.builder() }
        val updateCamera = lastViewState?.tramLineStops.isNullOrEmpty()
        tramLineStops.forEach { tramStop ->
            val latLng = LatLng(tramStop.lat, tramStop.lng)
            if (updateCamera) {
                boundsBuilder.include(latLng)
            }
            val markerIcon = if (markedTramStopIds.contains(tramStop.id)) {
                BitmapDescriptorFactory.fromBitmap(redDotBitmap)
            } else {
                BitmapDescriptorFactory.fromBitmap(blueDotBitmap)
            }
            val marker = map.addMarker(MarkerOptions()
                .position(latLng)
                .title(tramStop.name)
                .icon(markerIcon)
            )
            marker.tag = TimeTableDetails(tramLine.name, tramLine.direction, tramStop.name, tramStop.id)
        }
        if (updateCamera) {
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), dpToPx(36).toInt()))
        }
    }

    private fun openTimeTable(timeTableDesc: TimeTableDetails) {
        view?.let {
            val directions = TramLineFragmentDirections.actionTramLineFragmentToTimeTableFragment(
                timeTableDesc.stopId, timeTableDesc.stopName, timeTableDesc.lineName, timeTableDesc.lineDirection)
            Navigation.findNavController(it).navigate(directions)
        }
    }

    private fun saveViewState(state: TramLineViewState) {
        if (!state.getTramLineInProgress) {
            lastViewState = state
        }
    }

    private fun tryUpdateGoogleMapPadding() {
        val topPadding = mapTopPadding
        val bottomPadding = mapBottomPadding
        googleMap?.apply {
            if (topPadding != null && bottomPadding != null) {
                setPadding(0, topPadding, 0, bottomPadding)
            }
        }
    }

    companion object {

        val defaultTramLineDesc = TramLineDesc("35", "Banacha")
    }
}