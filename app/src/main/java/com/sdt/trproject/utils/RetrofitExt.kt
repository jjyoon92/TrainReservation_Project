package com.sdt.trproject.utils

import android.content.Context
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Path

interface RetrofitRequestService {
    @POST("{path}")
    fun onRequest(@Path("path") requestPath: String, requestBody: RequestBody): Call<ResponseBody>
}

inline fun <reified RESPONSE> RetrofitRequestService.request(
    context: Context,
    requestPath: String,
    data: JSONArray,
    noinline failListener: ((message: String, httpCode: Int) -> Unit)? = null,
    crossinline successListener: (response: RESPONSE) -> Unit
) {
    val requestBody = data.toString()
        .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

    requestOperation<RESPONSE>(context, requestPath, requestBody, failListener, successListener)
}

inline fun <reified RESPONSE> RetrofitRequestService.request(
    context: Context,
    requestPath: String,
    data: JSONObject,
    noinline failListener: ((message: String, httpCode: Int) -> Unit)? = null,
    crossinline successListener: (response: RESPONSE) -> Unit
) {
    val requestBody = data.toString()
        .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

    requestOperation<RESPONSE>(context, requestPath, requestBody, failListener, successListener)
}

inline fun <reified RESPONSE> RetrofitRequestService.requestOperation(
    context: Context,
    requestPath: String,
    requestBody: RequestBody,
    noinline failListener: ((message: String, httpCode: Int) -> Unit)? = null,
    crossinline successListener: (response: RESPONSE) -> Unit
) {
    val call = onRequest(requestPath, requestBody)
    println("call : $call")
    println("보낸거 있음? : ${requestBody.toString()}")
    call.enqueue(object : Callback<ResponseBody> {
        override fun onResponse(
            call: Call<ResponseBody>,
            response: Response<ResponseBody>
        ) {
            println("응답 있음? : ${response.body()}")
            if (!response.isSuccessful) {
                onFailure(
                    call, t = HttpException(
                        Response.error<RESPONSE>(1000 + response.code(), "요청 실패".toResponseBody("text/plain".toMediaType()))
                    )
                )
                return
            }

            val apiResponse = response.body()
            println("응답 확인 : $apiResponse")
            if (apiResponse == null) {
                onFailure(
                    call, t = HttpException(
                        Response.error<RESPONSE>(1000 + 500, "응답 없음".toResponseBody("text/plain".toMediaType()))
                    )
                )
                return
            }
            println("apiResponse : $apiResponse.toString()")
            val response: RESPONSE = Gson().fromJson(apiResponse.string(), RESPONSE::class.java)
            successListener.invoke(response)
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            failListener?.invoke(t.message?: "", if (t is HttpException) t.code() else 0)
                ?: context.showToast(t.message?: "")
        }
    })
}

