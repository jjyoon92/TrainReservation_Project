package com.sdt.trproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class UserNumPage : AppCompatActivity() {
    private lateinit var foundUserNum: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_num_page)



        foundUserNum = findViewById<TextView?>(R.id.foundUserNum).apply {
//            foundUserNum.text =
        }


    }
}