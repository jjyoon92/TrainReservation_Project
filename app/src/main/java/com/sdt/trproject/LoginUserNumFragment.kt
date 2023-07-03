package com.sdt.trproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.sdt.trproject.databinding.FragmentLoginUserNumBinding
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import okhttp3.Call
import okhttp3.Callback
import okhttp3.JavaNetCookieJar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.cookieToString
import org.chromium.net.impl.RequestFinishedInfoImpl.FinishedReason
import org.json.JSONObject
import java.io.IOException
import java.net.CookieManager
import java.net.CookieStore

class LoginUserNumFragment : Fragment() {

    lateinit var loginUserNumBinding: FragmentLoginUserNumBinding

    private lateinit var userNumId: TextView
    private lateinit var userPw: TextView

    private lateinit var saveUserNumBtn: CheckBox
    private lateinit var autoLoginBtn: CheckBox
    private lateinit var loginBtn: Button

    // OkHttpClient 인스턴스 생성
    private val httpClient by lazy {
        OkHttpClient().newBuilder().apply {
            cookieJar(JavaNetCookieJar(CookieManager()))
        }.build()
    }


    // SharedPreferences 인스턴스 생성
    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences(SharedPrefKeys.PREF_NAME, Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        loginUserNumBinding = FragmentLoginUserNumBinding.inflate(inflater, container, false)

        // View 요소 초기화
        userNumId = loginUserNumBinding.root.findViewById(R.id.userNumId)
        userPw = loginUserNumBinding.root.findViewById(R.id.userPw)

        saveUserNumBtn = loginUserNumBinding.root.findViewById(R.id.saveUserNumBtn)
        autoLoginBtn = loginUserNumBinding.root.findViewById(R.id.autoLoginBtn)
        loginBtn = loginUserNumBinding.root.findViewById(R.id.loginBtn)

        userNumId.text = sharedPreferences.getString("savedUserNumber", "")
        saveUserNumBtn.isChecked = sharedPreferences.getBoolean("isCheckboxChecked", false)
        autoLoginBtn.isChecked = sharedPreferences.getBoolean("isAutoLogin", false)

        // 자동 로그인 로직
        if (autoLoginBtn.isChecked) {
            val savedUserId = sharedPreferences.getString("savedUserId", "")
            val savedUserPw = sharedPreferences.getString("savedUserPw", "")

            if (!savedUserId.isNullOrEmpty() && !savedUserPw.isNullOrEmpty()) {
                sendCredentials(savedUserId, savedUserPw)
            }
        }

        // 체크박스 클릭 시, 상태를 저장하고 member_get 텍스트를 저장 또는 삭제
        saveUserNumBtn.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("isCheckboxChecked", isChecked) // 체크박스 상태 저장
            if (isChecked) { // 체크박스가 체크된 경우
                // 로그인 성공시에 저장하도록 변경
                showToast("사용자 번호 저장을 활성화하였습니다!")
            } else { // 체크박스가 해제된 경우
                // 저장된 텍스트 삭제
                editor.remove("savedUserNumber")
                showToast("사용자 번호 저장을 비활성화하였습니다!")
            }
            editor.apply() // 변경 사항 적용
        }

        // 자동 로그인 체크박스 클릭 시 상태 저장
        autoLoginBtn.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("isAutoLogin", isChecked)
            editor.apply()

            if (!isChecked) {
                editor.remove("savedUserId")
                editor.remove("savedUserPw")
                editor.apply()
            }
        }

        // 로그인 버튼 클릭 시 이벤트 처리
        loginBtn.setOnClickListener {
            val memberId = userNumId.text.toString()
            val memberPw = userPw.text.toString()

            if (saveUserNumBtn.isChecked) { // 회원번호 저장 체크박스가 체크된 경우
                // 사용자 번호 저장
                val editor = sharedPreferences.edit()
                editor.putString("savedUserNumber", memberId)
                editor.apply()
            }

            if (autoLoginBtn.isChecked) { // 자동 로그인 체크박스가 체크된 경우
                // 사용자 ID, PW 저장
                val editor = sharedPreferences.edit()
                editor.putString("savedUserId", memberId)
                editor.putString("savedUserPw", memberPw)
                editor.apply()
            }

            sendCredentials(memberId, memberPw)
        }

        return loginUserNumBinding.root
    }

    // 토스트 메시지 출력 함수
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    @Parcelize
    data class MyData(
        val id: String = "",
        val pw: String = ""

    ) : Parcelable

    private fun sendCredentials(id: String, pw: String) {
        // 요청을 보낼 URL 설정
        val url = "${BuildConfig.SERVER_ADDR}/member/login"

        val gson = Gson()
        val data = MyData(id, pw)
        val json = gson.toJson(data)
        val mediaType = "application/json".toMediaType()
        val requestBody = json.toRequestBody(mediaType)


        val request = Request.Builder()
            .url(url)
//          .addHeader()
            .post(requestBody)
            .build()



        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 요청 실패 시 처리
                e.printStackTrace()
                lifecycleScope.launch(Dispatchers.Main) {
                    showToast("아이디 및 비밀번호 값 전송에 실패하였습니다.")
                }
            }

            override fun onResponse(call: Call, response: Response) {

                if (!response.isSuccessful) {

                    // 요청 실패 처리
                    lifecycleScope.launch(Dispatchers.Main) {
                        showToast("아이디 및 비밀번호 값 전송에 실패하였습니다.")

                    }
                    return
                }

                val responseData = response.body?.string()
                // 응답 데이터 처리
                lifecycleScope.launch(Dispatchers.Main) {

//                    response.headers.forEach {
//                        println("${it.first} : ${it.second} ")
//
//                        if(it.first == "Set-Cookie"){
//                            println("!!!!!!!!!!!!! ${it.second}")
//                        }
//
//                    }


//                    val cookieHeaderValue = response.header(SharedPrefKeys.SET_COOKIE)?.substringBefore(";")
//                    val editor = sharedPreferences.edit()
//                    editor.putString(SharedPrefKeys.SET_COOKIE, cookieHeaderValue)
//                    editor.apply()
//
//                    Log.d("cookieHeaderValue" , cookieHeaderValue!!)


                    // 쿠키 변수
                    val cookies = response.headers(SharedPrefKeys.SET_COOKIE)

                    val jsonString = JSONObject(responseData)

                    val responseResult = jsonString.getString("result")


                    // SingUpActivity 로 넘기기
                    // key : JSESSIONID
                    when (responseResult) {

                        "success" -> {
                            val responseUserData = jsonString.getString("data")
                            //val responseMemberNAme = responseUserData.split(",")[0]
                            val dataJsonObject = JSONObject(responseUserData)
                            val responseUserName = dataJsonObject.getString("memberName")
                            val responseReservationCnt = dataJsonObject.getString("reservationCnt")


                            Log.d(">>>>DataName<<<<<", "${ responseUserName }")
                            Log.d(">>>>DataName<<<<<", "${ responseReservationCnt }")


                            val cookieHeaderValue =
                                response.header(SharedPrefKeys.SET_COOKIE)?.substringBefore(";")
                            val editor = sharedPreferences.edit()
                            editor.putString(SharedPrefKeys.SET_COOKIE, cookieHeaderValue)
                            editor.putString(SharedPrefKeys.USER_NAME, responseUserName)
                            editor.putString(SharedPrefKeys.RESULVATION_CNT, responseReservationCnt)
                            editor.apply()

                            val activity = requireActivity()
                            val intent = Intent(activity, MainActivity::class.java)
                            //intent.putExtra(MainActivity.INPUT_COOKIE,cookies.toString())
                            startActivity(intent)
                            activity.finish()
                        }

                        "failure" -> {
                            val messageResult: String? = jsonString.getString("message")

                            if (messageResult.equals("input_error")) showToast("아이디가 존재하지 않습니다.")
                            if (messageResult.equals("password_no_match")) showToast("비밀번호를 확인해주세요.")

                            Log.d("message", "message : ${messageResult}")
                        }
                    }
                }
            }
        })
    }


}
