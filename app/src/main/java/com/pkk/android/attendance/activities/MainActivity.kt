package com.pkk.android.attendance.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.pkk.android.attendance.R
import com.pkk.android.attendance.databinding.ActivityMainBinding
import com.pkk.android.attendance.databinding.NavHeaderMainBinding
import com.pkk.android.attendance.misc.CentralVariables
import com.pkk.android.attendance.misc.SharedPref
import com.pkk.android.attendance.misc.Utils
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var _headerBinding: NavHeaderMainBinding? = null
    private val headerBinding get() = _headerBinding!!

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        _headerBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0))
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        binding.appBarMain.toolbar.bringToFront()
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_meetings,
                R.id.nav_profile,
                R.id.nav_feedback
            ), binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        headerBinding.navHeaderProfilePic.setImageResource(
            Utils.getAvatars()[SharedPref.getInt(
                this,
                CentralVariables.KEY_PROFILE_PIC,
                0
            )]
        )
//        checkForPermissions(0, true)
        headerBinding.navHeaderUsername.text =
            SharedPref.getString(this, CentralVariables.KEY_USERNAME, "")
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    private fun checkForPermissions(requestCode: Int, askForPermissions: Boolean): Boolean {
        val permissions = ArrayList<String>()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ) if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissions.size > 0) {
            if (askForPermissions) ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray(),
                requestCode
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}