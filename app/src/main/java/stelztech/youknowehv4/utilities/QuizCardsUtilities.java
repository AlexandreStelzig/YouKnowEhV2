package stelztech.youknowehv4.utilities;

import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.quiz.Quiz;

/**
 * Created by alex on 12/10/2017.
 */

public class QuizCardsUtilities {

    public static void setQuizStats(int quizId) {
        Quiz quiz = Database.mQuizDao.fetchQuizById(quizId);

        int total = quiz.getTotalPassed() + quiz.getTotalFailed() + quiz.getTotalSkipped();

        if (total == 0) {

            int passed = Database.mQuizCardDao.fetchNumberPassedQuizCardFromQuizId(quizId);
            int failed = Database.mQuizCardDao.fetchNumberFailedQuizCardFromQuizId(quizId);
            int skipped = Database.mQuizCardDao.fetchNumberQuizCardFromQuizId(quizId) - (passed + failed);

            Database.mQuizDao.updateTotalQuizStats(quizId, passed, failed, skipped);

        }
    }
}
