package com.sdt.trproject.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
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

fun JSONArray.requestBody(): RequestBody =
    this.toString().requestBody("application/json; charset=utf-8".toMediaTypeOrNull())

fun JSONObject.requestBody(): RequestBody =
    toString().requestBody("application/json; charset=utf-8".toMediaTypeOrNull())

fun String.requestBody(mediaType: MediaType? = null): RequestBody =
    toRequestBody(mediaType)


//interface A {
//    fun a() {}
//}
//
//interface B {
//    fun b() {}
//}
//
//abstract class AC {
//    fun ac() {}
//}
//
//
//
//fun call(a: A) {
//    if(a is AC) {
//
//    }
//}
//
//inline fun <reified G, T> callG(g: T) where T: A, T: AC, T: B{
//    g.a()
//    g.ac()
//    g.b()
//
//    var gg: G = G::class.java.newInstance()
//}

//fun <T,R> List<T>.fn(): List<R> {
//
//}

inline fun <RESPONSE, reified CONVERT> Call<RESPONSE>.handle(
    context: Context,
    noinline onFail: ((message: String, httpCode: Int) -> Unit)? = null,
    crossinline onSuccess: (response: CONVERT) -> Unit
) {
    enqueue(object : Callback<RESPONSE> {
        override fun onResponse(
            call: Call<RESPONSE>,
            response: Response<RESPONSE>
        ) {
            if (!response.isSuccessful) {
                onFailure(
                    call, t = HttpException(
                        Response.error<CONVERT>(
                            1000 + response.code(),
                            "요청 실패".toResponseBody("text/plain".toMediaType())
                        )
                    )
                )
                return
            }

            val resp = response.body()
            if (resp == null) {
                onFailure(
                    call, t = HttpException(
                        Response.error<CONVERT>(
                            1000 + 500,
                            "응답 없음".toResponseBody("text/plain".toMediaType())
                        )
                    )
                )
                return
            }
            println("resp : ${resp is ResponseBody}")
            val converted: CONVERT = if(resp is ResponseBody) {
                //val respBody = resp as ResponseBody
                val type = object : TypeToken<CONVERT>() {}.type
                Gson().fromJson(resp.string(), CONVERT::class.java)
            } else {
                CONVERT::class.java.cast(resp)
            }
            onSuccess.invoke(converted)
        }

        override fun onFailure(call: Call<RESPONSE>, t: Throwable) {
            onFail?.invoke(t.message ?: "", if (t is HttpException) t.code() else 0)
                ?: context.showToast(t.message ?: "")
        }
    })
}

