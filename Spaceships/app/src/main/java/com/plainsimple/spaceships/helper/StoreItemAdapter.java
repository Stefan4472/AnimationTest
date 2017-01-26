package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import plainsimple.spaceships.R;

/**
 * Created by Stefan on 1/25/2017.
 */

public class StoreItemAdapter extends RecyclerView.Adapter<StoreItemAdapter.ViewHolder> {

    private Context c;
    // id of image to display
    private BitmapID bitmapID;

    public StoreItemAdapter(Context context, BitmapID bitmapID) {
        c = context;
        this.bitmapID = bitmapID;
    }

    @Override
    public StoreItemAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.storeitem_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StoreItemAdapter.ViewHolder viewHolder, int i) {
        viewHolder.storeItemImage.setImageBitmap(BitmapCache.getImage(bitmapID, c));
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView storeItemImage;

        public ViewHolder(View view) {
            super(view);
            storeItemImage = (ImageView) view.findViewById(R.id.storeitem_image);
        }
    }
}
