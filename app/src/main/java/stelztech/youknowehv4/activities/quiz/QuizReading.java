package stelztech.youknowehv4.activities.quiz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.Orientation;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activities.profilepicker.ProfilePickerCardAdapter;
import stelztech.youknowehv4.database.quizcard.QuizCard;
import stelztech.youknowehv4.manager.ThemeManager;

/**
 * Created by alex on 2017-05-09.
 */

public class QuizReading extends QuizActivity implements DiscreteScrollView.OnItemChangedListener{

    // flow
    private Button showButton;
    private Button passButton;
    private Button failButton;

    private DiscreteScrollView scrollView;
    RecyclerView.Adapter adapter;

    private static final float SCROLLVIEW_TRANSFORM_SCALE = 0.8f;
    private static final int TRANSITION_TIME_MILLIS = 200;

    protected void onCreate(Bundle savedInstanceState) {


        setTheme(ThemeManager.getInstance().getCurrentAppThemeValue());
        setContentView(R.layout.activity_quiz_reading);

        getSupportActionBar().setTitle("Reading Quiz");

        showButton = (Button) findViewById(R.id.quiz_show_button);
        passButton = (Button) findViewById(R.id.quiz_pass_button);
        failButton = (Button) findViewById(R.id.quiz_fail_button);


        initButtons();

        quizCardList.add(new QuizCard(0,0,false,0,0));
        quizCardList.add(new QuizCard(0,0,false,0,0));
        quizCardList.add(new QuizCard(0,0,false,0,0));
        quizCardList.add(new QuizCard(0,0,false,0,0));
        quizCardList.add(new QuizCard(0,0,false,0,0));
        quizCardList.add(new QuizCard(0,0,false,0,0));
        quizCardList.add(new QuizCard(0,0,false,0,0));
        quizCardList.add(new QuizCard(0,0,false,0,0));
        quizCardList.add(new QuizCard(0,0,false,0,0));
        quizCardList.add(new QuizCard(0,0,false,0,0));
        quizCardList.add(new QuizCard(0,0,false,0,0));
        quizCardList.add(new QuizCard(0,0,false,0,0));

        scrollView = (DiscreteScrollView) findViewById(R.id.quiz_reading_scrollview);

        scrollView.setOrientation(Orientation.HORIZONTAL);
        scrollView.addOnItemChangedListener(this);

        adapter = (new QuizReadingAdapter(this));

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

        super.onCreate(savedInstanceState);

    }


    private void initButtons() {
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipCard();
            }
        });
        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numPassed++;
                updateProgressBar();
                showNextCard();
            }
        });

        failButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numFailed++;
                updateProgressBar();
                showNextCard();
            }
        });

    }

    private void flipCard() {
    }

    private void showNextCard(){

        currentCardPosition++;
        scrollView.smoothScrollToPosition(currentCardPosition);
    }



    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {

    }
}
