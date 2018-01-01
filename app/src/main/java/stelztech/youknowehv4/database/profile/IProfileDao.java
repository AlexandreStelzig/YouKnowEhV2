package stelztech.youknowehv4.database.profile;

import java.util.Date;
import java.util.List;

import stelztech.youknowehv4.manager.ThemeManager;

/**
 * Created by alex on 10/14/2017.
 */

public interface IProfileDao {

    List<Profile> fetchAllProfiles();
    Profile fetchProfileById(int profileId);
    boolean deleteProfile(int profileId);
    int createProfile(String name);
    int createProfile(String name, String frontLabel, String backLabel);
    boolean updateProfileName(int profileId, String name);
    boolean updateProfileFrontLabel(int profileId, String questionLabel);
    boolean updateProfileBackLabel(int profileId, String answerLabel);
    boolean changeProfileColor(int profileId, ThemeManager.THEME_COLORS themeColor);
    boolean updateLastTimeOpened(int profileId, String lastTimeOpenedDate);
    boolean updateProfileImage(int profileId, int picture);
    int fetchActiveQuizId();
    boolean setActiveQuizId(int profileId, int quizId);
    boolean toggleDisplayNumDecksAllCards();
    boolean toggleDisplayNumDecksSpecificCard();
    boolean toggleAllowSearchOnQueryChanged();
    boolean updateDefaultSortPosition(int position);
    boolean updateQuickToggleReviewHours(int hours);

}
