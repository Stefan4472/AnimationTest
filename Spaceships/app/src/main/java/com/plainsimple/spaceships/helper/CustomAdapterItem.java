package com.plainsimple.spaceships.helper;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import plainsimple.spaceships.R;

/**
 * Adapter used to populate ListView in StoreActivity
 */

public class CustomAdapterItem extends ArrayAdapter<CustomItemData> { // TESTING

    Context c;
    int layoutResourceId;
    CustomItemData data[] = null;

    public CustomAdapterItem(Context mContext, int layoutResourceId, CustomItemData[] data) {
        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        c = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) { // inflate a new layout (otherwise given View will be recycled)
            LayoutInflater inflater = ((Activity) c).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }
        // object item based on the position
        CustomItemData objectItem = data[position];
        // get the TextView and then set the text (item name) and tag (item ID) values
        TextView textViewItem = (TextView) convertView.findViewById(R.id.textViewItem);
        textViewItem.setText(objectItem.getString());
        textViewItem.setTag(objectItem.getNumber());
        return convertView;
    }

}
