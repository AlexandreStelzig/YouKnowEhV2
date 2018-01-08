package stelztech.youknowehv4.database.quiz;

import java.util.List;

import stelztech.youknowehv4.database.profile.Profile;

/**
 * Created by alex on 10/14/2017.
 */

public interface IQuizDao {
    List<Quiz> fetchAllFinishedQuizzesForActiveProfile();
    Quiz fetchQuizById(int quizId);
    boolean deleteQuiz(int quizId);
    int createQuiz( Quiz.MODE mode, boolean reverse, boolean reviewOnly);
    int createQuiz( Quiz.MODE mode, boolean reverse, int nbPassed, int nbFailed, int nbSkipped, String dateCreate, String dateFinished, Quiz.STATE quizState);
    boolean markQuizAsQuizFinished(int quizId);
    boolean markQuizAsRoundFinished(int quizId);
    boolean markQuizAsActive(int quizId);
    boolean updateTotalQuizStats(int quizId, int totalPassed, int totalFailed, int totalSkipped);


}
