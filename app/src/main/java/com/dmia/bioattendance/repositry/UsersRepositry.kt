package com.dmia.bioattendance.repositry

import com.dmia.bioattendance.data.api.login.UsersHelperImpl
import com.dmia.bioattendance.helper.BaseApiResponse
import com.dmia.bioattendance.helper.NetworkResult
import com.dmia.bioattendance.model.CommonModelResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import javax.inject.Inject

class UsersRepositry @Inject constructor(
    private val usersHelperImpl: UsersHelperImpl
) : BaseApiResponse() {

    suspend fun getDomainData(
    ): Flow<NetworkResult<ResponseBody>> {
        return flow {
            emit(safeApiCall { usersHelperImpl.getdomainResponse() })
        }.flowOn(Dispatchers.IO)
    }
    suspend fun getUserLoginData(
        userid: String, password: String
    ): Flow<NetworkResult<String>> {
        return flow {
            emit(safeApiCall { usersHelperImpl.getLoginResponse(userid, password) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getActivateUserData(
        fingerprintvalue: String, employeeid: String
    ): Flow<NetworkResult<CommonModelResponse>> {
        return flow {
            emit(safeApiCall {
                usersHelperImpl.getRegistrationApprovalResponse(
                    fingerprintvalue,
                    employeeid
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getEmployeeSearchData(
        employeename: String
    ): Flow<NetworkResult<ResponseBody>> {
        return flow {
            emit(safeApiCall { usersHelperImpl.getEmployeeSearchResponse(employeename) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postUserAttendanceData(
        empID: String?,
        fingerprintvalue: String?,
        Latitude: String?,
        Longitude: String?, getGPsPositions: String?
    ): Flow<NetworkResult<CommonModelResponse>> {
        return flow {
            emit(safeApiCall {
                usersHelperImpl.postUserAttendanceData(
                    empID,
                    fingerprintvalue,
                    Latitude,
                    Longitude,
                    getGPsPositions
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postUserRegistrtaionData(
        fingerprintvalue: String?, empID: String?
    ): Flow<NetworkResult<CommonModelResponse>> {
        return flow {
            emit(safeApiCall {
                usersHelperImpl.postUserRegistration(
                    fingerprintvalue,empID

                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getDeactivateUserData(
        employeeid: String
    ): Flow<NetworkResult<CommonModelResponse>> {
        return flow {
            emit(safeApiCall { usersHelperImpl.getDeactivationResponse(employeeid) })
        }.flowOn(Dispatchers.IO)
    }

}