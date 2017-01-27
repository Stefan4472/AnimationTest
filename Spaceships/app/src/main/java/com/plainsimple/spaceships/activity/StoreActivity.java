package com.plainsimple.spaceships.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.plainsimple.spaceships.helper.EquipmentManager;
import com.plainsimple.spaceships.helper.StoreRowAdapter;
import com.plainsimple.spaceships.helper.StoreRow;

import plainsimple.spaceships.R;

/**
 * Created by Stefan on 1/25/2017.
 */

public class StoreActivity extends Activity {

    ListView listView;

    @Override // initialize the layout and populate the main ListView
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.store_layout);
        listView = (ListView) findViewById(R.id.list_view);
        // rows to be displayed in ListView (categories of upgrades)
        StoreRow[] rows = initStoreRows();
        // create adapter instance to display content in each row of ListView
        StoreRowAdapter adapter = new StoreRowAdapter(this, R.layout.listview_item, rows);
        listView.setAdapter(adapter);
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("StoreActivity.java", "Position = " + position + ", id = " + id);
            }
        });*/
    }

    // get status of equipment and initialize data required to display the store
    private StoreRow[] initStoreRows() {
        StoreRow[] rows = new StoreRow[3];
        EquipmentManager equipment = new EquipmentManager(this);
        rows[0] = new StoreRow(0, "Cannons");
        rows[0].addStoreItem(equipment.getEquipment(EquipmentManager.LASER_KEY));
        rows[0].addStoreItem(equipment.getEquipment(EquipmentManager.ION_KEY));
        rows[0].addStoreItem(equipment.getEquipment(EquipmentManager.PLASMA_KEY));
        rows[0].addStoreItem(equipment.getEquipment(EquipmentManager.PLUTONIUM_KEY));
        rows[1] = new StoreRow(1, "Rockets");
        rows[1].addStoreItem(equipment.getEquipment(EquipmentManager.ROCKET_KEY));
        rows[2] = new StoreRow(2, "Armor");
        rows[2].addStoreItem(equipment.getEquipment(EquipmentManager.ARMOR_KEY));
        return rows;
    }
}
