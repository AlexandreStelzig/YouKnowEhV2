package stelztech.youknowehv4.activities;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.utilities.DateUtilities;
import stelztech.youknowehv4.utilities.Helper;
import stelztech.youknowehv4.manager.CardInfoToolbarManager;
import stelztech.youknowehv4.database.card.Card;
import stelztech.youknowehv4.database.deck.Deck;
import stelztech.youknowehv4.database.profile.Profile;
import stelztech.youknowehv4.manager.ThemeManager;

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
    private long mInitialDeckId;
    private List<Deck> mDeckList;
    private CharSequence[] mDeckListDisplayName;
    private List<Deck> mCardSpecificDeck;

    // components
    private TextView numberOfDecksTextView;
    private TextView numberOfDecksString;
    private EditText questionEditTextView;
    private EditText answerEditTextView;
    private EditText noteEditTextView;
    private AlertDialog.Builder deckListAlertDialog;
    private LinearLayout createAnotherCardLayout;
    private CheckBox createAnotherCardCheckBox;
    private Button deckInfoButton;
    private LinearLayout dateInfoLayout;
    private TextView dateCreatedTV;
    private TextView dateModifiedTV;
    private TextView questionTextView;
    private TextView answerTextView;
    private TextView noteTextView;
    private View separator1;
    private View separator2;
    private ScrollView scrollView;
    private LinearLayout reverseLayout;
    private Button reverseButton;

    // temp variables
    private String questionTemp = "";
    private String answerTemp = "";
    private String noteTemp = "";
    private boolean[] mInitPartOfDeckTemp;

    private boolean goBackToViewModeFromEdit;


    // mode
    private CardInfoState currentState;
    private int mCardId;
    private boolean createAnotherCard;
    private CardInfoState initalStateFromIntent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeManager.getInstance().setApplicationTheme(this, true);

        overridePendingTransition(R.anim.enter, R.anim.exit);

        setContentView(R.layout.activity_card_info);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        goBackToViewModeFromEdit = false;

        // init
        numberOfDecksTextView = (TextView) findViewById(R.id.number_of_decks_text_view);
        numberOfDecksString = (TextView) findViewById(R.id.number_of_decks_text_view_text);
        questionEditTextView = (EditText) findViewById(R.id.card_info_question_edit_text);
        answerEditTextView = (EditText) findViewById(R.id.card_info_answer_edit_text);
        noteEditTextView = (EditText) findViewById(R.id.card_info_note_edit_text);
        createAnotherCardLayout = (LinearLayout) findViewById(R.id.another_card_layout);
        deckInfoButton = (Button) findViewById(R.id.deck_info_button);
        createAnotherCardCheckBox = (CheckBox) findViewById(R.id.another_card_checkbox);
        createAnotherCard = createAnotherCardCheckBox.isChecked();
        dateInfoLayout = (LinearLayout) findViewById(R.id.card_info_date_layout);
        dateCreatedTV = (TextView) findViewById(R.id.card_date_created);
        dateModifiedTV = (TextView) findViewById(R.id.card_date_modified);
        separator1 = (View) findViewById(R.id.card_info_separator_1);
        separator2 = (View) findViewById(R.id.card_info_separator_2);
        reverseLayout = (LinearLayout) findViewById(R.id.card_info_reverse_layout);
        reverseButton = (Button) findViewById(R.id.card_info_reverse_button);

        questionTextView = (TextView) findViewById(R.id.card_info_question_text_view);
        answerTextView = (TextView) findViewById(R.id.card_info_answer_text_view);
        noteTextView = (TextView) findViewById(R.id.card_info_note_text_view);

        questionTextView.setMovementMethod(new ScrollingMovementMethod());
        answerTextView.setMovementMethod(new ScrollingMovementMethod());
        noteTextView.setMovementMethod(new ScrollingMovementMethod());

        // question answer labels

        Profile currentProfile = Database.mUserDao.fetchActiveProfile();

        String questionLabel = currentProfile.getFrontLabel();
        String answerLabel = currentProfile.getBackLabel();
        TextView questionLabelTextView = (TextView) findViewById(R.id.card_info_question_label);
        TextView answerLabelTextView = (TextView) findViewById(R.id.card_info_answer_label);

        questionLabelTextView.setText(questionLabel);
        answerLabelTextView.setText(answerLabel);

        scrollView = (ScrollView) findViewById(R.id.card_info_scroll);

        scrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                noteEditTextView.getParent().requestDisallowInterceptTouchEvent(false);
                questionEditTextView.getParent().requestDisallowInterceptTouchEvent(false);
                answerEditTextView.getParent().requestDisallowInterceptTouchEvent(false);
                noteTextView.getParent().requestDisallowInterceptTouchEvent(false);
                questionTextView.getParent().requestDisallowInterceptTouchEvent(false);
                answerTextView.getParent().requestDisallowInterceptTouchEvent(false);

                return false;
            }
        });

        setStopScrollingOnView(noteEditTextView);
        setStopScrollingOnView(questionEditTextView);
        setStopScrollingOnView(answerEditTextView);
        setStopScrollingOnView(noteTextView);
        setStopScrollingOnView(questionTextView);
        setStopScrollingOnView(answerTextView);


        ((TextView) findViewById(R.id.create_another_card_string)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAnotherCard = !createAnotherCard;
                createAnotherCardCheckBox.setChecked(createAnotherCard);
            }
        });

        createAnotherCardCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAnotherCard = !createAnotherCard;
            }
        });

        reverseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reserveButtonClicked();
            }
        });

        // set tags to be remembered when switching between editable and non editable
        questionEditTextView.setTag(questionEditTextView.getKeyListener());
        answerEditTextView.setTag(answerEditTextView.getKeyListener());
        noteEditTextView.setTag(noteEditTextView.getKeyListener());

        deckInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deckInfoButtonClicked();
            }
        });

        // get activity inital state
        initalStateFromIntent = (CardInfoState) getIntent().getSerializableExtra("initialState");
        initState(initalStateFromIntent);
    }


    /////// INITIALIZING ///////

//    supportInvalidateOptionsMenu

    private void initState(CardInfoState state) {
        switch (state) {
            case NEW:
                mInitialDeckId = getIntent().getIntExtra("initialDeckId", -1);
                setStateNew();
                break;
            case VIEW:
                mCardId = (int) getIntent().getLongExtra("cardId", 1);
                setStateView();
                break;
            case EDIT:
                mCardId = (int) getIntent().getLongExtra("cardId", 1);
                setStateEdit();
                break;
            default:
                Log.e("CardInfoActivity", "error while switching state");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_card_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            if (currentState != CardInfoState.VIEW && cardHaveChanges()) {
                setKeyboardVisibility(false);
                confirmationDialog("Are you sure you want to cancel?\nAll modifications will be lost.", ConfirmationEventId.HOME);
            } else {
                if (goBackToViewModeFromEdit) {
                    setStateView();
                } else {
                    setKeyboardVisibility(false);
                    finish();
                }

            }
        }

        if (currentState == CardInfoState.NEW && item.getItemId() == (R.id.action_done_card_info)) {


            if (createAnotherCard) {
                boolean isAddingCardSuccessful = addCardToDatabase();
                if (isAddingCardSuccessful) {
                    resetFields();
                    questionEditTextView.requestFocus();
                }
            } else {
                boolean isAddingCardSuccessful = addCardToDatabase();
                if (isAddingCardSuccessful) {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    setKeyboardVisibility(false);
                    finish();
                }
            }

        } else if (currentState == CardInfoState.EDIT && item.getItemId() == (R.id.action_done_card_info)) {
            boolean isAddingCardSuccessful = updateCard();
            if (isAddingCardSuccessful) {
                resetNumberOfDecksCounter();

                if (goBackToViewModeFromEdit) {
                    setStateView();

                    mCardSpecificDeck.clear();
                    for (int i = 0; i < mIsPartOfDeckList.length; i++) {
                        if (mIsPartOfDeckList[i]) {
                            mCardSpecificDeck.add(mDeckList.get(i));
                        }
                    }
                } else {
                    setKeyboardVisibility(false);
                    finish();
                }

            }


        } else if (item.getItemId() == (R.id.action_edit_card_info)) {
            goBackToViewModeFromEdit = true;
            setStateEdit();
        } else if (item.getItemId() == (R.id.action_cancel_card_info)) {
            actionCancel();
        }

        return super.onOptionsItemSelected(item);
    }

    public void actionCancel() {
        if (cardHaveChanges()) {
            setKeyboardVisibility(false);
            confirmationDialog("Are you sure you want to cancel?\nAll modifications will be lost", ConfirmationEventId.CANCEL);
        } else {
            if (goBackToViewModeFromEdit) {
                setStateView();
                Toast.makeText(CardInfoActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
            } else {
                setKeyboardVisibility(false);
                Toast.makeText(CardInfoActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        CardInfoToolbarManager.getInstance().setState(currentState, menu);
        return true;
    }

    @Override
    public void onBackPressed() {

        if (currentState == CardInfoState.EDIT) {
            actionCancel();
        } else {
            super.onBackPressed();
        }
    }

    // navigation options

    private void setStateNew() {
        goBackToViewModeFromEdit = false;
        getSupportActionBar().setTitle("New Card");
        currentState = CardInfoState.NEW;
        initActivityInformationNew();
        supportInvalidateOptionsMenu(); // call toolbar menu again
        setEditTextEditable(true);
        setKeyboardVisibility(true);
        setupTempVariables();
        createAnotherCardLayout.setVisibility(View.VISIBLE);
        dateInfoLayout.setVisibility(View.GONE);
        deckInfoButton.setText("EDIT");
        reverseLayout.setVisibility(View.VISIBLE);

    }

    private void setStateEdit() {
        getSupportActionBar().setTitle("Edit Card");
        currentState = CardInfoState.EDIT;
        initActivityInformationEdit();
        setFieldsInfo();
        setEditTextEditable(true);
        setupTempVariables();
        supportInvalidateOptionsMenu(); // call toolbar menu again
        createAnotherCardLayout.setVisibility(View.GONE);
        dateInfoLayout.setVisibility(View.VISIBLE);
        reverseLayout.setVisibility(View.VISIBLE);
        deckInfoButton.setText("EDIT");

        // hide keyboard
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        Toast.makeText(CardInfoActivity.this, "Edit Card", Toast.LENGTH_SHORT).show();


        scrollView.smoothScrollTo(0, 0);

    }


    private void setStateView() {

        goBackToViewModeFromEdit = false;
        getSupportActionBar().setTitle("Card Information");
        currentState = CardInfoState.VIEW;
        initActivityInformationView();
        setKeyboardVisibility(false);
        setEditTextEditable(false);
        setFieldsInfo();
        supportInvalidateOptionsMenu(); // call toolbar menu again
        createAnotherCardLayout.setVisibility(View.GONE);
        dateInfoLayout.setVisibility(View.VISIBLE);
        reverseLayout.setVisibility(View.GONE);
        deckInfoButton.setText("VIEW");
        scrollView.smoothScrollTo(0, 0);

    }

    private void initActivityInformationNew() {


        mDeckList = Database.mDeckDao.fetchAllDecks();
        mIsPartOfDeckList = new boolean[mDeckList.size()];
        for (int counter = 0; counter < mIsPartOfDeckList.length; counter++) {
            if (mDeckList.get(counter).getDeckId() == (mInitialDeckId))
                mIsPartOfDeckList[counter] = true;
        }
        mTempPartOfDeckList = mIsPartOfDeckList.clone();

        mDeckListDisplayName = new CharSequence[mDeckList.size()];

        for (int i = 0; i < mDeckList.size(); i++) {
            mDeckListDisplayName[i] = mDeckList.get(i).getDeckName();
        }

        setEditTextEditable(true);
        resetNumberOfDecksCounter();
    }


    private void initActivityInformationEdit() {

        mCardSpecificDeck = Database.mCardDeckDao.fetchDecksByCardId(mCardId);

        mDeckList = Database.mDeckDao.fetchAllDecks();
        mIsPartOfDeckList = new boolean[mDeckList.size()];
        for (int counter = 0; counter < mIsPartOfDeckList.length; counter++) {
            for (int i = 0; i < mCardSpecificDeck.size(); i++) {
                if (mDeckList.get(counter).getDeckId() == (mCardSpecificDeck.get(i).getDeckId()))
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

        mCardSpecificDeck = Database.mCardDeckDao.fetchDecksByCardId(mCardId);

        mDeckList = Database.mDeckDao.fetchAllDecks();
        mIsPartOfDeckList = new boolean[mDeckList.size()];
        for (int counter = 0; counter < mIsPartOfDeckList.length; counter++) {
            for (int i = 0; i < mCardSpecificDeck.size(); i++) {
                if (mDeckList.get(counter).getDeckId() == (mCardSpecificDeck.get(i).getDeckId()))
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

    public void deckInfoButtonClicked() {

        deckListAlertDialog = new AlertDialog.Builder(this);


        if (currentState == CardInfoState.NEW) {
            setupDialogNewEdit("Add/Remove Decks:");
        } else if (currentState == CardInfoState.EDIT) {
            setupDialogNewEdit("Add/Remove Decks:");
        } else {
            setupDialogView();
        }

        deckListAlertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelDialogAction();
                if (currentState != CardInfoState.VIEW)
                    Toast.makeText(CardInfoActivity.this, "Canceled", Toast.LENGTH_SHORT).show();

            }
        });
        deckListAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancelDialogAction();
                if (currentState != CardInfoState.VIEW)
                    Toast.makeText(CardInfoActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        });

        if (currentState == CardInfoState.NEW || currentState == CardInfoState.EDIT) {

            deckListAlertDialog.setNeutralButton("+ Create", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    createUpdateDeckDialog().show();
                }
            });
        }

        final AlertDialog alert = deckListAlertDialog.create();

        Helper.getInstance().hideKeyboard(this);
        alert.show();

    }

    private void setupDialogNewEdit(String message) {

        deckListAlertDialog.setTitle(message);
        // display a checkbox list

        if (mDeckListDisplayName.length == 0) {
            final TextView input = new TextView(this);
            deckListAlertDialog.setMessage("No decks");
            deckListAlertDialog.setView(input);
        }

        deckListAlertDialog.setMultiChoiceItems(mDeckListDisplayName, mIsPartOfDeckList, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                // selects the item - does it automatically
            }
        });

        deckListAlertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(deckListIsDifferent()){
                    Toast.makeText(CardInfoActivity.this, "Decks modified", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CardInfoActivity.this, "Nothing to change", Toast.LENGTH_SHORT).show();
                }


                resetNumberOfDecksCounter();

            }
        });
    }

    private void setupDialogView() {
        deckListAlertDialog.setTitle("Decks:");
        // display a normal list

        if (mDeckListDisplayName.length == 0) {
            final TextView input = new TextView(this);
            deckListAlertDialog.setMessage("No decks");
            deckListAlertDialog.setView(input);
        } else {
            final ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter<CharSequence>(
                    this,
                    android.R.layout.simple_list_item_1);
            arrayAdapter.addAll(mDeckListDisplayName);
            deckListAlertDialog.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int indexSelected) {
                    long deckIdReturn = mCardSpecificDeck.get(indexSelected).getDeckId();
                    getIntent().putExtra("deckIdReturn", deckIdReturn);
                    setResult(1, getIntent());
                    setKeyboardVisibility(false);
                    finish();
                }
            });
        }
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
                        Toast.makeText(CardInfoActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
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

    private AlertDialog createUpdateDeckDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_CLASS_TEXT);
        input.setSingleLine();

        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.default_padding);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.default_padding);

        input.setLayoutParams(params);
        container.addView(input);

        input.setHint("Deck name");
        builder.setCustomTitle(Helper.getInstance().customTitle("New Deck"));

        builder.setView(container);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            // when button OK is press
            public void onClick(DialogInterface dialog, int which) {
                // init later
            }
        });
        // if cancel button is press, close dialog
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                deckInfoButtonClicked();
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
                        String deckNameHolder = input.getText().toString();
                        // check if valid name
                        if (deckNameHolder.trim().isEmpty()) {

                            Toast.makeText(CardInfoActivity.this, "Invalid name: cannot be empty", Toast.LENGTH_SHORT).show();
                            return;
                        } else {

                            Database.mDeckDao.createDeck(deckNameHolder);
                            Toast.makeText(CardInfoActivity.this, "Deck created", Toast.LENGTH_SHORT).show();

                            if (currentState == CardInfoState.EDIT)
                                setStateEdit();
                            else
                                setStateNew();


                            alertDialog.dismiss();
                            deckInfoButtonClicked();

                        }
                    }
                });
            }
        });

        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        input.setSelection(input.getText().length());
        return alertDialog;


    }


    private void confirmationYes(ConfirmationEventId id) {
        switch (id) {
            case CANCEL:
                // reset normal
                setStateView();

                break;
            case HOME:
                setKeyboardVisibility(false);
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
        if (question.trim().isEmpty() || answer.trim().isEmpty()) {
            String toastMessageError = "";
            Profile profile = Database.mUserDao.fetchActiveProfile();
            String questionLabel = profile.getFrontLabel();
            String answerLabel = profile.getBackLabel();
            if (question.trim().isEmpty() && answer.trim().isEmpty())
                toastMessageError = questionLabel + " and " + answerLabel + " cannot be empty";
            else if (question.trim().isEmpty() && !answer.trim().isEmpty())
                toastMessageError = questionLabel + " cannot be empty";
            else if (!question.trim().isEmpty() && answer.trim().isEmpty())
                toastMessageError = answerLabel + " cannot be empty";

            Toast.makeText(this, (String) toastMessageError,
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            // create new card
            int newCardId = Database.mCardDao.createCard(question, answer, note);
            if (newCardId == -1) {
                Toast.makeText(this, "Error while adding card to database",
                        Toast.LENGTH_SHORT).show();
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
                Database.mCardDeckDao.createCardDeck(newCardId, checkedDecks.get(counter).getDeckId());
            }

            Toast.makeText(this, "Card created",
                    Toast.LENGTH_SHORT).show();
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

                if (question.trim().isEmpty() || answer.trim().isEmpty()) {
                    String toastMessageError = "";
                    Profile profile = Database.mUserDao.fetchActiveProfile();
                    String questionLabel = profile.getFrontLabel();
                    String answerLabel = profile.getBackLabel();
                    if (question.trim().isEmpty() && answer.trim().isEmpty())
                        toastMessageError = questionLabel + " and " + answerLabel + " cannot be empty";
                    else if (question.trim().isEmpty() && !answer.trim().isEmpty())
                        toastMessageError = questionLabel + " cannot be empty";
                    else if (!question.trim().isEmpty() && answer.trim().isEmpty())
                        toastMessageError = answerLabel + " cannot be empty";

                    Toast.makeText(this, (String) toastMessageError,
                            Toast.LENGTH_SHORT).show();
                    return false;

                } else {
                    // create new card
                    Database.mCardDao.updateCard(mCardId, question, answer, note);
                }

            } else {
                for (int counter = 0; counter < mIsPartOfDeckList.length; counter++) {
                    if (mIsPartOfDeckList[counter] != mInitPartOfDeckTemp[counter]) {
                        if (mIsPartOfDeckList[counter] == true) {
                            Database.mCardDeckDao.createCardDeck(mCardId, mDeckList.get(counter).getDeckId());
                        } else {
                            Database.mCardDeckDao.deleteCardDeck(mCardId, mDeckList.get(counter).getDeckId());
                        }
                    }
                }
            }

            if (areFieldChanged() && isDecksChanged())
                Toast.makeText(this, "Card modified", Toast.LENGTH_LONG).show();
            else if (areFieldChanged())
                Toast.makeText(this, "Card information modified", Toast.LENGTH_LONG).show();
            else if (isDecksChanged())
                Toast.makeText(this, "Card's Decks modified", Toast.LENGTH_LONG).show();

            return true;
        } else {
            Toast.makeText(this, "Nothing to change", Toast.LENGTH_LONG).show();
            return true;
        }
    }

    private boolean deckListIsDifferent(){

        for(int i = 0; i < mTempPartOfDeckList.length; i++){
            if(mTempPartOfDeckList[i] != mIsPartOfDeckList[i])
                return true;
        }

        return false;
    }

    /////// COMPONENTS HELPER ///////

    private void resetFields() {
        questionEditTextView.setText("");
        answerEditTextView.setText("");
        noteEditTextView.setText("");
    }

    private void setFieldsInfo() {
        Card card = Database.mCardDao.fetchCardById(mCardId);


        if (currentState == CardInfoState.VIEW) {
            answerTextView.setText(card.getAnswer());
            questionTextView.setText(card.getQuestion());
            noteTextView.setText(card.getMoreInfo());

            answerTextView.scrollTo(0, 0);
            questionTextView.scrollTo(0, 0);
            noteTextView.scrollTo(0, 0);

        } else {
            answerEditTextView.setText(card.getAnswer());
            questionEditTextView.setText(card.getQuestion());
            noteEditTextView.setText(card.getMoreInfo());

            answerEditTextView.scrollTo(0, 0);
            questionEditTextView.scrollTo(0, 0);
            noteEditTextView.scrollTo(0, 0);
        }


        dateCreatedTV.setText((card.getDateCreated()));
        dateModifiedTV.setText((card.getDateModified()));
    }

    private void setEditTextEditable(boolean isEditable) {
        if (!isEditable) {
            noteEditTextView.setVisibility(View.GONE);
            answerEditTextView.setVisibility(View.GONE);
            questionEditTextView.setVisibility(View.GONE);
            noteTextView.setVisibility(View.VISIBLE);
            answerTextView.setVisibility(View.VISIBLE);
            questionTextView.setVisibility(View.VISIBLE);

            noteTextView.setTextIsSelectable(true);
            answerTextView.setTextIsSelectable(true);
            questionTextView.setTextIsSelectable(true);

            TypedValue typedValue = new TypedValue();
            TypedArray a = this.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorPrimary });
            int color = a.getColor(0, 0);
            a.recycle();

            separator1.setBackgroundColor(color);
            separator2.setBackgroundColor(color);

        } else {
            noteEditTextView.setVisibility(View.VISIBLE);
            answerEditTextView.setVisibility(View.VISIBLE);
            questionEditTextView.setVisibility(View.VISIBLE);
            noteTextView.setVisibility(View.GONE);
            answerTextView.setVisibility(View.GONE);
            questionTextView.setVisibility(View.GONE);

            noteEditTextView.setSelection(noteEditTextView.getText().length());
            questionEditTextView.setSelection(questionEditTextView.getText().length());
            answerEditTextView.setSelection(answerEditTextView.getText().length());

            separator1.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            separator2.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        }


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

    private void setKeyboardVisibility(boolean isVisible) {
        if (isVisible) {

            if (questionEditTextView.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(questionEditTextView, InputMethodManager.SHOW_IMPLICIT);
            }

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

    private void setStopScrollingOnView(final View view) {
        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int lineCount = ((TextView) view).getLineCount();
                if (lineCount > 3)
                    view.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });
    }

    private void reserveButtonClicked() {

        String questionString = questionEditTextView.getText().toString();
        questionEditTextView.setText(answerEditTextView.getText().toString());
        answerEditTextView.setText(questionString);

        ObjectAnimator.ofFloat(reverseButton, "rotation", 0, 180).start();

    }

}
