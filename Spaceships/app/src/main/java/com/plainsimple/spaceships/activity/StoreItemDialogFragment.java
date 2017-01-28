package com.plainsimple.spaceships.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.plainsimple.spaceships.helper.Equipment;
import com.plainsimple.spaceships.view.FontTextView;

import plainsimple.spaceships.R;

/**
 * Pop-up displayed when a store item is clicked.
 * Displays image and information on store item.
 * Actions include purchasing the item, equipping/unequipping
 * the item, and closing the dialog.
 */

public class StoreItemDialogFragment extends DialogFragment {

    private final static String LABEL_KEY = "LABEL_KEY";
    private final static String DESC_KEY = "DESC_KEY";
    private final static String COST_KEY = "COST_KEY";
    private final static String STATUS_KEY = "STATUS_KEY";
    private final static String R_ID_KEY = "R_ID_KEY";

    // interface used to send events to GameActivity
    // passes the fragment back as well as current settings
    public interface StoreListener {
        void onGameVolumeChanged(DialogFragment dialog, float gameVolume);
        void onMusicVolumeChanged(DialogFragment dialog, float musicVolume);
        void onResumePressed(DialogFragment dialog);
        void onQuitPressed(DialogFragment dialog);
        void onRestartPressed(DialogFragment dialog);
    }

    // used to notify StoreActivity
    StoreListener storeListener;
    // equipment this pop-up is displaying
    private Equipment displayEquipment;

    // optional static constructor to pass volume args
    public static StoreItemDialogFragment newInstance(Equipment equipment) {
        StoreItemDialogFragment fragment = new StoreItemDialogFragment();
        fragment.setDisplayEquipment(equipment);
        /*Bundle args = new Bundle();
        args.putString(LABEL_KEY, equipment.getLabel());
        args.putString(DESC_KEY, equipment.getDescription());
        args.putInt(COST_KEY, equipment.getCost());
        args.putString(STATUS_KEY, equipment.getStatus());
        equipment.get
        p.setArguments(args);
        return p;*/
        return fragment;
    }

    public void setDisplayEquipment(Equipment toDisplay) {
        this.displayEquipment = toDisplay;
    }

    @Override // instantiates the listener and makes sure host activity implements the interface
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //try {
        //    storeListener = (StoreListener) activity;
        //} catch (ClassCastException e) {
        //    throw new ClassCastException(activity.toString() + " must implement StoreListener");
        //}
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // retrieve arguments from bundle (can't use savedInstanceState)
        Bundle bundle = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // inflate the layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialog_layout = inflater.inflate(R.layout.storeitem_layout, null);
        FontTextView label = (FontTextView) dialog_layout.findViewById(R.id.storeItem_label);
        //label.setText(displayEquipment.getLabel());
        builder.setView(dialog_layout);
        return builder.create();
    }
}
