package com.sdt.trproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast


class FindUserIdResultPage : AppCompatActivity() {

    companion object {
        const val INPUT_USER_ID = "ID"
    }

    private lateinit var foundUserId: TextView
    private lateinit var changeThisToMain : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_user_id_result_page)

        foundUserId = findViewById<TextView>(R.id.foundUserId)
        foundUserId.setText("회원번호 : ${intent.getStringExtra(INPUT_USER_ID)}")

        changeThisToMain = findViewById<TextView?>(R.id.changeThisToMainText).apply {
            setOnClickListener(){
                intent = Intent(this@FindUserIdResultPage,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }


        Log.d("GetStringExtra", "${intent.getStringExtra(INPUT_USER_ID)}")
//

        foundUserId.setOnClickListener(){

            val userIdStr = foundUserId.text.toString().split(" ")[2]
            copyTextOnClick(this, userIdStr)
            showToast("클리보드에 저장되었습니다.")

        }
        // 버튼을 누르면 split 된 회원번호가 저장된다.
    }


    fun copyTextOnClick(context: Context, text : String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", text)
        clipboardManager.setPrimaryClip(clipData)
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}