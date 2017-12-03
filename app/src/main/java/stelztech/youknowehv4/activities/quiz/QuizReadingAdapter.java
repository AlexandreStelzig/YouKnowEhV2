package stelztech.youknowehv4.activities.quiz;

import android.content.Intent;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activities.profilepicker.ProfilePickerActivity;

/**
 * Created by alex on 12/2/2017.
 */

public class QuizReadingAdapter extends RecyclerView.Adapter<QuizReadingAdapter.ViewHolder>  {

    private RecyclerView parentRecycler;
    private int itemHeight;
    private int itemWidth;

    private QuizReading quizReading;

    private final static float CARD_WIDTH_SCALE = 0.75f;
    private final static float CARD_HEIGHT_SCALE = 0.55f;

    public QuizReadingAdapter(QuizReading quizReading ) {
        this.quizReading = quizReading;

        Point windowDimensions = new Point();
        quizReading.getWindowManager().getDefaultDisplay().getSize(windowDimensions);
        itemHeight = Math.round(windowDimensions.y * CARD_HEIGHT_SCALE);
        itemWidth = Math.round(windowDimensions.x * CARD_WIDTH_SCALE);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        parentRecycler = recyclerView;
    }

    @Override
    public QuizReadingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.custom_quiz_reading_card, parent, false);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                itemWidth,
                itemHeight);
        v.setLayoutParams(params);
        return new QuizReadingAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(QuizReadingAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return quizReading.getQuizCardList().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        ViewHolder(View itemView) {
            super(itemView);

        }

    }
}
