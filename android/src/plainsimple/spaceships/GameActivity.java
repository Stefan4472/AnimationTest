package plainsimple.spaceships;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

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
        pauseButton.setBackgroundResource(R.drawable.pause);
        muteButton = (ImageButton) findViewById(R.id.mutebutton);
        muteButton.setBackgroundResource(R.drawable.sound_on);
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
            pauseButton.setBackgroundResource(R.drawable.pause);
            gameView.setPaused(false);
        } else {
            pauseButton.setBackgroundResource(R.drawable.play);
            gameView.setPaused(true);
        }
    }

    public void onMutePressed(View view) {
        if(gameView.getMuted()) {
            muteButton.setBackgroundResource(R.drawable.sound_on);
            gameView.setMuted(false);
        } else {
            muteButton.setBackgroundResource(R.drawable.sound_off);
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
