package stelztech.youknowehv4.firsttimeopening;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.firsttimeopening.ViewPagerProfile;
import stelztech.youknowehv4.firsttimeopening.ViewPagerWelcome;

/**
 * Created by alex on 2017-04-28.
 */

public class FirstTimeOpeningActivity extends FragmentActivity {


    public enum ViewPagerState {
        WELCOME,
        CREATE_PROFILE
    }

    private static final int NUM_PAGES = 2;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    private ViewPagerProfile mViewPagerProfile;
    private ViewPagerWelcome mViewPagerWelcome;

    private Fragment currentFragment;


    private static final int REQUEST_WRITE_STORAGE = 112;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_opening);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mViewPagerProfile = new ViewPagerProfile();
        mViewPagerWelcome = new ViewPagerWelcome();

        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }


//        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    public void changeState(ViewPagerState state) {
        switch (state) {
            case WELCOME:
                mPager.setCurrentItem(0);
                break;
            case CREATE_PROFILE:
                mPager.setCurrentItem(1);
                break;
        }

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                // welcome
                case 0:
                    currentFragment = mViewPagerWelcome;
                    break;
                // profile creation
                case 1:
                    currentFragment = mViewPagerProfile;
                    break;
            }
            return currentFragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //reload my activity with permission granted or use the features what required the permission
                }
            }
        }

    }
}
