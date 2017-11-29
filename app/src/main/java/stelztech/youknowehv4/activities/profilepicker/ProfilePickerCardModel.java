package stelztech.youknowehv4.activities.profilepicker;

import android.media.Image;

/**
 * Created by alex on 2017-11-28.
 */

public class ProfilePickerCardModel {


    private int thumbnailImage;

    public ProfilePickerCardModel(int thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }

    public int getThumbnailImage() {
        return thumbnailImage;
    }

    public void setThumbnailImage(int thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }
}
