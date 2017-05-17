package com.plainsimple.spaceships.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.plainsimple.spaceships.helper.GameMode;
import com.plainsimple.spaceships.helper.GameModeManager;
import com.plainsimple.spaceships.store.Equipment;

import java.util.List;

import plainsimple.spaceships.R;

/**
 * Adapter for displaying available GameModes in PlayScreen's Recyclerview. Takes array of GameMode
 * keys, gets a handle to GameModeManager and loads in GameMode objects to get the display data. Each
 * GameMode button has an OnClickListener that fires when clicked and reports the selected GameMode
 * object to PlayScreenActivity, where it can be loaded into a StartGameDialogFragment.
 */

public class GameModeAdapter extends RecyclerView.Adapter<GameModeAdapter.ViewHolder> {

    private Context c;
    // GameModes to display
    private GameMode[] displayedModes;
    // receives button clicked events
    private GameModeAdapter.OnButtonClickedListener listener;

    // listener fired when user selects one of the GameMode buttons. Passes the button's corresponding GameMode object
    public interface OnButtonClickedListener {
        void onGameModeSelected(GameMode selectedGameMode);
    }

    public GameModeAdapter(Context context, String[] gameModesToDisplay,
                           GameModeAdapter.OnButtonClickedListener listener) throws NullPointerException {
        if (listener == null) {
            throw new NullPointerException("Listener cannot be null");
        } else {
            c = context;
            this.listener = listener;
            // retrieve specified GameModes from GameModeManager
            displayedModes = new GameMode[gameModesToDisplay.length];
            for (int i = 0; i < gameModesToDisplay.length; i++) {
                displayedModes[i] = GameModeManager.retrieve(context, gameModesToDisplay[i]);
            }
        }
    }

    @Override
    public GameModeAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gamemode_layout, viewGroup, false);
        // set the onClickListener to fire onGameModeSelected with the GameMode at this position
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onGameModeSelected(displayedModes[position]);
            }
        });
        return new GameModeAdapter.ViewHolder(view);
    }

    @Override // set itemThumbnail to the right bitmap, bind item to listener
    public void onBindViewHolder(GameModeAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.displayName.setText(displayedModes[position].getName());
//        viewHolder.itemThumbnail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listener.onItemClick(displayedModes.get(position));
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return displayedModes.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        // displays the name of the GameMode (simple label)
        private FontTextView displayName;

        public ViewHolder(View view) {
            super(view);
            displayName = (FontTextView) view.findViewById(R.id.gamemode_name);
        }
    }
}
