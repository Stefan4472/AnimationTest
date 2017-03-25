package com.plainsimple.spaceships.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.plainsimple.spaceships.store.Equipment;
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

    // interface used to send events to host Activity
    // passes the fragment as well as the id of equipment to take action on
    // and any other parameters needed
    public interface StoreItemDialogListener {
        void onEquipItem(DialogFragment dialog, String toEquipId);
        void onBuyItem(DialogFragment dialog, String toBuyId, int cost);
    }

    private StoreItemDialogListener storeListener;

    private final static String LABEL_KEY = "LABEL_KEY";
    private final static String ID_KEY = "ID_KEY";
    private final static String DESC_KEY = "DESC_KEY";
    private final static String COST_KEY = "COST_KEY";
    private final static String STATUS_KEY = "STATUS_KEY";
    private final static String R_ID_KEY = "R_ID_KEY";
    private final static String COINS_AVAILABLE_KEY = "COINS_AVAILABLE_KEY";

    // load equipment fields and coins available into a Bundle to pass to the fragment
    public static StoreItemDialogFragment newInstance(Equipment equipment, int coinsAvailable) {
        StoreItemDialogFragment fragment = new StoreItemDialogFragment();

        Bundle args = new Bundle();
        args.putString(LABEL_KEY, equipment.getLabel());
        args.putString(DESC_KEY, equipment.getDescription());
        args.putString(ID_KEY, equipment.getId());
        args.putInt(COST_KEY, equipment.getCost());
        args.putString(STATUS_KEY, equipment.getStatus().toString());
        args.putInt(R_ID_KEY, equipment.getrDrawableId());
        args.putInt(COINS_AVAILABLE_KEY, coinsAvailable);

        fragment.setArguments(args);
        return fragment;
    }

    @Override // instantiates the listener and makes sure host activity implements the interface
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            storeListener = (StoreItemDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement StoreItemDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request window without title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(true);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.storedialog_layout, container);
    }

    // used to switch between locked and unlocked layouts
    private ViewSwitcher viewSwitcher;
    // contains Equipment parameters
    private Bundle args;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // retrieve arguments from bundle (can't use savedInstanceState)
        args = getArguments();

        // set the dialog's label
        FontTextView label = (FontTextView) view.findViewById(R.id.storeItem_label);
        label.setText(args.getString(LABEL_KEY));

        // set the dialog's thumbnail/image
        ImageView image = (ImageView) view.findViewById(R.id.storeItem_image);
        image.setImageBitmap(BitmapFactory.decodeResource(getResources(), args.getInt(R_ID_KEY)));

        // set the dialog's description
        FontTextView description = (FontTextView) view.findViewById(R.id.storeItem_description);
        description.setText(args.getString(DESC_KEY));

        // determine equipment's status
        // result determines whether to show locked_actionbar or unlocked_actionbar
        Equipment.Status status = Equipment.Status.valueOf(args.getString(STATUS_KEY));

        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher);

        // equipment is locked: populate and display locked_actionbar
        if (status.equals(Equipment.Status.LOCKED)) {
            populateLocked(view);
        } else {
            populateUnlocked(view, status);
            viewSwitcher.showNext();
        }
    }

    private void populateLocked(final View view) { // todo: clean up? best practices?
        // set the buy button
        final int cost = args.getInt(COST_KEY);
        FontButton buy_button = (FontButton) view.findViewById(R.id.storeItem_buy);
        buy_button.setText(getString(R.string.equipment_buy, Integer.toString(cost)));

        // user has enough money: add onClickListener to purchase
        if (cost <= args.getInt(COINS_AVAILABLE_KEY)) {
            buy_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    storeListener.onBuyItem(StoreItemDialogFragment.this, args.getString(ID_KEY), cost);
                    args.putInt(COINS_AVAILABLE_KEY, args.getInt(COINS_AVAILABLE_KEY) - cost);
                    // populate unlocked layout and switch to it
                    populateUnlocked(view, Equipment.Status.UNLOCKED);
                    viewSwitcher.showNext();
                }
            });
        } else { // user does not have enough money: disable and darken "buy" button
            buy_button.setClickable(false);
            buy_button.setAlpha(0.7f);
        }
    }

    private void populateUnlocked(final View view, Equipment.Status status) {
        final FontButton equip_button = (FontButton) view.findViewById(R.id.storeItem_equip);

        // unlocked: give option to equip the item
        if (status.equals(Equipment.Status.UNLOCKED)) {
            equip_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { // send event back to storeListener
                    storeListener.onEquipItem(StoreItemDialogFragment.this, args.getString(ID_KEY));
                    // disable action button
                    equip_button.setAlpha(0.5f);
                    equip_button.setClickable(false);
                }
            });
        } else { // already equipped: disable button
            equip_button.setClickable(false);
            equip_button.setAlpha(0.7f);
        }
    }
}