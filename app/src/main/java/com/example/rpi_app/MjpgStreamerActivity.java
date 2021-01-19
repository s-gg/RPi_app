package com.example.rpi_app;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class MjpgStreamerActivity extends Activity implements Runnable{
    private SurfaceHolder holder;
    private Thread mythread;
    private Canvas canvas;
    URL videoUrl;
    private String url;
    private int w;
    private int h;
    HttpURLConnection conn;
    Bitmap bmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mjpg_streamer);

        url = getIntent().getExtras().getString("CameralURL");

        w = getWindowManager().getDefaultDisplay().getWidth();
        h = getWindowManager().getDefaultDisplay().getHeight();

        SurfaceView surface = findViewById(R.id.surface);

        surface.setKeepScreenOn(true);// 保持屏幕常亮
        mythread = new Thread(this);
        holder = surface.getHolder();
        holder.addCallback(new Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // TODO Auto-generated method stub

            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // TODO Auto-generated method stub
                mythread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                       int height) {
                // TODO Auto-generated method stub

            }
        });

    }
    private void draw() {
        // TODO Auto-generated method stub
        try {
            InputStream inputstream = null;
            //创建一个URL对象
            videoUrl=new URL(url);
            //利用HttpURLConnection对象从网络中获取网页数据
            conn = (HttpURLConnection)videoUrl.openConnection();
            //设置输入流
            conn.setDoInput(true);
            //连接
            conn.connect();
            //得到网络返回的输入流
            inputstream = conn.getInputStream();
            //创建出一个bitmap
            bmp = BitmapFactory.decodeStream(inputstream);
            canvas = holder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            RectF rectf = new RectF(0, 0, w, h);
            canvas.drawBitmap(bmp, null, rectf, null);
            holder.unlockCanvasAndPost(canvas);
            //关闭HttpURLConnection连接
            conn.disconnect();
        } catch (Exception ex) {
        } finally {
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while(true){
            draw();
        }
    }

}
