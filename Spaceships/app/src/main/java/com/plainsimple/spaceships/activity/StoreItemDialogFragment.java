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
import com.plainsimple.spaceships.helper.EquipmentManager;
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
    private final static String ID_KEY = "ID_KEY";
    private final static String DESC_KEY = "DESC_KEY";
    private final static String COST_KEY = "COST_KEY";
    private final static String STATUS_KEY = "STATUS_KEY";
    private final static String R_ID_KEY = "R_ID_KEY";

    // load equipment fields into a Bundle to pass to the fragment
    public static StoreItemDialogFragment newInstance(Equipment equipment) {
        StoreItemDialogFragment fragment = new StoreItemDialogFragment();
        Bundle args = new Bundle();
        args.putString(LABEL_KEY, equipment.getLabel());
        args.putString(DESC_KEY, equipment.getDescription());
        args.putString(ID_KEY, equipment.getId());
        args.putInt(COST_KEY, equipment.getCost());
        args.putString(STATUS_KEY, equipment.getStatus().toString());
        args.putInt(R_ID_KEY, equipment.getrDrawableId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // retrieve arguments from bundle (can't use savedInstanceState)
        final Bundle bundle = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // inflate the layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialog_layout = inflater.inflate(R.layout.storedialog_layout, null);
        // set the dialog's label
        FontTextView label = (FontTextView) dialog_layout.findViewById(R.id.storeItem_label);
        label.setText(bundle.getString(LABEL_KEY));
        // set the dialog's thumbnail/image
        ImageView image = (ImageView) dialog_layout.findViewById(R.id.storeItem_image);
        image.setImageBitmap(BitmapFactory.decodeResource(getResources(), bundle.getInt(R_ID_KEY)));
        // set the dialog's description
        FontTextView description = (FontTextView) dialog_layout.findViewById(R.id.storeItem_description);
        description.setText(bundle.getString(DESC_KEY));

        // determine equipment's status
        // result determines whether to show locked_actionbar or unlocked_actionbar
        Equipment.Status status = Equipment.Status.valueOf(bundle.getString(STATUS_KEY));

        // display and populate the "unlocked" LinearLayout
        if (status.equals(Equipment.Status.EQUIPPED) || status.equals(Equipment.Status.UNLOCKED)) {
            LinearLayout unlocked = (LinearLayout) dialog_layout.findViewById(R.id.unlocked_actionbar);
            unlocked.setVisibility(View.VISIBLE);
            // set the status
            FontTextView display_status = (FontTextView) dialog_layout.findViewById(R.id.storeItem_status);
            display_status.setText(bundle.getString(STATUS_KEY));
            FontButton action_button = (FontButton) dialog_layout.findViewById(R.id.storeItem_equip);
            if (status.equals(Equipment.Status.UNLOCKED)) { // give option to equip the item
                action_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("StoreItemDialogFragment", "Chose to equip " + bundle.getString(LABEL_KEY));
                        StoreActivity.equipment.equip(bundle.getString(ID_KEY));
                    }
                });
            } else { // already equipped: don't show button
                action_button.setVisibility(View.GONE);
            }
        } else { // display and populate the "locked" LinearLayout
            LinearLayout locked = (LinearLayout) dialog_layout.findViewById(R.id.locked_actionbar);
            locked.setVisibility(View.VISIBLE);
            final int cost = bundle.getInt(COST_KEY);
            FontButton buy_button = (FontButton) dialog_layout.findViewById(R.id.storeItem_buy);
            buy_button.setText("Buy for " + cost);
            // user has enough money: add onClickListener to purchase
            if (cost <= StoreActivity.equipment.getCoins()) {
                buy_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    Log.d("StoreItemDialogFragment", "Chose to buy " + bundle.getString(LABEL_KEY));
                    StoreActivity.equipment.buy(bundle.getString(ID_KEY));
                    StoreActivity.equipment.spendCoins(cost);
                    }
                });
            } else { // user does not have enough money: disable "buy" button
                buy_button.setEnabled(false);
            }
        }
        builder.setCancelable(true);
        builder.setView(dialog_layout);
        return builder.create();
    }
}
