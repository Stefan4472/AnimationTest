package com.plainsimple.spaceships.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;

import plainsimple.spaceships.R;

/**
 * Created by Kussmaul on 1/8/2017.
 */

public class PauseDialog2 extends DialogFragment {

    SeekBar gameVolumeSelector;
    SeekBar musicVolumeSelector;

    public final static String GAMEVOLUME_KEY = "gameVolume";
    public final static String MUSICVOLUME_KEY = "musicVolume";

    // optional static constructor to pass volume args
    public static PauseDialog2 newInstance(float gameVolume, float musicVolume) {
        PauseDialog2 p = new PauseDialog2();
        Bundle args = new Bundle();
        args.putFloat(GAMEVOLUME_KEY, gameVolume);
        args.putFloat(MUSICVOLUME_KEY, musicVolume);
        p.setArguments(args);
        return p;
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

        builder.setView(dialog_layout);
        return builder.create();
        /*Dialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;*/
    }
}
