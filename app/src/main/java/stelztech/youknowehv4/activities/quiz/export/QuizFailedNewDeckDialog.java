package stelztech.youknowehv4.activities.quiz.export;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.deck.Deck;
import stelztech.youknowehv4.fragments.deck.DeckListFragment;
import stelztech.youknowehv4.utilities.Helper;

/**
 * Created by alex on 12/10/2017.
 */

public abstract class QuizFailedNewDeckDialog {

    private String deckNameHolder = "";

    public void showDialog(final Activity activity, final Context context) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final EditText input = new EditText(activity);
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_CLASS_TEXT);
        input.setSingleLine();

        FrameLayout container = new FrameLayout(activity);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = activity.getResources().getDimensionPixelSize(R.dimen.default_padding);
        params.rightMargin = activity.getResources().getDimensionPixelSize(R.dimen.default_padding);

        input.setLayoutParams(params);
        container.addView(input);

        input.setHint("Deck name");
        builder.setCustomTitle(Helper.getInstance().customTitle("Export failed Cards to new Deck:"));
        builder.setCancelable(false);
        builder.setView(container);
        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            // when button OK is press
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNeutralButton("< Back", new DialogInterface.OnClickListener() {
            @Override
            // when button OK is press
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        // if cancel button is press, close dialog
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // opens keyboard on creation with selection at the end
        final AlertDialog alertDialog = builder.create();


        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deckNameHolder = input.getText().toString();
                        // check if valid name
                        if (deckNameHolder.trim().isEmpty()) {
                            Toast.makeText(context, "Invalid name: cannot be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            alertDialog.dismiss();
                            int deckId = addToDatabase(deckNameHolder);
                            onDeckCreated(deckId);
                        }
                    }
                });

                Button neutralButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_NEUTRAL);
                neutralButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Helper.getInstance().hideKeyboard(activity);
                        onPreviousClick();
                    }
                });
            }
        });

        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        input.setSelection(input.getText().length());
        alertDialog.show();
    }

    public abstract void onDeckCreated(int deckId);

    public abstract void onPreviousClick();

    private int addToDatabase(String name) {
        return Database.mDeckDao.createDeck(name);
    }
}
