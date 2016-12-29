package com.plainsimple.spaceships.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import plainsimple.spaceships.R;

/**
 * GameActivity Pause Dialog
 */

public class PauseDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.pause_layout, null))
                .setPositiveButton(R.string.resume, new DialogInterface.OnClickListener() {
                    @Override // resume game
                    public void onClick(DialogInterface dialog, int id) {
                        getDialog().cancel();
                    }
                })
                .setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
                    @Override // quit and return to MainActivity
                    public void onClick(DialogInterface dialog, int id) {
                        getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
