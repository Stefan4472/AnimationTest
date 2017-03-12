package com.plainsimple.spaceships.helper;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import plainsimple.spaceships.R;

/**
 * Adapter used to populate ListView in StoreActivity
 */

public class StoreRowAdapter extends ArrayAdapter<StoreRow> {

    private Context c;
    private int id;
    // contains StoreRow objects to display
    private StoreRow data[];
    // receives StoreItem button clicked events
    private StoreItemAdapter.OnButtonClickedListener buttonListener;

    public StoreRowAdapter(Context mContext, int id, StoreRow[] data,
                           StoreItemAdapter.OnButtonClickedListener buttonListener) {
        super(mContext, id, data);
        c = mContext;
        this.id = id;
        this.data = data;
        this.buttonListener = buttonListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) { // inflate a new layout (otherwise given View will be recycled)
            LayoutInflater inflater = ((Activity) c).getLayoutInflater();
            convertView = inflater.inflate(id, parent, false);
        }
        // get the queued row
        StoreRow row = data[position];
        //Log.d("StoreRowAdapter.java", "Queued row: " + position + " contains \n" + row);
        // set row's label
        TextView label = (TextView) convertView.findViewById(R.id.label);
        label.setText(row.getRowLabel());
        label.setTag(row.getId());
        // create recyclerview to display individual elements in store row
        RecyclerView row_display = (RecyclerView) convertView.findViewById(R.id.recycler_view);
        row_display.setLayoutManager(new LinearLayoutManager(c, LinearLayoutManager.HORIZONTAL, false));
        row_display.setAdapter(new StoreItemAdapter(c, row.getRowItems(), buttonListener));
        return convertView;
    }
}
