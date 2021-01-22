package com.example.rpi_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import java.util.HashMap;
import java.util.Map;


public class Main2Activity extends AppCompatActivity {
    Map<String,Object> componentMap=new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initView();
    }

    protected void initView(){
        componentMap.put("webview",findViewById(R.id.wv_stream));
    }

    /**
     * 获取树莓派快照图片
     * @param v
     */
    public void btn_photo(View v){
        Intent intent=new Intent();
        intent.setAction("com.example.rpi_app.VIDEOPLAY");
        intent.putExtra("CameralURL","http://192.168.50.90:8080/?action=snapshot");
        startActivity(intent);
    }

    /**
     * 内置浏览器获取并显示实时视频流
     * @param v
     */
    public void btn_video(View v){
        WebView wv=(WebView)componentMap.get("webview");
        wv.setInitialScale(200);
        wv.loadUrl("http://192.168.50.90:8080/?action=stream");
    }

}
