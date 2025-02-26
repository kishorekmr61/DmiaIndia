package com.dmia.bioattendance.model

import com.dmia.bioattendance.R
import com.dmia.bioattendance.utils.resource.WidgetViewModel


data class EmployeeNameSearchModel(
    var userdata: ArrayList<UsersData>
)

data class UsersData(
    var ID: String = "",
    var Name: String? = "",
) : WidgetViewModel {
    override fun layoutId(): Int {
        return R.layout.employees_row
    }
}
