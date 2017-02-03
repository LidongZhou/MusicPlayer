package com.htc.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

/**
 * Created by Lidong_Zhou on 2017/2/3.
 */
public class RemoteControlClientReceiver extends BroadcastReceiver {
    @SuppressWarnings("unused")
    private static final String TAG = "MEDIA_ACTION_MEDIA_BUTTON";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equalsIgnoreCase(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent event = (KeyEvent) intent
                    .getParcelableExtra(Intent.EXTRA_KEY_EVENT);


            System.out.println("HTC Music getKeyCode " + event.getKeyCode());
            switch (event.getKeyCode()) {
            /*
             * one click => play/pause long click => previous double click =>
             * next
             */
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    break;
            }

        }
    }
}