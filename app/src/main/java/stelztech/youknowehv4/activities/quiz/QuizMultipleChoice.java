package stelztech.youknowehv4.activities.quiz;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.Orientation;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.quiz.Quiz;
import stelztech.youknowehv4.database.quizcard.QuizCard;

/**
 * Created by alex on 2017-05-09.
 */

public class QuizMultipleChoice extends QuizActivity {

    private DiscreteScrollView scrollView;
    private RecyclerView.Adapter adapter;
    private List<Button> buttonList;


    private static final float SCROLLVIEW_TRANSFORM_SCALE = 0.8f;
    private static final int TRANSITION_TIME_MILLIS = 50;

    private boolean isAnimating;

    private int answerPosition;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isAnimating = false;

        getSupportActionBar().setTitle("MC Questions Quiz");

        Quiz.STATE quizState = Database.mQuizDao.fetchQuizById(quizId).getState();
        if (quizState == Quiz.STATE.FINISHED_ROUND ||
                Database.mQuizCardDao.fetchUnansweredQuizCardsByQuizId(quizId).isEmpty()) {
            replaceContainerWithFinishedLayout();
        } else {
            replaceContainerWithQuizType();
        }

        answerPosition = 0;

    }

    private void initScrollView() {
        scrollView.setOrientation(Orientation.HORIZONTAL);

        adapter = (new QuizMutlipleChoiceAdapter(this));

        scrollView.setAdapter(adapter);
        scrollView.setItemTransitionTimeMillis(TRANSITION_TIME_MILLIS);
        scrollView.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(SCROLLVIEW_TRANSFORM_SCALE)
                .build());
        scrollView.setSlideOnFling(false);

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true; // disables touch scrolling
            }
        });

        ((QuizMutlipleChoiceAdapter) adapter).getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        isAnimating = false;
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        isAnimating = true;
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        isAnimating = true;
                        break;
                }
            }
        });

        scrollView.scrollToPosition(currentCardPosition);
    }

    private void initButtons() {

        for (int i = 0; i < buttonList.size(); i++) {
            final int index = i;
            buttonList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isAnimating)
                        mcButtonClicked((String) buttonList.get(index).getText());
                }
            });
        }
        setButtonLabels();
    }

    private void mcButtonClicked(String buttonLabel) {
        String answerText;

        if (isOrientationReversed)
            answerText = quizCardList.get(currentCardPosition).getQuestion();
        else
            answerText = quizCardList.get(currentCardPosition).getAnswer();

        if (buttonLabel.equals(answerText)) {
            cardPassed();
        } else {
            cardFailed();
        }
    }

    private void setButtonLabels() {

        List<QuizCard> temp = new ArrayList<>(quizCardList);

        // random position for answer
        Random rand = new Random();
        answerPosition = rand.nextInt(4);

        if (isOrientationReversed)
            buttonList.get(answerPosition).setText(quizCardList.get(currentCardPosition).getQuestion());
        else
            buttonList.get(answerPosition).setText(quizCardList.get(currentCardPosition).getAnswer());

        temp.remove(currentCardPosition);

        // fetch 3 random cards
        for (int i = 0; i < buttonList.size(); i++) {
            if (i != answerPosition) {
                rand = new Random();
                int position = rand.nextInt(temp.size());

                if (isOrientationReversed)
                    buttonList.get(i).setText(temp.get(position).getQuestion());
                else
                    buttonList.get(i).setText(temp.get(position).getAnswer());
                temp.remove(position);
            }
        }

    }

    protected void showNextCardContainer() {
        scrollView.smoothScrollToPosition(currentCardPosition);
        setButtonLabels();
    }

    protected void resetContainer() {
        adapter.notifyDataSetChanged();
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollToPosition(0);
            }
        });
    }

    @Override
    protected void initView() {
        scrollView = (DiscreteScrollView) findViewById(R.id.quiz_mc_scrollview);
        Button answer1Button = (Button) findViewById(R.id.quiz_mc_answer1);
        Button answer2Button = (Button) findViewById(R.id.quiz_mc_answer2);
        Button answer3Button = (Button) findViewById(R.id.quiz_mc_answer3);
        Button answer4Button = (Button) findViewById(R.id.quiz_mc_answer4);


        buttonList = new ArrayList<>();
        buttonList.add(answer1Button);
        buttonList.add(answer2Button);
        buttonList.add(answer3Button);
        buttonList.add(answer4Button);

        initScrollView();
        initButtons();
    }


}
