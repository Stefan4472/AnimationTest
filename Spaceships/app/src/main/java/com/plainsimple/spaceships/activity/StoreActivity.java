package com.plainsimple.spaceships.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

import com.plainsimple.spaceships.store.Equipment;
import com.plainsimple.spaceships.store.EquipmentManager;
import com.plainsimple.spaceships.store.StoreItemAdapter;
import com.plainsimple.spaceships.store.StoreRowAdapter;
import com.plainsimple.spaceships.store.StoreRow;
import com.plainsimple.spaceships.view.FontTextView;

import plainsimple.spaceships.R;

/**
 * Created by Stefan on 1/25/2017.
 */

public class StoreActivity extends Activity implements StoreItemDialogFragment.StoreItemDialogListener {

    private ListView listView;
    private FontTextView coinCounter;
    public static EquipmentManager equipment; // todo: coins spent listener


    @Override // initialize the layout and populate the main ListView
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.store_layout);
        equipment = new EquipmentManager(this);
        listView = (ListView) findViewById(R.id.list_view);
        // rows to be displayed in ListView (categories of upgrades)
        StoreRow[] rows = initStoreRows();
        // create adapter instance to display content in each row of ListView
        StoreRowAdapter adapter = new StoreRowAdapter(this, R.layout.storerow_layout,
                rows, new StoreItemAdapter.OnButtonClickedListener() {
            @Override // fires when a store button is clicked. Displays dialog
            public void onItemClick(Equipment selectedItem) {
                Log.d("StoreActivity.java", "Detected click on " + selectedItem.getLabel());
                DialogFragment d = StoreItemDialogFragment.newInstance(selectedItem);
                d.show(getFragmentManager(), "Store");
            }
        });
        listView.setAdapter(adapter);
        coinCounter = (FontTextView) findViewById(R.id.coin_counter);
        coinCounter.setText(Integer.toString(equipment.getCoins()));
        ImageView rotatingCoin = (ImageView) findViewById(R.id.rotating_coin);

        // add the "Back to Menu" button below the ListView as a footer
        View footerView = (LayoutInflater.from(this)).inflate(R.layout.return_to_menu_button, null);
        listView.addFooterView(footerView);
    }

    // get status of equipment and initialize data required to display the store
    private StoreRow[] initStoreRows() {
        StoreRow[] rows = new StoreRow[3];
        rows[0] = new StoreRow(0, "Cannons"); // todo: should add all items automatically
        rows[0].addStoreItem(equipment.getEquipment(EquipmentManager.CANNONS_0_KEY));
        rows[0].addStoreItem(equipment.getEquipment(EquipmentManager.CANNONS_1_KEY));
        rows[0].addStoreItem(equipment.getEquipment(EquipmentManager.CANNONS_2_KEY));
        rows[0].addStoreItem(equipment.getEquipment(EquipmentManager.CANNONS_3_KEY));
        rows[1] = new StoreRow(1, "Rockets");
        rows[1].addStoreItem(equipment.getEquipment(EquipmentManager.ROCKET_0_KEY));
        rows[1].addStoreItem(equipment.getEquipment(EquipmentManager.ROCKET_1_KEY));
        rows[1].addStoreItem(equipment.getEquipment(EquipmentManager.ROCKET_2_KEY));
        rows[1].addStoreItem(equipment.getEquipment(EquipmentManager.ROCKET_3_KEY));
        rows[2] = new StoreRow(2, "Armor");
        rows[2].addStoreItem(equipment.getEquipment(EquipmentManager.ARMOR_0_KEY));
        rows[2].addStoreItem(equipment.getEquipment(EquipmentManager.ARMOR_1_KEY));
        rows[2].addStoreItem(equipment.getEquipment(EquipmentManager.ARMOR_2_KEY));
        rows[2].addStoreItem(equipment.getEquipment(EquipmentManager.ARMOR_3_KEY));
        return rows;
    }

    // return to main menu
    public void onLeavePressed(View view) {
        finish();
    }

    @Override // StoreItemDialogFragment listener callback. Process the change
    public void onEquipItem(DialogFragment dialog, String toEquipId) {
        Log.d("StoreItemDialogFragment", "Chose to equip " + toEquipId);
        equipment.equip(toEquipId);
    }

    @Override // StoreItemDialogFragment listener callback. Process the transaction
    public void onBuyItem(DialogFragment dialog, String toBuyId, int cost) {
        Log.d("StoreItemDialogFragment", "Chose to buy " + toBuyId);
        equipment.buy(toBuyId);
        equipment.spendCoins(cost);
        Log.d("StoreItemDialogFragment", "Updating CoinCounter to " + equipment.getCoins());
        coinCounter.setText(Integer.toString(equipment.getCoins()));
    }
}
