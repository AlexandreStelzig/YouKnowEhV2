package stelztech.youknowehv4.activities.quiz;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.Orientation;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import stelztech.youknowehv4.R;

/**
 * Created by alex on 2017-05-09.
 */

public class QuizReading extends QuizActivity {

    // flow
    private Button showButton;
    private Button passButton;
    private Button failButton;

    private DiscreteScrollView scrollView;
    RecyclerView.Adapter adapter;

    private static final float SCROLLVIEW_TRANSFORM_SCALE = 0.8f;
    private static final int TRANSITION_TIME_MILLIS = 50;

    private boolean isShowingBackCard;

    private boolean isAnimating;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isAnimating = isShowingBackCard = false;

        replaceContainerWithQuizType();

        getSupportActionBar().setTitle("Reading Quiz");

    }

    private void initScrollView() {

        scrollView.setOrientation(Orientation.HORIZONTAL);

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

        ((QuizReadingAdapter) adapter).getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
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
    }

    private void initButtons() {
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAnimating && !isFinished)
                    flipCard();
            }
        });
        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAnimating && !isFinished)
                    cardPassed();
            }
        });

        failButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAnimating && !isFinished)
                    cardFailed();
            }
        });

    }

    private void flipCard() {
        final View view = ((QuizReadingAdapter) adapter).getRecyclerView().findViewHolderForAdapterPosition(currentCardPosition).itemView;

        Animation animationOut = AnimationUtils.loadAnimation(this, R.anim.shrink_to_middle);
        final Animation animationIn = AnimationUtils.loadAnimation(this, R.anim.grow_from_middle);

        animationIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isShowingBackCard) {
                    isShowingBackCard = false;
                    if (isOrientationReversed)
                        ((TextView) view.findViewById(R.id.quiz_reading_card_front)).setText(quizCardList.get(currentCardPosition).getAnswer());
                    else
                        ((TextView) view.findViewById(R.id.quiz_reading_card_front)).setText(quizCardList.get(currentCardPosition).getQuestion());
                } else {
                    isShowingBackCard = true;
                    if (isOrientationReversed)
                        ((TextView) view.findViewById(R.id.quiz_reading_card_front)).setText(quizCardList.get(currentCardPosition).getQuestion());
                    else
                        ((TextView) view.findViewById(R.id.quiz_reading_card_front)).setText(quizCardList.get(currentCardPosition).getAnswer());
                }
                view.startAnimation(animationIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });


        view.startAnimation(animationOut);

    }


    protected void showNextCardContainer() {
        scrollView.smoothScrollToPosition(currentCardPosition);
        isShowingBackCard = false;
    }

    protected void resetContainer() {
        isShowingBackCard = false;
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
        showButton = (Button) findViewById(R.id.quiz_show_button);
        passButton = (Button) findViewById(R.id.quiz_pass_button);
        failButton = (Button) findViewById(R.id.quiz_fail_button);
        scrollView = (DiscreteScrollView) findViewById(R.id.quiz_reading_scrollview);


        initScrollView();
        initButtons();
    }
}
