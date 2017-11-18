package stelztech.youknowehv4.database.quizcard;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class QuizCard {

    private int quizId;
    private int cardId;
    private boolean passed;
    private int numFailed;
    private int position;

    public QuizCard(int quizId, int cardId, boolean passed, int numFailed, int position) {
        this.quizId = quizId;
        this.cardId = cardId;
        this.passed = passed;
        this.numFailed = numFailed;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
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

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public int getNumFailed() {
        return numFailed;
    }

    public void setNumFailed(int numFailed) {
        this.numFailed = numFailed;
    }
}
