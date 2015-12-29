package plainsimple.spaceships;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class SpaceShipsActivity extends Activity {

    private TitleView titleView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TitleView title_view = new TitleView(this);
        //title_view.setKeepScreenOn(true);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setContentView(title_view);
        setContentView(R.layout.mainscreen_layout);
        //titleView = (TitleView) findViewById(R.id.spaceships);
        //titleView.setKeepScreenOn(true);
    }
}
