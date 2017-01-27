package com.plainsimple.spaceships.helper;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import plainsimple.spaceships.R;

/**
 * Adapter used to populate ListView in StoreActivity
 */

public class StoreRowAdapter extends ArrayAdapter<StoreRow> { // TESTING

    Context c;
    int id;
    // contains StoreRow objects to display
    StoreRow data[];

    public StoreRowAdapter(Context mContext, int id, StoreRow[] data) {
        super(mContext, id, data);
        c = mContext;
        this.id = id;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) { // inflate a new layout (otherwise given View will be recycled)
            LayoutInflater inflater = ((Activity) c).getLayoutInflater();
            convertView = inflater.inflate(id, parent, false);
        }
        // get the queued row
        StoreRow row = data[position];
        // set row's label
        TextView label = (TextView) convertView.findViewById(R.id.label);
        label.setText(row.getRowLabel());
        label.setTag(row.getId());
        RecyclerView row_display = (RecyclerView) convertView.findViewById(R.id.recycler_view);
        row_display.setLayoutManager(new LinearLayoutManager(c));
        row_display.setAdapter(new StoreItemAdapter(c, row.getRowItems()));
        return convertView;
    }

}
