package com.plainsimple.spaceships.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import com.plainsimple.spaceships.stats.StatsManager;
import com.plainsimple.spaceships.stats.StatsRowAdapter;

import plainsimple.spaceships.R;

/**
 * Created by Stefan on 2/4/2017.
 */

public class StatsActivity extends Activity {

    private ListView listView;


    @Override // initialize the layout and populate the main ListView with statistics
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.stats_layout);
        listView = (ListView) findViewById(R.id.stats_list_view);

        // get String array of StatsManager keys and values
        String[] keys = new StatsManager().getKeysToDisplay(); // todo: clean up, remove StatsContainer interface
        String[] vals = new String[keys.length];
        for (int i = 0; i < vals.length; i++) {
            vals[i] = new StatsManager().getFormatted(keys[i]);
        }
        // create adapter instance to display content in each row of ListView
        StatsRowAdapter adapter = new StatsRowAdapter(this, R.layout.statsrow_layout, keys, vals);
        listView.setAdapter(adapter);

        // add the "Back to Menu" button below the ListView as a footer
        View footerView = (LayoutInflater.from(this)).inflate(R.layout.return_to_menu_button, null);
        listView.addFooterView(footerView);
    }

    // return to main menu
    public void onLeavePressed(View view) {
        finish();
    }
}
