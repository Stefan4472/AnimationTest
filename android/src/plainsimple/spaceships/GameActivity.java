package plainsimple.spaceships;

import android.app.Activity;
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
        toggleBulletButton = (ImageButton) findViewById(R.id.toggleBulletButton);
        toggleBulletButton.setBackgroundResource(R.drawable.bullets_button_pressed);
        toggleRocketButton = (ImageButton) findViewById(R.id.toggleRocketButton);
        toggleRocketButton.setBackgroundResource(R.drawable.rockets_button);
    }

    // handle user pressing pause button //todo: is this an okay way of handling the event?
    public void onPausePressed(View view) {
        gameView.onPausePressed(pauseButton);
    }

    public void onMutePressed(View view) {

    }

    public void onToggleBulletPressed(View view) {
        // todo: performance issues?
        gameView.getMap().getSpaceship().setFiringMode(Spaceship.BULLET_MODE);
        toggleBulletButton.setBackgroundResource(R.drawable.bullets_button_pressed);
        toggleRocketButton.setBackgroundResource(R.drawable.rockets_button);
    }

    public void onToggleRocketPressed(View view) {
        gameView.getMap().getSpaceship().setFiringMode(Spaceship.ROCKET_MODE);
        toggleRocketButton.setBackgroundResource(R.drawable.rockets_button_pressed);
        toggleBulletButton.setBackgroundResource(R.drawable.bullets_button);
    }
}
