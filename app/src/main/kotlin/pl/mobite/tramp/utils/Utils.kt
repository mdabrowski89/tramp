package pl.mobite.tramp.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import androidx.core.content.res.ResourcesCompat
import pl.mobite.tramp.TrampApp


fun isLollipopOrHigher() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

fun isOreoOrHigher() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

fun isMarshmallowOrHigher() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

fun getBitmap(drawableRes: Int): Bitmap? {
    ResourcesCompat.getDrawable(TrampApp.instance.resources, drawableRes, null)?.let { drawable ->
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas()
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }
}

fun dpToPx(dp: Int) = dp * TrampApp.instance.resources.displayMetrics.density
