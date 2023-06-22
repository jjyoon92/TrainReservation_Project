package com.sdt.trproject.ksh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;

import com.sdt.trproject.R;

import retrofit2.*;


public class HelloworldActivity extends AppCompatActivity {

    Call<ResponseVo> call;
    Button goButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helloworld_actiity);

        goButton = findViewById(R.id.helloWorld);

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HelloworldActivity.this, BoardActivity.class);

                startActivity(intent);


            }
        });
        Intent secondIntent = getIntent();
        int index = secondIntent.getIntExtra("index",0);
        System.out.println("index = " + index);


    }
}