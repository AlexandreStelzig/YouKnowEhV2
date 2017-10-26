package stelztech.youknowehv4.database.user;

import java.util.List;

import stelztech.youknowehv4.database.profile.Profile;

/**
 * Created by alex on 10/14/2017.
 */

public interface IUserDao {

    User fetchUser(); // only one ever created
    long createUser();
    boolean updateDefaultSortPosition(int position);
    boolean updateQuickToggleReviewHours(int hours);
    boolean setActiveProfile(long profileId);
    boolean toggleAllowProfileDeletion();
    boolean toggleAllowSearchOnQueryChanged();
    Profile fetchActiveProfile();
}
