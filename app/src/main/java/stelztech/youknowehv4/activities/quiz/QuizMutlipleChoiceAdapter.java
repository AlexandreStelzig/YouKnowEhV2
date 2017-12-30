package stelztech.youknowehv4.activities.quiz;

import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import stelztech.youknowehv4.R;

/**
 * Created by alex on 12/2/2017.
 */

public class QuizMutlipleChoiceAdapter extends RecyclerView.Adapter<QuizMutlipleChoiceAdapter.ViewHolder> {

    private RecyclerView parentRecycler;
    private int itemHeight;
    private int itemWidth;

    private QuizMultipleChoice quizMultipleChoice;

    private final static float CARD_WIDTH_SCALE = 0.9f;
    private final static float CARD_HEIGHT_SCALE = 0.30f;

    public QuizMutlipleChoiceAdapter(QuizMultipleChoice quizMultipleChoice) {
        this.quizMultipleChoice = quizMultipleChoice;

        Point windowDimensions = new Point();
        quizMultipleChoice.getWindowManager().getDefaultDisplay().getSize(windowDimensions);
        itemHeight = Math.round(windowDimensions.y * CARD_HEIGHT_SCALE);
        itemWidth = Math.round(windowDimensions.x * CARD_WIDTH_SCALE);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        parentRecycler = recyclerView;
    }

    @Override
    public QuizMutlipleChoiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.custom_quiz_card_container, parent, false);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                itemWidth,
                itemHeight);
        v.setLayoutParams(params);
        return new QuizMutlipleChoiceAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(QuizMutlipleChoiceAdapter.ViewHolder holder, int position) {

            holder.questionTextView.setText(quizMultipleChoice.quizCardList.get(position).getAnswer());

    }

    @Override
    public int getItemCount() {
        return quizMultipleChoice.getQuizCardList().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView questionTextView;

        ViewHolder(View itemView) {
            super(itemView);

            questionTextView = (TextView) itemView.findViewById(R.id.quiz_reading_card_front);
        }
    }

    public RecyclerView getRecyclerView() {
        return parentRecycler;
    }


}
