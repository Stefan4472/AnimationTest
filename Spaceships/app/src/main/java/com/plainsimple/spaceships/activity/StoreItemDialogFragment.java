package com.plainsimple.spaceships.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.plainsimple.spaceships.helper.Equipment;
import com.plainsimple.spaceships.view.FontButton;
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

    // status equipment will change to when action button is clicked
    private Equipment.Status actionButtonChange;

    // interface used to send events to GameActivity
    // passes the fragment back as well as current settings
    public interface StoreItemListener {
        void onGameVolumeChanged(DialogFragment dialog, float gameVolume);
        void onMusicVolumeChanged(DialogFragment dialog, float musicVolume);
        void onResumePressed(DialogFragment dialog);
        void onQuitPressed(DialogFragment dialog);
        void onRestartPressed(DialogFragment dialog);
    }

    // used to notify StoreActivity
    StoreItemListener storeItemListener;

    // creates dialog with data to represent specified equipment
    public static StoreItemDialogFragment newInstance(Equipment equipment) {
        StoreItemDialogFragment fragment = new StoreItemDialogFragment();
        Bundle args = new Bundle();
        args.putString(LABEL_KEY, equipment.getLabel());
        args.putString(DESC_KEY, equipment.getDescription());
        args.putInt(COST_KEY, equipment.getCost());
        args.putString(STATUS_KEY, equipment.getStatus().toString());
        args.putInt(R_ID_KEY, equipment.getrDrawableId());
        fragment.setArguments(args);
        return fragment;
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
        View dialog_layout = inflater.inflate(R.layout.storedialog_layout, null);
        FontTextView label = (FontTextView) dialog_layout.findViewById(R.id.storeItem_label);
        label.setText(bundle.getString(LABEL_KEY));
        ImageView image = (ImageView) dialog_layout.findViewById(R.id.storeItem_image);
        image.setImageBitmap(BitmapFactory.decodeResource(getResources(), bundle.getInt(R_ID_KEY)));
        FontTextView description = (FontTextView) dialog_layout.findViewById(R.id.storeItem_description);
        description.setText(bundle.getString(DESC_KEY));

        // determine equipment's status
        // result determines whether to show locked_actionbar or unlocked_actionbar
        Equipment.Status status = Equipment.Status.valueOf(bundle.getString(STATUS_KEY));
        if (status.equals(Equipment.Status.EQUIPPED) || status.equals(Equipment.Status.UNLOCKED)) {
            LinearLayout unlocked = (LinearLayout) dialog_layout.findViewById(R.id.unlocked_actionbar);
            unlocked.setVisibility(View.VISIBLE);
            FontTextView display_status = (FontTextView) dialog_layout.findViewById(R.id.storeItem_status);
            display_status.setText(bundle.getString(STATUS_KEY));
            FontButton action_button = (FontButton) dialog_layout.findViewById(R.id.storeItem_equip);
            if (status.equals(Equipment.Status.UNLOCKED)) {
                // give option to equip the item
                action_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { // todo: send back to StoreActivity
                        Log.d("StoreItemDialogFragment", "Chose to equip");
                    }
                });
            } else { // already equipped: disable button
                action_button.setVisibility(View.GONE);
            }
        } else { // display button to buy the equipment
            LinearLayout locked = (LinearLayout) dialog_layout.findViewById(R.id.locked_actionbar);
            locked.setVisibility(View.VISIBLE);
            int cost = bundle.getInt(COST_KEY);
            FontButton buy_button = (FontButton) dialog_layout.findViewById(R.id.storeItem_buy);
            buy_button.setText("Buy for " + cost);
            // todo: disable if not enough money
            buy_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("StoreItemDialogFragment", "Chose to buy");
                }
            });
        }
        builder.setCancelable(true);
        builder.setView(dialog_layout);
        return builder.create();
    }
}
