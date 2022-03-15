package com.plainsimple.spaceships.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.plainsimple.spaceships.view.FontButton;

import androidx.annotation.Nullable;
import plainsimple.spaceships.R;

/**
 * Pop-up pause screen. Has controls for music volume and game volume.
 * Communicates with GameActivity when Resume or Quit button is clicked
 */

public class PauseDialogFragment extends DialogFragment {

    private SeekBar gameVolumeSelector;
    private SeekBar musicVolumeSelector;

    public final static String GAMEVOLUME_KEY = "gameVolume";
    public final static String MUSICVOLUME_KEY = "musicVolume";

    // interface used to send events to GameActivity
    // passes the fragment back as well as current settings
    public interface PauseDialogListener {
        void onGameVolumeChanged(DialogFragment dialog, float gameVolume);
        void onMusicVolumeChanged(DialogFragment dialog, float musicVolume);
        void onResumePressed(DialogFragment dialog);
        void onQuitPressed(DialogFragment dialog);
        void onRestartPressed(DialogFragment dialog);
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
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request window without title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(false);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        return inflater.inflate(R.layout.pausedialog_layout, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) { // todo: clean up
        // retrieve arguments from bundle (can't use savedInstanceState)
        Bundle bundle = getArguments();

        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        gameVolumeSelector = (SeekBar) view.findViewById(R.id.gamevolume_selector);
        if (bundle.containsKey(GAMEVOLUME_KEY)) {
            gameVolumeSelector.setProgress((int) (bundle.getFloat(GAMEVOLUME_KEY) * 100));
        }
        gameVolumeSelector.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mListener.onGameVolumeChanged(PauseDialogFragment.this, gameVolumeSelector.getProgress() / 100.0f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mListener.onGameVolumeChanged(PauseDialogFragment.this, gameVolumeSelector.getProgress() / 100.0f);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        musicVolumeSelector = (SeekBar) view.findViewById(R.id.musicvolume_selector);
        if (bundle.containsKey(MUSICVOLUME_KEY)) {
            musicVolumeSelector.setProgress((int) (bundle.getFloat(MUSICVOLUME_KEY) * 100));
        }
        musicVolumeSelector.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mListener.onMusicVolumeChanged(PauseDialogFragment.this, musicVolumeSelector.getProgress() / 100.0f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mListener.onMusicVolumeChanged(PauseDialogFragment.this, musicVolumeSelector.getProgress() / 100.0f);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        FontButton resume_button = (FontButton) view.findViewById(R.id.resumebutton);
        resume_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onResumePressed(PauseDialogFragment.this);
            }
        });

        FontButton quit_button = (FontButton) view.findViewById(R.id.quitbutton);
        quit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onQuitPressed(PauseDialogFragment.this);
            }
        });

        FontButton restart_button = (FontButton) view.findViewById(R.id.restartbutton);
        restart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRestartPressed(PauseDialogFragment.this);
            }
        });
    }
}
