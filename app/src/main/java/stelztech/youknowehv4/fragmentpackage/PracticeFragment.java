package stelztech.youknowehv4.fragmentpackage;


import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activitypackage.MainActivityManager;
import stelztech.youknowehv4.database.DatabaseManager;
import stelztech.youknowehv4.helper.Helper;
import stelztech.youknowehv4.manager.ActionButtonManager;
import stelztech.youknowehv4.model.Card;
import stelztech.youknowehv4.model.Deck;
import stelztech.youknowehv4.model.Profile;

/**
 * Created by alex on 2017-04-03.
 */

public class PracticeFragment extends Fragment {

    View view;

    public enum PracticeToggle {
        HOURS_3,
        HOURS_12,
        HOURS_24,
        HOURS_48,
        DAYS_3,
        DAYS_5,
        ALWAYS
    }

    // Spinner
    private Spinner spinner;
    private ArrayAdapter<String> deckArrayAdapter;
    private List<Deck> deckList;

    // database
    private DatabaseManager dbManager;

    // components
    private TextView questionTextView;
    private TextView answerTextView;
    private Button showButton;
    private Button nextButton;
    private Button infoButton;
    private LinearLayout reverseLayout;
    private Button reverseButton;
    private TextView orientationTextView;
    private TextView praticeNbCards;

    // question answers
    private List<String> questionList = new ArrayList<>();
    private List<String> answerList = new ArrayList<>();
    private List<Integer> questionOrder = new ArrayList<>();
    private int currentQuestion = 0;

    private ProgressBar progressBar;

    // hidden text
    private boolean answerHidden = true;
    private String answerHiddenString = "----";

    // reverse order
    private boolean isReverseOrder = false;

    private List<Card> mCardList = new ArrayList<>();
    ;

    private final int SELECT_ALL_CARDS = 0;
    private final int SPINNER_OFFSET = 1;

    private boolean alwaysShowAnswer = false;

    // orientation labels
    private String orientationQuestionAnswer;
    private String orientationAnswerQuestion;
    private String questionLabel;
    private String answerLabel;

    // show previous
    private boolean showPreviousIsOkay = false;
    private boolean showingPrevious = false;
    private Card previousCard;

    private boolean loading = false;

    private int selectedSpinnerPosition = 0;

    private int nbCards;
    private boolean firstTimeOpening;
    private boolean loadNewData = false;

    private boolean[] tempPartOfDeckList;
    private boolean[] isPartOfDeckList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_practice, container, false);

        ActionButtonManager.getInstance().setState(ActionButtonManager.ActionButtonState.GONE, getActivity());
        setHasOptionsMenu(true);

        firstTimeOpening = true;
        loadNewData = false;
        // init
        dbManager = DatabaseManager.getInstance(getActivity());
        spinner = ((Spinner) view.findViewById(R.id.practice_spinner));
        questionTextView = (TextView) view.findViewById(R.id.practice_question);
        answerTextView = (TextView) view.findViewById(R.id.practice_answer);
        showButton = (Button) view.findViewById(R.id.practice_show_button);
        nextButton = (Button) view.findViewById(R.id.practice_next_button);
        infoButton = (Button) view.findViewById(R.id.practice_info);
        praticeNbCards = (TextView) view.findViewById(R.id.practice_nb_cards);
        reverseLayout = (LinearLayout) view.findViewById(R.id.practice_reverse_layout);
        reverseButton = (Button) view.findViewById(R.id.practice_reverse_button);
        orientationTextView = (TextView) view.findViewById(R.id.practice_orientation);
        progressBar = (ProgressBar) view.findViewById(R.id.practice_progress_bar);


        // init orientation text holders
        Profile currentProfile = dbManager.getActiveProfile();
        questionLabel = currentProfile.getQuestionLabel();
        answerLabel = currentProfile.getAnswerLabel();

        orientationQuestionAnswer = questionLabel + " - " + answerLabel;
        orientationAnswerQuestion = answerLabel + " - " + questionLabel;

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelectedDeckAll() && !(dbManager.getUser().isAllowPracticeAll())) {
                    Toast.makeText(getContext(), "Select a Deck", Toast.LENGTH_SHORT).show();
                } else {
                    if (!loading)
                        showButtonClicked();
                }

            }
        });

        questionTextView.setMovementMethod(new ScrollingMovementMethod());
        answerTextView.setMovementMethod(new ScrollingMovementMethod());


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelectedDeckAll() && !(dbManager.getUser().isAllowPracticeAll())) {
                    Toast.makeText(getContext(), "Select a Deck", Toast.LENGTH_SHORT).show();
                } else {
                    if (!loading)
                        nextButtonClicked();
                }


            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelectedDeckAll()) {
                    Toast.makeText(getContext(), "Select a Deck", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (deckList.size() > 0) {
                    if (!loading)
                        ((MainActivityManager) getActivity())
                                .displayDeckInfo(deckList.get(selectedSpinnerPosition - SPINNER_OFFSET).getDeckId());
                }

            }
        });


        reverseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!loading)
                    reserveButtonClicked();
            }
        });


        setOrientationText();
        if (firstTimeOpening)
            initSpinner();
        switchPracticeCards();
        setShowButtonEnable();


        return view;
    }

    private void setShowButtonEnable() {

        if (!alwaysShowAnswer) {
            showButton.setText("show");
            showButton.setEnabled(true);
        } else {
            showButton.setText("always showing");
            showButton.setEnabled(false);
        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.toolbar_practice, menu);

        ActionBar actionBar = ((MainActivityManager) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        getActivity().findViewById(R.id.spinner_nav_layout).setVisibility(View.GONE);


        final MenuItem alwaysShowAnswerItem = menu.findItem(R.id.action_always_show_answer);
        if (alwaysShowAnswer)
            alwaysShowAnswerItem.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_check_box_black_24dp));
        else
            alwaysShowAnswerItem.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_check_box_outline_blank_black_24dp));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (!loading) {
            switch (id) {
                case R.id.action_always_show_answer:
                    alwaysShowAnswer = !alwaysShowAnswer;
                    if (alwaysShowAnswer)
                        item.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_check_box_black_24dp));
                    else
                        item.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_check_box_outline_blank_black_24dp));

                    if (isSelectedDeckAll() && (dbManager.getUser().isAllowPracticeAll()) || !isSelectedDeckAll()) {
                        setQuestionAnswerText();
                    }

                    setShowButtonEnable();
                    return true;
                case R.id.action_previous:
                    if (showPreviousIsOkay) {
                        showPreviousIsOkay = false;
                        showingPrevious = true;
                        setQuestionAnswerText();
                        setNbPracticeCards();
                    } else {
                        Toast.makeText(getContext(), "Cannot show previous card", Toast.LENGTH_SHORT).show();
                    }


                    return true;
                case R.id.action_remove_practice:


                    if (mCardList != null && !mCardList.isEmpty() && !isSelectedDeckAll()) {
                        togglePracticeClicked();

                    } else {
                        Toast.makeText(getContext(), "Cannot toggle from review", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                case R.id.action_quick_remove_practice:
                    if (mCardList != null && !mCardList.isEmpty() && !isSelectedDeckAll()) {
                        toggleFromPractice(dbManager.getUser().getQuickToggle());

                    } else {
                        Toast.makeText(getContext(), "Cannot toggle from review", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                case R.id.action_modify_card_decks:
                    if (mCardList != null && !mCardList.isEmpty()) {
                        if (showingPrevious) {
                            showModifyCardDecksDialog(previousCard);
                        } else {
                            showModifyCardDecksDialog(mCardList.get(questionOrder.get(currentQuestion)));
                        }
                    } else {
                        Toast.makeText(getContext(), "Please select a deck", Toast.LENGTH_SHORT).show();
                    }

                    return true;
            }

        }

        return super.onOptionsItemSelected(item);
    }

    private int findCardIndex(Card card) {
        for (int i = 0; i < mCardList.size(); i++) {
            if (mCardList.get(i).getCardId() == card.getCardId())
                return i;
        }
        return -1;
    }


    private int findQuestionIndex(int index) {
        for (int i = 0; i < questionOrder.size(); i++) {
            int temp = questionOrder.get(i);
            if (temp == index)
                return i;
        }
        return -1;
    }

    private void setNbPracticeCards() {
        String nbCardsString = "";
        if (showingPrevious)
            nbCardsString = "Previous / ";
        else if (nbCards != 0)
            nbCardsString = (currentQuestion + 1) + " / ";
        nbCardsString += nbCards + " ";

        if (nbCards == 1)
            nbCardsString += "Review Card";
        else
            nbCardsString += "Review Cards";

        praticeNbCards.setText(nbCardsString);
    }

    private void initSpinner() {
        if (deckArrayAdapter != null)
            deckArrayAdapter.clear();
        if (deckList != null)
            deckList.clear();

        deckList = dbManager.getDecks();
        final List<String> deckListString = new ArrayList<>();


        if (dbManager.getUser().isAllowPracticeAll())
            deckListString.add("All Cards");
        else
            deckListString.add("- Select Deck -");
        spinner.setEnabled(true);


        for (int counter = 0; counter < deckList.size(); counter++) {
            deckListString.add(deckList.get(counter).getDeckName());
        }

        deckArrayAdapter = new ArrayAdapter<String>(
                getContext(), R.layout.custom_spinner_item, deckListString) {


            @Override
            public boolean isEnabled(int position) {

                if (position == SELECT_ALL_CARDS && !(dbManager.getUser().isAllowPracticeAll())) {
                    return false;
                } else {
                    return true;
                }

            }

            @NonNull
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;

                mTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);


                if (position == SELECT_ALL_CARDS) {
                    if (!(dbManager.getUser().isAllowPracticeAll())) {
                        mTextView.setTypeface(null, Typeface.NORMAL);
                        mTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorDivider));
                        mTextView.setTextColor(Color.GRAY);
                    } else {
                        mTextView.setTypeface(null, Typeface.BOLD);
                        mTextView.setTextColor(Color.BLACK);
                    }


                } else {
                    mTextView.setTypeface(null, Typeface.NORMAL);
                    mTextView.setTextColor(Color.BLACK);
                }


                if (position == selectedSpinnerPosition)
                    mView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ripple_grey));
                else
                    mView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ripple_normal));


                return mTextView;
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;
                mTextView.setTextColor(Color.BLACK);
                if (position == SELECT_ALL_CARDS && !(dbManager.getUser().isAllowPracticeAll())) {
                    mTextView.setTextColor(Color.GRAY);
                } else {
                    mTextView.setTextColor(Color.BLACK);
                }
                return mTextView;
            }
        };


        spinner.setAdapter(deckArrayAdapter);

        SpinnerInteractionListener listener = new SpinnerInteractionListener();
        spinner.setOnTouchListener(listener);
        spinner.setOnItemSelectedListener(listener);


    }

    public class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

        boolean userSelect = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            userSelect = true;
            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (userSelect) {
                loadNewData = true;
                selectedSpinnerPosition = pos;
                switchPracticeCards();
                userSelect = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }

    }

    private void switchPracticeCards() {

        if (isSelectedDeckAll() && !(dbManager.getUser().isAllowPracticeAll())) {

            questionTextView.setText("Select Deck");
            answerTextView.setText("");
            praticeNbCards.setText("0 Review Cards");
            progressBar.setVisibility(View.GONE);

        } else {
            if (loadNewData || mCardList.isEmpty()) {
                progressBar.setVisibility(View.VISIBLE);
                questionTextView.setVisibility(View.GONE);
                answerTextView.setText("");
                praticeNbCards.setVisibility(View.GONE);
                ((MainActivityManager) getActivity()).enableDrawerSwipe(false);

                showPreviousIsOkay = false;
                previousCard = null;
                showingPrevious = false;

                int selectedDeck = selectedSpinnerPosition;
                spinner.setEnabled(false);

                currentQuestion = 0;
                new LoadData().execute(selectedDeck);
            } else {
                progressBar.setVisibility(View.GONE);

                nbCards = mCardList.size();
                setNbPracticeCards();
                setQuestionAnswerText();
            }

            questionTextView.setTextIsSelectable(true);
            answerTextView.setTextIsSelectable(true);
        }

    }

    // helpers
    private void nextButtonClicked() {

        if (questionOrder.size() < 2) {
            String message = "";
            if (questionOrder.size() == 0) {
                message = "No Review Cards";
            } else if (questionOrder.size() == 1) {
                message = "Only one Review card in deck";
            }
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        } else {

            if (showingPrevious) {
                showPreviousIsOkay = true;
                showingPrevious = false;
                setQuestionAnswerText();
            } else {
                showPreviousIsOkay = true;
                showingPrevious = false;
                previousCard = mCardList.get(questionOrder.get(currentQuestion));

                answerTextView.scrollTo(0, 0);
                questionTextView.scrollTo(0, 0);

                if ((currentQuestion + 1) < questionOrder.size()) {
                    currentQuestion++;
                    setQuestionAnswerText();
                } else {
                    // reset
                    randomizeQuestionOrder();
                    setQuestionAnswerText();
                    Toast.makeText(getContext(), "Order reset", Toast.LENGTH_SHORT).show();
                }
            }

            if (!alwaysShowAnswer)
                showButton.setText("show");
        }

        setNbPracticeCards();
    }

    private void showButtonClicked() {

        if (questionOrder.size() == 0) {
            Toast.makeText(getContext(), "No Review Cards", Toast.LENGTH_SHORT).show();
        } else {

            if (answerHidden) {
                if (showingPrevious) {
                    if (isReverseOrder)
                        answerTextView.setText(previousCard.getQuestion());
                    else
                        answerTextView.setText(previousCard.getAnswer());
                } else {
                    answerTextView.setText(answerList.get(questionOrder.get(currentQuestion)));
                }

                answerHidden = false;
                if (!alwaysShowAnswer)
                    showButton.setText("hide");
            } else {
                if (!alwaysShowAnswer)
                    answerTextView.setText(answerHiddenString);
                else {
                    if (showingPrevious) {
                        answerTextView.setText(previousCard.getAnswer());
                    } else {
                        answerTextView.setText(answerList.get(questionOrder.get(currentQuestion)));
                    }
                }

                answerHidden = true;
                showButton.setText("show");
            }
        }
    }

    private void randomizeQuestionOrder() {

        if (questionOrder.size() > 1) {

            int lastQuestion = questionOrder.get(currentQuestion);

            long seed = System.nanoTime();
            Collections.shuffle(questionOrder, new Random(seed));

            if (questionOrder.get(0) == lastQuestion && currentQuestion != 0) {
                int random = (int) (Math.random() * (questionOrder.size() - 1));
                Collections.swap(questionOrder, random + 1, 0);
            }

        }
        currentQuestion = 0;
    }

    private void setQuestionAnswerText() {

        answerTextView.scrollTo(0, 0);
        questionTextView.scrollTo(0, 0);

        if (questionOrder.size() == 0) {
            questionTextView.setText("No Review Cards");
            answerTextView.setText("");
        } else {
            if (showingPrevious) {

                if (isReverseOrder) {
                    questionTextView.setText(previousCard.getAnswer());
                    if (!alwaysShowAnswer)
                        answerTextView.setText(answerHiddenString);
                    else
                        answerTextView.setText(previousCard.getQuestion());
                } else {
                    questionTextView.setText(previousCard.getQuestion());
                    if (!alwaysShowAnswer)
                        answerTextView.setText(answerHiddenString);
                    else
                        answerTextView.setText(previousCard.getAnswer());
                }


            } else {
                questionTextView.setText(questionList.get(questionOrder.get(currentQuestion)));
                if (!alwaysShowAnswer)
                    answerTextView.setText(answerHiddenString);
                else
                    answerTextView.setText(answerList.get(questionOrder.get(currentQuestion)));
            }
            answerHidden = true;
        }
    }


    private void setOrientationText() {
        if (isReverseOrder) {
            orientationTextView.setText(orientationAnswerQuestion);
        } else {
            orientationTextView.setText(orientationQuestionAnswer);
        }

    }

    private void setQuestionAnswerOrder() {
        questionList.clear();
        answerList.clear();
        if (!isReverseOrder) {
            for (int counter = 0; counter < questionOrder.size(); counter++) {
                questionList.add(mCardList.get(counter).getQuestion());
                answerList.add(mCardList.get(counter).getAnswer());
            }
        } else {
            for (int counter = 0; counter < questionOrder.size(); counter++) {
                answerList.add(mCardList.get(counter).getQuestion());
                questionList.add(mCardList.get(counter).getAnswer());
            }
        }
    }

    private boolean isSelectedDeckAll() {
        return selectedSpinnerPosition == SELECT_ALL_CARDS;
    }

    private void reserveButtonClicked() {


        ObjectAnimator.ofFloat(reverseButton, "rotation", 0, 180).start();
        isReverseOrder = !isReverseOrder;


        if (!questionList.isEmpty()) {
            boolean temp = answerHidden;
            setQuestionAnswerOrder();
            setQuestionAnswerText();

            if (!temp) {
                if (showingPrevious)
                    answerTextView.setText(previousCard.getAnswer());

                answerTextView.setText(answerList.get(questionOrder.get(currentQuestion)));
                answerHidden = false;
            }
        }


        setOrientationText();
        Toast.makeText(getContext(), "Orientation: " + orientationTextView.getText().toString(), Toast.LENGTH_SHORT).show();
    }


    private void togglePracticeClicked() {

        Helper helper = Helper.getInstance();
        String[] options = new String[]{helper.convertPracticeToggleToString(PracticeToggle.ALWAYS), helper.convertPracticeToggleToString(PracticeToggle.HOURS_3),
                helper.convertPracticeToggleToString(PracticeToggle.HOURS_12),
                helper.convertPracticeToggleToString(PracticeToggle.HOURS_24), helper.convertPracticeToggleToString(PracticeToggle.HOURS_48),
                helper.convertPracticeToggleToString(PracticeToggle.DAYS_3), helper.convertPracticeToggleToString(PracticeToggle.DAYS_5)};

        Card currentCard = mCardList.get(currentQuestion);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Toggle From Review: " + currentCard.getQuestion() + " / " + currentCard.getAnswer())
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                toggleFromPractice(-1);
                                break;
                            case 1:
                                toggleFromPractice(3);
                                break;
                            case 2:
                                toggleFromPractice(12);
                                break;
                            case 3:
                                toggleFromPractice(24);
                                break;
                            case 4:
                                toggleFromPractice(48);
                                break;
                            case 5:
                                toggleFromPractice(72);
                                break;
                            case 6:
                                toggleFromPractice(120);
                                break;

                        }
                    }
                });
        builder.create().show();
    }

    private void toggleFromPractice(int hours) {
        String cardInfo = "";

        if (showingPrevious) {
            int deckPosition = selectedSpinnerPosition - 1;

            showPreviousIsOkay = false;
            showingPrevious = false;


            dbManager.togglePractice_Card(previousCard.getCardId(),
                    deckList.get(deckPosition).getDeckId(), hours);

            cardInfo = "\'" + previousCard.getQuestion() + "/" + previousCard.getAnswer() + "\'";

            int previousCardIndex = findCardIndex(previousCard);
            int previousCardOrderIndex = findQuestionIndex(previousCardIndex);

            mCardList.remove(previousCardIndex);
            questionList.remove(previousCardIndex);
            answerList.remove(previousCardIndex);
            questionOrder.remove(previousCardOrderIndex);

            for (int i = 0; i < questionOrder.size(); i++) {
                int temp = questionOrder.get(i);
                if (questionOrder.get(i) > previousCardIndex)
                    questionOrder.set(i, temp - 1);
            }

            if (previousCardOrderIndex < currentQuestion)
                currentQuestion--;

        } else {

            int deckPosition = selectedSpinnerPosition - 1;
            int indexRemoved = questionOrder.get(currentQuestion);

            dbManager.togglePractice_Card(mCardList.get(indexRemoved).getCardId(),
                    deckList.get(deckPosition).getDeckId(), hours);


            cardInfo = "\'" + mCardList.get(indexRemoved).getQuestion() + "/" + mCardList.get(indexRemoved).getAnswer() + "\'";


            mCardList.remove(indexRemoved);
            questionList.remove(indexRemoved);
            answerList.remove(indexRemoved);

            questionOrder.remove(currentQuestion);

            for (int i = 0; i < questionOrder.size(); i++) {
                int temp = questionOrder.get(i);
                if (questionOrder.get(i) > indexRemoved)
                    questionOrder.set(i, temp - 1);
            }
        }

        if (questionOrder.size() == 0) {
            questionTextView.setText("No Review Cards");
            answerTextView.setText("");
        } else {
            if (currentQuestion == questionOrder.size()) {
                long seed = System.nanoTime();
                Collections.shuffle(questionOrder, new Random(seed));
                currentQuestion = 0;
            }
            setQuestionAnswerText();
        }

        nbCards = mCardList.size();

        setNbPracticeCards();

        showPreviousIsOkay = false;

        String lengthMessage = "";
        if (hours == -1)
            lengthMessage = "until manual toggle";
        else
            lengthMessage = "for " + hours + " hours";
        Toast.makeText(getContext(), cardInfo + " toggled from review " + lengthMessage, Toast.LENGTH_SHORT).show();
    }

    private void showModifyCardDecksDialog(final Card card) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View dialogView = inflater.inflate(R.layout.custom_scrollable_dialog_list, null, false);

        dialogView.findViewById(R.id.card_info_dialog_add_deck).setVisibility(View.GONE);

        android.app.AlertDialog.Builder deckListAlertDialog = new android.app.AlertDialog.Builder(getContext());

        deckListAlertDialog.setTitle("Modify " + card.getQuestion() + "/" + card.getAnswer() + " decks");


        List<Deck> cardSpecificDeck = dbManager.getDecksFromCard(card.getCardId());


        isPartOfDeckList = new boolean[deckList.size()];
        for (int counter = 0; counter < isPartOfDeckList.length; counter++) {
            for (int i = 0; i < cardSpecificDeck.size(); i++) {
                if (deckList.get(counter).getDeckId().equals(cardSpecificDeck.get(i).getDeckId()))
                    isPartOfDeckList[counter] = true;
            }
        }
        tempPartOfDeckList = isPartOfDeckList.clone();


        CharSequence[] deckListDisplayName = new CharSequence[deckList.size()];

        for (int i = 0; i < deckList.size(); i++) {
            deckListDisplayName[i] = deckList.get(i).getDeckName();
        }


        final int deckPosition = selectedSpinnerPosition - 1;

        // display a checkbox list
        deckListAlertDialog.setMultiChoiceItems(deckListDisplayName, isPartOfDeckList, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {


                if (indexSelected == deckPosition) {
                    ((android.app.AlertDialog) dialog).getListView().setItemChecked(indexSelected, true);
                    Toast.makeText(getContext(), "Cannot remove from displayed deck", Toast.LENGTH_SHORT).show();
                }
            }

        });

        deckListAlertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (deckListIsDifferent()) {

                    for (int i = 0; i < tempPartOfDeckList.length; i++) {
                        if (tempPartOfDeckList[i] != isPartOfDeckList[i]){
                            if(tempPartOfDeckList[i]){
                                // remove
                                dbManager.deleteCardDeck(card.getCardId(), deckList.get(i).getDeckId());
                            }else{
                                // add
                                dbManager.createCardDeck(card.getCardId(), deckList.get(i).getDeckId());
                            }
                        }
                    }


                    Toast.makeText(getContext(), "Decks modified", Toast.LENGTH_SHORT).show();
                }

            }
        });

        deckListAlertDialog.setView(dialogView);
        deckListAlertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        deckListAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });

        final android.app.AlertDialog alert = deckListAlertDialog.create();

        alert.show();
    }

    private boolean deckListIsDifferent() {

        for (int i = 0; i < tempPartOfDeckList.length; i++) {
            if (tempPartOfDeckList[i] != isPartOfDeckList[i])
                return true;
        }

        return false;
    }

    private class LoadData extends AsyncTask<Integer, Void, String> {


        @Override
        protected String doInBackground(Integer... params) {
            loading = true;

            int selectedDeck = params[0];

            if (isSelectedDeckAll()) {
                mCardList = dbManager.getCards();
            } else {
                String deckId = deckList.get(selectedDeck - SPINNER_OFFSET).getDeckId();
                mCardList = dbManager.getDeckPracticeCards(deckId);
            }

            questionOrder.clear();

            // init order
            for (int i = 0; i < mCardList.size(); i++) {
                questionOrder.add(i);
            }

            setQuestionAnswerOrder();
            randomizeQuestionOrder();

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            nbCards = mCardList.size();
            setNbPracticeCards();
            setQuestionAnswerText();


            ((MainActivityManager) getActivity()).enableDrawerSwipe(true);


            loading = false;

            spinner.setEnabled(true);

            if (!alwaysShowAnswer)
                showButton.setText("show");

            progressBar.setVisibility(View.GONE);
            questionTextView.setVisibility(View.VISIBLE);
            answerTextView.setVisibility(View.VISIBLE);
            praticeNbCards.setVisibility(View.VISIBLE);

        }
    }

    public boolean isLoading() {
        return loading;
    }

    public void resetFragment() {
        mCardList.clear();
        questionOrder.clear();
        questionList.clear();
        answerList.clear();
    }
}
