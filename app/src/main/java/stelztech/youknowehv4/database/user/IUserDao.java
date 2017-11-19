package stelztech.youknowehv4.database.user;

import stelztech.youknowehv4.database.profile.Profile;

/**
 * Created by alex on 10/14/2017.
 */

public interface IUserDao {

    User fetchUser(); // only one ever created
    int createUser();
    boolean updateDefaultSortPosition(int position);
    boolean updateQuickToggleReviewHours(int hours);
    boolean setActiveProfile(int profileId);
    boolean toggleReviewAllCards();
    boolean toggleAllowSearchOnQueryChanged();
    Profile fetchActiveProfile();
    boolean toggleDisplayNumDecksAllCards();
    boolean toggleDisplayNumDecksSpecificCard();
}
