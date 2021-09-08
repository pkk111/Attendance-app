package com.pkk.android.attendance.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pkk.android.attendance.R
import com.pkk.android.attendance.fragments.HomeFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .replace(R.id.blank_layout, HomeFragment.newInstance()).addToBackStack(null).commit()
    }

}