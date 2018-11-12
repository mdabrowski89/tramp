package pl.mobite.tramp.ui.components.tramline

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.disposables.CompositeDisposable
import pl.mobite.tramp.R
import pl.mobite.tramp.ViewModelFactory
import pl.mobite.tramp.data.repositories.models.TramLineDesc
import pl.mobite.tramp.ui.base.BaseFragment
import pl.mobite.tramp.ui.components.tramline.TramLineIntent.GetTramLineIntent


class TramLineFragment: BaseFragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private var mapBottomPadding: Int? = null

    private val intentsRelay = PublishRelay.create<TramLineIntent>()
    private var lastViewState: TramLineViewState? = null
    private lateinit var viewModel: TramLineViewModel
    private lateinit var disposable: CompositeDisposable

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

        (childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment).getMapAsync(this)
    }

    override fun onStart() {
        super.onStart()
        disposable = CompositeDisposable()
        disposable.add(viewModel.states.subscribe(this::render))
        viewModel.processIntents(intentsRelay)
    }

    override fun onStop() {
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
            mapBottomPadding = insets.systemWindowInsetBottom
            tryUpdateGoogleMapPadding()
        }
        return insets
    }

    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map
        tryUpdateGoogleMapPadding()
        intentsRelay.accept(GetTramLineIntent(defaultTramLineDesc))
    }

    private fun render(state: TramLineViewState) {
        saveViewState(state)
        with(state) {

            googleMap?.let { map ->
                if (tramLine != null) {

                    if (tramLine.stops.isEmpty()) {
                        val msg = getString(R.string.tram_line_no_stops, tramLine.name, tramLine.direction)
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    } else {
                        map.clear()
                        tramLine.stops.forEach { tramStop ->
                            val latLng = LatLng(tramStop.lat, tramStop.lng)
                            map.addMarker(MarkerOptions().position(latLng).title(tramStop.name))
                        }
                    }
                }
            }

            if (getTramLineError?.shouldBeDisplayed() == true) {
                Toast.makeText(requireContext(), "Error occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveViewState(state: TramLineViewState) {
        if (!(state.getTramLineInProgress)) {
            lastViewState = state
        }
    }

    private fun tryUpdateGoogleMapPadding() {
        mapBottomPadding?.let { padding ->
            googleMap?.apply {
                setPadding(0, 0, 0, padding)
            }
        }
    }

    companion object {

        val defaultTramLineDesc = TramLineDesc("35", "Banacha")
    }
}