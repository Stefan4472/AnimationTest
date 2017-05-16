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

import com.plainsimple.spaceships.view.FontButton;
import com.plainsimple.spaceships.view.FontTextView;
import com.plainsimple.spaceships.view.GameView;

import plainsimple.spaceships.R;

/**
 * Created by Stefan on 5/15/2017.
 */

public class StartGameDialogFragment extends DialogFragment {

    // key of GameMode name set in bundle
    private static final String GAMEMODE_KEY = "SPECIFIED_GAME_MODE";
    private static final String INITIAL_DIFFICULTY = "INITIAL_DIFFICULTY";

    // interface used to send events to host Activity
    public interface StartGameDialogListener {
        // fired when a button is clicked to set difficulty level. Returns level selected
        void onDifficultySelected(DialogFragment dialog, String difficulty);
        // fired when the "Go!" button is clicked, telling the listener to launch the game
        void onPlayPressed(DialogFragment dialog);
    }

    // listener receiving events
    private StartGameDialogListener listener;

    // load equipment fields and coins available into a Bundle to pass to the fragment
    public static StartGameDialogFragment newInstance(String gameMode, String initDifficulty) {
        StartGameDialogFragment fragment = new StartGameDialogFragment();

        Bundle args = new Bundle();
        args.putString(GAMEMODE_KEY, gameMode);
        args.putString(INITIAL_DIFFICULTY, initDifficulty);

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // retrieve arguments from bundle (can't use savedInstanceState)
        Bundle args = getArguments();

        // give buttons on-click listeners that fire onDifficultySelected and set proper button's
        // background to difficultybutton_selected.xml and the others to difficultybutton.xml
        // (sort of like a radio button)
        final FontButton set_easy = (FontButton) view.findViewById(R.id.set_easy);
        final FontButton set_medium = (FontButton) view.findViewById(R.id.set_medium);
        final FontButton set_hard = (FontButton) view.findViewById(R.id.set_hard);

        set_easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDifficultySelected(StartGameDialogFragment.this, GameActivity.DIFFICULTY_EASY);
                set_easy.setBackgroundResource(R.drawable.difficultybutton_selected);
                set_medium.setBackgroundResource(R.drawable.difficultybutton);
                set_hard.setBackgroundResource(R.drawable.difficultybutton);
            }
        });

        set_medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDifficultySelected(StartGameDialogFragment.this, GameActivity.DIFFICULTY_MED);
                set_easy.setBackgroundResource(R.drawable.difficultybutton);
                set_medium.setBackgroundResource(R.drawable.difficultybutton_selected);
                set_hard.setBackgroundResource(R.drawable.difficultybutton);
            }
        });

        set_hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDifficultySelected(StartGameDialogFragment.this, GameActivity.DIFFICULTY_HARD);
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

        // set title to specified game mode
        FontTextView dialog_title = (FontTextView) view.findViewById(R.id.gamemode);
        if (args != null) {
            dialog_title.setText(args.getString(GAMEMODE_KEY));
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
