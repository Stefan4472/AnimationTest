package com.plainsimple.spaceships.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import com.plainsimple.spaceships.stats.StatsContainer;
import com.plainsimple.spaceships.stats.StatsRowAdapter;
import com.plainsimple.spaceships.view.FontButton;
import com.plainsimple.spaceships.view.FontTextView;

import plainsimple.spaceships.R;

/**
 * Pop-up dialog when game has finished. Displays statistics from game that was just completed
 * (provided by StatsContainer object) as well as whether it was a highscore. Gives option to play
 * again or quit and return to main menu
 */

public class GameOverDialogFragment extends DialogFragment {

    // interface used to send events back to GameActivity
    // reuses methods from PauseDialogFragment
    public interface GameOverDialogListener {
        void onQuitPressed(DialogFragment dialog);
        void onRestartPressed(DialogFragment dialog);
    }

    // listener that receives events
    GameOverDialogListener mListener;

    // keys used for putting keys[] and values[] arrays into the bundle
    // for later use in StatsRowAdapter
    private static final String KEYS_ARRAY = "KEYS_ARRAY";
    private static final String VALUES_ARRAY = "VALUES_ARRAY";

    // key used for storing whether this is a highscore
    private static final String HIGHSCORE_KEY = "HIGHSCORE";

    // initializes and returns a new instance of GameOverDialogFragment // todo: only show non-zero stats?
    public static GameOverDialogFragment newInstance(StatsContainer displayedStats, boolean highScore) {
        GameOverDialogFragment dialog = new GameOverDialogFragment();

        Bundle bundle = new Bundle();

        // get keys from StatsContainer
        String[] keys = displayedStats.getKeysToDisplay();

        // create corresponding array of formatted values
        String[] values = new String[keys.length];

        for (int i = 0; i < keys.length; i++) {
            values[i] = displayedStats.getFormatted(keys[i]);
        }

        // put both arrays and highscore boolean in the bundle
        bundle.putStringArray(KEYS_ARRAY, keys);
        bundle.putStringArray(VALUES_ARRAY, values);
        bundle.putBoolean(HIGHSCORE_KEY, highScore);

        dialog.setArguments(bundle);

        dialog.setStyle(DialogFragment.STYLE_NO_FRAME, 0);

        return dialog;
    }

    @Override // instantiates the listener and makes sure host activity implements the interface
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (GameOverDialogFragment.GameOverDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement PauseDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request window without title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // set windowAnimations to those specified in DialogAnimation style
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        // don't allow user to cancel (we want them to hit "Play Again" or "Quit")
        dialog.setCancelable(false);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gameover_layout, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // retrieve arguments from bundle (can't use savedInstanceState)
        Bundle bundle = getArguments();

        // populate ListView using StatsRowAdapter and Strings[] in bundle
        ListView game_stats = (ListView) view.findViewById(R.id.gamestats_display);

        // create adapter instance to display content in each row of ListView
        StatsRowAdapter adapter = new StatsRowAdapter(getActivity(), R.layout.statsrow_layout,
                bundle.getStringArray(KEYS_ARRAY), bundle.getStringArray(VALUES_ARRAY));
        game_stats.setAdapter(adapter);

        // make highscore FontTextView visible if this game was a highscore
        if (bundle.getBoolean(HIGHSCORE_KEY)) {
            FontTextView highscore_label = (FontTextView) view.findViewById(R.id.highscore);
            highscore_label.setVisibility(View.VISIBLE);
        }
        // button to quit game
        FontButton quit_button = (FontButton) view.findViewById(R.id.quitbutton);
        quit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onQuitPressed(GameOverDialogFragment.this);
            }
        });

        // button to restart game i.e. directly launch a new game
        FontButton restart_button = (FontButton) view.findViewById(R.id.restartbutton);
        restart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRestartPressed(GameOverDialogFragment.this);
            }
        });
    }

}
