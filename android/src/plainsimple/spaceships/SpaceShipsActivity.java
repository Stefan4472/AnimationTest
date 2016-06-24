package plainsimple.spaceships;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.mainscreen_layout);
        preferences = this.getPreferences(Context.MODE_PRIVATE);
        // fade in view elements
        FontTextView title = (FontTextView) findViewById(R.id.title);
        Animation fade_in_animation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        title.startAnimation(fade_in_animation);
        // work on offsets
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

    // handle user pressing "Play" button
    public void onPlayPressed(View view) {
        /*// underline text
        Button button = (Button) findViewById(R.id.playbutton);
        button.setPaintFlags(button.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);*/
        // launch GameActivity
        Intent game_intent = new Intent(this, GameActivity.class);
        this.startActivity(game_intent);
    }

    // handle user releasing "Play" button
    public void onPlayReleased(View view) {

    }
    // handle user pressing "Store" button
    public void onStorePressed(View view) {

    }

    // handle user pressing "Stats" button
    public void onStatsPressed(View view) {

    }
}
