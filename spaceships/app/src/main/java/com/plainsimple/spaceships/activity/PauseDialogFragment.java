package com.plainsimple.spaceships.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.plainsimple.spaceships.sprite.Spaceship;

import plainsimple.spaceships.R;

/**
 * GameActivity Pause Dialog: Original code from https://developer.android.com/guide/topics/ui/dialogs.html
 */

public class PauseDialogFragment extends DialogFragment {

    private SeekBar gameVolumeSelector;
    private SeekBar musicVolumeSelector;

    public static PauseDialogFragment newInstance(float gameVolume, float musicVolume, boolean gyroEnabled) {
        PauseDialogFragment fragment = new PauseDialogFragment();
        Bundle args = new Bundle();
        args.putFloat("gameVolume", gameVolume);
        args.putFloat("musicVolume", musicVolume);
        args.putBoolean("gyroEnabled", gyroEnabled);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pause_layout, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gameVolumeSelector = (SeekBar) view.findViewById(R.id.gamevolume_selector);
        gameVolumeSelector.setProgress((int) Float.parseFloat(savedInstanceState.getString("gameVolume")));
        musicVolumeSelector = (SeekBar) view.findViewById(R.id.musicvolume_selector);
        musicVolumeSelector.setProgress((int) Float.parseFloat(savedInstanceState.getString("musicVolume")));
    }
}
