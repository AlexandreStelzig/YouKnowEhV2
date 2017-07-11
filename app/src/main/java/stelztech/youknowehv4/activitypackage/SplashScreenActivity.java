package stelztech.youknowehv4.activitypackage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.database.DatabaseManager;

/**
 * Created by alex on 2017-04-28.
 */

public class SplashScreenActivity extends AppCompatActivity {


    private final int LOADING_TIME = 1000;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);


        DatabaseManager dbManager = DatabaseManager.getInstance(this);
        dbManager.verifyPracticeCards();

        if (dbManager.getUser() == null) {
            dbManager.createUser();
        }
        if (dbManager.getProfiles().isEmpty()) {
            Intent i = new Intent(SplashScreenActivity.this, FirstTimeOpeningActivity.class);
            startActivity(i);
            finish();
        } else {
            transitionThread().start();
        }


    }

    private Thread transitionThread() {
        final Thread transitionThread = new Thread() {

            @Override
            public void run() {
                try {
                    super.run();
                    sleep(LOADING_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent i = new Intent(SplashScreenActivity.this, MainActivityManager.class);
                    startActivity(i);
                    finish();
                }
            }
        };

        return transitionThread;
    }

}
