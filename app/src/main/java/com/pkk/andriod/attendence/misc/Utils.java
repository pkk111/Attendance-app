package com.pkk.andriod.attendence.misc;

import android.content.Context;
import android.widget.Toast;

public class Utils {

    public static void showShortToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
