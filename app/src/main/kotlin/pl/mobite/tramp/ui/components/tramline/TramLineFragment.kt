package pl.mobite.tramp.ui.components.tramline

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.Navigation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_tram_line.*
import pl.mobite.tramp.R
import pl.mobite.tramp.data.repositories.models.FilterStopsQuery
import pl.mobite.tramp.data.repositories.models.TramLineDesc
import pl.mobite.tramp.ui.base.MviBaseFragment
import pl.mobite.tramp.ui.components.MainActivity
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineAction
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineAction.FilterStopsAction
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineAction.GetTramLineAction
import pl.mobite.tramp.ui.components.tramline.mvi.TramLineResult
import pl.mobite.tramp.ui.models.TimeTableDetails
import pl.mobite.tramp.ui.models.TramLineDetails
import pl.mobite.tramp.ui.models.TramStopDetails
import pl.mobite.tramp.ui.models.toTramStop
import pl.mobite.tramp.utils.dpToPx
import pl.mobite.tramp.utils.getBitmap
import pl.mobite.tramp.utils.getCurrentTime
import pl.mobite.tramp.utils.hasNetwork
import java.util.concurrent.TimeUnit


class TramLineFragment: MviBaseFragment<TramLineAction, TramLineResult, TramLineViewState, TramLineViewModel>(
    TramLineViewModel::class.java
), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private var mapPadding: MapPadding? = null

    private var filteringDisposable: CompositeDisposable? = null

    private val handler = Handler()
    private val showLocatingTramsInfoRunnable = Runnable { locatingTramsInfo.visibility = View.VISIBLE }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tram_line, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (googleMap == null) {
            (childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment).getMapAsync(this)
            if (!hasNetwork(requireContext())) {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.no_network_title)
                    .setMessage(R.string.no_network_msg)
                    .setPositiveButton(R.string.button_ok, null)
                    .show()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireActivity() as MainActivity).showLightSystemUI()
    }

    override fun onStop() {
        filteringDisposable?.dispose()
        handler.removeCallbacksAndMessages(null)
        super.onStop()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onApplyInsets(v: View, insets: WindowInsets): WindowInsets {
        if (!insets.isConsumed) {
            mapPadding = MapPadding(insets.systemWindowInsetTop, insets.systemWindowInsetBottom)
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
        handler.post { mviController.accept(GetTramLineAction(defaultTramLineDesc)) }
    }

    override fun render(state: TramLineViewState) {
        with(state) {
            var isLocatingTramsInProgress = false
            googleMap?.let { map ->
                if (tramLineDetails != null && tramLineStops != null) {
                    isLocatingTramsInProgress = getMarkedTramStopIdsInProgress

                    refreshStopsWithTramsFiltering(tramLineDetails.name, tramLineStops)

                    map.clear()
                    if (tramLineStops.isEmpty()) {
                        val msg = getString(R.string.tram_line_no_stops, tramLineDetails.name, tramLineDetails.direction)
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    } else {
                        renderTramLineStops(map, tramLineDetails, tramLineStops, markedTramStopIds.orEmpty())
                    }
                }

                getTramLineError?.consume {
                    Toast.makeText(requireContext(), R.string.tram_line_fetch_error, Toast.LENGTH_LONG).show()
                }
            }

            if (isLocatingTramsInProgress) {
                handler.postDelayed(showLocatingTramsInfoRunnable, 1000)
            } else {
                handler.removeCallbacks(showLocatingTramsInfoRunnable)
                locatingTramsInfo.visibility = View.GONE
            }
        }
    }

    private fun refreshStopsWithTramsFiltering(tramLineName: String, tramLineStops: List<TramStopDetails>) {
        filteringDisposable?.let {
            val oldTramStopArray = mviController.viewState?.tramLineStops.orEmpty().toTypedArray()
            val newTramStopsArray = tramLineStops.toTypedArray()
            if (!(oldTramStopArray contentEquals newTramStopsArray)) {
                // if tram line stops has changed restart filtering
                it.dispose()
            }
        }

        if ((filteringDisposable == null || filteringDisposable?.isDisposed == true) && tramLineStops.isNotEmpty()) {
            fun createFilterAction() = FilterStopsAction(
                FilterStopsQuery(getCurrentTime(), tramLineName, tramLineStops.map { it.toTramStop() })
            )
            mviController.accept(createFilterAction())
            filteringDisposable = CompositeDisposable()
            filteringDisposable?.add(
                Observable
                    .interval(20, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (mviController.viewState?.getMarkedTramStopIdsInProgress != true) {
                            // refresh filtering only if it is not currently in progress
                            mviController.accept(createFilterAction())
                        }
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
        val blueDotBitmap = getBitmap(requireContext(), R.drawable.ic_blue_dot)
        val redDotBitmap = getBitmap(requireContext(), R.drawable.ic_red_dot)
        val boundsBuilder by lazy { LatLngBounds.builder() }
        // update camera if stops are loaded for the first time
        val updateCamera = mviController.viewState?.tramLineStops.isNullOrEmpty()
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
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), dpToPx(requireContext(), 36).toInt()))
        }
    }

    private fun openTimeTable(timeTableDesc: TimeTableDetails) {
        view?.let {
            val directions = TramLineFragmentDirections.actionTramLineFragmentToTimeTableFragment(
                timeTableDesc.stopId, timeTableDesc.stopName, timeTableDesc.lineName, timeTableDesc.lineDirection)
            Navigation.findNavController(it).navigate(directions)
        }
    }

    private fun tryUpdateGoogleMapPadding() {
        googleMap?.apply {
            mapPadding?.apply {
                setPadding(0, topPadding, 0, bottomPadding)
            }
        }
    }

    companion object {

        val defaultTramLineDesc = TramLineDesc("35", "Banacha")
    }

    private data class MapPadding(val topPadding: Int, val bottomPadding: Int)
}