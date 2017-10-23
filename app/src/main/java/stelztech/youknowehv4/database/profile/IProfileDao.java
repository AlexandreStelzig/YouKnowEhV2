package stelztech.youknowehv4.database.profile;

import java.util.List;

import stelztech.youknowehv4.database.card.Card;

/**
 * Created by alex on 10/14/2017.
 */

public interface IProfileDao {

    List<Profile> fetchAllProfiles();
    Profile fetchProfileFromId(int profileId);
    boolean deleteProfile(int profileId);
    int createProfile(String name);
    boolean updateProfile(int profileId, String name);
    boolean updateProfileQuestionLabel(int profileId, String questionLabel);
    boolean updateProfileAnswerLabel(int profileId, String answerLabel);
    boolean toggleAllowPracticeAll();
    boolean toggleDisplayNumDecksAllCards();
    boolean toggleDisplayNumDecksSpecificCard();

}