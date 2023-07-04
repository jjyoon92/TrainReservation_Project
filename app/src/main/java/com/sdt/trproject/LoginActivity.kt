package com.sdt.trproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.sdt.trproject.databinding.ActivityLoginBinding
import okhttp3.OkHttpClient


class LoginActivity : AppCompatActivity() {

    lateinit var loginBinding: ActivityLoginBinding

    lateinit var currentFragment: Fragment

    private lateinit var member_btn: Button
    private lateinit var email_btn: Button
    private lateinit var phone_btn: Button


    private lateinit var member_find_btn: Button
    private lateinit var pw_find_btn: Button
    private lateinit var join_btn : Button

    private lateinit var nav_btn : ImageView
    private lateinit var sidebar: DrawerLayout;
    private lateinit var loginBtnInAppbarFooter : TextView
    private lateinit var appbarTitle : TextView



    private val httpClient by lazy { OkHttpClient() }

    // SharedPreferences 인스턴스 생성
    private val sharedPreferences by lazy {
//        getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
        getSharedPreferences(SharedPrefKeys.PREF_NAME, Context.MODE_PRIVATE)
    }

//

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        appbarTitle = findViewById<TextView?>(R.id.appbarTitle).apply {
            setText("로그인")
        }

        nav_btn = findViewById(R.id.nav_btn)
        sidebar = findViewById(R.id.activity_login)
        nav_btn.setOnClickListener {
            if (!sidebar.isDrawerOpen(GravityCompat.START)) {
                sidebar.openDrawer(GravityCompat.START)
            } else {
                sidebar.closeDrawer(GravityCompat.START)
            }
        }
        loginBtnInAppbarFooter = findViewById(R.id.loginBtnInAppbarFooter)
        loginBtnInAppbarFooter.setOnClickListener {
            sidebar.closeDrawer(GravityCompat.START)
            loginFun(loginBtnInAppbarFooter)
        }


        member_btn = findViewById(R.id.member_btn)
        email_btn = findViewById(R.id.email_btn)
        phone_btn = findViewById(R.id.phone_btn)

        val defaultLoginType = sharedPreferences.getString(SharedPrefKeys.DEFAULT_LOGIN_TYPE,"login_user_id_fragment")!!
        fragmentController(defaultLoginType, false)

        member_btn.setOnClickListener {
            fragmentController("login_user_id_fragment", false)
        }
        email_btn.setOnClickListener {
            fragmentController("login_email_fragment", false)
        }
        phone_btn.setOnClickListener {
            fragmentController("login_phone_fragment", false)
        }

        join_btn =findViewById(R.id.join_btn)

        join_btn.setOnClickListener(){
            val intent = Intent(this@LoginActivity,SignUpVerificationActivity::class.java)
            startActivity(intent)
        }

        member_find_btn = findViewById(R.id.member_find_btn)

        member_find_btn.setOnClickListener(){
            val intent = Intent(this@LoginActivity,FindUserIdMainActivity::class.java)
            startActivity(intent)
        }

        member_find_btn = findViewById(R.id.member_find_btn)

        member_find_btn.setOnClickListener(){
            val intent = Intent(this@LoginActivity,FindUserIdMainActivity::class.java)
            startActivity(intent)
        }

        pw_find_btn = findViewById(R.id.pw_find_btn)

        pw_find_btn.setOnClickListener(){
            val intent = Intent(this@LoginActivity,FindPasswordActivity::class.java)
            startActivity(intent)
        }


    }

    private fun loginFun(loginBtnInAppbarFooter: TextView) {

        when (loginBtnInAppbarFooter.text.toString()) {

            AppbarKeys.LOG_IN -> {
                finish()
                val intent = Intent(this@LoginActivity, LoginActivity::class.java)
                startActivity(intent)
            }


            else -> {
                Log.d("goLoginButton", "코드오류")

                val intent = Intent(this@LoginActivity, this@LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun fragmentController(name: String, add: Boolean) {
        when (name) {
            "login_user_id_fragment" -> {
                currentFragment = LoginUserIdFragment()
            }
            "login_email_fragment" -> {
                currentFragment = LoginEmailFragment()
            }
            "login_phone_fragment" -> {
                currentFragment = LoginPhoneFragment()
            }

        }
        val trans = supportFragmentManager.beginTransaction()
        trans.replace(R.id.activity_login_view, currentFragment)

        if (add) {
            trans.addToBackStack(name)
        }

        trans.commit()
    }
    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}