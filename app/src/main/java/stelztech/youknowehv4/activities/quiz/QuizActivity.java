package stelztech.youknowehv4.activities.quiz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.components.DoubleProgressBar;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.quiz.Quiz;
import stelztech.youknowehv4.database.quizcard.QuizCard;
import stelztech.youknowehv4.utilities.BlurBuilder;
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
    protected boolean isOrientationReversed;
    protected Quiz.MODE quizMode;
    boolean intentContinue;

    protected boolean isFinished;

    protected int quizId;


    public static final String EXTRA_INTENT_CONTINUE = "quiz_continue";
    public static final String EXTRA_INTENT_TYPE = "quiz_type";
    public static final String EXTRA_INTENT_REVERSE = "quiz_is_reverse";

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
        initializeQuizCardData();
        initProgressBar();
    }

    protected void fetchExtraIntentInformation() {
        intentContinue = getIntent().getBooleanExtra(EXTRA_INTENT_CONTINUE, false);

        if (intentContinue) {
            quizId = Database.mProfileDao.fetchActiveQuizId();
            Quiz quiz = Database.mQuizDao.fetchQuizById(quizId);

            isOrientationReversed = quiz.isReverse();
            quizMode = quiz.getMode();


        } else {

            isOrientationReversed = getIntent().getBooleanExtra(EXTRA_INTENT_REVERSE, false);
            quizMode = Quiz.MODE.valueOf(getIntent().getStringExtra(EXTRA_INTENT_TYPE));

            quizId = Database.mProfileDao.fetchActiveQuizId();
        }


    }


    private void initializeQuizCardData() {
        // todo fetch real data

        quizCardList.clear();
        quizCardList = Database.mQuizCardDao.fetchQuizCardsByQuizId(quizId);


        currentCardPosition = 0;
        maximumNumQuestion = quizCardList.size();
    }

    private void repopulateQuizCardListWithWrongAnswers() {
        List<QuizCard> temp = new ArrayList<>();

        for (int counter = 0; counter < quizCardList.size(); counter++) {
            QuizCard quizCard = quizCardList.get(counter);
            if (!quizCard.isPassed()) {
                temp.add(quizCard);
            }
        }

        quizCardList = temp;
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
        quizCardList.get(currentCardPosition).setPassed(true);
        // todo database change
        updateProgressBar();
        showNextCard();
    }

    protected void cardFailed() {
        numFailed++;
        QuizCard quizCard = quizCardList.get(currentCardPosition);
        quizCard.setNumFailed(quizCard.getNumFailed() + 1);
        // todo database change
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

        replaceContainerWithLayout(R.layout.quiz_finished_container);

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

    protected void replaceContainerWithLayout(int layout){
        FrameLayout rl = (FrameLayout) findViewById(R.id.quiz_type_container);
        rl.removeAllViews();
        rl.addView(View.inflate(this, layout, null));
    }

    protected void replaceContainerWithQuizType(){
        switch (quizMode){
            case READING:
                replaceContainerWithLayout(R.layout.quiz_reading_container);
                break;
            case WRITING:
                replaceContainerWithLayout(R.layout.quiz_writing_container);
                break;
            case MULTIPLE_CHOICE:
                replaceContainerWithLayout(R.layout.quiz_multiple_choice_container);
                break;
        }
        initView();
    }

    public List<QuizCard> getQuizCardList() {
        return quizCardList;
    }

    protected abstract void showNextCardContainer();

    protected abstract void resetContainer();

    protected abstract void initView();

    public void repeatFailedCardsOnClick(View view) {
        repopulateQuizCardListWithWrongAnswers();
        if(quizCardList.isEmpty()){
            Toast.makeText(this, "No failed cards", Toast.LENGTH_SHORT).show();
        }else{
            replaceContainerWithQuizType();
            resetQuiz();
        }
    }

    public void exportFailedCardsToDeckOnClick(View view) {

    }

    public void finishOnClick(View view) {

    }
}
