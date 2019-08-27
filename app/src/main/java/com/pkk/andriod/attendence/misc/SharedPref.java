package com.pkk.andriod.attendence.misc;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPref {

    public void settype(Context context,String userType){
        getEditor(context).putString("type",userType);
    }

    public void gettype(Context context,String userType){
        getEditor(context).putString("type",userType);
    }

    private SharedPreferences.Editor getEditor(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).edit();
    }

    public void clearPrefs(Activity activity) {
        getEditor(activity).clear().putBoolean("isLoggedIn", false).apply();
    }
}
