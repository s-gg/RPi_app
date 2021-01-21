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
        intent.setAction("com.example.rpi_app.VIDEOPLAY");
        //intent.setData(Uri.parse("192.168.50.71:8081"));
        intent.putExtra("CameralURL","http://192.168.50.90:8080/?action=stream");
        startActivity(intent);
    }

}
