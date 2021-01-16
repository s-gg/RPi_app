package com.example.rpi_app;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener{

    Map<String, Object> componentMap = new HashMap<>(); //用于存储组件
    private final String ParamURL = "192.168.50.71:8081"; //树莓派URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    /**
     * 初始化活动组件
     */
    public void initView() {
        //为活动添加必要组件
        componentMap.put("btn_getVideo", findViewById(R.id.btn_getVideo)); //从树莓派获取视频的按钮
        componentMap.put("btn_startPlay",findViewById(R.id.btn_playVideo)); //读取视频文件并播放视频的按钮
        componentMap.put("tv_videoStat", findViewById(R.id.tv_videoStatus)); //提示是否接收到视频

        VideoView vv=findViewById(R.id.vv_recv);
        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Button btn_play=(Button)componentMap.get("btn_startPlay");
                btn_play.setEnabled(true);
            }
        });
        componentMap.put("vv_recv",vv );  //视频播放组件
        componentMap.put("sb_videoProgressController",findViewById(R.id.sb_vprg));//控制视频进度条

        ToggleButton tgb = findViewById(R.id.tgb_swi); //控制视频播放暂停的按钮
        tgb.setOnCheckedChangeListener(this); //设置该按键状态改变监听器
        componentMap.put("tb_videoCtrl",tgb);
    }

    /**
     * ToggleButton控制视频播放的方法
     * @param cb
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton cb,boolean isChecked){
        switch (cb.getId()){
            case R.id.tgb_swi:
                //如果改变之后是true则置ToggleButton为TextOff，否则为TextOn
                if(isChecked){
                    cb.setChecked(false);
                } else{
                    cb.setChecked(true);
                }
                //控制视频播放
                videoCtrl((VideoView) componentMap.get("vv_recv"));
        }
    }

    /**
     * 视频播放控制方法
     * @param vv
     */
    public void videoCtrl(VideoView vv){
        if(vv!=null) {
            if (vv.isPlaying()) {
                vv.pause();
                Toast.makeText(this, "暂停播放", Toast.LENGTH_LONG).show();
            } else {
                vv.start();
                Toast.makeText(this, "开始播放", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * btn_getVideo 的onClick方法，用于从树莓派获取视频
     * @param view
     */
    public void getVideo(View view) {
        //建立局域网连接
        HttpURLConnection connection = getConn();
        //为接收视频创建必要流
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        try {
            bis = new BufferedInputStream(connection.getInputStream());
            fos = new FileOutputStream("video_cache.mp4");

            byte[] videoBuff = new byte[1024 * 1024 * 100];  //视频接收上限为100M
            int len = 0;

            while ((len = bis.read(videoBuff)) > 0) {
                fos.write(videoBuff, 0, len);
            }
            //显示视频接收结果
            TextView tv_recvStat = (TextView) componentMap.get("tv_videoStat");
            if (len > 0) {
                tv_recvStat.setText("视频接收成功");
            } else {
                tv_recvStat.setText("视频接收失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //释放流对象
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从存储中读取并播放视频
     * @param view
     */
    public void playVideo(View view){
        File file = new File("video_cache.mp4");
        if (!file.exists()){
            Toast.makeText(this, "视频不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        final VideoView vv=(VideoView)componentMap.get("vv_recv");
        vv.setVideoPath(file.getPath());
        //如果没有视频正在播放则
        if(!vv.isPlaying()){
            final SeekBar seekBar=(SeekBar)componentMap.get("sb_videoProgressController");
            initSeekBar(seekBar);
            vv.start();
            //创建一个新线程，每0.5s更新一次视频进度条
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (vv.isPlaying()) {
                            int currentProgress = vv.getCurrentPosition();
                            seekBar.setProgress(currentProgress);
                            Thread.sleep(500);
                        }
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        //视频播放后设置按钮不可用，只有停止播放后才能恢复
        Button btn_startPlay=(Button)componentMap.get("btn_startPlay");
        btn_startPlay.setEnabled(false);
    }

    /**
     * 根据VideoView接收到的视频初始化进度条配置和监听器
     * @param seekBar
     */
    public void initSeekBar(SeekBar seekBar){
        final VideoView vv=(VideoView)componentMap.get("vv_recv");
        if(vv!=null) {
            seekBar.setMax(vv.getDuration());
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int progress = seekBar.getProgress();
                    if (vv.isPlaying()) {
                        vv.seekTo(progress);
                    }
                }
            });
        }
    }

    /**
     * 停止播放按钮onclick方法
     * @param view
     */
    public void StopPlaying(View view){
        VideoView vv=(VideoView)componentMap.get("vv_recv");
        if(vv!=null&&vv.isPlaying()){
            vv.stopPlayback();
            //恢复开始播放按钮为可用
            Button btn_startPlay=(Button)componentMap.get("btn_startPlay");
            btn_startPlay.setEnabled(true);
        }
    }

    /**
     * 封装网络连接和配置方法并返回HttpURLConnection对象
     * @return
     */
    public HttpURLConnection getConn(){
        HttpURLConnection connection=null;
        InputStream is=null;
        try{
            URL url=new URL(ParamURL);
            connection=(HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(20000);
        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return connection;
    }

}