package com.pkk.android.attendance.misc

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.pkk.android.attendance.R

class SharedPref {

    companion object {

        fun setString(context: Context, key: String, value: String) {
            getEditor(context).putString(key, value)
        }

        fun setInt(context: Context, key: String, value: Int) {
            getEditor(context).putInt(key, value)
        }

        fun getString(context: Context, key: String?, def: String = ""): String? {
            return getSharedPreference(context).getString(key, def)
        }

        fun getInt(context: Context, key: String, def: Int = -1): Int {
            return getSharedPreference(context).getInt(key, def)
        }

        private fun getSharedPreference(context: Context): SharedPreferences {
            return context.getSharedPreferences(
                context.getString(R.string.file_preference),
                Context.MODE_PRIVATE
            )
        }

        private fun getEditor(context: Context): SharedPreferences.Editor {
            return getSharedPreference(context).edit()
        }

        fun clearPrefs(activity: Activity) {
            getEditor(activity).clear().apply()
        }

    }
}