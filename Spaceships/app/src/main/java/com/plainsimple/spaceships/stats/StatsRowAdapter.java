package com.plainsimple.spaceships.stats;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.plainsimple.spaceships.view.FontTextView;

import java.util.InputMismatchException;

import plainsimple.spaceships.R;

/**
 * Adapter used to populate a ListView with all statistics from a
 * Stats object through the StatsContainer interface. The data is stored
 * in two String arrays. The keys array stores the keys in the order prescribed
 * by the StatsContainer. The values array stores the formatted String
 * values of each actual statistic, in corresponding order (i.e. values[i]
 * is the value of keys[i]). Two constructors are given: one takes a
 * StatsContainer object and creates the two arrays from it, and the
 * other takes two already-created arrays. The arrays must be of the same
 * length.
 */

public class StatsRowAdapter extends ArrayAdapter<String> {

    private Context context;
    private int rId;
    // array of keys in display order
    private String[] keys;
    // array of values in display order  (must be same length as keys[]).
    // each index of values corresponds to the same index in keys[]
    private String[] values;

    // constructor taking StatsContainer object
    public StatsRowAdapter(Context context, int rId, StatsContainer stats) {
        super(context, rId, stats.getOrganizedKeysAsArray());
        this.context = context;
        this.rId = rId;

        keys = stats.getOrganizedKeysAsArray();
        values = new String[keys.length];

        // populate values[] array
        for (int i = 0; i < keys.length; i++) {
            values[i] = stats.getFormatted(keys[i]);
        }
    }

    // constructor taking keys[] and values[] arrays. Must be same length
    public StatsRowAdapter(Context context, int rId, String[] keys, String[] values) throws IllegalArgumentException {
        super(context, rId, keys);
        this.context = context;
        this.rId = rId;

        if (keys == null || values == null) {
            throw new NullPointerException("Cannot be null");
        } else if (keys.length != values.length) {
            throw new IllegalArgumentException("Keys.length must be equal to values.length");
        }

        this.keys = keys;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) { // inflate a new layout (otherwise given View will be recycled)
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(rId, parent, false);
        }
        // set row's label
        FontTextView label = (FontTextView) convertView.findViewById(R.id.statitem_label);
        label.setText(keys[position]);

        // set row's value
        FontTextView value = (FontTextView) convertView.findViewById(R.id.statitem_value);
        value.setText(values[position]);
        return convertView;
    }
}
