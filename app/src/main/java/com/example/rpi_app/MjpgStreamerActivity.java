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
    private SurfaceHolder holder;   //用于控制SurfaceView组件
    private Canvas canvas;          //用于向界面写入图形
    URL videoUrl;                   //URL地址
    private String url;             //URL字符串
    Bitmap bmp;                     //位图，作为写入UI界面的数据类型
    private int w;                  //位图宽
    private int h;                  //位图高
    private InputStream videoIS;    //用于接收树莓派的图片流
    private HttpURLConnection conn; //用于连接到树莓派
    private RectF rectF=new RectF();//用于指定往UI上写入图片的尺寸

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mjpg_streamer);
        //从Main2Activity中接收传递过来的url字符串参数
        url = getIntent().getExtras().getString("CameralURL");
        //从布局中获取SurfaceView图片显示对象
        SurfaceView surface = findViewById(R.id.surface);
        // 保持屏幕常亮
        surface.setKeepScreenOn(true);
        //将字符串转为url类型
        try {
            videoUrl=new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //控制SurfaceView的生命周期，实现Surface功能函数
        holder = surface.getHolder();
        holder.addCallback(new Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                    draw();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                       int height) {
            }
        });

    }

    /**
     * 显示图片
     */
    private void draw() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                    try {
                        //与树莓派建立连接
                        conn = (HttpURLConnection) videoUrl.openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        //获取输入流
                        videoIS = conn.getInputStream();
                        //将输入流的数据解码成位图
                        bmp = BitmapFactory.decodeStream(videoIS);
                        //因为子线程无法修改UI，故需要传递位图参数在主线程修改UI界面
                        UIop(bmp);
                    }catch (MalformedURLException e) {
                        e.printStackTrace();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        // 释放所用到的流对象
                        if(conn!=null){
                            conn.disconnect();
                        }
                        if (videoIS != null) {
                            try {
                                videoIS.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

        }).start();
    }

    /**
     * 进入主线程修改UI界面
     * @param bitmap
     */

    private void UIop(final Bitmap bitmap){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    //获取图片的尺寸，以便通过进行RectF显示
                    w=bitmap.getWidth();
                    h=bitmap.getHeight();
                    //获取画笔并加锁，开始在SurfaceView上写入图片
                    canvas = holder.lockCanvas();
                    canvas.drawColor(Color.WHITE);
                    rectF.set(0,0,w,h);
                    canvas.drawBitmap(bmp, null, rectF, null);
                    //解锁画笔，图片显示完毕
                    holder.unlockCanvasAndPost(canvas);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

}
