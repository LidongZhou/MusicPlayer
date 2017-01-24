package com.htc.music;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        intentService = new Intent(MainActivity.this,MusicService.class);

        TextView musicNameView =(TextView) findViewById(R.id.htcMusicName);

        MusicSeekbar = (SeekBar) findViewById(R.id.htcMusicSeekBar);
      //  MusicSeekbar.setVisibility(View.GONE);
        MusicUtil.setMusicbar(mHandler,musicNameView);



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
               // mHandler.sendEmptyMessage(0);

            }
        });

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
}

