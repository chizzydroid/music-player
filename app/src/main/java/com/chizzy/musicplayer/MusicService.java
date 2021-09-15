package com.chizzy.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.util.ArrayList;

import static com.chizzy.musicplayer.playerActivity2.listSongs;
import static com.chizzy.musicplayer.playerActivity2.visualizer;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    IBinder mBinder = new MyBinder();
    static MediaPlayer mediaPlayer;
    ArrayList<MusicFiles> musicFiles = new ArrayList<>();
    Uri uri;
    int position = -1;
    ActionPlay actionPlaying;
    public static  final  String MUSIC_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static  final  String ARTIST_NAME = "ARTIST_NAME";
    public static final String SONG_NAME = "SONG_NAME";
    MediaSessionCompat mediaSessionCompat;


    Toast toast;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(),"My Audio");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("Bind", "Method");
        return mBinder;
    }

    int getAudioSessionId() {
        int audioSessionId = mediaPlayer.getAudioSessionId();
        if (audioSessionId != -1) {
            visualizer.setAudioSessionId(audioSessionId);
        }
        return audioSessionId;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPosition = intent.getIntExtra("servicePosition",-1);
        String actionName = intent.getStringExtra("ActionName");
        if (myPosition != -1) {
           playMedia(myPosition);
        }

        if (actionName != null){
            switch (actionName) {
                case "playPause":
                    toast = Toast.makeText(this, "PlayPause", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                    if (actionPlaying != null){
                        Log.e("Inside","Action");
                        actionPlaying.playPauseBtnClicked();
                    }
                    break;
                        case  "next":
                            toast = Toast.makeText(this,"next",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM,0,0);
                            toast.show();
                            if (actionPlaying != null) {
                                Log.e("Inside","Action");
                                actionPlaying.nextBtnClicked();
                            }
                                  break;
                        case  "Previous":
                            toast = Toast.makeText(this,"Previous",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM,0,0);
                            toast.show();
                            if (actionPlaying != null) {
                                Log.e("Inside","Action");
                                actionPlaying.preBtnClicked();
                            }
                             break;

            }

        }
        return START_STICKY;
    }

    private void playMedia(int StartPosition) {
        musicFiles = listSongs;
        position = StartPosition;

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();

            if (musicFiles != null) {
                createMediaPlayer(position);
                mediaPlayer.start();
            }
            }else{
                createMediaPlayer(position);
                mediaPlayer.start();

        }

    }
    void start (){

        mediaPlayer.start();
    }

    boolean isPlaying () {

        return mediaPlayer.isPlaying();

    }

    void stop() {
        mediaPlayer.stop();
    }

    void release () {

        mediaPlayer.release();
    }

    int getDuration () {

        return mediaPlayer.getDuration();
    }

    void seekTo ( int position){
        mediaPlayer.seekTo(position);
    }

   int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
   }
    void createMediaPlayer ( int positionInner){
        position = positionInner;
        uri = Uri.parse(musicFiles.get(position).getPath());
        SharedPreferences .Editor editor = getSharedPreferences(MUSIC_LAST_PLAYED,
                MODE_PRIVATE)
                .edit();
     editor.putString(MUSIC_FILE,uri.toString());
     editor.putString(ARTIST_NAME,musicFiles.get(position).getArtist());
     editor.putString(SONG_NAME,musicFiles.get(position).getTitle());
     editor.apply();
        mediaPlayer = MediaPlayer.create(getBaseContext(),uri);
    }
 void pause() {
        mediaPlayer.pause();
 }

 void OnCompleted(){
        mediaPlayer.setOnCompletionListener(this);
 }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (actionPlaying != null){
          actionPlaying.nextBtnClicked();
    }
           createMediaPlayer(position);
            mediaPlayer.start();
           OnCompleted();

            int audioSessionId = mediaPlayer.getAudioSessionId();
            if (audioSessionId != -1) {
                visualizer.setAudioSessionId(audioSessionId);

        }

    }

    public class MyBinder extends Binder {
        MusicService getService(){
            return MusicService.this;
        }
}
    void setCallBack(ActionPlay actionPlaying){
 this.actionPlaying = actionPlaying;

    }
    void nextBtnClicked(){
        
    }
}

