package com.plainsimple.spaceships.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import plainsimple.spaceships.R;

/**
 * GameActivity Pause Dialog: Original code from https://developer.android.com/guide/topics/ui/dialogs.html
 */

public class PauseDialogFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
         * implement this interface in order to receive event callbacks.
         * Each method passes the DialogFragment in case the host needs to query it. */
    public interface PauseDialogListener {
        void onPausePositiveClick(DialogFragment dialog);
        void onPauseNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    PauseDialogFragment.PauseDialogListener pListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());//, R.style.DialogStyle);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.pause_layout, null))
                .setPositiveButton(R.string.resume, new DialogInterface.OnClickListener() {
                    @Override // resume game
                    public void onClick(DialogInterface dialog, int id) {
                        pListener.onPausePositiveClick(PauseDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
                    @Override // quit and return to MainActivity
                    public void onClick(DialogInterface dialog, int id) {
                        pListener.onPauseNegativeClick(PauseDialogFragment.this);
                    }
                });
        return builder.create();
    }

    // Override the Fragment.onAttach() method to instantiate the PauseDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            pListener = (PauseDialogFragment.PauseDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement PauseDialogListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            pListener = (PauseDialogFragment.PauseDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement PauseDialogListener");
        }
    }
}
