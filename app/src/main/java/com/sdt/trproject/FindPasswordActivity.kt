package com.sdt.trproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.Timer

class FindPasswordActivity : AppCompatActivity() {

    // 타이머 관련 변수
    private var timer: Timer? = null
    private var remainingSeconds = 90

    // 비밀번호 변수
    var token: String? = null

    // OkHttpClient 인스턴스 생성
    private val httpClient by lazy { OkHttpClient() }

    private lateinit var userNumText: TextView

    private lateinit var inputEmail4Text: TextView
    private lateinit var emailAutoComplete1Text: AutoCompleteTextView
    private lateinit var sendEmailBtn: Button

    private lateinit var authCodeText: TextView
    private lateinit var timerTextView: TextView
    private lateinit var authCodeBtn: Button

    private lateinit var changedPwBtn: Button


    private lateinit var userNum: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_password)

        userNumText = findViewById(R.id.userNumText)
        inputEmail4Text = findViewById(R.id.inputEmail4)

        emailAutoComplete1Text = findViewById(R.id.emailAutoComplete1)
        val emailItems = arrayOf("gmail.com", "naver.com", "daum.net")

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, emailItems)
        emailAutoComplete1Text.setAdapter(adapter)
        emailAutoComplete1Text.setOnItemClickListener() { parent, view, position, id ->
            val selectedEmailItem = parent.getItemAtPosition(position).toString()
            showToast("Selected : $selectedEmailItem")
        }


        sendEmailBtn = findViewById(R.id.sendEmailBtn)

        var userEmailSave: String = ""

        sendEmailBtn.setOnClickListener() {

            val userEmail: String =
                inputEmail4Text.text.toString() + "@" + emailAutoComplete1Text.text.toString()
            userEmailSave = userEmail
            existEmailAndId(
                userNumText.text.toString(),
                userEmail
            )
            println(
                "!!!!!!!!!!!!!!!!!!" + existEmailAndId(
                    userNumText.text.toString(),
                    userEmail
                )
            )


        }

        authCodeText = findViewById(R.id.authCodeText)
        timerTextView = findViewById(R.id.timerTextView)
        authCodeBtn = findViewById(R.id.authCodeBtn)

        authCodeBtn.setOnClickListener() {

            userNum = userNumText.text.toString()
            sendEmailToken(userEmailSave, authCodeText.text.toString())

        }

        changedPwBtn = findViewById(R.id.changedPwBtn)

        changedPwBtn.setOnClickListener() {
            val intent =
                Intent(this@FindPasswordActivity, ChangePasswordActivity::class.java)
            intent.putExtra(ChangePasswordActivity.INPUT_USER_NUM, userNumText.text.toString())
            this@FindPasswordActivity.startActivity(intent)
        }

        changedPwBtn.isEnabled = false


    }

    @Parcelize
    data class MyEmail(
        val email: String = "",
    ) : Parcelable

    @Parcelize
    data class AuthCode(
        val email: String = "",
        val authCode: String = ""
    ) : Parcelable

    @Parcelize
    data class SendEmail(
        val id: String = "",
        val email: String = ""

    ) : Parcelable


    private fun changedSendEmail(email: String) {
        // 이메일 요청 구현
        // 요청을 보낼 URL 설정
        val url = "${BuildConfig.SERVER_ADDR}/member/emailSend"
        //요청을 담을 파라미터 설정
//        val requestBody = FormBody.Builder()
//            .add("email",email).build()

        val gson = Gson()
        val data = MyEmail(email)
        val json = gson.toJson(data)
        val mediaType = "application/json".toMediaType()
        val requestBody = json.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 요청 실패 시 처리
                e.printStackTrace()
                lifecycleScope.launch(Dispatchers.Main) {
                    // 화면에 표시할 게 없으면 IO
                    // 화면에 표시할게 있으면 Main
                    showToast("이메일 전송에 실패하였습니다.")
                    // 현 상황은 IO로 돌리다가 표시할 게 있어서 Main 으로 전환 했다가 다시 IO로 돌아오는 상황
                    sendEmailBtn.isEnabled = true
                }

            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    //요청 실패 처리
                    println("Request failed")
                    lifecycleScope.launch(Dispatchers.Main) {
                        showToast("이메일 전송에 실패하였습니다.")
                    }
                    return
                }

                val responseData = response.body?.string()
                // 응답 데이터 처리
                println(responseData)
                lifecycleScope.launch(Dispatchers.Main) {

                    val jsonString = JSONObject(responseData)

                    val responseResult = jsonString.getString("result")

                    showToast("이메일 전송에 성공.")
                    if (responseResult == "success") {
                        if (remainingSeconds != 0 or 90) {
                            remainingSeconds = 90
                            timerTextView.text = remainingSeconds.toString()
                            timer = null
                        }
                        startTimer()
                    } else {
                        showToast("코드오류")
                    }

                    sendEmailBtn.isEnabled = true
                }

            }
        })

    }

    private fun sendEmailToken(email: String, authCode: String) {
        // 요청을 보낼 URL 설정
        val url = "${BuildConfig.SERVER_ADDR}/member/updatePw/matchEmailCode"

        val gson = Gson()
        val data = AuthCode(email, authCode)
        val json = gson.toJson(data)
        val mediaType = "application/json".toMediaType()
        val requestBody = json.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 요청 실패 시 처리
                e.printStackTrace()
                lifecycleScope.launch(Dispatchers.Main) {
                    showToast("이메일 및 토큰 값 전송에 실패하였습니다.")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    // 요청 실패 처리
                    println("Request failed")
                    lifecycleScope.launch(Dispatchers.Main) {
                        showToast("이메일 및 토큰 값 전송에 실패하였습니다.")
                    }
                    return
                }

                val responseData = response.body?.string()
                // 응답 데이터 처리
                println(responseData)
                lifecycleScope.launch(Dispatchers.Main) {

                    val jsonString = JSONObject(responseData)

                    val responseResult = jsonString.getString("result")

                    when (timerTextView.text) {
                        "" -> showToast("이메일 인증을 해주세요")
                        // SingUpActivity 로 넘기기

                        else ->



                            when (responseResult) {
                                "EmailFail" -> {

                                    showToast("인증번호를 확인해주세요")

                                }

                                "expired" -> {
                                    showToast("인증번호 기한이 만료되었습니다.")
                                }

                                "success" -> {
                                    showToast("인증에 성공했습니다.")
                                    stopTimer()
                                    timerTextView.text = ""
                                    changedPwBtn.isEnabled = true

                                }

                                else -> {
                                    showToast("코드오류")
                                    println(responseData)

                                }
                            }
                    }
                }
            }
        })
    }

    // id = email
    private fun existEmailAndId(id: String, email: String) {
        //학원용
        val url = "${BuildConfig.SERVER_ADDR}/member/updatePw/exist"


        val gson = Gson()
        val data = SendEmail(id, email)
        val json = gson.toJson(data)
        val mediaType = "application/json".toMediaType()
        val requestBody = json.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 요청 실패 시 처리
                e.printStackTrace()

                lifecycleScope.launch(Dispatchers.Main) {

                    showToast("회원번호 및 이메일 전송에 실패하였습니다.")
                    println("io익셉션~")

                }

            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    // 요청 실패 처리
                    println("Request failed")
                    lifecycleScope.launch(Dispatchers.Main) {
                        showToast("회원번호 및 이메일 전송에 실패하였습니다.")
                        println("리스폰스 요청실패")
                    }
                    return
                }

                val responseData = response.body?.string()
                // 응답 데이터 처리
                println(responseData)
                lifecycleScope.launch(Dispatchers.Main) {
                    showToast("회원번호 및 이메일 성공적으로 전송 했습니다.")
                    println(request.toString())

                    val jsonString = JSONObject(responseData)

                    val responseResult = jsonString.getString("result")

                    when (responseResult) {

                        "Exist" -> changedSendEmail(email)

                        "Not Exist" -> showToast("회원번호 및 이메일을 확인해주세요.")

                        else -> showToast("코드 오류")
                    }


                }
            }
        })
    }


    // 타이머 시작 함수
    fun startTimer() {
        // 이미 타이머가 실행 중인 경우 중복 실행 방지
        if (timer != null) {
            return
        }
        stopTimer()

        remainingSeconds = 90 // 초기화
        timer = Timer()
        lifecycleScope.launch(Dispatchers.IO) {
            remainingSeconds = 90
            while (true) {
                delay(1_000)

                withContext(Dispatchers.Main) {
                    // 남은 시간을 표시하고 1초씩 감소
                    timerTextView.text = remainingSeconds.toString()
                    remainingSeconds--


                    // 시간이 만료되면 타이머 중지, 비밀번호 초기화
                    if (remainingSeconds < 0) {
                        stopTimer()
                        token = null
                        showToast("인증번호가 만료되었습니다.")
                        authCodeText.text = ""
                        timerTextView.text = ""
                    }
                }
            }
        }
    }


    // 타이머 정지 함수
    private fun stopTimer() {
        lifecycleScope.coroutineContext.cancelChildren()
    }


    // 토스트 메시지 출력 함수
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}