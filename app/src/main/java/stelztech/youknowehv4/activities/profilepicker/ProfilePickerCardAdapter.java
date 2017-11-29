package stelztech.youknowehv4.activities.profilepicker;

import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import stelztech.youknowehv4.R;

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
        itemHeight = Math.round(windowDimensions.y * CARD_WIDTH_SCALE);
        itemWidth = Math.round(windowDimensions.x * CARD_HEIGHT_SCALE);
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
        Glide.with(holder.itemView.getContext())
                .load(profilePickerActivity.getProfileCards().get(position).getThumbnailImage())
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return profilePickerActivity.getProfileCards().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.profile_picker_card_thumbnail);
//
//
            itemView.findViewById(R.id.profile_picker_card_container).setOnClickListener(this);
//            itemView.findViewById(R.id.custom_travel_card_layout_settings).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int itemClickedPosition = getAdapterPosition();
//
//                    if(itemClickedPosition == fragmentTravels.getCurrentItem()){
//                        Toast.makeText(fragmentTravels.getContext(), "SETTINGS " + itemClickedPosition, Toast.LENGTH_SHORT).show();
//                    }else{
//                        parentRecycler.smoothScrollToPosition(getAdapterPosition());
//                    }
//                }
//            });

        }

        @Override
        public void onClick(View view) {

            int itemClickedPosition = getAdapterPosition();

            if(itemClickedPosition == profilePickerActivity.getCurrentCardIndex()){
                Toast.makeText(profilePickerActivity, "OPEN " + itemClickedPosition, Toast.LENGTH_SHORT).show();
            }else{
                parentRecycler.smoothScrollToPosition(getAdapterPosition());
            }


        }

    }


}
