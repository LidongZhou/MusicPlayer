package com.htc.music;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import java.util.Random;


public class MusicService extends Service {

	public static int curPosition = -1 ;
	
	MyBinder mBinder = new MyBinder();
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);


		Bundle bundle = intent.getBundleExtra("bundle");
		int position = bundle.getInt("position");

		if(curPosition == position){

			if(MusicUtil.mediaPlayer.isPlaying()){
				MusicUtil.pause();
			}else{
				MusicUtil.play(position);
			}
		}else{

			curPosition = position;
			MusicUtil.stop();
			MusicUtil.play(position).setOnCompletionListener(new endMusicListener());

		}
	}
	

	class endMusicListener implements OnCompletionListener {
		@Override
		public void onCompletion(MediaPlayer mp) {
			if(MusicUtil.mode.equals("顺序播放")){
				if(curPosition == MusicUtil.musicCount-1){
					curPosition = 0 ;
				}else{
					curPosition ++;
				}
			}else if(MusicUtil.mode.equals("单曲循环")){
				
			}else{
				curPosition = new Random().nextInt(MusicUtil.musicCount);
			}
			MusicUtil.stop();
			MusicUtil.play(curPosition).setOnCompletionListener(this);

		}
		
	}

	public class MyBinder extends Binder {
		public MusicService getService(){
			return MusicService.this;
		}
	}
	
	
    
}
