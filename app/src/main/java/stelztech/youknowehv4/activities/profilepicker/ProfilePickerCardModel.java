package stelztech.youknowehv4.activities.profilepicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.profile.Profile;
import stelztech.youknowehv4.utilities.BitmapUtilities;
import stelztech.youknowehv4.utilities.DateUtilities;
import stelztech.youknowehv4.manager.ThemeManager;

/**
 * Created by alex on 2017-11-28.
 */

public class ProfilePickerCardModel {


    private int profileId;
    private Bitmap thumbnailImage;
    private String name;
    private int nbCards;
    private int nbDecks;
    private int nbHoursLastOpened;
    private int profileColor;
    private ThemeManager.THEME_COLORS themeColors;

    public ProfilePickerCardModel(Profile profile, Context context) {
        this.profileId = profile.getProfileId();

        try{
            this.thumbnailImage = BitmapUtilities.getBitmapFromFile(profile.getProfileImagePath());
        } catch (Exception e){
            this.thumbnailImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.default1);
        }

        this.name = profile.getProfileName();
        this.nbCards = Database.mCardDao.fetchNumberOfCardsByProfileId(profile.getProfileId());
        this.nbDecks = Database.mDeckDao.fetchNumberOfDecksByProfileId(profile.getProfileId());

        Date lastTimeOpened = DateUtilities.stringToDate(profile.getLastTimeOpened());
        Date dateNow = DateUtilities.getDateNow();

        long diff = dateNow.getTime() - lastTimeOpened.getTime();

        long hours = TimeUnit.MILLISECONDS.toHours(diff);

        this.nbHoursLastOpened = (int) hours;
        this.profileColor = ThemeManager.getInstance().getThemePrimaryColor(context, profile.getProfileColor());
        this.themeColors = profile.getProfileColor();
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public Bitmap getThumbnailImage() {
        return thumbnailImage;
    }

    public void setThumbnailImage(Bitmap thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNbCards() {
        return nbCards;
    }

    public void setNbCards(int nbCards) {
        this.nbCards = nbCards;
    }

    public int getNbDecks() {
        return nbDecks;
    }

    public void setNbDecks(int nbDecks) {
        this.nbDecks = nbDecks;
    }

    public int getNbHoursLastOpened() {
        return nbHoursLastOpened;
    }

    public void setNbHoursLastOpened(int nbHoursLastOpened) {
        this.nbHoursLastOpened = nbHoursLastOpened;
    }

    public int getProfileColor() {
        return profileColor;
    }

    public void setProfileColor(int profileColor) {
        this.profileColor = profileColor;
    }

    public ThemeManager.THEME_COLORS getThemeColors() {
        return themeColors;
    }

    public void setThemeColors(ThemeManager.THEME_COLORS themeColors) {
        this.themeColors = themeColors;
    }
}
