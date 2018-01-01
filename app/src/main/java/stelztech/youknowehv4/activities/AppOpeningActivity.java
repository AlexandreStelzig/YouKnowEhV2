package stelztech.youknowehv4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import stelztech.youknowehv4.activities.profilepicker.ProfilePickerActivity;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.profile.Profile;
import stelztech.youknowehv4.firsttimeopening.FirstTimeOpeningActivity;
import stelztech.youknowehv4.manager.CardInfoToolbarManager;
import stelztech.youknowehv4.manager.CardToolbarManager;
import stelztech.youknowehv4.manager.FloatingActionButtonManager;
import stelztech.youknowehv4.manager.SortingStateManager;
import stelztech.youknowehv4.manager.ThemeManager;
import stelztech.youknowehv4.utilities.Helper;

/**
 * Created by alex on 2017-04-28.
 */

public class AppOpeningActivity extends AppCompatActivity {


    private final int LOADING_TIME = 1000;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Helper helper = Helper.getInstance();
        helper.setContext(this);

        CardToolbarManager.getInstance().setContext(this);
        CardInfoToolbarManager.getInstance().setContext(this);
        FloatingActionButtonManager.getInstance().setContext(this);

        Window window = getWindow();


        Database.open(this);


        if (Database.mUserDao.fetchUser() == null) {
            Database.mUserDao.createUser();
        }
        if (Database.mProfileDao.fetchAllProfiles().isEmpty()) {
            Intent i = new Intent(AppOpeningActivity.this, ProfilePickerActivity.class);
            startActivity(i);
            finish();
        } else {
            if (Database.mUserDao.fetchUser().getActiveProfileId() != Profile.NO_PROFILES)
                ThemeManager.getInstance().changeTheme(Database.mUserDao.fetchActiveProfile().getProfileColor());

            Intent i = new Intent(AppOpeningActivity.this, SplashScreenActivity.class);
            startActivity(i);
            finish();
        }

    }
}
