package stelztech.youknowehv4.database.quizcard;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class QuizCard {

    private long quizId;
    private long cardId;
    private boolean passed;
    private int numFailed;
    private int position;

    public QuizCard(long quizId, long cardId, boolean passed, int numFailed, int position) {
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

    public long getQuizId() {
        return quizId;
    }

    public void setQuizId(long quizId) {
        this.quizId = quizId;
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
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
