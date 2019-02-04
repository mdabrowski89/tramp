package pl.mobite.tramp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.ConnectivityManager
import android.os.Build
import androidx.core.content.res.ResourcesCompat
import pl.mobite.tramp.data.repositories.models.TimeEntry
import java.text.SimpleDateFormat
import java.util.*


fun isLollipopOrHigher() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

fun isOreoOrHigher() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

fun isMarshmallowOrHigher() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

fun getBitmap(context: Context, drawableRes: Int): Bitmap? {
    return ResourcesCompat.getDrawable(context.resources, drawableRes, null)?.let { drawable ->
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas()
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        bitmap
    }
}

fun dpToPx(context: Context, dp: Int) = dp * context.resources.displayMetrics.density

@SuppressLint("SimpleDateFormat")
fun getCurrentTime(): TimeEntry {
    val time = SimpleDateFormat("HH:mm").format(Calendar.getInstance().time)
    val hour = time.subSequence(0, 2).toString().toIntOrNull() ?: 0
    val minute = time.subSequence(3, 5).toString().toIntOrNull() ?: 0
    return TimeEntry(hour, minute)
}

fun hasNetwork(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return cm.activeNetworkInfo?.isConnected ?: false
}
