package plainsimple.spaceships.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import plainsimple.spaceships.R;
import plainsimple.spaceships.sprites.Spaceship;
import plainsimple.spaceships.util.*;
import plainsimple.spaceships.view.FontTextView;
import plainsimple.spaceships.view.GameView;

import java.util.Hashtable;

/**
 * Created by Stefan on 10/17/2015.
 */
public class GameActivity extends Activity {

    private GameView gameView;
    // used to render game background
    private Background background;
    // whether background has been initialized yet
    private boolean backgroundInitialized = false;
    //private ResponseReceiver receiver;
    private static FontTextView scoreView;
    private ImageView backgroundView;
    private Bitmap gameBackground;
    private Canvas gameBackgroundCanvas;
    private ImageButton pauseButton;
    private ImageButton muteButton;
    private ImageButton toggleBulletButton;
    private ImageButton toggleRocketButton;
    private static SoundPool soundPool;
    private static Hashtable<RawResource, Integer> soundIDs;
    private static boolean paused = false;
    private static boolean muted = false;
    private static int score = 0;
    private static float difficulty = 0;
    // points a coin is worth
    public static final int COIN_VALUE = 100;

    /* Called when activity first created */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // go full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /*// set up IntentFilter and ResponseReceiver to register local broadcasts from DrawBackgroundService
        IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, filter);*/
        // set content view/layout to gameview layout
        setContentView(R.layout.gameview_layout);
        gameView = (GameView) findViewById(R.id.spaceships); // todo: what should go in onResume()?
        gameView.setKeepScreenOn(true);
        gameView.setGameActivity(this);
        pauseButton = (ImageButton) findViewById(R.id.pausebutton);
        pauseButton.setBackgroundResource(R.drawable.pause);
        muteButton = (ImageButton) findViewById(R.id.mutebutton);
        muteButton.setBackgroundResource(R.drawable.sound_on);
        toggleBulletButton = (ImageButton) findViewById(R.id.toggleBulletButton);
        toggleBulletButton.setBackgroundResource(R.drawable.bullets_button_pressed);
        toggleRocketButton = (ImageButton) findViewById(R.id.toggleRocketButton);
        toggleRocketButton.setBackgroundResource(R.drawable.rockets_button);
        gameBackground = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        gameBackgroundCanvas = new Canvas(gameBackground);
        // set volume control to proper stream
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    private void initMedia() {
        Log.d("Activity Class", "Creating SoundPool");
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundIDs = new Hashtable<>();
        Log.d("Activity Class", "Loading Sounds");
        soundIDs.put(RawResource.LASER, soundPool.load(this, EnumUtil.getID(RawResource.LASER), 1));
        soundIDs.put(RawResource.ROCKET, soundPool.load(this, EnumUtil.getID(RawResource.ROCKET), 1));
        soundIDs.put(RawResource.EXPLOSION, soundPool.load(this, EnumUtil.getID(RawResource.EXPLOSION), 1));
        soundIDs.put(RawResource.BUTTON_CLICKED, soundPool.load(this, EnumUtil.getID(RawResource.BUTTON_CLICKED), 1));
        soundIDs.put(RawResource.TITLE_THEME, soundPool.load(this, EnumUtil.getID(RawResource.TITLE_THEME), 1));
        Log.d("Activity Class", soundIDs.size() + " sounds loaded");
    }


    /*public class ResponseReceiver extends BroadcastReceiver {
       public static final String ACTION_RESP = "com.plainsimple.intent.action.BACKGROUND_RENDERED";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("GameActivity", "Action is finished");
            backgroundView.setImageBitmap(renderedBackground);
        }
    }*/

    // plays a sound using the SoundPool given SoundParams
    public static void playSound(SoundParams parameters) {
        soundPool.play(soundIDs.get(parameters.getResourceID()), parameters.getLeftVolume(),
                parameters.getRightVolume(), parameters.getPriority(), parameters.getLoop(),
                parameters.getRate());
    }

    // calls DrawBackgroundService to render the background having scrolled
    public void updateBackground(int toScroll) {
        if (!backgroundInitialized) {
            background = new Background(gameView.getWidth(), gameView.getHeight()); // todo: proper dimensions?
            gameBackground = Bitmap.createBitmap(gameView.getWidth(), gameView.getHeight(), Bitmap.Config.ARGB_8888);
            gameBackgroundCanvas = new Canvas(gameBackground);
            Log.d("GameActivity", "Background Initialized with width " + gameView.getWidth() + " and height " + gameView.getHeight());
            backgroundInitialized = true;
        }
        Log.d("GameActivity", "updateBackground called with parameter " + toScroll);
        new DrawBackgroundTask().execute(toScroll);
        /*Log.d("GameActivity Class", "Should Update the Background Now");
        Intent serviceIntent = new Intent(this, DrawBackgroundService.class);
        serviceIntent.putExtra(DrawBackgroundService.PARAM_WIDTH, Integer.toString(screenWidth));
        serviceIntent.putExtra(DrawBackgroundService.PARAM_HEIGHT, Integer.toString(screenHeight));
        serviceIntent.putExtra(DrawBackgroundService.PARAM_TO_SCROLL, Integer.toString(toScroll));
        startService(serviceIntent);*/
    }

    private class DrawBackgroundTask extends AsyncTask<Integer, Void, Bitmap> {
        // scrolls and renders the background in a worker thread
        @Override
        protected Bitmap doInBackground(Integer... scroll) {
            background.scroll(scroll[0]);
            Log.d("DrawBackgroundTask", "Rendering");
            background.draw(gameBackgroundCanvas);
            return gameBackground;
        }

        // posts the Bitmap rendered in doInBackground to backgroundView
        @Override
        protected void onPostExecute(Bitmap renderedBackground) {
            Log.d("DrawBackgroundTask", "Finished Rendering Background");
        }
    }

    public Bitmap getGameBackground() {
        return gameBackground;
    }

    // handle user pressing pause button
    public void onPausePressed(View view) {
        if(paused) {
            pauseButton.setBackgroundResource(R.drawable.pause);
            paused = false;
            soundPool.autoResume();
        } else {
            pauseButton.setBackgroundResource(R.drawable.play);
            paused = true;
            soundPool.autoPause();
        }
    }

    public static boolean getPaused() {
        return paused;
    }

    public static int getScore() {
        return score;
    }

    public static void incrementScore(int toAdd) {
        score += toAdd;
        Log.d("GameActivity Class", "Incrementing Score by " + toAdd + " to " + score);
        scoreView.setText("" + score); // todo: not updating
    }

    public static boolean isMuted() {
        return muted;
    }

    public static float getDifficulty() {
        return difficulty;
    }

    public static void incrementDifficulty(float toAdd) {
        difficulty += toAdd;
    }

    public void onMutePressed(View view) {
        if(muted) {
            muteButton.setBackgroundResource(R.drawable.sound_on);
            muted = false;
        } else {
            muteButton.setBackgroundResource(R.drawable.sound_off);
            muted = true;
        }
        AudioManager a_manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        a_manager.setStreamMute(AudioManager.STREAM_MUSIC, muted);

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
        // todo: persist any data
    }

    @Override
    public void onResume() {
        super.onResume();
        initMedia();
        Log.d("Activity Class", "Media Initialized");
        scoreView = (FontTextView) findViewById(R.id.scoreview);
        scoreView.setText("0");
        // todo: recreate any persisted data
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }
}
