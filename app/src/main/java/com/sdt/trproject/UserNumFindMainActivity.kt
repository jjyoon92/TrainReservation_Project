package com.sdt.trproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class UserNumFindMainActivity : AppCompatActivity() {

    private lateinit var email_user_find_btn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_num_find_main)

        email_user_find_btn = findViewById(R.id.email_user_find_btn)

        email_user_find_btn.setOnClickListener(){
            intent = Intent(this@UserNumFindMainActivity,UserNumFindSendMailActivity::class.java)
            this@UserNumFindMainActivity.startActivity(intent)
        }
    }
}