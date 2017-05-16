package com.plainsimple.spaceships.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.plainsimple.spaceships.view.FontButton;
import com.plainsimple.spaceships.view.GameView;

import plainsimple.spaceships.R;

/**
 * The PlayScreen shows the user the possible GameTypes they can choose. It is an intermediate between
 * MainActivity and GameActivity.
 */

public class PlayScreenActivity extends Activity {

    private FontButton playButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // set content view/layout to gameview layout
        setContentView(R.layout.playscreen_layout);
        // set up view elements
        playButton = (FontButton) findViewById(R.id.playbutton);
    }

    public void onPlayPressed(View view) {
        // launch PlayScreenActivity
        Intent game_intent = new Intent(this, GameActivity.class);
        // specify difficulty level
        Bundle b = new Bundle();
        b.putString(GameActivity.DIFFICULTY_KEY, GameActivity.DIFFICULTY_HARD);
        game_intent.putExtras(b);
        startActivity(game_intent);
    }
}
