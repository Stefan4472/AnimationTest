package plainsimple.spaceships;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Stefan on 10/17/2015.
 */
public class GameActivity extends Activity {

    private GameView gameView;
    private ImageButton pauseButton;
    private ImageButton muteButton;
    private ImageButton toggleBulletButton;
    private ImageButton toggleRocketButton;

    /* Called when activity first created */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // go full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // set content view/layout to gameview layout
        setContentView(R.layout.gameview_layout);
        gameView = (GameView) findViewById(R.id.spaceships);
        gameView.setKeepScreenOn(true);
        pauseButton = (ImageButton) findViewById(R.id.pausebutton);
        pauseButton.setBackgroundResource(R.drawable.pausebutton_pause);
        muteButton = (ImageButton) findViewById(R.id.mutebutton);
        muteButton.setBackgroundResource(R.drawable.mute_button);
        toggleBulletButton = (ImageButton) findViewById(R.id.toggleBulletButton);
        toggleBulletButton.setBackgroundResource(R.drawable.bullets_button_pressed);
        toggleRocketButton = (ImageButton) findViewById(R.id.toggleRocketButton);
        toggleRocketButton.setBackgroundResource(R.drawable.rockets_button);
        // set volume control to proper stream
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    // handle user pressing pause button //todo: is this an okay way of handling the event?
    public void onPausePressed(View view) {
        if(gameView.getPaused()) {
            pauseButton.setBackgroundResource(R.drawable.pausebutton_pause);
            gameView.setPaused(false);
        } else {
            pauseButton.setBackgroundResource(R.drawable.pausebutton_play);
            gameView.setPaused(true);
        }
    }

    public void onMutePressed(View view) {
        if(gameView.getMuted()) {
            muteButton.setBackgroundResource(R.drawable.mute_button);
            gameView.setMuted(false);
        } else {
            muteButton.setBackgroundResource(R.drawable.mute_button_muted);
            gameView.setMuted(true);
        }
        AudioManager a_manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        a_manager.setStreamMute(AudioManager.STREAM_MUSIC, gameView.getMuted());
    }

    public void onToggleBulletPressed(View view) {
        gameView.setFiringMode(Spaceship.BULLET_MODE);
        toggleBulletButton.setBackgroundResource(R.drawable.bullets_button_pressed);
        toggleRocketButton.setBackgroundResource(R.drawable.rockets_button);
    }

    public void onToggleRocketPressed(View view) {
        gameView.setFiringMode(Spaceship.ROCKET_MODE);
        toggleRocketButton.setBackgroundResource(R.drawable.rockets_button_pressed);
        toggleBulletButton.setBackgroundResource(R.drawable.bullets_button);
    }
}
