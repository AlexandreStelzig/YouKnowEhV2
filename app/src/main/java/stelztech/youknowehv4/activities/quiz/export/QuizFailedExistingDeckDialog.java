package stelztech.youknowehv4.activities.quiz.export;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.deck.Deck;
import stelztech.youknowehv4.utilities.Helper;

/**
 * Created by alex on 12/10/2017.
 */

public abstract class QuizFailedExistingDeckDialog {

    private int selected = 0;

    public void showDialog(final Activity activity, final Context context) {

        final List<Deck> deckList = Database.mDeckDao.fetchAllDecks();

        CharSequence[] deckListDisplayName = new CharSequence[deckList.size()];
        for (int i = 0; i < deckList.size(); i++) {
            deckListDisplayName[i] = deckList.get(i).getDeckName();
        }


        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle("Export failed cards to Deck:");
        builder.setCancelable(false);
        builder.setNeutralButton("< Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // logic later
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // dismiss
            }
        });

        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // logic later
            }
        });

        builder.setSingleChoiceItems(deckListDisplayName, selected,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected = which;
                    }
                });

        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        onDeckCreated(deckList.get(selected).getDeckId());
                    }
                });

                Button neutralButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_NEUTRAL);
                neutralButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        onPreviousClick();
                    }
                });
            }
        });
        alertDialog.show();
    }

    public abstract void onDeckCreated(int deckId);

    public abstract void onPreviousClick();

}
