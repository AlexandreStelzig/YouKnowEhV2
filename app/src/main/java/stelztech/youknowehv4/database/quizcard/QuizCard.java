package stelztech.youknowehv4.database.quizcard;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class QuizCard {

    public enum QUIZ_CARD_STATE{
        PASSED,
        FAILED,
        UNANSWERED
    }

    private int quizId;
    private int cardId;
    private String question;
    private String answer;
    private QUIZ_CARD_STATE quizCardState;
    private int numFailed;
    private int position;

    public QuizCard(int quizId, int cardId, String question, String answer, QUIZ_CARD_STATE quizCardState, int numFailed, int position) {
        this.quizId = quizId;
        this.cardId = cardId;
        this.question = question;
        this.answer = answer;
        this.quizCardState = quizCardState;
        this.numFailed = numFailed;
        this.position = position;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public QUIZ_CARD_STATE getQuizCardState() {
        return quizCardState;
    }

    public void setQuizCardState(QUIZ_CARD_STATE quizCardState) {
        this.quizCardState = quizCardState;
    }

    public int getNumFailed() {
        return numFailed;
    }

    public void setNumFailed(int numFailed) {
        this.numFailed = numFailed;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
