package com.plainsimple.spaceships.store;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
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
    // receives button clicked events
    private OnButtonClickedListener listener;

    // listener fired when user selects one of the store buttons
    // passes the Equipment represented by the button
    public interface OnButtonClickedListener {
        void onItemClick(Equipment selectedItem);
    }

    public StoreItemAdapter(Context context, List<Equipment> storeItems, OnButtonClickedListener listener) {
        c = context;
        items = storeItems;
        this.listener = listener;
    }

    @Override
    public StoreItemAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.storeitem_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override // set itemThumbnail to the right bitmap, bind item to listener
    public void onBindViewHolder(StoreItemAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.itemThumbnail.setImageBitmap(BitmapFactory.decodeResource(c.getResources(), items.get(position).getrDrawableId()));
        viewHolder.itemThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(items.get(position));
            }
        });
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
