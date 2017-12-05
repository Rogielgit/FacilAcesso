package app.facilacesso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by computeiro on 03/12/17.
 */

public class InitialScreen extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initialscreen);

        int secondsDelayed = 8;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(InitialScreen.this,  MapsActivity.class));
                finish();
            }
        }, secondsDelayed * 1000);
    }
}
