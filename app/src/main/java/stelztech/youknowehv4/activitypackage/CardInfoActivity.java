package stelztech.youknowehv4.activitypackage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.database.DatabaseManager;
import stelztech.youknowehv4.helper.Helper;
import stelztech.youknowehv4.manager.CardInfoToolbarManager;
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

    private enum ConfirmationEventId {
        CANCEL,
        MODIFY,
        HOME
    }

    // deck list variables
    private boolean[] mIsPartOfDeckList;
    private boolean[] mTempPartOfDeckList;
    private String mInitialDeckId;
    private List<Deck> mDeckList;
    private CharSequence[] mDeckListDisplayName;
    private List<Deck> mCardSpecificDeck;

    // database
    private DatabaseManager dbManager;

    // components
    private TextView numberOfDecksTextView;
    private TextView numberOfDecksString;
    private EditText questionEditTextView;
    private EditText answerEditTextView;
    private EditText noteEditTextView;
    private AlertDialog.Builder deckListAlertDialog;

    // temp variables
    private String questionTemp = "";
    private String answerTemp = "";
    private String noteTemp = "";
    private boolean[] mInitPartOfDeckTemp;

    // mode
    private CardInfoState currentState;
    private String mCardId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_card_info);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        // init
        dbManager = DatabaseManager.getInstance(this);
        numberOfDecksTextView = (TextView) findViewById(R.id.number_of_decks_text_view);
        numberOfDecksString = (TextView) findViewById(R.id.number_of_decks_text_view_text);
        questionEditTextView = (EditText) findViewById(R.id.question_edit_text);
        answerEditTextView = (EditText) findViewById(R.id.answer_edit_text);
        noteEditTextView = (EditText) findViewById(R.id.note_edit_text);

        // set tags to be remembered when switching between editable and non editable
        questionEditTextView.setTag(questionEditTextView.getKeyListener());
        answerEditTextView.setTag(answerEditTextView.getKeyListener());
        noteEditTextView.setTag(noteEditTextView.getKeyListener());


        // get activity inital state
        CardInfoState initalStateFromIntent = (CardInfoState) getIntent().getSerializableExtra("initialState");
        initState(initalStateFromIntent);
    }

    /////// INITIALIZING ///////

//    supportInvalidateOptionsMenu

    private void initState(CardInfoState state) {
        switch (state) {
            case NEW:
                mInitialDeckId = getIntent().getStringExtra("initialDeckId");
                setStateNew();
                break;
            case VIEW:
                mCardId = getIntent().getStringExtra("cardId");
                setStateView();
                break;
            case EDIT:
                mCardId = getIntent().getStringExtra("cardId");
                setStateEdit();
                break;
            default:
                Log.e("CardInfoActivity", "error while switching state");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toobal_card_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            if (currentState == CardInfoState.EDIT && cardHaveChanges()) {
                setKeyboardVisibility(false);
                confirmationDialog("Are you sure you want to cancel?\nAll modifications will be lost", ConfirmationEventId.HOME);
            } else {
                finish(); // close this activity and return to preview activity (if there is any)
            }
        }

        if (currentState == CardInfoState.NEW && item.getItemId() == (R.id.action_done_card_info)) {
            boolean isAddingCardSuccessful = addCardToDatabase();
            if (isAddingCardSuccessful) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        } else if (currentState == CardInfoState.EDIT && item.getItemId() == (R.id.action_done_card_info)) {
            boolean isAddingCardSuccessful = updateCard();
            if (isAddingCardSuccessful){
                resetNumberOfDecksCounter();
                setStateView();

                mCardSpecificDeck.clear();
                for(int i = 0; i < mIsPartOfDeckList.length; i++){
                    if(mIsPartOfDeckList[i]){
                        mCardSpecificDeck.add(mDeckList.get(i));
                    }
                }
            }


        } else if (item.getItemId() == (R.id.action_next_card_info)) {
            boolean isAddingCardSuccessful = addCardToDatabase();
            if (isAddingCardSuccessful) {
                resetFields();
                questionEditTextView.requestFocus();
            }
        } else if (item.getItemId() == (R.id.action_edit_card_info)) {
            setStateEdit();
        } else if (item.getItemId() == (R.id.action_cancel_card_info)) {
            if (cardHaveChanges()) {
                setKeyboardVisibility(false);
                confirmationDialog("Are you sure you want to cancel?\nAll modifications will be lost", ConfirmationEventId.CANCEL);
            } else {
                setStateView();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        CardInfoToolbarManager.getInstance().setState(currentState, menu);
        return true;
    }

    // navigation options

    private void setStateNew() {
        getSupportActionBar().setTitle("New Card");
        currentState = CardInfoState.NEW;
        initActivityInformationNew();
        supportInvalidateOptionsMenu(); // call toolbar menu again
        setKeyboardVisibility(true);
    }

    private void setStateEdit() {
        getSupportActionBar().setTitle("Edit Card");
        currentState = CardInfoState.EDIT;
        initActivityInformationEdit();
        setEditTextEditable(true);
        setupTempVariables();
        supportInvalidateOptionsMenu(); // call toolbar menu again
        setKeyboardVisibility(true);
    }


    private void setStateView() {
        getSupportActionBar().setTitle("View Card");
        currentState = CardInfoState.VIEW;
        initActivityInformationView();
        setEditTextEditable(false);
        setFieldsInfo();
        supportInvalidateOptionsMenu(); // call toolbar menu again
        setKeyboardVisibility(false);
    }

    private void initActivityInformationNew() {


        mDeckList = dbManager.getDecks();
        if(mIsPartOfDeckList == null){
            mIsPartOfDeckList = new boolean[mDeckList.size()];
            for (int counter = 0; counter < mIsPartOfDeckList.length; counter++) {
                if (mDeckList.get(counter).getDeckId().equals(mInitialDeckId))
                    mIsPartOfDeckList[counter] = true;
            }
            mTempPartOfDeckList = mIsPartOfDeckList.clone();
        }else{

        }


        mDeckListDisplayName = new CharSequence[mDeckList.size()];

        for (int i = 0; i < mDeckList.size(); i++) {
            mDeckListDisplayName[i] = mDeckList.get(i).getDeckName();
        }

        setEditTextEditable(true);
        resetNumberOfDecksCounter();
    }


    private void initActivityInformationEdit() {

        mCardSpecificDeck = dbManager.getDecksFromCard(mCardId);

        mDeckList = dbManager.getDecks();
        mIsPartOfDeckList = new boolean[mDeckList.size()];
        for (int counter = 0; counter < mIsPartOfDeckList.length; counter++) {
            for (int i = 0; i < mCardSpecificDeck.size(); i++) {
                if (mDeckList.get(counter).getDeckId().equals(mCardSpecificDeck.get(i).getDeckId()))
                    mIsPartOfDeckList[counter] = true;
            }
        }
        mTempPartOfDeckList = mIsPartOfDeckList.clone();


        mDeckListDisplayName = new CharSequence[mDeckList.size()];

        for (int i = 0; i < mDeckList.size(); i++) {
            mDeckListDisplayName[i] = mDeckList.get(i).getDeckName();
        }

        resetNumberOfDecksCounter();

    }

    private void initActivityInformationView() {

        mCardSpecificDeck = dbManager.getDecksFromCard(mCardId);

        mDeckList = dbManager.getDecks();
        mIsPartOfDeckList = new boolean[mDeckList.size()];
        for (int counter = 0; counter < mIsPartOfDeckList.length; counter++) {
            for (int i = 0; i < mCardSpecificDeck.size(); i++) {
                if (mDeckList.get(counter).getDeckId().equals(mCardSpecificDeck.get(i).getDeckId()))
                    mIsPartOfDeckList[counter] = true;
            }
        }
        mTempPartOfDeckList = mIsPartOfDeckList.clone();


        mDeckListDisplayName = new CharSequence[mCardSpecificDeck.size()];

        for (int i = 0; i < mCardSpecificDeck.size(); i++) {
            mDeckListDisplayName[i] = mCardSpecificDeck.get(i).getDeckName();
        }

        resetNumberOfDecksCounter();

    }

    /////// DIALOG ///////

    public void deckInfoButtonClicked(final View view) {

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_scrollable_dialog_list, null, false);

        deckListAlertDialog = new AlertDialog.Builder(this);

        if (currentState == CardInfoState.NEW) {
            setupDialogNewEdit("Add Deck to Card:");
        } else if (currentState == CardInfoState.EDIT) {
            setupDialogNewEdit("Update Card\'s Deck:");
        } else {
            setupDialogView();
        }

        deckListAlertDialog.setView(dialogView);
        deckListAlertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelDialogAction();

            }
        });
        deckListAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancelDialogAction();
            }
        });

        AlertDialog alert = deckListAlertDialog.create();
        Helper.getInstance().hideKeyboard(this);
        alert.show();

    }

    private void setupDialogNewEdit(String message) {

        deckListAlertDialog.setTitle(message);
        // display a checkbox list
        deckListAlertDialog.setMultiChoiceItems(mDeckListDisplayName, mIsPartOfDeckList, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                // selects the item - does it automatically
            }
        });

        deckListAlertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetNumberOfDecksCounter();

            }
        });
    }

    private void setupDialogView() {
        deckListAlertDialog.setTitle("Decks:");
        // display a normal list
        final ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter<CharSequence>(
                this,
                android.R.layout.simple_list_item_1);


        List<CharSequence> displayName = new ArrayList<>();

        for(int i = 0; i < mIsPartOfDeckList.length; i++){
            if(mIsPartOfDeckList[i])
                displayName.add(mDeckListDisplayName[i]);
        }

        arrayAdapter.addAll(mDeckListDisplayName);
        deckListAlertDialog.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int indexSelected) {
                String deckIdReturn = mCardSpecificDeck.get(indexSelected).getDeckId();
                getIntent().putExtra("deckIdReturn", deckIdReturn);
                setResult(1, getIntent());
                finish();
            }
        });
    }

    private void cancelDialogAction() {
        mIsPartOfDeckList = mTempPartOfDeckList.clone();
    }

    private void confirmationDialog(String message, final ConfirmationEventId id) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        confirmationYes(id);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // do nothing
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void confirmationYes(ConfirmationEventId id) {
        switch (id) {
            case CANCEL:
                // reset normal
                setStateView();
                break;
            case MODIFY:
                // update word
                break;
            case HOME:
                finish();
                break;
        }
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

    private boolean updateCard() {

        if (cardHaveChanges()) {

            if (areFieldChanged()) {
                // get edit text values
                String question = questionEditTextView.getText().toString();
                String answer = answerEditTextView.getText().toString();
                String note = noteEditTextView.getText().toString();

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
                    dbManager.updateCard(mCardId, question, answer, note);
                }

            } else {
                for (int counter = 0; counter < mIsPartOfDeckList.length; counter++) {
                    if (mIsPartOfDeckList[counter] != mInitPartOfDeckTemp[counter]) {
                        if (mIsPartOfDeckList[counter] == true) {
                            dbManager.createCardDeck(mCardId, mDeckList.get(counter).getDeckId());
                        } else {
                            dbManager.deleteCardDeck(mCardId, mDeckList.get(counter).getDeckId());
                        }
                    }
                }
            }

            if (areFieldChanged() && isDecksChanged())
                Toast.makeText(this, "Card modified", Toast.LENGTH_LONG).show();
            else if (areFieldChanged())
                Toast.makeText(this, "Card data modified", Toast.LENGTH_LONG).show();
            else if (isDecksChanged())
                Toast.makeText(this, "Card's Decks modified", Toast.LENGTH_LONG).show();

            return true;
        } else {
            Toast.makeText(this, "Nothing to change", Toast.LENGTH_LONG).show();
            return true;
        }
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
        setViewEnable(answerEditTextView, isEditable);
        setViewEnable(questionEditTextView, isEditable);
        setViewEnable(noteEditTextView, isEditable);
    }

    private void setupTempVariables() {
        questionTemp = questionEditTextView.getText().toString();
        answerTemp = answerEditTextView.getText().toString();
        noteTemp = noteEditTextView.getText().toString();
        mInitPartOfDeckTemp = mIsPartOfDeckList.clone();
    }

    private boolean areFieldChanged() {
        return !questionTemp.equals(questionEditTextView.getText().toString()) ||
                !answerTemp.equals(answerEditTextView.getText().toString()) || !noteTemp.equals(noteEditTextView.getText().toString());
    }

    private boolean isDecksChanged() {

        for (int counter = 0; counter < mIsPartOfDeckList.length; counter++) {
            if (mIsPartOfDeckList[counter] != mInitPartOfDeckTemp[counter])
                return true;
        }

        return false;
    }

    private boolean cardHaveChanges() {
        boolean fieldChanged = areFieldChanged();
        boolean deckChanged = isDecksChanged();

        return fieldChanged || deckChanged;
    }

    private void setViewEnable(EditText view, boolean isEnable) {

        if (!isEnable) {
            view.setKeyListener(null);
        } else {
            view.setKeyListener((KeyListener) view.getTag());
        }
        view.setSelection(view.getText().length());
    }

    private void setKeyboardVisibility(boolean isVisible) {
        if (isVisible) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(questionEditTextView, InputMethodManager.SHOW_IMPLICIT);
        } else {
            Helper.getInstance().hideKeyboard(this);
        }
    }

    private void resetNumberOfDecksCounter() {
        int numberOfDecks = 0;

        for (int i = 0; i < mIsPartOfDeckList.length; i++) {
            if (mIsPartOfDeckList[i] == true)
                numberOfDecks++;
        }


        if (numberOfDecks == 0) {
            numberOfDecksTextView.setText("");
            numberOfDecksString.setText("No Deck");
        } else {
            numberOfDecksTextView.setText("" + numberOfDecks);
            if (numberOfDecks == 1)
                numberOfDecksString.setText(" Deck");
            else
                numberOfDecksString.setText(" Decks");
        }


        mTempPartOfDeckList = mIsPartOfDeckList.clone();

    }


}
