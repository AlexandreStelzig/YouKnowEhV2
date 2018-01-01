package stelztech.youknowehv4.activities.profilepicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.Orientation;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activities.MainActivityManager;
import stelztech.youknowehv4.activities.ProfileNewEditActivity;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.profile.Profile;
import stelztech.youknowehv4.utilities.BlurBuilder;
import stelztech.youknowehv4.manager.ThemeManager;

/**
 * Created by alex on 2017-11-27.
 */

public class ProfilePickerActivity extends AppCompatActivity implements DiscreteScrollView.OnItemChangedListener {


    private int currentCardIndex = 0;

    private List<ProfilePickerCardModel> profilePickerCardModelList;

    // others
    private BlurBuilder blurBuilder;

    // scrollview data
    private static final int TRANSITION_TIME_MILLIS = 200;
    private static final float SCROLLVIEW_TRANSFORM_SCALE = 0.8f;
    private static final int FLING_THRESHOLD = 2000;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_picker);


        // init blurBuilder with default values
        if (blurBuilder == null)
            blurBuilder = new BlurBuilder();

        List<Profile> profiles = Database.mProfileDao.fetchAllProfiles();

        final DiscreteScrollView scrollView = (DiscreteScrollView) findViewById(R.id.profile_picker_profiles_scrollview);
        final LinearLayout noProfileLinearLayout = (LinearLayout) findViewById(R.id.profile_picker_no_profiles_layout);
        final LinearLayout addButtonLinearLayout = (LinearLayout) findViewById(R.id.profile_picker_add_button_layout);

        if (profiles.isEmpty()) {
            scrollView.setVisibility(View.GONE);
            noProfileLinearLayout.setVisibility(View.VISIBLE);
            addButtonLinearLayout.setVisibility(View.GONE);

            ((Button) findViewById(R.id.profile_picker_create_profile_label)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ThemeManager.getInstance().changeTheme(ThemeManager.THEME_COLORS.BLUE);
                    Intent i = new Intent(ProfilePickerActivity.this, ProfileNewEditActivity.class);
                    startActivity(i);
                    finish();
                }
            });

        } else {
            scrollView.setVisibility(View.VISIBLE);
            noProfileLinearLayout.setVisibility(View.GONE);
            addButtonLinearLayout.setVisibility(View.VISIBLE);


            profilePickerCardModelList = new ArrayList<>();


            for (Profile profile : profiles) {
                profilePickerCardModelList.add(new ProfilePickerCardModel(profile, this));
            }



            scrollView.setOrientation(Orientation.HORIZONTAL);
            scrollView.addOnItemChangedListener(this);

            RecyclerView.Adapter infiniteAdapter = (new ProfilePickerCardAdapter(this));

            scrollView.setAdapter(infiniteAdapter);
            scrollView.setItemTransitionTimeMillis(TRANSITION_TIME_MILLIS);
            scrollView.setItemTransformer(new ScaleTransformer.Builder()
                    .setMinScale(SCROLLVIEW_TRANSFORM_SCALE)
                    .build());
            scrollView.setSlideOnFling(true);
            scrollView.setSlideOnFlingThreshold(FLING_THRESHOLD);

            int openOnIndex = getIntent().getIntExtra("OpenOnIndex", 0);
            scrollView.scrollToPosition(openOnIndex);


            Button addButton = (Button) findViewById(R.id.profile_picker_add_button);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // default value
                    ThemeManager.getInstance().changeTheme(ThemeManager.THEME_COLORS.BLUE);
                    Intent i = new Intent(ProfilePickerActivity.this, ProfileNewEditActivity.class);
                    i.putExtra("LastIndex", scrollView.getCurrentItem());

                    startActivity(i);
                    finish();
                }
            });
        }

    }

    public void setLayoutBackground(int image) {

        // blur the image
        Bitmap bitmap = blurBuilder.blur(this, BitmapFactory.decodeResource(getResources(), image));
        // change the background image
        ((ImageView) findViewById(R.id.profile_picker_background)).setImageBitmap(bitmap);
    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
        currentCardIndex = adapterPosition;
        setLayoutBackground(profilePickerCardModelList.get(adapterPosition).getThumbnailImage());
    }

    public List<ProfilePickerCardModel> getProfileCards() {
        return profilePickerCardModelList;
    }


    public int getCurrentCardIndex() {
        return currentCardIndex;
    }

    protected void selectProfile(int position) {
        int profileId = getProfileCards().get(position).getProfileId();
        Database.mUserDao.setActiveProfile(profileId);
        Profile profile = Database.mProfileDao.fetchProfileById(profileId);
        ThemeManager themeManager = ThemeManager.getInstance();
        ThemeManager.THEME_COLORS color = profile.getProfileColor();

        Database.mProfileDao.changeProfileColor(Database.mUserDao.fetchActiveProfile().getProfileId(), color);
        themeManager.changeTheme(color);
        setTheme(ThemeManager.getInstance().getCurrentAppThemeValue());

        Intent i = new Intent(this, MainActivityManager.class);
        startActivity(i);
        finish();
    }

    protected void openSettings(int profileId, int lastIndex) {
        Intent i = new Intent(this, ProfileNewEditActivity.class);
        i.putExtra("ProfileId", profileId);
        i.putExtra("ReturnLocation", ProfileNewEditActivity.RETURN_LOCATION.PROFILE_PICKER.toString());
        i.putExtra("LastIndex", lastIndex);

        startActivity(i);
        finish();
    }

}
