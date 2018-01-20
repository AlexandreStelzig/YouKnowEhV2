package stelztech.youknowehv4.activities.quiz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import stelztech.youknowehv4.activities.quiz.export.QuizFailedCardsExportOptionsDialog;
import stelztech.youknowehv4.activities.quiz.export.QuizFailedQuizCardsDialog;
import stelztech.youknowehv4.components.CustomProgressDialog;
import stelztech.youknowehv4.components.CustomYesNoDialog;
import stelztech.youknowehv4.components.DoubleProgressBar;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.profile.Profile;
import stelztech.youknowehv4.database.quiz.Quiz;
import stelztech.youknowehv4.database.quizcard.QuizCard;
import stelztech.youknowehv4.manager.ThemeManager;
import stelztech.youknowehv4.utilities.BitmapUtilities;
import stelztech.youknowehv4.utilities.BlurBuilder;
import stelztech.youknowehv4.utilities.CardUtilities;
import stelztech.youknowehv4.utilities.QuizCardsUtilities;

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

        ThemeManager.getInstance().setApplicationTheme(this, true);
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
        Profile activeProfile = Database.mUserDao.fetchActiveProfile();

        try{
            imageView.setImageBitmap(BitmapUtilities.getBitmapFromFile(activeProfile.getProfileImagePath()));
        } catch (Exception e){
            imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.default1));
        }

        Bitmap bitmap = new BlurBuilder().blur(this, ((BitmapDrawable) imageView.getDrawable()).getBitmap());
        // change the background image
        imageView.setImageBitmap(bitmap);

        isFinished = false;
        initializeQuizCardData();
        initProgressBar();
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
            case R.id.action_quiz_done:
                Database.mQuizDao.markQuizAsRoundFinished(quizId);
                replaceContainerWithFinishedLayout();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        final MenuItem quizDone = menu.findItem(R.id.action_quiz_done);
        quizDone.setVisible(Database.mQuizDao.fetchQuizById(quizId).getState() == Quiz.STATE.ACTIVE);

        return super.onPrepareOptionsMenu(menu);
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

        Database.mQuizDao.markQuizAsRoundFinished(quizId);
        QuizCardsUtilities.setQuizStats(quizId);
        replaceContainerWithFinishedLayout();

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


    protected void replaceContainerWithLayout(int layout) {
        FrameLayout rl = (FrameLayout) findViewById(R.id.quiz_type_container);
        rl.removeAllViews();
        rl.addView(View.inflate(this, layout, null));
    }

    protected void replaceContainerWithFinishedLayout() {
        replaceContainerWithLayout(R.layout.quiz_finished_container);


        Button repeatButton = ((Button) findViewById(R.id.quiz_repeat_failed_cards));

        if (Database.mQuizCardDao.fetchUnansweredQuizCardsByQuizId(quizId).isEmpty()) {
            repeatButton.setText("Repeat Failed Cards");
            repeatButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_repeat_white_24dp, 0, 0, 0);
        } else {
            repeatButton.setText("Continue");
            repeatButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_forward_white_24dp, 0, 0, 0);
        }

        int remaining = maximumNumQuestion - numFailed - numPassed;


        repeatButton.setEnabled(numFailed != 0 || remaining != 0 && Database.mQuizDao.fetchQuizById(quizId).getState() == Quiz.STATE.FINISHED_ROUND);

        Button exportButton = ((Button) findViewById(R.id.quiz_export_failed_cards));

        exportButton.setEnabled(numFailed != 0);


        invalidateOptionsMenu();
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

        invalidateOptionsMenu();
        initView();
    }

    public List<QuizCard> getQuizCardList() {
        return quizCardList;
    }

    protected abstract void showNextCardContainer();

    protected abstract void resetContainer();

    protected abstract void initView();

    public void onRepeatFailedCardsButtonClick(View view) {


        if (Database.mQuizCardDao.fetchUnansweredQuizCardsByQuizId(quizId).isEmpty()) {
            if (quizMode == Quiz.MODE.MULTIPLE_CHOICE && Database.mQuizCardDao.fetchNumberFailedQuizCardFromQuizId(quizId) < 4) {
                Toast.makeText(this, "4 failed cards are required for MC quiz", Toast.LENGTH_SHORT).show();
            } else if (Database.mQuizCardDao.fetchNumberFailedQuizCardFromQuizId(quizId) == 0) {
                Toast.makeText(this, "No failed cards", Toast.LENGTH_SHORT).show();
            } else {
                resetQuizCardListAndReset();
                Database.mQuizDao.markQuizAsActive(quizId);
            }
        } else {
            replaceContainerWithQuizType();
            Database.mQuizDao.markQuizAsActive(quizId);
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
                        currentCardPosition = 0;
                        replaceContainerWithQuizType();
                        resetQuiz();
                    }
                });

            }
        };

        customProgressDialog.startDialog();
    }

    public void onExportFailedCardsToDeckButtonClick(View view) {


        final List<QuizCard> failedQuizCardList = Database.mQuizCardDao.fetchFailedQuizCardsByQuizId(quizId);


        QuizFailedQuizCardsDialog quizFailedQuizCardsDialog = new QuizFailedQuizCardsDialog() {

            @Override
            public void onCardsSelected(final boolean[] exportList) {
                QuizFailedCardsExportOptionsDialog quizFailedCardsExportOptionsDialog = new QuizFailedCardsExportOptionsDialog() {
                    @Override
                    public void addFailedQuizCardsToDeck(int deckId) {
                        int numberCards = 0;
                        for (int counter = 0; counter < exportList.length; counter++) {
                            if (exportList[counter]) {
                                Database.mCardDeckDao.createCardDeck(failedQuizCardList.get(counter).getCardId(), deckId);
                                numberCards++;
                            }
                        }
                        String deckName = Database.mDeckDao.fetchDeckById(deckId).getDeckName();
                        CardUtilities.mergeDuplicates(null, deckId);
                        Toast.makeText(QuizActivity.this, numberCards + " failed Cards quiz exported to Deck: '" + deckName + "'", Toast.LENGTH_SHORT).show();
                    }
                };
                quizFailedCardsExportOptionsDialog.showExportOptions(QuizActivity.this, QuizActivity.this, failedQuizCardList, exportList, this);
            }
        };


        boolean[] isExporting = new boolean[failedQuizCardList.size()];
        for (int counter = 0; counter < isExporting.length; counter++) {
            isExporting[counter] = true;
        }

        quizFailedQuizCardsDialog.showFailedQuizCards(failedQuizCardList, isExporting, QuizActivity.this);
    }

    public void onFinishButtonClick(View view) {


        final CustomYesNoDialog customYesNoDialog = new CustomYesNoDialog(this,
                "Finish Quiz", "Are you sure you want to finish the quiz?") {
            @Override
            protected void onNegativeButtonClick() {

            }

            @Override
            protected void onPositiveButtonClick() {
                finishQuiz();
            }
        };
        customYesNoDialog.show();
    }

    private void finishQuiz() {
        int numberOfCards = Database.mQuizCardDao.fetchNumberQuizCardFromQuizId(quizId);
        Database.mQuizDao.markQuizAsQuizFinished(quizId);

        CustomProgressDialog customProgressDialog = new CustomProgressDialog("Removing Quiz",
                numberOfCards, QuizActivity.this, this) {

            @Override
            public void loadInformation() {
                QuizCardsUtilities.setQuizStats(quizId);

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
                        finish();
                    }
                });

            }
        };

        customProgressDialog.startDialog();
    }
}