package com.plainsimple.spaceships.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

import com.plainsimple.spaceships.helper.Equipment;
import com.plainsimple.spaceships.helper.EquipmentManager;
import com.plainsimple.spaceships.helper.StoreItemAdapter;
import com.plainsimple.spaceships.helper.StoreRowAdapter;
import com.plainsimple.spaceships.helper.StoreRow;
import com.plainsimple.spaceships.view.FontTextView;

import plainsimple.spaceships.R;

/**
 * Created by Stefan on 1/25/2017.
 */

public class StoreActivity extends Activity {

    private ListView listView;
    private FontTextView coinCounter;
    private EquipmentManager equipment;


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
        StoreRowAdapter adapter = new StoreRowAdapter(this, R.layout.listview_item,
                rows, new StoreItemAdapter.OnButtonClickedListener() {
            @Override
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
    }

    // get status of equipment and initialize data required to display the store
    private StoreRow[] initStoreRows() {
        StoreRow[] rows = new StoreRow[3];
        rows[0] = new StoreRow(0, "Cannons"); // todo: should add all items automatically
        rows[0].addStoreItem(equipment.getEquipment(EquipmentManager.LASER_KEY));
        rows[0].addStoreItem(equipment.getEquipment(EquipmentManager.ION_KEY));
        rows[0].addStoreItem(equipment.getEquipment(EquipmentManager.PLASMA_KEY));
        rows[0].addStoreItem(equipment.getEquipment(EquipmentManager.PLUTONIUM_KEY));
        rows[1] = new StoreRow(1, "Rockets");
        rows[1].addStoreItem(equipment.getEquipment(EquipmentManager.ROCKET_KEY));
        rows[2] = new StoreRow(2, "Armor");
        rows[2].addStoreItem(equipment.getEquipment(EquipmentManager.ARMOR0_KEY));
        rows[2].addStoreItem(equipment.getEquipment(EquipmentManager.ARMOR1_KEY));
        rows[2].addStoreItem(equipment.getEquipment(EquipmentManager.ARMOR2_KEY));
        rows[2].addStoreItem(equipment.getEquipment(EquipmentManager.ARMOR3_KEY));
        return rows;
    }

    // return to main menu
    public void onLeavePressed(View view) {
        finish();
    }
}
