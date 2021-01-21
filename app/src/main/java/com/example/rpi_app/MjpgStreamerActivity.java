package com.example.rpi_app;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

import androidx.appcompat.app.AppCompatActivity;

public class MjpgStreamerActivity extends AppCompatActivity {
    private SurfaceHolder holder;
    private Canvas canvas;
    URL videoUrl;
    private String url;
    private int w;
    private int h;
    private InputStream videoIS;
    private HttpURLConnection conn;
    private RectF rectF=new RectF();
    Bitmap bmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mjpg_streamer);

        url = getIntent().getExtras().getString("CameralURL");

        SurfaceView surface = findViewById(R.id.surface);

        surface.setKeepScreenOn(true);// 保持屏幕常亮

        try {
            videoUrl=new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        holder = surface.getHolder();
        holder.addCallback(new Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                while(true){
                    draw();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                       int height) {
                // TODO Auto-generated method stub

            }
        });

    }

    private void draw() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    conn=(HttpURLConnection)videoUrl.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
              //      conn.setDoInput(true);
                    videoIS = conn.getInputStream();
                    bmp = BitmapFactory.decodeStream(videoIS);
                    UIop(bmp);

                }catch(MalformedURLException e){
                    e.printStackTrace();
                }catch(IOException e){
                    e.printStackTrace();
                }finally {
                    if(conn!=null){
                        conn.disconnect();
                    }
                    if(videoIS!=null){
                        try {
                            videoIS.close();
                        } catch(IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    private void UIop(final Bitmap bitmap){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    w=bitmap.getWidth();
                    h=bitmap.getHeight();
                    canvas = holder.lockCanvas();
                    canvas.drawColor(Color.WHITE);
                    rectF.set(0,0,w,h);
                    canvas.drawBitmap(bmp, null, rectF, null);
                    holder.unlockCanvasAndPost(canvas);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

}
