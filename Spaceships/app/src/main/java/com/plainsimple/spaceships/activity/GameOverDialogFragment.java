package com.plainsimple.spaceships.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import com.plainsimple.spaceships.view.FontButton;

import plainsimple.spaceships.R;

/**
 * Pop-up dialog when game has finished. Gives option to restart the game
 * or quit and return to main menu
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

    @Override // instantiates the listener and makes sure host activity implements the interface
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (GameOverDialogFragment.GameOverDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement PauseDialogListener");
        }
    }

    public static GameOverDialogFragment newInstance() {
        GameOverDialogFragment dialog = new GameOverDialogFragment();
        Bundle bundle = new Bundle();
        dialog.setArguments(bundle);

        dialog.setStyle(DialogFragment.STYLE_NO_FRAME, 0);

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // retrieve arguments from bundle (can't use savedInstanceState)
        Bundle bundle = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // inflate the layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialog_layout = inflater.inflate(R.layout.gameover_layout, null);

        FontButton quit_button = (FontButton) dialog_layout.findViewById(R.id.quitbutton);
        quit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onQuitPressed(GameOverDialogFragment.this);
            }
        });
        FontButton restart_button = (FontButton) dialog_layout.findViewById(R.id.restartbutton);
        restart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRestartPressed(GameOverDialogFragment.this);
            }
        });
        builder.setView(dialog_layout);
        builder.setCancelable(false);
        return builder.create();
    }
}
