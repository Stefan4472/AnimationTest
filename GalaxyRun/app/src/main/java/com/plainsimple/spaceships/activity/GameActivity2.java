package com.plainsimple.spaceships.activity;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.plainsimple.spaceships.engine.GameEngine;
import com.plainsimple.spaceships.engine.GameRunner;
import com.plainsimple.spaceships.helper.DrawParams;
import com.plainsimple.spaceships.helper.GameModeManager;
import com.plainsimple.spaceships.helper.SoundID;
import com.plainsimple.spaceships.sprite.Spaceship;
import com.plainsimple.spaceships.stats.GameStats;
import com.plainsimple.spaceships.view.ArrowButtonView;
import com.plainsimple.spaceships.view.GameView;
import com.plainsimple.spaceships.view.HealthBarView;
import com.plainsimple.spaceships.view.IGameViewListener;

import java.util.List;

import plainsimple.spaceships.R;

/**
 * Created by Stefan on 10/17/2015.
 */
public class GameActivity2 extends FragmentActivity
        implements GameRunner.Callback {

    private GameEngine gameEngine;
    private GameRunner mGameRunner;
    private long startTime;
    private long numUpdates;
    HealthBarView healthbar;
    /*
    Callback fired when the activity is created. Loads everything required and
    starts the game.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) throws IllegalArgumentException {
        super.onCreate(savedInstanceState);

        // Go full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // Set content view/layout
        setContentView(R.layout.game_layout2);
        healthbar = (HealthBarView) findViewById(R.id.healthbar);
        gameEngine = new GameEngine(getApplicationContext(), 800, 600);

        mGameRunner = new GameRunner(new Handler(), this, gameEngine);
        mGameRunner.start();
        mGameRunner.prepareHandler();
        startTime = System.currentTimeMillis();
        mGameRunner.queueUpdate();
    }

    @Override
    public void onGameStateUpdated(List<DrawParams> drawCalls, List<String> events) {
//        Log.d("GameActivity", "Got latest game update");
        if (numUpdates != 0 && numUpdates % 100 == 0) {
            long curr_time = System.currentTimeMillis();
            Log.d("GameActivity", "fps: " + numUpdates / ((curr_time - startTime) / 1000));
        }
        numUpdates++;
        healthbar.setMovingToHealth((int) (numUpdates % 100));

        mGameRunner.queueUpdate();
    }

    /*
    Callback fired when the activity is paused.
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.d("GameActivity", "onPause called");
    }

    @Override
    public void onDestroy() {
        mGameRunner.quit();
        super.onDestroy();
    }

    /*
    Callback fired when the activity is resumed.
    TODO: HANDLE ACTIVITY LIFECYCLE CORRECTLY
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d("GameActivity", "onResume called");
    }
}
