package com.pkk.android.attendance.misc

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.Toast

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
    }
}