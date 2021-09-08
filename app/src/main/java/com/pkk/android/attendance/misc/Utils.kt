package com.pkk.android.attendance.misc


import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.widget.Toast
import com.pkk.android.attendance.R


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
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context.resources.getDrawable(drawable, theme)
            } else {
                context.resources.getDrawable(drawable)
            }
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

    }
}