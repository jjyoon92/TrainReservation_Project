package com.sdt.trproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context


class FindUserIdResultPage : AppCompatActivity() {

    companion object {
        const val INPUT_USER_ID = "ID"
    }

    private lateinit var foundUserId: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_user_id_result_page)

        foundUserId = findViewById<TextView>(R.id.foundUserId)

//        Log.d("GetStringExtra", "${arrayOf(intent.getStringExtra(INPUT_USER_ID).split(" "))[2]}")
//        foundUserId.setText("회원번호 : ${intent.getStringExtra(INPUT_USER_ID)}")



        copyTextOnClick(this, foundUserId.toString())

    }

    fun copyTextOnClick(context: Context, text : String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", text)
        clipboardManager.setPrimaryClip(clipData)
    }


}