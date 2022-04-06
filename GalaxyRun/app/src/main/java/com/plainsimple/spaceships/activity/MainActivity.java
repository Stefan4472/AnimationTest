package com.plainsimple.spaceships.activity;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import plainsimple.spaceships.R;

public class MainActivity extends Activity {

    private MediaPlayer songPlayer;

    // TODO: play a song? play an animation?
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
        AnimatorSet title_zoom = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.zoom_in_out);
        title_zoom.setTarget(title);
        title_zoom.start();

        // Animate the PlayButton to fade in after slight delay
        Button play_button = findViewById(R.id.playbutton);
        AnimatorSet fade_in_1 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.menubuttons_fadein);
        fade_in_1.setStartDelay(200);
        fade_in_1.setTarget(play_button);
        fade_in_1.start();

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
