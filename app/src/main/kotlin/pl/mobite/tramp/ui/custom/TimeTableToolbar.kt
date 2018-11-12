package pl.mobite.tramp.ui.custom

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.view_time_table_toolbar.view.*
import pl.mobite.tramp.R
import pl.mobite.tramp.utils.isLollipopOrHigher


class TimeTableToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
): FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_time_table_toolbar, this, true)
        setBackgroundColor(ContextCompat.getColor(context, R.color.darkViolet))
        if (isLollipopOrHigher()) {
            setOnApplyWindowInsetsListener { _, insets ->
                if (!insets.isConsumed) {
                    this.setPadding(paddingLeft, insets.systemWindowInsetTop, paddingRight, paddingBottom)
                }
                insets
            }
        }

        buttonClose.setOnClickListener {
            (context as Activity).onBackPressed()
        }
    }

    fun setLineNumber(lineNumber: String) {
        lineNumberText.text = lineNumber
    }
}