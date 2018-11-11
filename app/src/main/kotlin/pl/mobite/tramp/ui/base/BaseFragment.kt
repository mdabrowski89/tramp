package pl.mobite.tramp.ui.base

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import pl.mobite.tramp.utils.isLollipopOrHigher


abstract class BaseFragment: Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isLollipopOrHigher()) {
            view.setOnApplyWindowInsetsListener { v, insets ->
                onApplyInsets(v, insets)
            }

            /**
             * Each new fragment view need to request insets because it is not done automatically
             * when doing fragment transactions using navigationController
             */
            view.requestApplyInsets()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    open fun onApplyInsets(v: View, insets: WindowInsets): WindowInsets {
        if (!insets.isConsumed) {
            v.setPadding(v.paddingLeft, insets.systemWindowInsetTop, v.paddingRight, insets.systemWindowInsetBottom)
        }
        return insets
    }
}