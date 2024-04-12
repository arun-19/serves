package com.example.seves;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class splashSacreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splach_activity);
        Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake);
        ImageView homeapp=findViewById(R.id.apphome);
        homeapp.setAnimation(animation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.swipe_right,R.anim.swip_left);
                finish();

            }
        },1200);
    }
}