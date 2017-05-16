package com.plainsimple.spaceships.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class PlayScreenActivity extends Activity implements StartGameDialogFragment.StartGameDialogListener {

    private FontButton playButton;
    // key of difficulty level selected
    private String selectedDifficulty = GameActivity.DIFFICULTY_EASY;

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

    // launch StartGameDialogFragment
    public void onPlayPressed(View view) {
        DialogFragment d = StartGameDialogFragment.newInstance("Endless", GameActivity.DIFFICULTY_EASY);
        d.show(getFragmentManager(), "Start Game");
    }

    @Override // update selectedDifficulty with given difficulty
    public void onDifficultySelected(DialogFragment dialog, String difficulty) {
        selectedDifficulty = difficulty;
        Log.d("PlayScreenActivity", "Difficulty updated to " + difficulty);
    }

    @Override // launch the game with last-selected difficulty
    public void onPlayPressed(DialogFragment dialog) {
        // launch PlayScreenActivity
        Intent game_intent = new Intent(this, GameActivity.class);
        // specify difficulty level
        Bundle b = new Bundle();
        b.putString(GameActivity.DIFFICULTY_KEY, selectedDifficulty);
        game_intent.putExtras(b);
        startActivity(game_intent);
    }
}
