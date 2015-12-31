package plainsimple.spaceships;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class SpaceShipsActivity extends Activity {

    private TitleView titleView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.mainscreen_layout);
    }

    public void onPlayPressed(View view) {
        Intent game_intent = new Intent(this, GameActivity.class);
        this.startActivity(game_intent);
    }
}
