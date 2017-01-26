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

public class StoreRowAdapter extends ArrayAdapter<CustomItemData> { // TESTING

    Context c;
    int id;
    CustomItemData data[] = null;

    public StoreRowAdapter(Context mContext, int id, CustomItemData[] data) {
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
        // object item based on the position
        CustomItemData objectItem = data[position];
        // get the TextView and then set the text (item name) and tag (item ID) values
        TextView textViewItem = (TextView) convertView.findViewById(R.id.label);
        textViewItem.setText(objectItem.getString());
        textViewItem.setTag(objectItem.getNumber()); // todo: we'll need more information to construct the store tiles
        RecyclerView upgrade_display = (RecyclerView) convertView.findViewById(R.id.recycler_view);
        upgrade_display.setLayoutManager(new LinearLayoutManager(c));
        upgrade_display.setAdapter(new StoreItemAdapter(c, BitmapID.SPACESHIP));
        return convertView;
    }

}
