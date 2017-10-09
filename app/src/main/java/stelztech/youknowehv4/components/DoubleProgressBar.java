package stelztech.youknowehv4.components;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Created by alex on 10/7/2017.
 */

public class DoubleProgressBar extends ProgressBar {

    private Paint progressBarPaint;

    public DoubleProgressBar(Context context) {
        super(context);
        progressBarPaint = new Paint();
        progressBarPaint.setColor(Color.BLACK);
    }

    public DoubleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        progressBarPaint = new Paint();
        progressBarPaint.setColor(Color.BLACK);
        setMax(30);
        setProgress(12);
        setSecondaryProgress(20);

    }
}
