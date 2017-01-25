package com.plainsimple.spaceships.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.plainsimple.spaceships.helper.CustomAdapterItem;
import com.plainsimple.spaceships.helper.CustomItemData;

import plainsimple.spaceships.R;

/**
 * Created by Stefan on 1/25/2017.
 */

public class StoreActivity extends Activity {

    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_layout);
        listView = (ListView) findViewById(R.id.list_view);
        // rows to be displayed in ListView (categories of upgrades)
        CustomItemData[] rows = new CustomItemData[3];
        rows[0] = new CustomItemData(0, "Cannons");
        rows[1] = new CustomItemData(1, "Rockets");
        rows[2] = new CustomItemData(2, "Armor");
        // create adapter instance to display content in listview elements
        CustomAdapterItem adapter = new CustomAdapterItem(this, R.layout.listview_item, rows);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
}
