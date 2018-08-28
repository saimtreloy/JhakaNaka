package saim.com.jhakanaka.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.IOException;

import saim.com.jhakanaka.Activity.Player;
import saim.com.jhakanaka.R;

public class PlayerService extends Service implements MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener{

    public static MediaPlayer mp;

    //URL
    public String songUrl = "http://download.music.com.bd/Music/H/Habib/Habib%20-%20Bhalo_Bashbo_Bashbore%20(music.com.bd).mp3";
    public Handler handler = new Handler();


    //Notifications
    public String ns = Context.NOTIFICATION_SERVICE;
    Notification notification;
    RemoteViews notificationView;
    NotificationManager notificationManager;

    public PlayerService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        this.mp = new MediaPlayer();
        mp.reset();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);


        registerReceiver(receiverPlayPauseButtonEvent, new IntentFilter("saim.com.jhakanaka.receiverPlayPauseButtonEvent"));
        registerReceiver(receiverSeekChangeEvent, new IntentFilter("saim.com.jhakanaka.receiverSeekChangeEvent"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiverPlayPauseButtonEvent);
        unregisterReceiver(receiverSeekChangeEvent);

        notificationManager.cancelAll();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        init();

        return START_NOT_STICKY;
    }


    public void init() {
        playSong();
    }

    public void playSong() {
        try {
            mp.setDataSource(songUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.prepareAsync();
        mp.setOnPreparedListener(this);
        mp.setOnBufferingUpdateListener(this);
        mp.setOnCompletionListener(this);
        mp.setOnSeekCompleteListener(this);
    }


    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        new Handler().post(updateProgress);
        Intent intent = new Intent().setAction("saim.com.jhakanaka.receiverUpdatePlayPauseButton");
        intent.putExtra("ISPLAYING", mp.isPlaying());
        sendBroadcast(intent);

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        Intent intent = new Intent().setAction("saim.com.jhakanaka.receiverUpdateSeekbarSecondery");
        intent.putExtra("PERCENT", i);
        sendBroadcast(intent);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.stop();
        sendBroadcast(new Intent().setAction("saim.com.jhakanaka.receiverCompleteListener"));
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {

        return true;
    }

    public void setNotification(String songName){
        notificationManager = (NotificationManager) getSystemService(ns);
        notification = new Notification(R.drawable.ic_logo, null, System.currentTimeMillis());
        notificationView = new RemoteViews(getPackageName(), R.layout.layout_player_notification);

        //the intent that is started when the notification is clicked (works)
        Intent notificationIntent = new Intent(this, Player.class);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        notification.contentView = notificationView;
        notification.contentIntent = pendingNotificationIntent;
        notification.flags |= Notification.FLAG_NO_CLEAR;

        //this is the intent that is supposed to be called when the button is clicked
        //Intent switchIntent = new Intent(this, receiverPlayPauseButtonEvent.class);
        Intent notificationPlay = new Intent().setAction("saim.com.jhakanaka.receiverPlayPauseButtonEvent");
        PendingIntent pendingPlayIntent = PendingIntent.getBroadcast(this, 0, notificationPlay, 0);

        notificationView.setOnClickPendingIntent(R.id.imgPlayerNotificationPlay, pendingPlayIntent);
        notificationManager.notify(1, notification);
    }


    Runnable updateProgress = new Runnable() {
        @Override
        public void run() {
            if (((mp.getCurrentPosition() * 100) / mp.getDuration()) >= 100) {

            } else {
                handler.postDelayed(updateProgress, 500);
            }

            Intent intent = new Intent().setAction("saim.com.jhakanaka.receiverUpdateTimerAndSeekbar");
            intent.putExtra("TOTAL", mp.getDuration());
            intent.putExtra("CURRENT", mp.getCurrentPosition());
            intent.putExtra("PERCENT", (mp.getCurrentPosition() * 100) / mp.getDuration());
            sendBroadcast(intent);
        }
    };

    public BroadcastReceiver receiverPlayPauseButtonEvent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mp.isPlaying() == true){
                mp.pause();
                notificationView.setImageViewResource(R.id.imgPlayerNotificationPlay, R.drawable.ic_player_play);
            } else {
                mp.start();
                notificationView.setImageViewResource(R.id.imgPlayerNotificationPlay, R.drawable.ic_player_pause);
            }

        }
    };

    public BroadcastReceiver receiverSeekChangeEvent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int i = intent.getExtras().getInt("SEEK");
            int t = mp.getDuration();
            int c = (i*t)/100;
            mp.seekTo(c);

        }
    };
}
