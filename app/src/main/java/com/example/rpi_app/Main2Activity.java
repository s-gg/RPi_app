package com.example.rpi_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public void btn_openVideo(View v){
        Intent intent=new Intent();
        intent.setAction("android.intent.action.VIEW");
        //intent.setData(Uri.parse("192.168.50.71:8081"));
        intent.setData(Uri.parse("https://www.hao123.com/"));
        startActivity(intent);
    }

}