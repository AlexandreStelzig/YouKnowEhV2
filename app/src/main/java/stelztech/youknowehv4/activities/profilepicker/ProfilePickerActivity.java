package stelztech.youknowehv4.activities.profilepicker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.Orientation;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.profile.Profile;
import stelztech.youknowehv4.helper.BlurBuilder;

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
        profilePickerCardModelList = new ArrayList<>();


        for (Profile profile : profiles) {
            profilePickerCardModelList.add(new ProfilePickerCardModel(profile, this));
        }


        DiscreteScrollView scrollView = (DiscreteScrollView) findViewById(R.id.profile_picker_profiles_scrollview);

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

}
