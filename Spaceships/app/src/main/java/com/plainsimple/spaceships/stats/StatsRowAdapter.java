package com.plainsimple.spaceships.stats;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.plainsimple.spaceships.view.FontTextView;

import plainsimple.spaceships.R;

/**
 * Adapter used to populate a listview with all statistics from a
 * LifeTimeGameStats object
 */

public class StatsRowAdapter extends ArrayAdapter<String> {

    private Context context;
    private int rId;
    // contains keys with which to get LifeTimeGameStats
    private String[] keys;
    // stores stats to display
    private StatsContainer stats;

    public StatsRowAdapter(Context context, int rId, StatsContainer stats) {
        super(context, rId, stats.getOrganizedKeysAsArray());
        this.context = context;
        this.rId = rId;
        this.stats = stats;
        keys = stats.getOrganizedKeysAsArray();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) { // inflate a new layout (otherwise given View will be recycled)
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(rId, parent, false);
        }
        // get the queued row
        String key = keys[position];
        // set row's label
        FontTextView label = (FontTextView) convertView.findViewById(R.id.statitem_label);
        label.setText(key);
        // set row's value (getting formatted value from LifeTimeStats object)
        FontTextView value = (FontTextView) convertView.findViewById(R.id.statitem_value);
        value.setText(stats.getFormatted(key));
        return convertView;
    }
}
