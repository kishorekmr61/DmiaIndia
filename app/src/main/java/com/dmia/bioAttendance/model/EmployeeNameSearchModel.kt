package com.dmia.bioAttendance.model

import com.customer.bms.utils.resource.WidgetViewModel
import com.dmia.bioAttendance.R
import org.json.JSONObject


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
