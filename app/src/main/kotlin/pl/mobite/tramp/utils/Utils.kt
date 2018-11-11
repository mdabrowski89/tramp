package pl.mobite.tramp.utils

import android.os.Build


fun isLollipopOrHigher() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

fun isOreoOrHigher() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

fun isMarshmallowOrHigher() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
