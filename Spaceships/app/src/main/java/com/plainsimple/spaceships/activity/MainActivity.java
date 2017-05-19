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
import com.plainsimple.spaceships.store.EquipmentManager;
import com.plainsimple.spaceships.view.FontButton;
import com.plainsimple.spaceships.view.FontTextView;

import plainsimple.spaceships.R;

public class MainActivity extends Activity {

    // todo: background animation? SlideInTransition?
    // used to play background song
    private MediaPlayer mediaPlayer;
    // used to play short sounds i.e. button clicked sound
    private SoundPool soundPool;
    // soundID created when button clicked sound is loaded
    private int soundID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate Called");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.mainscreen_layout);

        // initialize classes requiring Context to access SharedPreferences
        EquipmentManager.init(this);
        GameModeManager.init(this);
        StatsManager.init(this);

        initMedia();

        // fade in view elements todo: update Animations to use AnimatorSet
        FontTextView title = (FontTextView) findViewById(R.id.title);
        Animation fade_in_animation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        title.startAnimation(fade_in_animation);

        fade_in_animation.setStartOffset(300);
        FontButton button = (FontButton) findViewById(R.id.playbutton);
        button.startAnimation(fade_in_animation);

        fade_in_animation.setStartOffset(200);
        button = (FontButton) findViewById(R.id.storebutton);
        button.startAnimation(fade_in_animation);

        fade_in_animation.setStartOffset(400);
        button = (FontButton) findViewById(R.id.statsbutton);
        button.startAnimation(fade_in_animation);

        AnimatorSet title_zoom = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.zoom_in_out);
        title_zoom.setTarget(title);
        title_zoom.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("MainActivity", "onPause Called");
        mediaPlayer.release();
        mediaPlayer = null;
        soundPool.release();
        soundPool = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume Called");
        initMedia();
    }

    // initializes MediaPlayer and SoundPool
    private void initMedia() {
        // prepare background song todo: use the asynchronous method
        mediaPlayer = MediaPlayer.create(this, R.raw.title_theme);
        mediaPlayer.setLooping(true);
        //mediaPlayer.start();
        // set up SoundPool for playing buttonclick sound
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundID = soundPool.load(this, R.raw.button_clicked, 1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MainActivity", "onStop Called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity", "onDestroy Called");
    }

    // handle user pressing "Play" button
    public void onPlayPressed(View view) {
        /*// underline text
        Button button = (Button) findViewById(R.id.playbutton);
        button.setPaintFlags(button.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);*/
        // play button clicked sound
        soundPool.play(soundID, 1.0f, 1.0f, 1, 0, 1.0f);
        // launch PlayScreenActivity
        Intent playscreen_intent = new Intent(this, PlayScreenActivity.class);
        startActivity(playscreen_intent);
    }

    // handle user pressing "Store" button
    public void onStorePressed(View view) {
        soundPool.play(soundID, 1.0f, 1.0f, 1, 0, 1.0f);
        // launch StoreActivity
        Intent store_intent = new Intent(this, StoreActivity.class);
        startActivity(store_intent);
    }

    // handle user pressing "Stats" button
    public void onStatsPressed(View view) {
        soundPool.play(soundID, 1.0f, 1.0f, 1, 0, 1.0f);
        // launch StatsActivity
        Intent stats_intent = new Intent(this, StatsActivity.class);
        startActivity(stats_intent);
    }
}
