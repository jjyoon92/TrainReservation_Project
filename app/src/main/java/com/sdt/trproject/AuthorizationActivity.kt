//package com.sdt.trproject
//
//import android.util.Log
//import android.widget.TextView
//import androidx.lifecycle.lifecycleScope
//import com.google.gson.Gson
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import okhttp3.Headers
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.Request
//import okhttp3.RequestBody.Companion.toRequestBody
//import org.json.JSONObject
//import java.io.IOException
//
//abstract class AuthorizationActivity : BaseActivity() {
//
//    override fun onNavigationMenuItemClicked(onProceed: (isSuccessful: Boolean)->Unit) {
//        authorized(onProceed = onProceed)
//    }
//
//    fun authorized(onProceed: ((isSuccessful: Boolean)->Unit)? = null) {
//        val url = "${BuildConfig.SERVER_ADDR}/member/authorized/member"
//        val gson = Gson()
//        val data = ""
//        val json = gson.toJson(data)
//        val mediaType = "application/json".toMediaType()
//        val requestBody = json.toRequestBody(mediaType)
//
//        val request = Request.Builder()
//            .url(url)
//            .headers(
//                Headers.headersOf(
//                    //SharedPrefKeys.SET_COOKIE
//                    SharedPrefKeys.COOKIE,
//                    sharedPreferences.getString(SharedPrefKeys.SET_COOKIE, "") ?: ""
//                    //, sharedPreferences.getString(SharedPrefKeys.SET_COOKIE, "") !!
//                )
//            )
//            .post(requestBody)
//            .build()
//
//
//        httpClient.newCall(request).enqueue(object : okhttp3.Callback {
//            override fun onFailure(call: okhttp3.Call, e: IOException) {
//                // 요청 실패 시 처리
//                e.printStackTrace()
//                lifecycleScope.launch(Dispatchers.Main) {
//                    Log.d("/member/authorized1", "IOException")
//                    showToast("쿠키값 확인 실패.")
//                }
//            }
//
//            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
//
//                if (!response.isSuccessful) {
//
//                    // 요청 실패 처리
//                    lifecycleScope.launch(Dispatchers.Main) {
//                        Log.d("/member/authorized2", "Response")
//                        showToast("쿠키값 확인 실패.")
//                    }
//                    return
//                }
//
//                val responseData = response.body?.string()
//                // 응답 데이터 처리
//                lifecycleScope.launch(Dispatchers.Main) {
//
//                    val jsonString = JSONObject(responseData)
//                    val responseResult = jsonString.getString("result")
//
//                    Log.d("/member/authorized3", "${responseData}")
//                    Log.d("이거 확인좀", (SharedPrefKeys.SET_COOKIE))
//                    Log.d(
//                        "이거 확인좀",
//                        (sharedPreferences.getString(SharedPrefKeys.SET_COOKIE, "") ?: "")
//                    )
//
//
//                    when (responseResult) {
//
//                        "failure" -> {
//                            loginText.text = AppbarKeys.LOG_OUT_STATUS
//                            loginBtnInAppbarFooter.text = AppbarKeys.LOG_IN
//                            Log.d("/member/authorized4", loginBtnInAppbarFooter.text.toString())
//                            profilePhotoBtn.isEnabled = false
//                            getCookie = false
//
//                            onProceed?.invoke(false);
//                        }
//
//                        "success" -> {
//
//                            val userName =
//                                sharedPreferences.getString(SharedPrefKeys.USER_NAME, "")
//                            val reservationCnt =
//                                sharedPreferences.getString(SharedPrefKeys.RESULVATION_CNT, "")
//
//                            loginText.text =
//                                " ${userName}" + AppbarKeys.LOG_IN_STATUS + "${reservationCnt}"
//                            loginBtnInAppbarFooter.text = AppbarKeys.LOG_OUT
//                            Log.d("/member/authorized5", loginBtnInAppbarFooter.text.toString())
//                            profilePhotoBtn.isEnabled = true
//                            getCookie = true
//                            // TODO: 이부분 수정할 것. null 처리 임시로 막아.
////                            resizeBitmap(profilePhotoBtn)
//
//                            onProceed?.invoke(true);
//                        }
//
//                    }
//                }
//            }
//        })
//    }
//}