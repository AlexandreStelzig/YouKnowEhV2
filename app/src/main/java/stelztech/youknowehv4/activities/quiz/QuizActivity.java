package stelztech.youknowehv4.activities.quiz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.components.DoubleProgressBar;
import stelztech.youknowehv4.database.quiz.Quiz;
import stelztech.youknowehv4.database.quizcard.QuizCard;
import stelztech.youknowehv4.helper.BlurBuilder;
import stelztech.youknowehv4.manager.ThemeManager;

/**
 * Created by alex on 2017-05-09.
 */

public abstract class QuizActivity extends AppCompatActivity {


    // progress
    private DoubleProgressBar progressBar;
    private TextView passText;
    private TextView failText;
    private TextView remainingText;
    protected int numPassed;
    protected int numFailed;
    protected int totalPassedFailed;
    protected int maximumNumQuestion;

    // questions
    protected List<QuizCard> quizCardList = new ArrayList<>();
    protected int currentCardPosition;

    // quiz variables
    protected boolean isReviewCardsOnly;
    protected boolean isOrientationReversed;
    protected ArrayList<Integer> deckIdList = new ArrayList<>();
    protected Quiz.MODE quizMode;

    protected boolean isFinished;


    public static final String EXTRA_INTENT_CONTINUE = "quiz_continue";
    public static final String EXTRA_INTENT_TYPE = "quiz_type";
    public static final String EXTRA_INTENT_REVIEW_ONLY = "quiz_is_review_only";
    public static final String EXTRA_INTENT_REVERSE = "quiz_is_reverse";
    public static final String EXTRA_INTENT_DECKS = "quiz_decks";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fetchExtraIntentInformation();

        setTheme(ThemeManager.getInstance().getCurrentAppThemeValue());
        setContentView(R.layout.activity_quiz);

        // intro animation
        overridePendingTransition(R.anim.enter, R.anim.exit);

        // set back button instead of drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // init components
        passText = (TextView) findViewById(R.id.quiz_progress_bar_pass_text);
        failText = (TextView) findViewById(R.id.quiz_progress_bar_fail_text);
        remainingText = (TextView) findViewById(R.id.quiz_progress_bar_remaining_text);

        ImageView imageView = (ImageView) findViewById(R.id.quiz_background);
        Bitmap bitmap = new BlurBuilder().blur(this, ((BitmapDrawable) imageView.getDrawable()).getBitmap());
        // change the background image
        imageView.setImageBitmap(bitmap);

        isFinished = false;
        initializeQuizVariables();
        initializeQuizCardData();
        initProgressBar();
    }

    protected void fetchExtraIntentInformation() {
        boolean intentContinue = getIntent().getBooleanExtra(EXTRA_INTENT_CONTINUE, false);

        if (intentContinue) {

        } else {

            isReviewCardsOnly = getIntent().getBooleanExtra(EXTRA_INTENT_REVIEW_ONLY, false);
            isOrientationReversed = getIntent().getBooleanExtra(EXTRA_INTENT_REVERSE, false);

            deckIdList = getIntent().getIntegerArrayListExtra(EXTRA_INTENT_DECKS);

            quizMode = Quiz.MODE.valueOf(getIntent().getStringExtra(EXTRA_INTENT_TYPE));

            createNewQuizCard();
        }

    }

    private void createNewQuizCard() {

    }

    private void initializeQuizVariables() {
        // todo fetch real data
        isReviewCardsOnly = false;
    }

    private void initializeQuizCardData() {
        // todo fetch real data

        quizCardList.clear();
        quizCardList.add(new QuizCard(0, 0, "question 1", "answer 1", false, 0, 0));
        quizCardList.add(new QuizCard(0, 0, "question 2", "answer 2", false, 0, 0));
        quizCardList.add(new QuizCard(0, 0, "question 3", "answer 3", false, 0, 0));
        quizCardList.add(new QuizCard(0, 0, "question 4", "answer 4", false, 0, 0));
        quizCardList.add(new QuizCard(0, 0, "question 5", "answer 5", false, 0, 0));
        quizCardList.add(new QuizCard(0, 0, "question 6", "answer 6", false, 0, 0));
        quizCardList.add(new QuizCard(0, 0, "question 7", "answer 7", false, 0, 0));


        currentCardPosition = 0;
        maximumNumQuestion = quizCardList.size();
    }

    private void repopulateQuizCardListWithWrongAnswers() {
        // todo fetch real data

        quizCardList.clear();
        quizCardList.add(new QuizCard(0, 0, "question 1", "answer 1", false, 0, 0));
        quizCardList.add(new QuizCard(0, 0, "question 2", "answer 2", false, 0, 0));
        quizCardList.add(new QuizCard(0, 0, "question 3", "answer 3", false, 0, 0));

        maximumNumQuestion = quizCardList.size();
    }


    private void initProgressBar() {
        totalPassedFailed = numPassed = numFailed = 0;
        progressBar = (DoubleProgressBar) findViewById(R.id.quiz_progress_bar);
        progressBar.setMax(maximumNumQuestion);
        updateProgressBar();
    }

    protected void cardPassed() {
        numPassed++;
        updateProgressBar();
        showNextCard();
    }

    protected void cardFailed() {
        numFailed++;
        updateProgressBar();
        showNextCard();
    }

    protected void showNextCard() {
        currentCardPosition++;

        if (currentCardPosition >= maximumNumQuestion) {
            loopCompleted();
        } else {
            showNextCardContainer();
        }

    }


    private void loopCompleted() {


//            repopulateQuizCardListWithWrongAnswers();
//            resetQuiz();

        // todo finished state
        isFinished = true;

    }

    protected void resetQuiz() {
        currentCardPosition = 0;
        resetProgressBar();
        resetContainer();
    }

    private void resetProgressBar() {
        totalPassedFailed = numPassed = numFailed = 0;
        progressBar.setMax(maximumNumQuestion);
        updateProgressBar();
    }

    protected void updateProgressBar() {

        totalPassedFailed = numPassed + numFailed;

        progressBar.setProgress(numPassed);
        progressBar.setSecondaryProgress(totalPassedFailed);

        updateProgressBarText();
    }

    private void updateProgressBarText() {

        int remaining = maximumNumQuestion - numFailed - numPassed;

        passText.setText(numPassed + " PASSED");
        failText.setText(numFailed + " FAILED");
        remainingText.setText(remaining + " REMAINING");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_quiz, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        switch (id) {

            // back button
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public List<QuizCard> getQuizCardList() {
        return quizCardList;
    }

    protected abstract void showNextCardContainer();

    protected abstract void resetContainer();

}
