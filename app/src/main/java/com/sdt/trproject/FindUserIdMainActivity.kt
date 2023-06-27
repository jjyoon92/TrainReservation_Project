package com.sdt.trproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView


class FindUserIdMainActivity : AppCompatActivity() {

    private lateinit var email_user_find_btn : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_user_id_main)

        email_user_find_btn = findViewById(R.id.BtnFindUserNum)

        email_user_find_btn.setOnClickListener(){
            intent = Intent(this@FindUserIdMainActivity,FindUserIdSendMailActivity::class.java)
            this@FindUserIdMainActivity.startActivity(intent)
            finish()
        }
    }
}