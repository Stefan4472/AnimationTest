package com.plainsimple.spaceships.stats;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import plainsimple.spaceships.R;

/**
 * Adapter used to populate a ListView with all statistics from a
 * Stats object through the StatsContainer interface. The data is stored
 * in two String arrays. The keys array stores the keys in the order prescribed
 * by the StatsContainer. The values array stores the formatted String
 * values of each actual statistic, in corresponding order (i.e. values[i]
 * is the value of keys[i]). The constructor takes these two already-created arrays, which
 * must be of the same length.
 */

public class StatsRowAdapter extends ArrayAdapter<String> {

    private Context context;
    private int rId;
    // array of keys in display order
    private String[] keys;
    // array of values in display order  (must be same length as keys[]).
    // each index of values corresponds to the same index in keys[]
    private String[] values;

    // constructor taking keys[] and values[] arrays. Must be same length todo: what's up with rId?
    public StatsRowAdapter(Context context, int rId, String[] keys, String[] values) throws IllegalArgumentException {
        super(context, rId, keys);
        this.context = context;
        this.rId = rId;

        if (keys.length != values.length) {
            throw new IllegalArgumentException("Keys.length must be equal to values.length");
        } else {
            this.keys = keys;
            this.values = values;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) { // inflate a new layout (otherwise given View will be recycled)
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(rId, parent, false);
        }
        // set row's label
        TextView label = (TextView) convertView.findViewById(R.id.statitem_label);
        label.setText(keys[position]);

        // set row's value
        TextView value = (TextView) convertView.findViewById(R.id.statitem_value);
        value.setText(values[position]);
        return convertView;
    }
}
