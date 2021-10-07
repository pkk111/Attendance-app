package com.pkk.android.attendance.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

        val triesLeft = SharedPref.getInt(this, "tries left", 10)
        if (triesLeft <= 0)
            finish()
        SharedPref.setInt(this, "tries left", triesLeft - 1)

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
        headerBinding.navHeaderUsername.text =
            SharedPref.getString(this, CentralVariables.KEY_USERNAME, "")
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        if (requestCode == PERMISSION_CODE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) Log.d(
//                TAG,
//                "Permission Granted"
//            ) else {
//                Toast.makeText(
//                    this,
//                    "Please grant permission to proceed further !",
//                    Toast.LENGTH_SHORT
//                ).show()
//                finish()
//                return
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }
}