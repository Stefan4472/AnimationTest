package com.plainsimple.spaceships.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;

import com.plainsimple.spaceships.view.FontButton;

import plainsimple.spaceships.R;

/**
 * Pop-up pause screen. Has controls for music volume and game volume.
 * Communicates with GameActivity when Resume or Quit button is clicked
 */

public class PauseDialogFragment extends DialogFragment { // todo: window feature_no_title
    // todo: change seekbar color

    private SeekBar gameVolumeSelector;
    private SeekBar musicVolumeSelector;

    public final static String GAMEVOLUME_KEY = "gameVolume";
    public final static String MUSICVOLUME_KEY = "musicVolume";

    // interface used to send events to GameActivity
    // passes the fragment back as well as current settings
    public interface PauseDialogListener {
        void onResumePressed(DialogFragment dialog, float gameVolume, float musicVolume);
        void onQuitPressed(DialogFragment dialog, float gameVolume, float musicVolume);
    }

    // used to notify GameActivity
    PauseDialogListener mListener;

    // optional static constructor to pass volume args
    public static PauseDialogFragment newInstance(float gameVolume, float musicVolume) {
        PauseDialogFragment p = new PauseDialogFragment();
        Bundle args = new Bundle();
        args.putFloat(GAMEVOLUME_KEY, gameVolume);
        args.putFloat(MUSICVOLUME_KEY, musicVolume);
        p.setArguments(args);
        return p;
    }

    @Override // instantiates the listener and makes sure host activity implements the interface
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (PauseDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement PauseDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // retrieve arguments from bundle (can't use savedInstanceState)
        Bundle bundle = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // inflate the layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialog_layout = inflater.inflate(R.layout.pause_layout, null);

        gameVolumeSelector = (SeekBar) dialog_layout.findViewById(R.id.gamevolume_selector);
        if (bundle.containsKey(GAMEVOLUME_KEY)) {
            gameVolumeSelector.setProgress((int) (bundle.getFloat(GAMEVOLUME_KEY) * 100));
        }
        musicVolumeSelector = (SeekBar) dialog_layout.findViewById(R.id.musicvolume_selector);
        if (bundle.containsKey(MUSICVOLUME_KEY)) {
            musicVolumeSelector.setProgress((int) (bundle.getFloat(MUSICVOLUME_KEY) * 100));
        }
        FontButton resume_button = (FontButton) dialog_layout.findViewById(R.id.resumebutton);
        resume_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onResumePressed(PauseDialogFragment.this, gameVolumeSelector.getProgress() / 100.0f, musicVolumeSelector.getProgress() / 100.0f);
            }
        });
        FontButton quit_button = (FontButton) dialog_layout.findViewById(R.id.quitbutton);
        quit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onQuitPressed(PauseDialogFragment.this, gameVolumeSelector.getProgress() / 100.0f, musicVolumeSelector.getProgress() / 100.0f);
            }
        });
        builder.setView(dialog_layout);
        return builder.create();
    }
}
