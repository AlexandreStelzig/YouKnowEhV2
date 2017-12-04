package stelztech.youknowehv4.database.quiz;

import java.util.List;

import stelztech.youknowehv4.database.profile.Profile;

/**
 * Created by alex on 10/14/2017.
 */

public interface IQuizDao {
    List<Quiz> fetchAllQuizzes();
    Quiz fetchQuizById(int quizId);
    boolean deleteQuiz(int quizId);
    int createQuiz( Quiz.MODE mode, boolean reverse, boolean reviewOnly);
    boolean updateDateFinished(int quizId, String date);
    boolean updateTotalQuizStats(int quizId, int totalPassed, int totalFailed, int totalSkipped);


}
