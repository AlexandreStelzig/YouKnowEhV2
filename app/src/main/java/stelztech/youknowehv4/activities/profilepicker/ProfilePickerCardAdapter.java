package stelztech.youknowehv4.activities.profilepicker;

import android.content.Intent;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activities.MainActivityManager;
import stelztech.youknowehv4.activities.SplashScreenActivity;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.profile.Profile;
import stelztech.youknowehv4.manager.ThemeManager;

/**
 * Created by alex on 2017-11-28.
 */

public class ProfilePickerCardAdapter extends RecyclerView.Adapter<ProfilePickerCardAdapter.ViewHolder> {

    private RecyclerView parentRecycler;
    private int itemHeight;
    private int itemWidth;

    private ProfilePickerActivity profilePickerActivity;

    private final static float CARD_WIDTH_SCALE = 0.7f;
    private final static float CARD_HEIGHT_SCALE = 0.7f;

    public ProfilePickerCardAdapter(ProfilePickerActivity profilePickerActivity) {
        this.profilePickerActivity = profilePickerActivity;

        Point windowDimensions = new Point();
        profilePickerActivity.getWindowManager().getDefaultDisplay().getSize(windowDimensions);
        itemHeight = Math.round(windowDimensions.y * CARD_HEIGHT_SCALE);
        itemWidth = Math.round(windowDimensions.x * CARD_WIDTH_SCALE);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        parentRecycler = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.custom_profile_picker_card, parent, false);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                itemWidth,
                itemHeight);
        v.setLayoutParams(params);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProfilePickerCardModel profilePickerCardModel = profilePickerActivity.getProfileCards().get(position);
        Glide.with(holder.itemView.getContext())
                .load(profilePickerCardModel.getThumbnailImage())
                .into(holder.image);
        holder.cardContainer.setBackgroundColor(profilePickerCardModel.getProfileColor());
        holder.nbCardTextView.setText(profilePickerCardModel.getNbCards() + "");
        holder.nbDeckTextView.setText(profilePickerCardModel.getNbDecks() + "");
        holder.profileNameTextView.setText(profilePickerCardModel.getName());
        int lastTimeOpenedHours = profilePickerCardModel.getNbHoursLastOpened();
        if (lastTimeOpenedHours < 1) {
            holder.lastOpenedTextView.setText("Last time opened: < 1 hour ago");
        } else if (lastTimeOpenedHours == 1) {
            holder.lastOpenedTextView.setText("Last time opened: 1 hour ago");
        } else if (lastTimeOpenedHours >= 24) {
            int lastTimeOpenedDays = lastTimeOpenedHours / 24;

            if (lastTimeOpenedDays > 365) {
                int lastTimeOpenedYears = lastTimeOpenedDays / 365;

                if (lastTimeOpenedDays == 1) {
                    holder.lastOpenedTextView.setText("Last time opened: 1 year ago");
                } else {
                    holder.lastOpenedTextView.setText("Last time opened: " + lastTimeOpenedYears + " years ago");
                }

            } else {
                if (lastTimeOpenedDays == 1) {
                    holder.lastOpenedTextView.setText("Last time opened: 1 day ago");
                } else {
                    holder.lastOpenedTextView.setText("Last time opened: " + lastTimeOpenedDays + " days ago");
                }
            }


        } else {
            holder.lastOpenedTextView.setText("Last time opened: " + lastTimeOpenedHours + " hours ago");
        }
    }

    @Override
    public int getItemCount() {
        return profilePickerActivity.getProfileCards().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private RelativeLayout cardContainer;
        private TextView nbCardTextView;
        private TextView nbDeckTextView;
        private TextView profileNameTextView;
        private TextView lastOpenedTextView;

        ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.profile_picker_card_thumbnail);
            cardContainer = (RelativeLayout) itemView.findViewById(R.id.profile_picker_card_container);
            nbCardTextView = (TextView) itemView.findViewById(R.id.profile_picker_nb_card_textview);
            nbDeckTextView = (TextView) itemView.findViewById(R.id.profile_picker_nb_deck_textview);
            profileNameTextView = (TextView) itemView.findViewById(R.id.profile_picker_profile_card_name_textview);
            lastOpenedTextView = (TextView) itemView.findViewById(R.id.profile_picker_card_last_opened_textview);

            // on click listener for the container itself
            itemView.findViewById(R.id.profile_picker_card_container).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemClickedPosition = getAdapterPosition();

                    if (itemClickedPosition == profilePickerActivity.getCurrentCardIndex()) {

                        int profileId = profilePickerActivity.getProfileCards().get(itemClickedPosition).getProfileId();
                        Database.mUserDao.setActiveProfile(profileId);
                        Profile profile = Database.mProfileDao.fetchProfileById(profileId);
                        ThemeManager themeManager = ThemeManager.getInstance();
                        ThemeManager.THEME_COLORS color = profile.getProfileColor();

                        Database.mProfileDao.changeProfileColor(Database.mUserDao.fetchActiveProfile().getProfileId(), color);
                        themeManager.changeTheme(color);
                        profilePickerActivity.setTheme(ThemeManager.getInstance().getCurrentAppThemeValue());

                        Intent i = new Intent(profilePickerActivity, MainActivityManager.class);
                        profilePickerActivity.startActivity(i);
                        profilePickerActivity.finish();

                    } else {
                        parentRecycler.smoothScrollToPosition(getAdapterPosition());
                    }
                }
            });

            // on click listener for the settings butter
            itemView.findViewById(R.id.profile_picker_card_settings).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemClickedPosition = getAdapterPosition();

                    if (itemClickedPosition == profilePickerActivity.getCurrentCardIndex()) {
                        Toast.makeText(profilePickerActivity, "SETTINGS " + itemClickedPosition, Toast.LENGTH_SHORT).show();
                    } else {
                        parentRecycler.smoothScrollToPosition(getAdapterPosition());
                    }
                }
            });

        }

    }

}
