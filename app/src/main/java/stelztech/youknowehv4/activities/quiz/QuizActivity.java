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
import java.util.Collections;
import java.util.List;
import java.util.Random;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.components.CustomProgressDialog;
import stelztech.youknowehv4.components.DoubleProgressBar;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.profile.Profile;
import stelztech.youknowehv4.database.quiz.Quiz;
import stelztech.youknowehv4.database.quizcard.QuizCard;
import stelztech.youknowehv4.fragments.quiz.QuizFragment;
import stelztech.youknowehv4.manager.ThemeManager;
import stelztech.youknowehv4.utilities.BlurBuilder;

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
        quizCardList.clear();
        if (intentContinue) {
            quizCardList = Database.mQuizCardDao.fetchQuizCardsByQuizId(quizId);
            numPassed = Database.mQuizCardDao.fetchNumberPassedQuizCardFromQuizId(quizId);
            numFailed = Database.mQuizCardDao.fetchNumberFailedQuizCardFromQuizId(quizId);
            currentCardPosition = numPassed + numFailed;

        } else {
            quizCardList = Database.mQuizCardDao.fetchQuizCardsByQuizId(quizId);
            currentCardPosition = 0;
            numPassed = numFailed = 0;
        }

        maximumNumQuestion = quizCardList.size();
    }

    private void repopulateQuizCardListWithWrongAnswers() {
        quizCardList.clear();
        quizCardList = Database.mQuizCardDao.fetchQuizCardsByQuizId(quizId);

        numPassed = numFailed = 0;

        maximumNumQuestion = quizCardList.size();
    }


    private void initProgressBar() {
        progressBar = (DoubleProgressBar) findViewById(R.id.quiz_progress_bar);
        progressBar.setMax(maximumNumQuestion);
        updateProgressBar();
    }

    protected void cardPassed() {
        numPassed++;
        QuizCard quizCard = quizCardList.get(currentCardPosition);
        Database.mQuizCardDao.markCardAsPassed(quizCard.getCardId(), quizCard.getQuizId());
        quizCardList.get(currentCardPosition).setQuizCardState(QuizCard.QUIZ_CARD_STATE.PASSED);
        // todo database change
        updateProgressBar();
        showNextCard();
    }

    protected void cardFailed() {
        numFailed++;
        QuizCard quizCard = quizCardList.get(currentCardPosition);
        Database.mQuizCardDao.incrementNumberFailed(quizCard.getCardId(), quizCard.getQuizId());
        Database.mQuizCardDao.markCardAsFailed(quizCard.getCardId(), quizCard.getQuizId());
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

        replaceContainerWithFinishedLayout();

        Button repeatButton = ((Button) findViewById(R.id.quiz_repeat_failed_cards));

        repeatButton.setEnabled(Database.mQuizCardDao.fetchNumberFailedQuizCardFromQuizId(quizId) != 0);


    }

    protected void resetQuiz() {
        currentCardPosition = 0;
        resetProgressBar();
        resetContainer();
    }

    private void resetProgressBar() {
        numPassed = numFailed = 0;
        progressBar.setMax(maximumNumQuestion);
        updateProgressBar();
    }

    protected void updateProgressBar() {

        int totalPassedFailed = numPassed + numFailed;

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

    protected void replaceContainerWithLayout(int layout) {
        FrameLayout rl = (FrameLayout) findViewById(R.id.quiz_type_container);
        rl.removeAllViews();
        rl.addView(View.inflate(this, layout, null));
    }

    protected void replaceContainerWithFinishedLayout() {
        replaceContainerWithLayout(R.layout.quiz_finished_container);
    }

    protected void replaceContainerWithQuizType() {


        switch (quizMode) {
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
        if (Database.mQuizCardDao.fetchNumberFailedQuizCardFromQuizId(quizId) == 0) {
            Toast.makeText(this, "No failed cards", Toast.LENGTH_SHORT).show();
        } else {
            resetQuizCardListAndReset();
        }
    }

    private void resetQuizCardListAndReset() {

        int numberPassedCards = Database.mQuizCardDao.
                fetchNumberPassedQuizCardFromQuizId(quizId);

        CustomProgressDialog customProgressDialog = new CustomProgressDialog("Removing Passed Cards",
                numberPassedCards, QuizActivity.this, this) {

            @Override
            public void loadInformation() {
                // passed cards
                List<QuizCard> passedQuizCardList = Database.mQuizCardDao.
                        fetchPassedQuizCardsByQuizId(Database.mProfileDao.fetchActiveQuizId());

                int position = 0;
                for (QuizCard quizCard : passedQuizCardList) {
                    Database.mQuizCardDao.deleteQuizCard(quizCard.getCardId(), quizCard.getQuizId());
                    position++;
                    setDialogProgress(position);
                }


                // failed cards
                setDialogTitle("Preparing Failed Cards");

                List<QuizCard> failedQuizCardList = Database.mQuizCardDao.
                        fetchFailedQuizCardsByQuizId(Database.mProfileDao.fetchActiveQuizId());

                setDialogMax(failedQuizCardList.size());
                setDialogProgress(0);
                long seed = System.nanoTime();
                Collections.shuffle(failedQuizCardList, new Random(seed));

                position = 0;
                // todo add tracker for dialog, randomize order
                for (QuizCard quizCard : failedQuizCardList) {
                    Database.mQuizCardDao.markCardAsUnanswered(quizCard.getCardId(), quizCard.getQuizId());
                    Database.mQuizCardDao.updateQuizCardPosition(quizCard.getCardId(), quizCard.getQuizId(), position);
                    position++;
                    setDialogProgress(position);
                }


                repopulateQuizCardListWithWrongAnswers();
            }

            @Override
            public void informationLoaded() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        dismiss();
                        replaceContainerWithQuizType();
                        resetQuiz();
                    }
                });

            }
        };

        customProgressDialog.startDialog();
    }

    public void exportFailedCardsToDeckOnClick(View view) {
        // todo implement this
        Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show();
    }

    public void finishOnClick(View view) {
        int numberOfCards = Database.mQuizCardDao.fetchNumberQuizCardFromQuizId(quizId);


        CustomProgressDialog customProgressDialog = new CustomProgressDialog("Removing Quiz",
                numberOfCards, QuizActivity.this, this) {

            @Override
            public void loadInformation() {
                List<QuizCard> quizCardList = Database.mQuizCardDao.
                        fetchQuizCardsByQuizId(Database.mProfileDao.fetchActiveQuizId());

                int position = 0;
                for (QuizCard quizCard : quizCardList) {
                    Database.mQuizCardDao.deleteQuizCard(quizCard.getCardId(), quizCard.getQuizId());
                    position++;
                    setDialogProgress(position);
                }

                Database.mProfileDao.setActiveQuizId(Database.mUserDao.fetchActiveProfile().getProfileId(),
                        Profile.NO_QUIZ);

            }

            @Override
            public void informationLoaded() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        finish();
                    }
                });

            }
        };

        customProgressDialog.startDialog();
    }
}
