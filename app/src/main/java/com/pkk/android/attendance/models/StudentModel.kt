package com.pkk.android.attendance.models

class StudentModel {

    var rollNo: Int
    var isPresent: Boolean
    var ipAddress: String = ""

    constructor(rollNo: Int) {
        this.rollNo = rollNo
        isPresent = false
    }

    constructor(rollNo: Int, isPresent: Boolean, ipAddress: String?) {
        this.rollNo = rollNo
        this.isPresent = isPresent
        if (ipAddress != null) this.ipAddress = ipAddress
    }
}