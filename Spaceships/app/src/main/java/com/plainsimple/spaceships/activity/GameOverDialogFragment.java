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

import com.plainsimple.spaceships.stats.GameStats;
import com.plainsimple.spaceships.stats.StatsRowAdapter;
import com.plainsimple.spaceships.view.FontButton;
import com.plainsimple.spaceships.view.FontTextView;
import com.plainsimple.spaceships.view.StarsEarnedView;

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

    // key used for storing main message to display as title
    private static final String MAIN_MSG_KEY = "MAIN_MESSAGE";
    // key used for storing game score
    private static final String GAME_SCORE_KEY = "GAME_SCORE";
    // key used for storing distance travelled
    private static final String DIST_TRAVELED_KEY = "DISTANCE_TRAVELED";
    // key used for storing time played
    private static final String ROUND_DURATION_KEY = "ROUND_DURATION";
    // key used for storing coins collected
    private static final String COINS_COLLECTED_KEY = "COINS_COLLECTED";
    // key used for storing whether this is a highscore
    private static final String HIGHSCORE_KEY = "HIGHSCORE?";
    // key used for storing number of stars earned
    private static final String STARS_EARNED_KEY = "STARS_EARNED";

    // initializes and returns a new instance of GameOverDialogFragment given statistics of the game,
    // main message to display (e.g. "You Won!", whether it was a highscore, and how many stars were earned
    public static GameOverDialogFragment newInstance(GameStats gameStats, String message,
                                                     boolean highScore, int starsEarned) {
        GameOverDialogFragment dialog = new GameOverDialogFragment();

        Bundle bundle = new Bundle();

        bundle.putString(GAME_SCORE_KEY, gameStats.getFormatted(GameStats.GAME_SCORE));
        bundle.putString(DIST_TRAVELED_KEY, gameStats.getFormatted(GameStats.DISTANCE_TRAVELED));
        bundle.putString(ROUND_DURATION_KEY, gameStats.getFormatted(GameStats.TIME_PLAYED));
        bundle.putString(COINS_COLLECTED_KEY, gameStats.getFormatted(GameStats.COINS_COLLECTED));
        bundle.putString(MAIN_MSG_KEY, message);
        bundle.putInt(STARS_EARNED_KEY, starsEarned);
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

        // set main message (depends on whether game was won or lost)
        FontTextView main_msg = (FontTextView) view.findViewById(R.id.message);
        main_msg.setText(bundle.getString(MAIN_MSG_KEY));

        // display how many stars were earned
        StarsEarnedView stars_earned = (StarsEarnedView) view.findViewById(R.id.starsearned_display);
        stars_earned.setFilledStars(bundle.getInt(STARS_EARNED_KEY));

        // display sub-message if desired

        // display GameScore
        FontTextView game_score = (FontTextView) view.findViewById(R.id.game_score);
        if (bundle.getBoolean(HIGHSCORE_KEY)) {
            game_score.setText("Score: " + bundle.getString(GAME_SCORE_KEY) + " (Highscore!)");
        } else {
            game_score.setText("Score: " + bundle.getString(GAME_SCORE_KEY));
        }

        // display other stats
        FontTextView other_stats = (FontTextView) view.findViewById(R.id.other_stats);
        String stats_str = bundle.getString(DIST_TRAVELED_KEY) + " / " +
                bundle.getString(ROUND_DURATION_KEY) + " / " +
                bundle.getString(COINS_COLLECTED_KEY) + " Coins";
        other_stats.setText(stats_str);

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
