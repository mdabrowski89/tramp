package pl.mobite.tramp.ui.components

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.activity_main.*
import pl.mobite.tramp.R
import pl.mobite.tramp.utils.isLollipopOrHigher
import pl.mobite.tramp.utils.isMarshmallowOrHigher
import pl.mobite.tramp.utils.isOreoOrHigher

class MainActivity: AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.mainNavHostFragment)

        setSystemUiVisibility()
    }

    private fun setSystemUiVisibility() {
        if (isLollipopOrHigher()) {
            var systemUiFlags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            if (isMarshmallowOrHigher()) {
                systemUiFlags = systemUiFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            if (isOreoOrHigher()) {
                systemUiFlags = systemUiFlags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
            mainView.systemUiVisibility = systemUiFlags
        }
    }

    override fun onSupportNavigateUp() = navController.navigateUp()
}
