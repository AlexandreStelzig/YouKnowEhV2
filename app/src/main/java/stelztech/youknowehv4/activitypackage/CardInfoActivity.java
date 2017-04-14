package stelztech.youknowehv4.activitypackage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.database.DatabaseManager;
import stelztech.youknowehv4.helper.Helper;
import stelztech.youknowehv4.model.Card;
import stelztech.youknowehv4.model.Deck;

/**
 * Created by alex on 2017-04-04.
 */

public class CardInfoActivity extends AppCompatActivity {


    public enum CardInfoState {
        NEW,
        EDIT,
        VIEW
    }


    // deck list variables
    private boolean[] mIsPartOfDeckList;
    private boolean[] mTempPartOfDeckList;
    private String mInitialDeckId;
    private List<Deck> mDeckList;
    private CharSequence[] mDeckListDisplayName;

    // database
    private DatabaseManager dbManager;

    // components
    private TextView numberOfDecksTextView;
    private TextView numberOfDecksString;
    private EditText questionEditTextView;
    private EditText answerEditTextView;
    private EditText noteEditTextView;

    // mode
    private CardInfoState currentState;
    private String mCardId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_card_info);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("New Card");

        // init
        dbManager = DatabaseManager.getInstance(this);
        numberOfDecksTextView = (TextView) findViewById(R.id.number_of_decks_text_view);
        numberOfDecksString = (TextView) findViewById(R.id.number_of_decks_text_view_text);
        questionEditTextView = (EditText) findViewById(R.id.question_edit_text);
        answerEditTextView = (EditText) findViewById(R.id.answer_edit_text);
        noteEditTextView = (EditText) findViewById(R.id.note_edit_text);


        // get activity inital state
        String initState = getIntent().getStringExtra("initialState");

        //TODO
        if (initState.equals("NEW")) {
            mInitialDeckId = getIntent().getStringExtra("initialDeckId");
            currentState = CardInfoState.NEW;
        } else {
            mCardId = getIntent().getStringExtra("cardId");
            currentState = CardInfoState.VIEW;
        }


        initActivityInformation();

    }

    /////// INITIALIZING ///////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_info_toolbar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        if (item.getItemId() == (R.id.action_done_card_info)) {
            boolean isAddingCardSuccessful = addCardToDatabase();
            if (isAddingCardSuccessful) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        }
        if (item.getItemId() == (R.id.action_next_card_info)) {
            boolean isAddingCardSuccessful = addCardToDatabase();
            if (isAddingCardSuccessful) {
                resetFields();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void initActivityInformation() {

        if (currentState == CardInfoState.NEW) {

            if (mIsPartOfDeckList == null) {
                mDeckList = dbManager.getDecks();
                mIsPartOfDeckList = new boolean[mDeckList.size()];
                for (int counter = 0; counter < mIsPartOfDeckList.length; counter++) {
                    if (mDeckList.get(counter).getDeckId().equals(mInitialDeckId))
                        mIsPartOfDeckList[counter] = true;
                    else
                        mIsPartOfDeckList[counter] = false;
                }
                mTempPartOfDeckList = mIsPartOfDeckList.clone();
            }

            if (mDeckListDisplayName == null) {
                mDeckListDisplayName = new CharSequence[mDeckList.size()];

                for (int i = 0; i < mDeckList.size(); i++) {
                    mDeckListDisplayName[i] = mDeckList.get(i).getDeckName();
                }
            }
            setEditTextEditable(true);

        } else {
            List<Deck> initSelected = dbManager.getDecksFromCard(mCardId);
            if (mIsPartOfDeckList == null) {
                mDeckList = dbManager.getDecks();
                mIsPartOfDeckList = new boolean[mDeckList.size()];
                for (int counter = 0; counter < mIsPartOfDeckList.length; counter++) {
                    for (int i = 0; i < initSelected.size(); i++) {
                        if (mDeckList.get(counter).getDeckId().equals(initSelected.get(i).getDeckId()))
                            mIsPartOfDeckList[counter] = true;
                        else
                            mIsPartOfDeckList[counter] = false;
                    }
                }
                mTempPartOfDeckList = mIsPartOfDeckList.clone();
            }

            if (mDeckListDisplayName == null) {
                mDeckListDisplayName = new CharSequence[initSelected.size()];

                for (int i = 0; i < initSelected.size(); i++) {
                    mDeckListDisplayName[i] = initSelected.get(i).getDeckName();
                }
            }

            setEditTextEditable(false);
            setFieldsInfo();
            Helper.getInstance().hideKeyboard(this);
        }

        resetNumberOfDecksCounter();


    }

    /////// COMPONENTS HELPER ///////

    private void resetFields() {
        questionEditTextView.setText("");
        answerEditTextView.setText("");
        noteEditTextView.setText("");
    }

    private void setFieldsInfo() {
        Card card = dbManager.getCardFromId(mCardId);
        answerEditTextView.setText(card.getAnswer());
        questionEditTextView.setText(card.getQuestion());
        noteEditTextView.setText(card.getMoreInfo());
    }

    private void setEditTextEditable(boolean isEditable) {
        answerEditTextView.setEnabled(isEditable);
        questionEditTextView.setEnabled(isEditable);
        noteEditTextView.setEnabled(isEditable);

        // TODO
        if (!isEditable) {
            answerEditTextView.setInputType(InputType.TYPE_NULL);
            questionEditTextView.setInputType(InputType.TYPE_NULL);
            noteEditTextView.setInputType(InputType.TYPE_NULL);
        } else {
            answerEditTextView.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
            questionEditTextView.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
            noteEditTextView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        }


    }

    private void resetNumberOfDecksCounter() {
        int numberOfDecks = 0;

        for (int i = 0; i < mIsPartOfDeckList.length; i++) {
            if (mIsPartOfDeckList[i] == true)
                numberOfDecks++;
        }
        numberOfDecksTextView.setText("" + numberOfDecks);

        if (numberOfDecks <= 1)
            numberOfDecksString.setText(" Deck");
        else
            numberOfDecksString.setText(" Decks");

        mTempPartOfDeckList = mIsPartOfDeckList.clone();

    }

    /////// DIALOG ///////

    public void deckInfoButtonClicked(final View view) {

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_scrollable_dialog_list, null, false);


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        if (currentState == CardInfoState.NEW)
            alertDialog.setTitle("Add to Deck:");
        else
            alertDialog.setTitle("Decks:");

        if (currentState == CardInfoState.NEW) {
            // display a checkbox list
            alertDialog.setMultiChoiceItems(mDeckListDisplayName, mIsPartOfDeckList, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {

                }
            });

            alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    resetNumberOfDecksCounter();

                }
            });
        } else {

            // display a normal list
            final ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter<CharSequence>(
                    this,
                    android.R.layout.simple_list_item_1);

            arrayAdapter.addAll(mDeckListDisplayName);
            alertDialog.setAdapter(arrayAdapter, null);
        }


        alertDialog.setView(dialogView);
        alertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelDialogAction();

            }
        });
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancelDialogAction();
            }
        });


        AlertDialog alert = alertDialog.create();
        Helper.getInstance().hideKeyboard(this);
        alert.show();

    }

    private void cancelDialogAction() {
        mIsPartOfDeckList = mTempPartOfDeckList.clone();
    }

    /////// DATABASE ///////

    private boolean addCardToDatabase() {
        // get edit text values
        String question = questionEditTextView.getText().toString();
        String answer = answerEditTextView.getText().toString();
        String note = noteEditTextView.getText().toString();

        // fields validation
        if (question.isEmpty() || answer.isEmpty()) {
            String toastMessageError = "";
            if (question.isEmpty() && answer.isEmpty())
                toastMessageError = "Question and Answer cannot be empty";
            else if (question.isEmpty() && !answer.isEmpty())
                toastMessageError = "Question cannot be empty";
            else if (!question.isEmpty() && answer.isEmpty())
                toastMessageError = "Answer cannot be empty";

            Toast.makeText(this, (String) toastMessageError,
                    Toast.LENGTH_LONG).show();
            return false;
        } else {
            // create new card
            String newCardId = dbManager.createCard(question, answer, note);
            if (newCardId == "-1") {
                Toast.makeText(this, "Error while adding card to database",
                        Toast.LENGTH_LONG).show();
                return false;
            }

            // get all and add mDeckList that were checked
            List<Deck> checkedDecks = new ArrayList<>();
            for (int counter = 0; counter < mIsPartOfDeckList.length; counter++) {
                if (mIsPartOfDeckList[counter] == true) {
                    checkedDecks.add(mDeckList.get(counter));
                }
            }

            for (int counter = 0; counter < checkedDecks.size(); counter++) {
                dbManager.createCardDeck(newCardId, checkedDecks.get(counter).getDeckId());
            }

            Toast.makeText(this, "Card successfully added",
                    Toast.LENGTH_LONG).show();
            return true;
        }
    }

}
