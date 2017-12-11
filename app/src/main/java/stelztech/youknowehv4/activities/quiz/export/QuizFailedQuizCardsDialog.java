package stelztech.youknowehv4.activities.quiz.export;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.card.Card;
import stelztech.youknowehv4.database.deck.Deck;
import stelztech.youknowehv4.database.quizcard.QuizCard;

/**
 * Created by alex on 12/10/2017.
 */

public abstract class QuizFailedQuizCardsDialog {

    private boolean[] isExporting;

    public void showFailedQuizCards(final List<QuizCard> quizCardList, boolean[] isExportingInit, final Context context) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle("Failed Quiz Cards");

        this.isExporting = isExportingInit;

        CharSequence[] cardDisplayName = new CharSequence[quizCardList.size()];
        for (int counter = 0; counter < quizCardList.size(); counter++) {
            QuizCard quizCard = quizCardList.get(counter);
            cardDisplayName[counter] = quizCard.getQuestion() + "/" + quizCard.getAnswer();
        }


        // display a checkbox list
        builder.setMultiChoiceItems(cardDisplayName, isExporting, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {

            }

        });

        builder.setCancelable(false);

        builder.setPositiveButton("next", null);
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        final android.app.AlertDialog alert = builder.create();

        alert.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) alert).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        boolean isEmpty = true;

                        for (int counter = 0; counter < isExporting.length && isEmpty; counter++) {
                            if(isExporting[counter]){
                                isEmpty = false;
                            }
                        }

                        if(isEmpty){
                            Toast.makeText(context, "You must select a least one failed card", Toast.LENGTH_SHORT).show();
                        }else{
                            alert.dismiss();
                            onCardsSelected(isExporting);
                        }

                    }
                });
            }
        });

        alert.show();
    }

    public abstract void onCardsSelected(boolean[] isExporting);


}
