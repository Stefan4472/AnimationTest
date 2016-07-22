package plainsimple.spaceships;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import java.util.Hashtable;

/**
 * Created by Stefan on 10/17/2015.
 */
public class GameActivity extends Activity {

    private GameView gameView;
    private ImageButton pauseButton;
    private ImageButton muteButton;
    private ImageButton toggleBulletButton;
    private ImageButton toggleRocketButton;
    private static SoundPool soundPool;
    private static Hashtable<RawResource, Integer> soundIDs;

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
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundIDs = new Hashtable<>();
        soundIDs.put(RawResource.LASER, soundPool.load(this, EnumUtil.getID(RawResource.LASER), 1));
        soundIDs.put(RawResource.ROCKET, soundPool.load(this, EnumUtil.getID(RawResource.ROCKET), 1));
        soundIDs.put(RawResource.EXPLOSION, soundPool.load(this, EnumUtil.getID(RawResource.EXPLOSION), 1));
        soundIDs.put(RawResource.BUTTON_CLICKED, soundPool.load(this, EnumUtil.getID(RawResource.BUTTON_CLICKED), 1));
        soundIDs.put(RawResource.TITLE_THEME, soundPool.load(this, EnumUtil.getID(RawResource.TITLE_THEME), 1));
    }

    // plays a sound using the SoundPool given SoundParams
    public static void playSound(SoundParams parameters) {
        soundPool.play(soundIDs.get(parameters.getResourceID()), parameters.getLeftVolume(),
                parameters.getRightVolume(), parameters.getPriority(), parameters.getLoop(),
                parameters.getRate());
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

    @Override
    public void onPause() {
        super.onPause();
        soundPool.release();
        soundPool = null;
    }
}
