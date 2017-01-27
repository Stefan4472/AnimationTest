package com.plainsimple.spaceships.helper;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.plainsimple.spaceships.util.EnumUtil;

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
    }

    @Override
    public StoreItemAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.storeitem_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StoreItemAdapter.ViewHolder viewHolder, int i) {
        viewHolder.storeItemImage.setImageBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.spaceship));
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
