package plainsimple.spaceships;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import plainsimple.imagetransition.SlideInTransition;

public class SpaceShipsActivity extends Activity {

    // global preferences
    public static SharedPreferences preferences;

    // todo: background animation
    // animation in background that pushes in galaxy draw
    private SlideInTransition slideIn;
    // used to play background song
    private MediaPlayer mediaPlayer;
    // used to play short sounds i.e. button clicked sound
    private SoundPool soundPool;
    // soundID created when button clicked sound is loaded
    private int soundID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.mainscreen_layout);
        preferences = this.getPreferences(Context.MODE_PRIVATE);
        // prepare background song todo: use the asynchronous method
        mediaPlayer = MediaPlayer.create(this, R.raw.title_theme);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        // set up SoundPool for playing buttonclick sound
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        soundID = soundPool.load(this, R.raw.button_clicked, 1);
        // fade in view elements
        FontTextView title = (FontTextView) findViewById(R.id.title);
        Animation fade_in_animation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        title.startAnimation(fade_in_animation);
        // todo: work on offsets
        fade_in_animation.setStartOffset(300);
        FontButton button = (FontButton) findViewById(R.id.playbutton);
        button.startAnimation(fade_in_animation);
        fade_in_animation.setStartOffset(200);
        button = (FontButton) findViewById(R.id.storebutton);
        button.startAnimation(fade_in_animation);
        fade_in_animation.setStartOffset(400);
        button = (FontButton) findViewById(R.id.statsbutton);
        button.startAnimation(fade_in_animation);
    }

    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.release();
        mediaPlayer = null;
        soundPool.release();
        soundPool = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // handle user pressing "Play" button
    public void onPlayPressed(View view) {
        /*// underline text
        Button button = (Button) findViewById(R.id.playbutton);
        button.setPaintFlags(button.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);*/
        // play button clicked sound
        soundPool.play(soundID, 1.0f, 1.0f, 1, 0, 1.0f);
        // launch GameActivity
        Intent game_intent = new Intent(this, GameActivity.class);
        this.startActivity(game_intent);
    }

    // handle user releasing "Play" button
    public void onPlayReleased(View view) {

    }
    // handle user pressing "Store" button
    public void onStorePressed(View view) {
        soundPool.play(soundID, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    // handle user pressing "Stats" button
    public void onStatsPressed(View view) {
        soundPool.play(soundID, 1.0f, 1.0f, 1, 0, 1.0f);
    }
}
