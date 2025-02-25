package com.dmia.bioAttendance.data.constant


interface ApiConstant {

    companion object {
        const val Description = "Description"
        const val EmpName = "EmpName"
        const val Status = "Status"
        const val CompanyUrl = "CompanyUrl"
        const val BioApp = "6DBioApp"
        const val EMPLOYEEID = "EMPLOYEEID"
        const val EMPLOYEEDETAILS = "EMPLOYEEDETAILS"

        // error codes
        const val ERROR_CODE_200 = 200
        const val ERROR_CODE_201 = 201
        const val ERROR_CODE_202 = 202
        const val ERROR_CODE_302 = 302
        const val ERROR_CODE400 = 400
        const val ERROR_CODE_404 = 404
        const val ERRORCODE_500 = 500

        const val INTERVAL = 1000L
        const val FASTEST_INTERVAL = 500L
    }
}