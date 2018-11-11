package pl.mobite.tramp.ui.components.tramstops

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.navigation.Navigation
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.fragment_tram_stops.*
import pl.mobite.tramp.R
import pl.mobite.tramp.ui.base.BaseFragment


class TramStopsFragment: BaseFragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tram_stops, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment).getMapAsync(this)

        timeTableButton.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_tramStopsFragment_to_timeTableFragment))
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onApplyInsets(v: View, insets: WindowInsets): WindowInsets {
        with(timeTableButton) {
            if (!insets.isConsumed) {
                layoutParams = (layoutParams as FrameLayout.LayoutParams).apply {
                    topMargin = insets.systemWindowInsetTop
                }
            }
        }
        return insets
    }

    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map
    }
}