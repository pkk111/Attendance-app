package com.pkk.android.attendance.misc


import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Build
import android.util.TypedValue
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.pkk.android.attendance.R
import java.lang.reflect.Method
import kotlin.random.Random


class Utils {

    companion object {
        @JvmStatic
        fun showShortToast(context: Context, message: String?) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        @JvmStatic
        fun showLongToast(context: Context, message: String?) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        fun getColorFromResource(
            context: Context,
            color: Int,
            theme: Resources.Theme? = null
        ): Int {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.resources.getColor(color, theme)
            } else {
                context.resources.getColor(color)
            }
        }

        fun getDrawableFromResource(
            context: Context,
            drawable: Int,
            theme: Resources.Theme? = null
        ): Drawable {
            val rss = ResourcesCompat.getDrawable(context.resources, drawable, theme)
            return rss ?: throw IllegalArgumentException("Drawable Resource not found")
        }

        fun Int.toDp(context: Context): Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                this.toFloat(),
                context.resources.displayMetrics
            )
        }

        fun getAvatars(): ArrayList<Int> {
            return ArrayList(
                listOf(
                    R.drawable.icon1,
                    R.drawable.icon2,
                    R.drawable.icon3,
                    R.drawable.icon4,
                    R.drawable.icon5,
                    R.drawable.icon6,
                    R.drawable.icon7,
                    R.drawable.icon8,
                    R.drawable.icon9,
                    R.drawable.icon10,
                    R.drawable.icon11,
                    R.drawable.icon12
                )
            )
        }

        fun getBackgrounds(): ArrayList<Int> {
            return ArrayList(
                listOf(
                    R.drawable.background_1,
                    R.drawable.background_2,
                    R.drawable.background_3,
                    R.drawable.background_4,
                    R.drawable.background_5,
                    R.drawable.background_6,
                )
            )
        }

        fun getNumberSuffix(n: Int): String {
            return when (n % 10) {
                1 -> "${n}st"
                2 -> "${n}nd"
                3 -> "${n}rd"
                else -> "${n}th"
            }
        }

        fun getRandomBackground(): Int {
            val size = getBackgrounds().size
            return Random(System.nanoTime()).nextInt(size)
        }

        fun isHotspotOn(context: Context): Boolean {
            val manager =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val method: Method = manager.javaClass.getMethod("getWifiApState")
            method.isAccessible = true
            val invoke = method.invoke(manager) as Int
            return invoke == 13 || invoke == 12
        }

        fun isWifiConnected(context: Context): Boolean {
            val manager =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            return manager.isWifiEnabled
        }

        fun isGPSLocationOff(context: Context): Boolean {
            val manager =
                context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }
    }
}