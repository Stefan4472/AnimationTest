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

import com.plainsimple.spaceships.helper.GameModeManager;
import com.plainsimple.spaceships.stats.StatsManager;
import com.plainsimple.spaceships.view.FontButton;
import com.plainsimple.spaceships.view.FontTextView;

import plainsimple.spaceships.R;

public class MainActivity extends Activity {

    // TODO: re-implement audio using modern Android API
//    // Used to play background song
//    private MediaPlayer mediaPlayer;
//    // Used to play short sounds i.e. button clicked sound
//    private SoundPool soundPool;
//    // SoundID created when button clicked sound is loaded
//    private int soundID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate Called");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.mainscreen_layout);

        // initialize classes requiring Context to access SharedPreferences
        GameModeManager.init(this);
        StatsManager.init(this);

//        initMedia();

        // Animate the Title to "inflate"/zoom in on start
        FontTextView title = (FontTextView) findViewById(R.id.title);
        AnimatorSet title_zoom = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.zoom_in_out);
        title_zoom.setTarget(title);
        title_zoom.start();

        // Animate the PlayButton to fade in after slight delay
        FontButton play_button = (FontButton) findViewById(R.id.playbutton);
        AnimatorSet fade_in_1 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.menubuttons_fadein);
        fade_in_1.setStartDelay(200);
        fade_in_1.setTarget(play_button);
        fade_in_1.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("MainActivity", "onPause Called");
//        mediaPlayer.release();
//        mediaPlayer = null;
//        soundPool.release();
//        soundPool = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume Called");
//        initMedia();
    }

    // initializes MediaPlayer and SoundPool
//    private void initMedia() {
//        // prepare background song todo: use the asynchronous method
//        mediaPlayer = MediaPlayer.create(this, R.raw.title_theme);
//        mediaPlayer.setLooping(true);
//        //mediaPlayer.start();
//        // set up SoundPool for playing buttonclick sound
//        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
//        soundID = soundPool.load(this, R.raw.button_clicked, 1);
//    }

    // handle user pressing "Play" button
    public void onPlayPressed(View view) {
        // play button clicked sound
//        soundPool.play(soundID, 1.0f, 1.0f, 1, 0, 1.0f);
        // Launch the GameActivity
        Intent game_intent = new Intent(this, GameActivity.class);
        startActivity(game_intent);
    }
}
