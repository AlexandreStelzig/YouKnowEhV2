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

public class QuizWriting extends QuizActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        replaceContainerWithQuizType();

        getSupportActionBar().setTitle("Writing Quiz");


    }



    protected void showNextCardContainer() {

    }

    protected void resetContainer() {

    }

    @Override
    protected void initView() {

    }
}
