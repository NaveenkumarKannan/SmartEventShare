package com.project.naveen.smarteventsshare;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }
    Thread splashTread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startAnimation();
    }

    public void startAnimation() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.tran2);
        anim.reset();
        LinearLayout i = (LinearLayout) findViewById(R.id.sl);
        i.clearAnimation();
        i.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        TextView iv = (TextView) findViewById(R.id.logo);
        iv.clearAnimation();
        iv.startAnimation(anim);

        splashTread = new Thread(){

            public void run()
            {
                try {
                    int waited = 0;
                    while (waited < 4000) {
                        sleep(100);
                        waited += 100;
                    }
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    SplashActivity.this.finish();
                } catch (InterruptedException e) {
                    //do notihng
                } finally {
                    SplashActivity.this.finish();
                }

            }

        };
        splashTread.start();
    }
}
