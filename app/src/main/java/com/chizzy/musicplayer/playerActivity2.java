package com.chizzy.musicplayer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

import static com.chizzy.musicplayer.AlbumDetailsAdapter.albumFiles;
import static com.chizzy.musicplayer.MainActivity.musicFiles;
import static com.chizzy.musicplayer.MainActivity.repeatBoolean;
import static com.chizzy.musicplayer.MainActivity.shuffleBoolean;
import static com.chizzy.musicplayer.MusicAdapter.mFiles;

public class playerActivity2 extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    TextView song_name, artist_name, durationTotal, durationPlayed;
    ImageView cover_art, nextBtn, preBtn, backBtn, shuffleBtn, repeatBtn;
    FloatingActionButton playPauseBtn;
    BarVisualizer visualizer;
    SeekBar seekBar;
    int position = -1;
    static ArrayList<MusicFiles> listSongs = new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Thread playThread, prevThread, nextThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player2);
        intView();
        getIntenMethod();
        song_name.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist());
        mediaPlayer.setOnCompletionListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        playerActivity2.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mediaPlayer != null) {

                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    durationPlayed.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shuffleBoolean){

                    shuffleBoolean = false;
                    shuffleBtn.setImageResource(R.drawable.ic_shuffle_off);

                }else {
                    shuffleBoolean = true;
                    shuffleBtn.setImageResource(R.drawable.ic_shuffle);

                }
            }
        });
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repeatBoolean){

                    repeatBoolean = false;
                    repeatBtn.setImageResource(R.drawable.ic_repeat);
                }else {
                    repeatBoolean = true;
                    repeatBtn.setImageResource(R.drawable.ic_repeat_on);

                }
            }
        });
    }

    @Override
    protected void onResume() {
        playThread();
        nextThread();
        prevThread();
        super.onResume();
    }

    private void prevThread() {
        prevThread = new Thread() {
            @Override
            public void run() {
                super.run();
                preBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        preBtnClicked();
                    }
                });

            }
        };
        prevThread.start();

    }

    private void preBtnClicked() {
        if (mediaPlayer.isPlaying()) {

            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(listSongs.size() -1);

              }
              else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1));
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            playerActivity2.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {

                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            int audioSessionId = mediaPlayer.getAudioSessionId();
            if (audioSessionId != -1) {
                visualizer.setAudioSessionId(audioSessionId);
            }
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();

        } else {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(listSongs.size() -1);

            }
            else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1));
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            playerActivity2.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {

                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setImageResource(R.drawable.ic_play_arrow);
        }
    }

    private void nextThread() {
        nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();
                    }
                });
            }
        };

        nextThread.start();
    }

    private void nextBtnClicked() {
        if (mediaPlayer.isPlaying()) {

            mediaPlayer.stop();
            mediaPlayer.release();

            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(listSongs.size() -1);

            }
            else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % listSongs.size());
            }
            //else position will be position..
            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            playerActivity2.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {

                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            int audioSessionId = mediaPlayer.getAudioSessionId();
            if (audioSessionId != -1) {
                visualizer.setAudioSessionId(audioSessionId);
            }

        } else {

            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(listSongs.size() -1);

            }
            else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % listSongs.size());
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            playerActivity2.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {

                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setImageResource(R.drawable.ic_pause);

        }
    }

    private int getRandom(int i) {
        Random rand = new Random();
        return rand.nextInt(i + 1);
    }


    private void playThread() {
        playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseBtnClicked();
                    }
                });

            }
        };
        playThread.start();
    }
    private void playPauseBtnClicked() {
        if (mediaPlayer.isPlaying()) {
            playPauseBtn.setImageResource(R.drawable.ic_play_arrow);
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            playerActivity2.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                        handler.postDelayed(this, 1000);

                }
            });

        } else {

            playPauseBtn.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            int audioSessionId = mediaPlayer.getAudioSessionId();
            if (audioSessionId != -1) {
                visualizer.setAudioSessionId(audioSessionId);
            }
        }
        playerActivity2.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {

                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                }
                handler.postDelayed(this, 1000);
            }

        });
    }


    private String formattedTime(int mCurrentPosition) {
        String totalout = "";
        String totalnew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalout = minutes + ":" + seconds;
        totalnew = minutes + ":" + "0" + seconds;

        if (seconds.length() == 1) {
            return totalnew;


        } else {

            return totalout;

        }
    }

    private void getIntenMethod(){
        position = getIntent().getIntExtra("Position",-1);
        String sender = getIntent().getStringExtra("sender");
        if (sender != null && sender.equals("albumDetails")){

            listSongs = albumFiles;
        }else {

            listSongs = mFiles;

        }

        if (listSongs != null) {
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(listSongs.get(position).getPath());
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();

        } else {

            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
            int audioSessionId = mediaPlayer.getAudioSessionId();
            if (audioSessionId != -1) {
                visualizer.setAudioSessionId(audioSessionId);
            }
        }
        seekBar.setMax(mediaPlayer.getDuration() / 1000);
        metaData(uri);


    }

    private void intView() {
        song_name = findViewById(R.id.songName);
        artist_name = findViewById(R.id.song_artist);
        durationPlayed= findViewById(R.id.durationPlayed);
        durationTotal= findViewById(R.id.durationTotal);
        cover_art = findViewById(R.id.cover_art);
        nextBtn = findViewById(R.id.id_next);
        preBtn = findViewById(R.id.id_prev);
        backBtn = findViewById(R.id.back_btn);
        shuffleBtn = findViewById(R.id.id_suffle_off);
        repeatBtn = findViewById(R.id.id_repeat);
        playPauseBtn = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seekBar);
        visualizer = findViewById(R.id.bar);
    }

    private void  metaData(Uri uri) {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotals = Integer.parseInt(listSongs.get(position).getDuration()) / 1000;

        durationTotal.setText(formattedTime(durationTotals));

        byte[] art = retriever.getEmbeddedPicture();
        Bitmap  bitmap;
        if (art != null) {

            Glide.with(this)
                    .asBitmap()
                    .load(art)
                    .into(cover_art);
            bitmap = BitmapFactory.decodeByteArray(art,0,art.length);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if (swatch != null){

                        ImageView gredient = findViewById(R.id.imageViewGredient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        gredient .setBackgroundResource(R.drawable.gridient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(),0X0000000});
                        gredient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(),swatch.getRgb()});
                       mContainer.setBackground(gradientDrawableBg);
                        song_name.setTextColor(swatch.getTitleTextColor());
                        artist_name.setTextColor(swatch.getBodyTextColor());
                    }else {
                        ImageView gredient = findViewById(R.id.imageViewGredient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        gredient.setBackgroundResource(R.drawable.gridient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000, 0X0000000});
                        gredient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000, 0xff000000});
                     mContainer.setBackground(gradientDrawableBg);
                        song_name.setTextColor(Color.RED);
                        artist_name.setTextColor(Color.RED);
                    }
                }
            });

        }else {
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.mk)
                    .into(cover_art);
            ImageView gredient = findViewById(R.id.imageViewGredient);
            RelativeLayout mContainer = findViewById(R.id.mContainer);
            gredient .setBackgroundResource(R.drawable.gridient_bg);
            mContainer.setBackgroundResource(R.drawable.main_bg);
            song_name.setTextColor(Color.RED);
            artist_name.setTextColor(Color.DKGRAY);
        }

    }
       public void  ImageAnimation(Context context,ImageView imageView,Bitmap bitmap){
        Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);

        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextBtnClicked();
        if (mediaPlayer != null){
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
            int audioSessionId = mediaPlayer.getAudioSessionId();
            if (audioSessionId != -1) {
                visualizer.setAudioSessionId(audioSessionId);
                ImageView gredient = findViewById(R.id.imageViewGredient);
                RelativeLayout mContainer = findViewById(R.id.mContainer);
                gredient .setBackgroundResource(R.drawable.gridient_bg);
                mContainer.setBackgroundResource(R.drawable.main_bg);
                song_name.setTextColor(Color.RED);
                artist_name.setTextColor(Color.DKGRAY);
            }
        }

    }
}







