package stelztech.youknowehv4.activitypackage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.firsttimeopening.FirstTimeOpeningActivity;
import stelztech.youknowehv4.manager.ThemeManager;

/**
 * Created by alex on 2017-04-28.
 */

public class SplashScreenActivity extends AppCompatActivity {


    private final int LOADING_TIME = 1000;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(ThemeManager.getInstance().getCurrentAppThemeNoActionBarValue());

        setContentView(R.layout.activity_splash_screen);


        Database.open(this);
        Database.mCardDeckDao.revalidateReviewCards();


        if (Database.mUserDao.fetchUser() == null) {
            Database.mUserDao.createUser();
        }
        if (Database.mProfileDao.fetchAllProfiles().isEmpty()) {
            Intent i = new Intent(SplashScreenActivity.this, FirstTimeOpeningActivity.class);
            startActivity(i);
            finish();
        } else {
            ThemeManager.getInstance().changeTheme(Database.mUserDao.fetchActiveProfile().getProfileColor());
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
