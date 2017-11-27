package stelztech.youknowehv4.activitypackage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.firsttimeopening.FirstTimeOpeningActivity;
import stelztech.youknowehv4.manager.ThemeManager;

/**
 * Created by alex on 2017-04-28.
 */

public class AppOpeningActivity extends AppCompatActivity {


    private final int LOADING_TIME = 1000;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();


        Database.open(this);


        if (Database.mUserDao.fetchUser() == null) {
            Database.mUserDao.createUser();
        }
        if (Database.mProfileDao.fetchAllProfiles().isEmpty()) {
            Intent i = new Intent(AppOpeningActivity.this, FirstTimeOpeningActivity.class);
            startActivity(i);
            finish();
        } else {

            ThemeManager.getInstance().changeTheme(Database.mUserDao.fetchActiveProfile().getProfileColor());
            Intent i = new Intent(AppOpeningActivity.this, SplashScreenActivity.class);
            startActivity(i);
            finish();
        }

    }
}
