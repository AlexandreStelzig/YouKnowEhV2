package stelztech.youknowehv4.activities.quiz;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.Orientation;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.quiz.Quiz;

/**
 * Created by alex on 2017-05-09.
 */

public class QuizWriting extends QuizActivity {

    private TextView questionTextView;
    private EditText answerEditText;
    private Button passButton;
    private Button verifyButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Writing Quiz");




        Quiz.STATE quizState = Database.mQuizDao.fetchQuizById(quizId).getState();
        if (quizState == Quiz.STATE.FINISHED_ROUND ||
                Database.mQuizCardDao.fetchUnansweredQuizCardsByQuizId(quizId).isEmpty()) {
            replaceContainerWithFinishedLayout();
        } else {
            replaceContainerWithQuizType();
        }



    }

    private void setQuestionText() {
        if (isOrientationReversed)
            questionTextView.setText(quizCardList.get(currentCardPosition).getAnswer());
        else
            questionTextView.setText(quizCardList.get(currentCardPosition).getQuestion());

    }
    private void initButtons() {
        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardPassed();
            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAnswer();
            }
        });
    }

    private void validateAnswer() {
        String providedAnswer = String.valueOf(answerEditText.getText());

        if(providedAnswer.trim().isEmpty()){
            Toast.makeText(this, "Answer cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String answerText;

        if (isOrientationReversed)
            answerText = quizCardList.get(currentCardPosition).getQuestion();
        else
            answerText = quizCardList.get(currentCardPosition).getAnswer();




        boolean passed = answerText.toLowerCase().equals(providedAnswer.toLowerCase());

        if(passed){
            Toast.makeText(this, "PASSED", Toast.LENGTH_SHORT).show();
            cardPassed();
        }else{
            String diff = StringUtils.difference(providedAnswer, answerText);
            Toast.makeText(this, "FAILED: " + answerText + " != " + providedAnswer, Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "Diff: " + diff, Toast.LENGTH_SHORT).show();
            cardFailed();
        }
    }

    private void resetAnswerEditText(){
        answerEditText.setText("");
        answerEditText.setFocusableInTouchMode(true);
        answerEditText.requestFocus();
    }

    protected void showNextCardContainer() {
        setQuestionText();
        resetAnswerEditText();
    }

    protected void resetContainer() {
        setQuestionText();
        resetAnswerEditText();
    }

    @Override
    protected void initView() {
        questionTextView = (TextView) findViewById(R.id.quiz_writing_question_text_view);
        answerEditText = (EditText) findViewById(R.id.quiz_writing_answer_edit_view);
        passButton = (Button) findViewById(R.id.quiz_writing_pass_button);
        verifyButton = (Button) findViewById(R.id.quiz_writing_verify_button);

        answerEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId==EditorInfo.IME_ACTION_DONE){
                    validateAnswer();
                }
                return true;
            }
        });

        initButtons();

        setQuestionText();
        resetAnswerEditText();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        answerEditText.requestFocus();

    }


}
