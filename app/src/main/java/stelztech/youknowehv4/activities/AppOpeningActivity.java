package stelztech.youknowehv4.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activities.profilepicker.ProfilePickerActivity;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.profile.Profile;
import stelztech.youknowehv4.manager.CardInfoToolbarManager;
import stelztech.youknowehv4.manager.CardToolbarManager;
import stelztech.youknowehv4.manager.FloatingActionButtonManager;
import stelztech.youknowehv4.manager.ThemeManager;
import stelztech.youknowehv4.utilities.BitmapUtilities;
import stelztech.youknowehv4.utilities.Helper;

/**
 * Created by alex on 2017-04-28.
 */

public class AppOpeningActivity extends AppCompatActivity {


    private final int LOADING_TIME = 1000;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        String path = Environment.getExternalStorageDirectory().toString() + "/Pictures/You Know Eh?";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        if(files == null || files.length == 0){
            List<Bitmap> bitmapList = new ArrayList<>();
            bitmapList.add(BitmapFactory.decodeResource(getResources(), R.drawable.default1));
            bitmapList.add(BitmapFactory.decodeResource(getResources(), R.drawable.default2));
            bitmapList.add(BitmapFactory.decodeResource(getResources(), R.drawable.default3));
            bitmapList.add(BitmapFactory.decodeResource(getResources(), R.drawable.default4));
            bitmapList.add(BitmapFactory.decodeResource(getResources(), R.drawable.default5));

            File dir = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/You Know Eh?");

            for(int counter = 0; counter < bitmapList.size(); counter++){
                boolean doSave = true;
                if (!dir.exists()) {
                    doSave = dir.mkdirs();
                }

                if (doSave) {
                    boolean passed = BitmapUtilities.saveBitmapToFile(dir, "You Know Eh Default " + counter + ".png", bitmapList.get(counter), Bitmap.CompressFormat.PNG, 100);
                    if(passed)
                        Log.d("app", "File saved.");
                } else {
                    Log.e("app", "Couldn't create target directory.");
                }
            }

        }

        Helper helper = Helper.getInstance();
        helper.setContext(this);

        CardToolbarManager.getInstance().setContext(this);
        CardInfoToolbarManager.getInstance().setContext(this);
        FloatingActionButtonManager.getInstance().setContext(this);

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
