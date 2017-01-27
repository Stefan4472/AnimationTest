package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import plainsimple.spaceships.R;

/**
 * Created by Stefan on 1/25/2017.
 */

public class StoreItemAdapter extends RecyclerView.Adapter<StoreItemAdapter.ViewHolder> {

    private Context c;
    // StoreItems to display
    private List<Equipment> items;

    public StoreItemAdapter(Context context, List<Equipment> storeItems) {
        c = context;
        items = storeItems; // todo: deep copy required?
        Log.d("StoreItemAdapter.java", items.size() + " items found");
    }

    @Override
    public StoreItemAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.storeitem_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override // set itemThumbnail to the right bitmap
    public void onBindViewHolder(StoreItemAdapter.ViewHolder viewHolder, int i) {
        viewHolder.itemThumbnail.setImageBitmap(BitmapFactory.decodeResource(c.getResources(), items.get(i).getrDrawableId()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView itemThumbnail;

        public ViewHolder(View view) {
            super(view);
            itemThumbnail = (ImageView) view.findViewById(R.id.item_thumbnail);
        }
    }
}
