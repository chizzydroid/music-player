package com.chizzy.musicplayer;

import static com.chizzy.musicplayer.ApplicationClass.ACTION_NEXT;
import static com.chizzy.musicplayer.ApplicationClass.ACTION_PLAY;
import static com.chizzy.musicplayer.ApplicationClass.ACTION_PREVIOUS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

 public class NotificationRecver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
String actionName = intent.getAction();
Intent serviceIntent = new Intent(context,MusicService.class);
if (actionName != null){
    switch (actionName){
        case ACTION_PLAY:
            serviceIntent.putExtra("ActionName","playPause");
            context.startService(serviceIntent);
             break;
        case ACTION_NEXT:
            serviceIntent.putExtra("ActionName","next");
            context.startService(serviceIntent);
            break;
        case ACTION_PREVIOUS:
            serviceIntent.putExtra("ActionName","Previous");
            context.startService(serviceIntent);
            break;
         }
      }
    }
}
