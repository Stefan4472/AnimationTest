package plainsimple.spaceships;

import android.app.Activity;
import android.os.Bundle;

public class SpaceShipsActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TitleView title_view = new TitleView(this);
        setContentView(title_view);
    }
}
