package com.plainsimple.spaceships.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.plainsimple.spaceships.helper.GameMode;
import com.plainsimple.spaceships.helper.GameModeManager;

import androidx.recyclerview.widget.RecyclerView;
import plainsimple.spaceships.R;

/**
 * Adapter for displaying available GameModes in PlayScreen's Recyclerview. Takes array of GameMode
 * keys, gets a handle to GameModeManager and loads in GameMode objects to get the display data. Each
 * GameMode button has an OnClickListener that fires when clicked and reports the selected GameMode
 * object to PlayScreenActivity, where it can be loaded into a StartGameDialogFragment.
 */
public class GameModeAdapter extends RecyclerView.Adapter<GameModeAdapter.ViewHolder> {

    // GameModes to display
    private GameMode[] displayedModes;
    // receives button clicked events
    private OnGameModeSelected listener;

    // listener fired when user selects one of the GameMode buttons. Passes the button's corresponding GameMode object
    public interface OnGameModeSelected {
        void onGameModeSelected(GameMode selectedGameMode);
    }

    public GameModeAdapter(String[] gameModesToDisplay, OnGameModeSelected listener) {
        this.listener = listener;
        // retrieve specified GameModes from GameModeManager
        displayedModes = new GameMode[gameModesToDisplay.length];
        for (int i = 0; i < gameModesToDisplay.length; i++) {
            displayedModes[i] = GameModeManager.retrieve(gameModesToDisplay[i]);
        }
    }

    @Override
    public GameModeAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int position) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gamemode_layout, viewGroup, false);
        // set the onClickListener to fire onGameModeSelected with the GameMode at this position
        return new GameModeAdapter.ViewHolder(view);
    }

    @Override // set itemThumbnail to the right bitmap, bind item to listener
    public void onBindViewHolder(GameModeAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.displayName.setText(displayedModes[position].getName());
        viewHolder.displayStars.setFilledStars(displayedModes[position].calculateStars(displayedModes[position].getHighscore()));
    }

    @Override
    public int getItemCount() {
        return displayedModes.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // displays the name of the GameMode (simple label)
        private FontTextView displayName;
        // displays most stars earned
        private StarsEarnedView displayStars;

        public ViewHolder(View view) {
            super(view);
            displayName = (FontTextView) view.findViewById(R.id.gamemode_name);
            displayStars = (StarsEarnedView) view.findViewById(R.id.starsearned_display);
            view.setOnClickListener(this);
        }

        @Override // fire onGameModeSelected with displayedModes of clicked position
        public void onClick(View v) {
            listener.onGameModeSelected(displayedModes[this.getLayoutPosition()]);
        }
    }
}
