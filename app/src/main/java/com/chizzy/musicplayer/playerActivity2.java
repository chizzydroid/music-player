package com.chizzy.musicplayer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.palette.graphics.Palette;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

import static com.chizzy.musicplayer.AlbumDetailsAdapter.albumFiles;
import static com.chizzy.musicplayer.ApplicationClass.ACTION_NEXT;
import static com.chizzy.musicplayer.ApplicationClass.ACTION_PLAY;
import static com.chizzy.musicplayer.ApplicationClass.ACTION_PREVIOUS;
import static com.chizzy.musicplayer.ApplicationClass.CHANNEL_ID_2;
import static com.chizzy.musicplayer.MainActivity.musicFiles;
import static com.chizzy.musicplayer.MainActivity.repeatBoolean;
import static com.chizzy.musicplayer.MainActivity.shuffleBoolean;
import static com.chizzy.musicplayer.MusicAdapter.mFiles;

public class playerActivity2<audioSessionId> extends AppCompatActivity implements ActionPlay, ServiceConnection {

    TextView song_name, artist_name, durationTotal, durationPlayed;
    ImageView cover_art, nextBtn, preBtn, backBtn, shuffleBtn, repeatBtn;
    FloatingActionButton playPauseBtn;
    static BarVisualizer visualizer;
    SeekBar seekBar;
    int position = -1;
    static ArrayList<MusicFiles> listSongs = new ArrayList<>();
    static Uri uri;
    MusicService musicService;
    private Thread playThread, prevThread, nextThread;
    MediaSessionCompat mediaSessionCompat;
    //static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_player2);
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(),"My Audio");
        intView();
        getIntenMethod();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (musicService != null && fromUser) {
                    musicService.seekTo(progress * 1000);
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
                if (musicService != null) {

                    int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    durationPlayed.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shuffleBoolean) {

                    shuffleBoolean = false;
                    shuffleBtn.setImageResource(R.drawable.ic_shuffle_off);

                } else {
                    shuffleBoolean = true;
                    shuffleBtn.setImageResource(R.drawable.ic_shuffle);

                }
            }
        });
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repeatBoolean) {

                    repeatBoolean = false;
                    repeatBtn.setImageResource(R.drawable.ic_repeat);
                } else {
                    repeatBoolean = true;
                    repeatBtn.setImageResource(R.drawable.ic_repeat_on);

                }
            }
        });
    }


    @Override
    protected void onResume() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        playThread();
        nextThread();
        prevThread();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);

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

    public void preBtnClicked() {
        if (musicService.isPlaying()) {

            musicService.stop();
            musicService.release();

            position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1));
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration() / 1000);
            playerActivity2.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {

                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            int audioSessionId = musicService.getAudioSessionId();
            if (audioSessionId != -1) {
                visualizer.setAudioSessionId(audioSessionId);
            }
            musicService.OnCompleted();
            showNotification(R.drawable.ic_pause);
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            musicService.start();

        } else {

            musicService.stop();
            musicService.release();
            position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1));
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration() / 1000);
            playerActivity2.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {

                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.OnCompleted();
            showNotification(R.drawable.ic_play_arrow);
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

    public void nextBtnClicked() {
        if (musicService.isPlaying()) {

            musicService.stop();
            musicService.release();

            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(listSongs.size() - 1);

            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % listSongs.size());
            }
            //else position will be position..
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration() / 1000);
            playerActivity2.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {

                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            int audioSessionId = musicService.getAudioSessionId();
            if (audioSessionId != -1) {
                visualizer.setAudioSessionId(audioSessionId);
            }
            musicService.OnCompleted();
            showNotification(R.drawable.ic_pause);
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            musicService.start();

        } else {

            musicService.stop();
            musicService.release();
            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(listSongs.size() - 1);

            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % listSongs.size());
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration() / 1000);
            playerActivity2.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {

                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.OnCompleted();
            showNotification(R.drawable.ic_play_arrow);
            playPauseBtn.setImageResource(R.drawable.ic_play_arrow);
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

    public void playPauseBtnClicked() {
        if (musicService.isPlaying()) {
            showNotification(R.drawable.ic_play_arrow);
            playPauseBtn.setImageResource(R.drawable.ic_play_arrow);
            musicService.pause();
            seekBar.setMax(musicService.getDuration() / 1000);
            playerActivity2.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    if (musicService != null) {


                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);

                }
            });

        } else {
            showNotification(R.drawable.ic_pause);
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            musicService.start();
            seekBar.setMax(musicService.getDuration() / 1000);
            int audioSessionId = musicService.getAudioSessionId();
            if (audioSessionId != -1) {
                visualizer.setAudioSessionId(audioSessionId);
            }
        }
        playerActivity2.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (musicService != null) {

                    int mCurrentPosition = musicService.getCurrentPosition() / 1000;
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

    private void getIntenMethod() {
        position = getIntent().getIntExtra("Position", -1);
        String sender = getIntent().getStringExtra("sender");
        if (sender != null && sender.equals("albumDetails")) {

            listSongs = albumFiles;
        } else {

            listSongs = mFiles;

        }

        if (listSongs != null) {
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(listSongs.get(position).getPath());
        }
        showNotification(R.drawable.ic_pause);
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("servicePosition", position);
        startService(intent);
    }

    void getAudioSessionId() {
        int audioSessionId = musicService.getAudioSessionId();
        if (audioSessionId != -1) {
            visualizer.setAudioSessionId(audioSessionId);
        }

    }


    private void intView() {
        song_name = findViewById(R.id.songName);
        artist_name = findViewById(R.id.song_artist);
        durationPlayed = findViewById(R.id.durationPlayed);
        durationTotal = findViewById(R.id.durationTotal);
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

    private void metaData(Uri uri) {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotals = Integer.parseInt(listSongs.get(position).getDuration()) / 1000;

        durationTotal.setText(formattedTime(durationTotals));

        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if (art != null) {

            Glide.with(this)
                    .asBitmap()
                    .load(art)
                    .into(cover_art);
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if (swatch != null) {

                        ImageView gredient = findViewById(R.id.imageViewGredient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        gredient.setBackgroundResource(R.drawable.gridient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(), 0X0000000});
                        gredient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(), swatch.getRgb()});
                        mContainer.setBackground(gradientDrawableBg);
                        song_name.setTextColor(swatch.getTitleTextColor());
                        artist_name.setTextColor(swatch.getBodyTextColor());
                    } else {
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
                        song_name.setTextColor(Color.BLACK);
                        artist_name.setTextColor(Color.BLACK);
                    }
                }
            });

        } else {
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.mii)
                    .into(cover_art);
            ImageView gredient = findViewById(R.id.imageViewGredient);
            RelativeLayout mContainer = findViewById(R.id.mContainer);
            gredient.setBackgroundResource(R.drawable.gridient_bg);
            mContainer.setBackgroundResource(R.drawable.gridient_bg);
            song_name.setTextColor(Color.RED);
            artist_name.setTextColor(Color.DKGRAY);
        }

    }

    public void ImageAnimation(Context context, ImageView imageView, Bitmap bitmap) {
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
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
        musicService = myBinder.getService();
        musicService.setCallBack(this);
        Toast.makeText(this, "Connected" + musicService,
                Toast.LENGTH_SHORT).show();
        seekBar.setMax(musicService.getDuration() / 1000);
        metaData(uri);
        song_name.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist());
        musicService.OnCompleted();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }
        void showNotification ( int playPauseBtn ) {
            Intent intent = new Intent(this, playerActivity2.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,
                    0);
            Intent prevIntent = new Intent(this, NotificationRecver.class)
                    .setAction(ACTION_PREVIOUS);

            PendingIntent prevPending = PendingIntent
                    .getBroadcast(this, 0, prevIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

            Intent pauseIntent = new Intent(this, NotificationRecver.class)
                    .setAction(ACTION_PLAY);

            PendingIntent pausePending = PendingIntent
                    .getBroadcast(this, 0, pauseIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            Intent nextIntent = new Intent(this, NotificationRecver.class)
                    .setAction(ACTION_NEXT);

            PendingIntent nextPending = PendingIntent
                    .getBroadcast(this, 0, nextIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            byte[] picture = null;
            picture = getAlbumArt(listSongs.get(position).getPath());
            Bitmap thumb = null;
            if (picture != null) {
                thumb = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            } else {
                thumb = BitmapFactory.decodeResource(getResources(), R.drawable.mii);
            }
            Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID_2)
                    .setSmallIcon(playPauseBtn)
                    .setLargeIcon(thumb)
                    .setContentTitle(listSongs.get(position).getTitle())
                    .setContentText(listSongs.get(position).getArtist())
                    .addAction(R.drawable.ic_skip_previous,"Previous",prevPending)
                    .addAction(playPauseBtn,"Pause",pausePending)
                    .addAction(R.drawable.ic_skip_next,"Next",nextPending)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOnlyAlertOnce(true)
                    .build();
            NotificationManager notificationManager =
                    (NotificationManager)  getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0,notification);

        }
    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}








