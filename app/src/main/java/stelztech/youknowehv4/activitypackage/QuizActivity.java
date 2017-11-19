package stelztech.youknowehv4.activitypackage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.components.DoubleProgressBar;

/**
 * Created by alex on 2017-05-09.
 */

public class QuizActivity extends AppCompatActivity {


    // progress
    private DoubleProgressBar progressBar;
    private TextView passText;
    private TextView failText;
    private TextView remainingText;
    int numPassed;
    int numFailed;
    int totalPassedFailed;
    int maximumNumQuestion;

    // flow
    private Button showButton;
    private Button passButton;
    private Button failButton;
    private LinearLayout showLayout;
    private LinearLayout passFailLayout;


    public QuizActivity(){
        maximumNumQuestion = 10;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // intro animation
        overridePendingTransition(R.anim.enter, R.anim.exit);

        setContentView(R.layout.activity_quiz);

        getSupportActionBar().setTitle("Quiz");
        // set back button instead of drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // init components
        showButton = (Button) findViewById(R.id.quiz_show_button);
        passButton = (Button) findViewById(R.id.quiz_pass_button);
        failButton = (Button) findViewById(R.id.quiz_fail_button);
        showLayout = (LinearLayout) findViewById(R.id.quiz_show_layout);
        passFailLayout = (LinearLayout) findViewById(R.id.quiz_pass_fail_layout);
        passText = (TextView) findViewById(R.id.quiz_progress_bar_pass_text);
        failText = (TextView) findViewById(R.id.quiz_progress_bar_fail_text);
        remainingText = (TextView) findViewById(R.id.quiz_progress_bar_remaining_text);

        initProgressBar();
        initButtons();
    }


    private void initProgressBar() {
        totalPassedFailed = numPassed = numFailed = 0;
        progressBar = (DoubleProgressBar) findViewById(R.id.quiz_progress_bar);
        progressBar.setMax(maximumNumQuestion);
        updateProgressBar();
    }


    private void initButtons() {
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setShowLayoutVisible(false);
            }
        });
        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numPassed++;
                setShowLayoutVisible(true);
                updateProgressBar();
            }
        });

        failButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numFailed++;
                setShowLayoutVisible(true);
                updateProgressBar();
            }
        });

    }

    private void resetProgressBar() {
        totalPassedFailed = numPassed = numFailed = 0;
        updateProgressBar();
    }

    private void updateProgressBar() {

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


    private void setShowLayoutVisible(boolean visible) {
        if (visible) {
            passFailLayout.setVisibility(View.GONE);
            showLayout.setVisibility(View.VISIBLE);
        } else {
            showLayout.setVisibility(View.GONE);
            passFailLayout.setVisibility(View.VISIBLE);
        }
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
}
