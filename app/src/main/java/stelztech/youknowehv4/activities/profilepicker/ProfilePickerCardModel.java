package stelztech.youknowehv4.activities.profilepicker;

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

    public ProfilePickerCardModel(int profileId, int thumbnailImage, String name, int nbCards, int nbDecks, int nbHoursLastOpened, int profileColor) {
        this.profileId = profileId;
        this.thumbnailImage = thumbnailImage;
        this.name = name;
        this.nbCards = nbCards;
        this.nbDecks = nbDecks;
        this.nbHoursLastOpened = nbHoursLastOpened;
        this.profileColor = profileColor;
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
