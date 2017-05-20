package com.plainsimple.spaceships.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.plainsimple.spaceships.helper.GameMode;
import com.plainsimple.spaceships.helper.GameModeManager;
import com.plainsimple.spaceships.view.FontButton;
import com.plainsimple.spaceships.view.FontTextView;
import com.plainsimple.spaceships.view.GameView;
import com.plainsimple.spaceships.view.StarsEarnedView;

import plainsimple.spaceships.R;

/**
 * Created by Stefan on 5/15/2017.
 */

public class StartGameDialogFragment extends DialogFragment {

    // key of GameMode name set in bundle
    private static final String GAMEMODE_KEY = "SPECIFIED_GAME_MODE";

    // interface used to send events to host Activity
    public interface StartGameDialogListener {
        // fired when a button is clicked to set difficulty level. Returns level selected
        void onDifficultySelected(DialogFragment dialog, GameView.Difficulty difficulty);
        // fired when the "Go!" button is clicked, telling the listener to launch the game
        void onPlayPressed(DialogFragment dialog);
    }

    // listener receiving events
    private StartGameDialogListener listener;

    // initialize fragment with a GameMode. Stores GameMode key in Bundle so it can be retrieved
    // later
    public static StartGameDialogFragment newInstance(GameMode selectedGameMode) {
        StartGameDialogFragment fragment = new StartGameDialogFragment();

        Bundle args = new Bundle();
        args.putString(GAMEMODE_KEY, selectedGameMode.getKey());

        fragment.setArguments(args);
        return fragment;
    }

    @Override // instantiates the listener and makes sure host activity implements the interface
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (StartGameDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement StartGameDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request window without title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(true);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.startgamedialog_layout, container);
    }

    @Override // requires Bundle with valid GAMEMODE_KEY and INITIAL_DIFFICULTY keys (otherwise
    // throws IllegalArgumentException)
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) throws IllegalArgumentException {
        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // give buttons on-click listeners that fire onDifficultySelected and set proper button's
        // background to difficultybutton_selected.xml and the others to difficultybutton.xml
        // (sort of like a radio button) todo: more efficient code
        final FontButton set_easy = (FontButton) view.findViewById(R.id.set_easy);
        final FontButton set_medium = (FontButton) view.findViewById(R.id.set_medium);
        final FontButton set_hard = (FontButton) view.findViewById(R.id.set_hard);

        // retrieve arguments from bundle (can't use savedInstanceState)
        Bundle args = getArguments();

        try {
            // retrieve the specified GameMode
            GameMode selected_mode = GameModeManager.retrieve(args.getString(GAMEMODE_KEY));

            // set title to GameMode name
            FontTextView dialog_title = (FontTextView) view.findViewById(R.id.title);
            dialog_title.setText(selected_mode.getName());

            // populate highscore field with GameMode's highscore
            FontTextView high_score = (FontTextView) view.findViewById(R.id.highscore);
            high_score.setText(getString(R.string.highscore_equals, selected_mode.getHighscore() + ""));

            // set stars_earned to the number of stars earned with that highscore todo: could be wrong if depends on difficulty level as well as highscore
            StarsEarnedView stars_earned = (StarsEarnedView) view.findViewById(R.id.starsearned_display);
            stars_earned.setFilledStars(selected_mode.calculateStars(selected_mode.getHighscore()));

            // determine difficulty to set selected and mark the corresponding button
            switch (selected_mode.getLastDifficulty()) {
                case EASY:
                    set_easy.setBackgroundResource(R.drawable.difficultybutton_selected);
                    break;
                case MEDIUM:
                    set_medium.setBackgroundResource(R.drawable.difficultybutton_selected);
                    break;
                case HARD:
                    set_hard.setBackgroundResource(R.drawable.difficultybutton_selected);
                    break;
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid key or bundle");
        }

        set_easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDifficultySelected(StartGameDialogFragment.this, GameView.Difficulty.EASY);
                set_easy.setBackgroundResource(R.drawable.difficultybutton_selected);
                set_medium.setBackgroundResource(R.drawable.difficultybutton);
                set_hard.setBackgroundResource(R.drawable.difficultybutton);
            }
        });

        set_medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDifficultySelected(StartGameDialogFragment.this, GameView.Difficulty.MEDIUM);
                set_easy.setBackgroundResource(R.drawable.difficultybutton);
                set_medium.setBackgroundResource(R.drawable.difficultybutton_selected);
                set_hard.setBackgroundResource(R.drawable.difficultybutton);
            }
        });

        set_hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDifficultySelected(StartGameDialogFragment.this, GameView.Difficulty.HARD);
                set_easy.setBackgroundResource(R.drawable.difficultybutton);
                set_medium.setBackgroundResource(R.drawable.difficultybutton);
                set_hard.setBackgroundResource(R.drawable.difficultybutton_selected);
            }
        });

        FontButton start_game = (FontButton) view.findViewById(R.id.start_game);
        start_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPlayPressed(StartGameDialogFragment.this);
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
