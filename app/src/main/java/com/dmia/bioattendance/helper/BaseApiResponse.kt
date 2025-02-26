package com.dmia.bioattendance.helper

import android.util.Log
import com.dmia.bioattendance.model.CommonModelResponse
import com.google.gson.Gson
import retrofit2.Response
import java.io.IOException

abstract class BaseApiResponse {

    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        try {
            val response = apiCall()
            Log.v("Request :", response.raw().request.url.toString())
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    Log.v("Response", response.body().toString())
                    return NetworkResult.Success(body)
                }
            }
            else if (response.code()== 400){
                return try {
                    var  mError =
                        Gson().fromJson(response.errorBody()?.string(), CommonModelResponse::class.java)
                    var datamesage = mError.EmpName
                    error(datamesage)
                } catch (e: IOException) {
                    error("Something went wrong please try after sometime")
                }
            }
            return error("${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(errorMessage: String): NetworkResult<T> =
        NetworkResult.Error(errorMessage)

}