package com.sdt.trproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sdt.trproject.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    lateinit var loginBinding: ActivityLoginBinding

    lateinit var currentFragment: Fragment

    private lateinit var member_btn: Button
    private lateinit var email_btn: Button
    private lateinit var phone_btn: Button

    private lateinit var member_find_btn: Button
    private lateinit var pw_find_btn: Button
    private lateinit var join_btn : Button
    



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        member_btn = findViewById(R.id.member_btn)
        email_btn = findViewById(R.id.email_btn)
        phone_btn = findViewById(R.id.phone_btn)

        fragmentController("login_user_num_fragment", false)

        member_btn.setOnClickListener {
            fragmentController("login_user_num_fragment", false)
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

        pw_find_btn = findViewById(R.id.pw_find_btn)

        pw_find_btn.setOnClickListener(){
            val intent = Intent(this@LoginActivity,FindPasswordActivity::class.java)
            startActivity(intent)
        }


    }

    fun fragmentController(name: String, add: Boolean) {
        when (name) {
            "login_user_num_fragment" -> {
                currentFragment = LoginUserNumFragment()
            }
//            "login_email_fragment" -> {
//                currentFragment = LoginEmailFragment()
//            }
//            "login_phone_fragment" -> {
//                currentFragment = LoginPhoneFragment()
//            }

        }
        val trans = supportFragmentManager.beginTransaction()
        trans.replace(R.id.activity_login_view, currentFragment)

        if (add) {
            trans.addToBackStack(name)
        }

        trans.commit()
    }
}