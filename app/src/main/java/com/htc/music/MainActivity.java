package com.htc.music;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ListView Musiclist;
    private Intent intentService = null;
    private SeekBar MusicSeekbar =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestReadExternalPermission();

    }

    private void  onCreateInit() {
        intentService = new Intent(MainActivity.this,MusicService.class);

        TextView musicNameView =(TextView) findViewById(R.id.htcMusicName);

        MusicSeekbar = (SeekBar) findViewById(R.id.htcMusicSeekBar);
        //  MusicSeekbar.setVisibility(View.GONE);
        MusicUtil.setInit(mHandler,musicNameView,this,MusicSeekbar);

        Musiclist =(ListView)findViewById(R.id.htcMusicList);
        Musiclist.setBackgroundColor(Color.WHITE);
        SimpleAdapter adapter = new SimpleAdapter(this, getMusicData(),R.layout.music_list,
                new String[]{"name","singer","time"},
                new int[]{R.id.name,R.id.singer,R.id.time});
        Musiclist.setAdapter(adapter);
        Musiclist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //start Music service
                Bundle bundle = new Bundle();

                bundle.putInt("position", position);
                intentService.putExtra("bundle", bundle);

                startService(intentService);

            }
        });

        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        ComponentName mComponentName = new ComponentName(this.getPackageName(),RemoteControlClientReceiver.class.getName());
        int result = audioManager
                .requestAudioFocus(new MyOnAudioFocusChangeListener(),
                        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        audioManager.registerMediaButtonEventReceiver(mComponentName);
        System.out.println("HTC Music Creat");
    }
    private ArrayList<Map<String,Object>> getMusicData(){
        ArrayList<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        Map<String,Object> map = null;
        ArrayList<Music> musicList =  MusicUtil.getDataMusic(MainActivity.this);
        for(Music m : musicList){
            map = new HashMap<String, Object>();
//            map.put("image", R.drawable.music);
            map.put("name", m.getTitle());
            map.put("singer", m.getSinger());
            map.put("time", MusicUtil.formatTime(m.getTime()));
            list.add(map);
        }
        return list;
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    //更新进度
                    if(MusicUtil.isChanging==true) {
                        mHandler.sendEmptyMessage(0);
                        break;
                    }
                    int position = MusicUtil.getMediaPlayer().getCurrentPosition();

                    int time = MusicUtil.getMediaPlayer().getDuration();
                    int max = MusicSeekbar.getMax();

                    MusicSeekbar.setProgress(position*max/time);
                    MusicSeekbar.setVisibility(View.VISIBLE);
                    if(MusicUtil.getMediaPlayer().isPlaying()){
                        mHandler.sendEmptyMessage(0);
                    }else{

                        mHandler.sendEmptyMessage(1);
                    }

                    break;
                default:
                    break;
            }

        }
    };




    class MyOnAudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch(focusChange) {

                case AudioManager.AUDIOFOCUS_GAIN:
                    // 重新获得焦点,  可做恢复播放，恢复后台音量的操作
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    // 永久丢失焦点除非重新主动获取，这种情况是被其他播放器抢去了焦点，  为避免与其他播放器混音，可将音乐暂停
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    // 暂时丢失焦点，这种情况是被其他应用申请了短暂的焦点，可压低后台音量
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    // 短暂丢失焦点，这种情况是被其他应用申请了短暂的焦点希望其他声音能压低音量（或者关闭声音）凸显这个声音（比如短信提示音），
                    break;
            }
        }
    }

    @SuppressLint("NewApi")
    private void requestReadExternalPermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                // 0 是自己定义的请求coude
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 0: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    // request successfully, handle you transactions
                    onCreateInit();
                } else {

                    // permission denied
                    // request failed
                }

                return;
            }
            default:
                break;

        }
    }

}

