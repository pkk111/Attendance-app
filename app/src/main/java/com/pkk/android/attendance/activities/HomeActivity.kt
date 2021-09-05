package com.pkk.android.attendance.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.pkk.android.attendance.R
import com.pkk.android.attendance.misc.CentralVariables.student
import com.pkk.android.attendance.misc.CentralVariables.teacher
import com.pkk.android.attendance.misc.Utils.Companion.toDp
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initialize()
    }

    private fun initialize() {
        var height = student_login_card.resources.configuration.screenHeightDp
        var width = student_login_card.resources.configuration.screenWidthDp
        student_login_card.radius = (height.coerceAtMost(width) * 0.5).toInt().toDp(this)
        height = teacher_login_card.resources.configuration.screenHeightDp
        width = teacher_login_card.resources.configuration.screenWidthDp
        teacher_login_card.radius = (height.coerceAtMost(width) * 0.5).toInt().toDp(this)
        studentLogin.setOnClickListener { studentLogin() }
        teacherLogin.setOnClickListener { teacherLogin() }
    }

    private fun studentLogin() {
        if (checkForPermissions(student, true)) {
            val i = Intent(this, StudentActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    private fun teacherLogin() {
        if (checkForPermissions(teacher, true)) {
            val i = Intent(this, TeacherActivity::class.java)
            startActivity(i)
            finish()
        }
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
            student -> if (checkForPermissions(student, false)) studentLogin()
            teacher -> if (checkForPermissions(teacher, false)) teacherLogin()
            else -> Log.e("permission", "permission result code: $requestCode")
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}