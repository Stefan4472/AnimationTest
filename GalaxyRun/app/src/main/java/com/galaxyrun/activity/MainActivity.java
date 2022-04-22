package com.galaxyrun.activity;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import galaxyrun.R;

public class MainActivity extends Activity {

    private MediaPlayer songPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.mainscreen_layout);

        // Animate the Title to "inflate"/zoom in on start
        TextView title = findViewById(R.id.title);
        AnimatorSet titleAnimation = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.menu_button);
        titleAnimation.setTarget(title);
        titleAnimation.start();

        // Animate the PlayButton to fade in after slight delay
        Button playButton = findViewById(R.id.playbutton);
        AnimatorSet playButtonAnimation = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.play_button);
        playButtonAnimation.setTarget(playButton);
        playButtonAnimation.start();

        songPlayer = MediaPlayer.create(getApplicationContext(), R.raw.main_song);
        songPlayer.setLooping(true);
        songPlayer.setVolume(0.3f, 0.3f);
    }

    @Override
    public void onResume() {
        super.onResume();
        songPlayer.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        songPlayer.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        songPlayer.release();
        songPlayer = null;
    }

    /*
    Handle user pressing the "Play" button
     */
    public void onPlayPressed(View view) {
        // TODO: play a "button clicked" sound
        // Launch the GameActivity
        Intent game_intent = new Intent(this, GameActivity.class);
        startActivity(game_intent);
    }
}
