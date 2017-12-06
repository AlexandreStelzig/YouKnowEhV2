package stelztech.youknowehv4.database.quizcard;

import java.util.List;

/**
 * Created by alex on 10/14/2017.
 */

public interface IQuizCardDao {
    QuizCard fetchQuizCardById(int cardId, int quizId);
    List<QuizCard> fetchQuizCardsByQuizId(int quizId);
    List<QuizCard> fetchPassedQuizCardsByQuizId(int quizId);
    boolean deleteQuizCard(int cardId, int quizId);
    int createQuizCard(int cardId, int quizId, String question, String answer, int position);
    boolean incrementNumberFailed(int cardId, int quizId);
    boolean markCardAsPassed(int cardId, int quizId);
    int fetchNumberQuizCardFromQuizId(int quizId);
}
