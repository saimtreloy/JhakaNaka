package saim.com.jhakanaka.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import saim.com.jhakanaka.R;
import saim.com.jhakanaka.Service.PlayerService;

public class Player extends AppCompatActivity {

    public SeekBar seekBarPlayer;
    public TextView txtPlayerCurrentTime, txtPlayerTotalTime;
    public ImageView imgPlayPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppThemeSplash);
        setContentView(R.layout.activity_player);

        init();
    }

    public void init() {

        seekBarPlayer = (SeekBar) findViewById(R.id.seekBarPlayer);

        txtPlayerCurrentTime = (TextView) findViewById(R.id.txtPlayerCurrentTime);
        txtPlayerTotalTime = (TextView) findViewById(R.id.txtPlayerTotalTime);

        imgPlayPause = (ImageView) findViewById(R.id.imgPlayPause);


        ElementEvent();
    }

    public void ElementEvent(){
        imgPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PlayerService.mp.isPlaying() == true) {
                    PlayerService.mp.pause();
                    imgPlayPause.setImageResource(R.drawable.ic_player_play);

                } else {
                    PlayerService.mp.start();
                    imgPlayPause.setImageResource(R.drawable.ic_player_pause);
                }
            }
        });
        if (PlayerService.mp.isPlaying() == true) {
            imgPlayPause.setImageResource(R.drawable.ic_player_pause);
        } else {
            imgPlayPause.setImageResource(R.drawable.ic_player_play);
        }

        seekBarPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int i = seekBar.getProgress();
                Intent intent = new Intent().setAction("saim.com.jhakanaka.receiverSeekChangeEvent");
                intent.putExtra("SEEK", i);
                sendBroadcast(intent);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver(receiverUpdateTimerAndSeekbar, new IntentFilter("saim.com.jhakanaka.receiverUpdateTimerAndSeekbar"));
        registerReceiver(receiverUpdateSeekbarSecondery, new IntentFilter("saim.com.jhakanaka.receiverUpdateSeekbarSecondery"));
        registerReceiver(receiverUpdatePlayPauseButton, new IntentFilter("saim.com.jhakanaka.receiverUpdatePlayPauseButton"));
        registerReceiver(receiverCompleteListener, new IntentFilter("saim.com.jhakanaka.receiverCompleteListener"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiverUpdateTimerAndSeekbar);
        unregisterReceiver(receiverUpdateSeekbarSecondery);
        unregisterReceiver(receiverUpdatePlayPauseButton);
        unregisterReceiver(receiverCompleteListener);
    }

    public BroadcastReceiver receiverUpdateTimerAndSeekbar = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int percent = intent.getExtras().getInt("PERCENT");
            int current = intent.getExtras().getInt("CURRENT");
            int total = intent.getExtras().getInt("TOTAL");

            seekBarPlayer.setProgress(percent);
            txtPlayerCurrentTime.setText(TimeUnit.MILLISECONDS.toSeconds(current)/60 + ":" + TimeUnit.MILLISECONDS.toSeconds(current)%60);
            txtPlayerTotalTime.setText(TimeUnit.MILLISECONDS.toSeconds(total)/60 + ":" + TimeUnit.MILLISECONDS.toSeconds(total)%60);
        }
    };

    public BroadcastReceiver receiverUpdateSeekbarSecondery = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int percent = intent.getExtras().getInt("PERCENT");
            seekBarPlayer.setSecondaryProgress(percent);
        }
    };

    public BroadcastReceiver receiverUpdatePlayPauseButton = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isPlaying = intent.getExtras().getBoolean("ISPLAYING");
            Log.d("SAIM IS PLAYING", isPlaying + " ");
            if (isPlaying == true) {
                imgPlayPause.setImageResource(R.drawable.ic_player_pause);
            } else {
                imgPlayPause.setImageResource(R.drawable.ic_player_play);
            }

        }
    };

    public BroadcastReceiver receiverCompleteListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            seekBarPlayer.setProgress(0);
            imgPlayPause.setImageResource(R.drawable.ic_player_play);

        }
    };
}
