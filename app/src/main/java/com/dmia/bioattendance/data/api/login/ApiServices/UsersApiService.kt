package com.dmia.bioattendance.data.api.login.ApiServices

 import com.dmia.bioattendance.model.CommonModelResponse
 import okhttp3.ResponseBody
 import retrofit2.Response
 import retrofit2.http.GET
import retrofit2.http.Query

interface UsersApiService {

    @GET("/Login/api/bio/domains")
    suspend fun getdomainResponse(): Response<ResponseBody>

    @GET("/Login/api/bio/check?")
    suspend fun postUserAttendance(
        @Query("empID") empID: String?,
        @Query("fingerprintvalue") fingerprintvalue: String?,
        @Query("lat") Latitude: String?,
        @Query("lng") Longitude: String?, @Query("callback") getGPsPositions: String?
    ): Response<CommonModelResponse>


    @GET("/Login/api/bio/deregister?")
    suspend fun getDeactivationResponse(@Query("empID") empID: String?): Response<CommonModelResponse>

    @GET("/Login/api/bio/fetchID?")
    suspend fun getEmployeeSearchResponse(@Query("EmployeeNameSearchString") EmployeeNameSearchString: String?): Response<ResponseBody>

    @GET("/Login/api/bio/admin?")
    suspend fun getLoginResponse(
        @Query("userName") userName: String?,
        @Query("password") password: String?
    ): Response<String>


    @GET("/Login/api/bio/register?")
    suspend fun RegisterUserResponse(
        @Query("fingerprintvalue") fingerprintvalue: String?,
        @Query("empID") empID: String?
    ): Response<CommonModelResponse>


    @GET("/Login/api/bio/registrationApproval?")
    suspend fun getRegistrationApprovalResponse(
        @Query("fingerprintvalue") fingerprintvalue: String?,
        @Query("empID") empID: String?
    ): Response<CommonModelResponse>

}