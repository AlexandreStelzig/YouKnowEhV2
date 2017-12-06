package stelztech.youknowehv4.activities.quiz;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import stelztech.youknowehv4.R;

/**
 * Created by alex on 2017-05-09.
 */

public class QuizMultipleChoice extends QuizActivity {


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
