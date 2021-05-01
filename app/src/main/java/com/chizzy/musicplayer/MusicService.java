package com.chizzy.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.util.ArrayList;

import static com.chizzy.musicplayer.playerActivity2.listSongs;
import static com.chizzy.musicplayer.playerActivity2.visualizer;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    IBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;


    ArrayList<MusicFiles> musicFiles = new ArrayList<>();
    Uri uri;
    int position = -1;

    @Override
    public void onCreate() {
        super.onCreate();
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
        public int onStartCommand (Intent intent,int flags, int startId){
        int myPosition = intent.getIntExtra("servicePosition",-1);
        if (myPosition != -1){
            playMedia(myPosition);
        }
            return START_STICKY;
        }

    private void playMedia(int StartPosition) {
        musicFiles = listSongs;
        position = StartPosition;

        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();

            if (musicFiles != null){
                createMediaPlayer(position);
                mediaPlayer.start();
            }else {
                createMediaPlayer(position);
                mediaPlayer.start();
            }
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

       int getCurrentPosition() {

        return mediaPlayer.getCurrentPosition();

        }

        void createMediaPlayer ( int position){
            uri = Uri.parse(musicFiles.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
        }

        void pause(){
        mediaPlayer.pause();
        }

        void onCompleted(){

        mediaPlayer.setOnCompletionListener(this);
        }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    public class MyBinder extends Binder {
            MusicService getService(){
                return MusicService.this;
            }
        }
    }

