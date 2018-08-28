package saim.com.jhakanaka.Activity;

import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import saim.com.jhakanaka.R;
import saim.com.jhakanaka.Service.PlayerService;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppThemeSplash);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startService(new Intent(getApplicationContext(), PlayerService.class));
                startActivity(new Intent(getApplicationContext(), Player.class));
                finish();
            }
        }, 3000);


        ImageView imgLogo = (ImageView) findViewById(R.id.imgLogo);
        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Player.class));
            }
        });
    }
}
