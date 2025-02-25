package com.dmia.bioAttendance.data.api.login

import com.dmia.bioAttendance.data.api.login.ApiServices.UsersApiService
import javax.inject.Inject

class UsersHelperImpl @Inject constructor(private val usersApiService: UsersApiService) {

    suspend fun getdomainResponse() =
        usersApiService.getdomainResponse()

    suspend fun postUserAttendanceData(
        empID: String?,
        fingerprintvalue: String?,
        Latitude: String?,
        Longitude: String?, getGPsPositions: String?
    ) =
        usersApiService.postUserAttendance(
            empID,
            fingerprintvalue,
            Latitude,
            Longitude,
            getGPsPositions
        )

    suspend fun getDeactivationResponse(empID: String?) =
        usersApiService.getDeactivationResponse(empID)

    suspend fun getEmployeeSearchResponse(employeeNameSearchString: String?) =
        usersApiService.getEmployeeSearchResponse(employeeNameSearchString)

    suspend fun getLoginResponse(
        userName: String?,
        password: String?
    ) =
        usersApiService.getLoginResponse(userName, password)

    suspend fun postUserRegistration(
        fingerprintvalue: String?,
        empID: String?
    ) =
        usersApiService.RegisterUserResponse(fingerprintvalue, empID)

    suspend fun getRegistrationApprovalResponse(
        fingerprintvalue: String?,
        empID: String?
    ) =
        usersApiService.getRegistrationApprovalResponse(fingerprintvalue, empID)

}