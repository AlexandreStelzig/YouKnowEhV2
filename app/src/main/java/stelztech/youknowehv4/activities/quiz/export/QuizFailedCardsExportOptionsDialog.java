package stelztech.youknowehv4.activities.quiz.export;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.List;

import stelztech.youknowehv4.database.quizcard.QuizCard;

/**
 * Created by alex on 12/10/2017.
 */

public abstract class QuizFailedCardsExportOptionsDialog {

    private int selected = 0;

    public void showExportOptions(final Activity activity, final Context context, final List<QuizCard> failedQuizCardList, final boolean[] exportList, final QuizFailedQuizCardsDialog quizFailedQuizCardsDialog) {


        final CharSequence exportOptions[] = new CharSequence[]{"Existing deck", "New deck"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Export failed cards to:");
        builder.setCancelable(false);
        builder.setNeutralButton("< Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                quizFailedQuizCardsDialog.showFailedQuizCards(failedQuizCardList, exportList, context);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String item = (String) exportOptions[selected];
                if (item.equals("Existing deck")) {
                    QuizFailedExistingDeckDialog quizFailedExistingDeckDialog = new QuizFailedExistingDeckDialog() {
                        @Override
                        public void onDeckCreated(int deckId) {
                            addFailedQuizCardsToDeck(deckId);
                        }

                        @Override
                        public void onPreviousClick() {
                            showExportOptions(activity, context, failedQuizCardList, exportList,
                                    quizFailedQuizCardsDialog);
                        }
                    };

                    quizFailedExistingDeckDialog.showDialog(activity, context);

                } else if (item.equals("New deck")) {
                    QuizFailedNewDeckDialog quizFailedNewDeckDialog = new QuizFailedNewDeckDialog() {
                        @Override
                        public void onDeckCreated(int deckId) {
                            addFailedQuizCardsToDeck(deckId);
                        }

                        @Override
                        public void onPreviousClick() {
                            showExportOptions(activity, context, failedQuizCardList, exportList,
                                    quizFailedQuizCardsDialog);
                        }
                    };
                    quizFailedNewDeckDialog.showDialog( activity, context);
                }
            }
        });

        builder.setSingleChoiceItems(exportOptions, selected,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected = which;
                    }
                });

        builder.show();
    }

    public abstract void addFailedQuizCardsToDeck(int deckId);
}
