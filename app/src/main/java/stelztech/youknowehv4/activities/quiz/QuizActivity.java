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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.components.DoubleProgressBar;
import stelztech.youknowehv4.database.card.Card;
import stelztech.youknowehv4.database.quizcard.QuizCard;
import stelztech.youknowehv4.helper.BlurBuilder;
import stelztech.youknowehv4.manager.ThemeManager;

/**
 * Created by alex on 2017-05-09.
 */

public class QuizActivity extends AppCompatActivity {


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


    public QuizActivity(){
        maximumNumQuestion = 10;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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
        Bitmap bitmap = new BlurBuilder().blur(this, ((BitmapDrawable)imageView.getDrawable()).getBitmap());
        // change the background image
        imageView.setImageBitmap(bitmap);


        currentCardPosition = 0;

        initProgressBar();
    }


    private void initProgressBar() {
        totalPassedFailed = numPassed = numFailed = 0;
        progressBar = (DoubleProgressBar) findViewById(R.id.quiz_progress_bar);
        progressBar.setMax(maximumNumQuestion);
        updateProgressBar();
    }




    private void resetProgressBar() {
        totalPassedFailed = numPassed = numFailed = 0;
        updateProgressBar();
    }

    protected void updateProgressBar() {

        totalPassedFailed = numPassed + numFailed;


        progressBar.setProgress(numPassed);
        progressBar.setSecondaryProgress(totalPassedFailed);


        if (totalPassedFailed == maximumNumQuestion) {

        } else if (totalPassedFailed == 0) {
            // reset
        }

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
}
