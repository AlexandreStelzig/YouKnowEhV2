package stelztech.youknowehv4.activities.profilepicker;

import android.content.Context;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.profile.Profile;
import stelztech.youknowehv4.utilities.DateUtilities;
import stelztech.youknowehv4.manager.ThemeManager;

/**
 * Created by alex on 2017-11-28.
 */

public class ProfilePickerCardModel {


    private int profileId;
    private int thumbnailImage;
    private String name;
    private int nbCards;
    private int nbDecks;
    private int nbHoursLastOpened;
    private int profileColor;

    public ProfilePickerCardModel(Profile profile, Context context) {
        this.profileId = profile.getProfileId();
        this.thumbnailImage = profile.getProfileImage();
        this.name = profile.getProfileName();
        this.nbCards = Database.mCardDao.fetchNumberOfCardsByProfileId(profile.getProfileId());
        this.nbDecks = Database.mDeckDao.fetchNumberOfDecksByProfileId(profile.getProfileId());

        Date lastTimeOpened = DateUtilities.stringToDate(profile.getLastTimeOpened());
        Date dateNow = DateUtilities.getDateNow();

        long diff = dateNow.getTime() - lastTimeOpened.getTime();

        long hours = TimeUnit.MILLISECONDS.toHours(diff);

        this.nbHoursLastOpened = (int) hours;
        this.profileColor = ThemeManager.getInstance().getThemePrimaryColor(context, profile.getProfileColor());
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public int getThumbnailImage() {
        return thumbnailImage;
    }

    public void setThumbnailImage(int thumbnailImage) {
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
}
