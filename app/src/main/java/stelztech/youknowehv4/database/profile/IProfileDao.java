package stelztech.youknowehv4.database.profile;

import java.util.List;

import stelztech.youknowehv4.database.card.Card;

/**
 * Created by alex on 10/14/2017.
 */

public interface IProfileDao {

    List<Profile> fetchAllProfiles();
    Profile fetchProfileById(long profileId);
    boolean deleteProfile(long profileId);
    long createProfile(String name);
    boolean updateProfile(long profileId, String name);
    boolean updateProfileQuestionLabel(long profileId, String questionLabel);
    boolean updateProfileAnswerLabel(long profileId, String answerLabel);
    boolean toggleAllowPracticeAll();
    boolean toggleDisplayNumDecksAllCards();
    boolean toggleDisplayNumDecksSpecificCard();

}
