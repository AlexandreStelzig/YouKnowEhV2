package stelztech.youknowehv4.fragments.review;


import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import stelztech.youknowehv4.activities.MainActivityManager;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.card.Card;
import stelztech.youknowehv4.database.deck.Deck;
import stelztech.youknowehv4.database.profile.Profile;
import stelztech.youknowehv4.fragments.FragmentCommon;
import stelztech.youknowehv4.utilities.CardUtilities;
import stelztech.youknowehv4.utilities.Helper;
import stelztech.youknowehv4.manager.FloatingActionButtonManager;

import static stelztech.youknowehv4.database.carddeck.CardDeck.REVIEW_TOGGLE_ID;

/**
 * Created by alex on 2017-04-03.
 */

public class ReviewFragment extends FragmentCommon {

    View view;

    public ReviewFragment(int animationLayoutPosition, boolean animationFade) {
        super(animationLayoutPosition, animationFade);
    }

    public enum ReviewToggle {
        HOURS_3,
        HOURS_12,
        HOURS_24,
        HOURS_48,
        DAYS_3,
        DAYS_5,
        ALWAYS
    }

    private final int TOGGLE_FOREVER = -1;
    private final int TOGGLE_REMOVE = -2;

    // Spinner
    private Spinner spinner;
    private ArrayAdapter<String> deckArrayAdapter;
    private List<Deck> deckList;

    // components
    private TextView questionTextView;
    private TextView answerTextView;
    private Button showButton;
    private Button nextButton;
    private Button infoButton;
    private LinearLayout reverseLayout;
    private Button reverseButton;
    private TextView orientationTextView;
    private TextView reviewNbCards;

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


    private final int SELECT_ALL_CARDS = 0;
    private final int SPINNER_OFFSET = 1;

    private boolean alwaysShowAnswer = false;

    // orientation labels
    private String orientationQuestionAnswer;
    private String orientationAnswerQuestion;
    private String questionLabel;
    private String answerLabel;

    // show previous
    private boolean showingPrevious = false;
    private Card previousCard;

    // show undo
    private boolean showingUndo = false;
    private Card undoCard;

    private boolean loading = false;

    private int selectedSpinnerPosition = 0;

    private boolean firstTimeOpening;
    private boolean loadNewData = false;

    private boolean[] tempPartOfDeckList;
    private boolean[] isPartOfDeckList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_review, container, false);

        FloatingActionButtonManager.getInstance().setState(FloatingActionButtonManager.ActionButtonState.GONE, getActivity());
        setHasOptionsMenu(true);

        firstTimeOpening = true;
        loadNewData = false;
        // init
        spinner = ((Spinner) view.findViewById(R.id.practice_spinner));
        questionTextView = (TextView) view.findViewById(R.id.practice_question);
        answerTextView = (TextView) view.findViewById(R.id.practice_answer);
        showButton = (Button) view.findViewById(R.id.practice_show_button);
        nextButton = (Button) view.findViewById(R.id.practice_next_button);
        infoButton = (Button) view.findViewById(R.id.practice_info);
        reviewNbCards = (TextView) view.findViewById(R.id.practice_nb_cards);
        reverseLayout = (LinearLayout) view.findViewById(R.id.practice_reverse_layout);
        reverseButton = (Button) view.findViewById(R.id.practice_reverse_button);
        orientationTextView = (TextView) view.findViewById(R.id.practice_orientation);
        progressBar = (ProgressBar) view.findViewById(R.id.practice_progress_bar);


        // init orientation text holders
        Profile currentProfile = Database.mUserDao.fetchActiveProfile();
        questionLabel = currentProfile.getQuestionLabel();
        answerLabel = currentProfile.getAnswerLabel();

        orientationQuestionAnswer = questionLabel + " - " + answerLabel;
        orientationAnswerQuestion = answerLabel + " - " + questionLabel;

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelectedDeckAll() && !(Database.mUserDao.fetchUser().isAllowPracticeAll())) {
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
                if (isSelectedDeckAll() && !(Database.mUserDao.fetchUser().isAllowPracticeAll())) {
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
        setButtonsEnable();

        return view;
    }

    private void setShowButtonEnable() {
        if(alwaysShowAnswer){
            showButton.setEnabled(false);
        }else{
            setButtonsEnable();
        }
        resetShowButtonLabel();
    }

    private void resetShowButtonLabel() {
        if (!alwaysShowAnswer) {
            showButton.setText("show");
        } else {
            showButton.setText("always showing");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.toolbar_review, menu);

        ActionBar actionBar = ((MainActivityManager) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        getActivity().findViewById(R.id.spinner_nav_layout).setVisibility(View.GONE);

        final MenuItem previousMenuItem = menu.findItem(R.id.action_previous);
        if(showingPrevious || previousCard == null && currentQuestion == 0 || showingUndo){
            previousMenuItem.setEnabled(false);
            previousMenuItem.getIcon().setAlpha(50);
        }else{
            previousMenuItem.setEnabled(true);
            previousMenuItem.getIcon().setAlpha(255);
        }

        final MenuItem undoMenuItem = menu.findItem(R.id.undo_toggle_review);
        if(showingUndo || undoCard == null){
            undoMenuItem.setEnabled(false);
            undoMenuItem.getIcon().setAlpha(50);
        }else{
            undoMenuItem.setEnabled(true);
            undoMenuItem.getIcon().setAlpha(255);
        }

        final MenuItem cardDecksMenuItem = menu.findItem(R.id.action_modify_card_decks);
        final MenuItem cardQuickInfoMenuItem = menu.findItem(R.id.action_quick_info);
        final MenuItem shuffleOrderMenuItem = menu.findItem(R.id.action_shuffle);
        if(mCardList == null || mCardList.isEmpty()){
            cardDecksMenuItem.setEnabled(false);
            cardDecksMenuItem.getIcon().setAlpha(50);
            cardQuickInfoMenuItem.setEnabled(false);
            cardQuickInfoMenuItem.getIcon().setAlpha(50);
            shuffleOrderMenuItem.setEnabled(false);
            shuffleOrderMenuItem.getIcon().setAlpha(50);
        }else{
            cardDecksMenuItem.setEnabled(true);
            cardDecksMenuItem.getIcon().setAlpha(255);
            cardQuickInfoMenuItem.setEnabled(true);
            cardQuickInfoMenuItem.getIcon().setAlpha(255);
            shuffleOrderMenuItem.setEnabled(true);
            shuffleOrderMenuItem.getIcon().setAlpha(255);
        }

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

                    if (!isSelectedDeckAll() || (Database.mUserDao.fetchUser().isAllowPracticeAll())) {
                        setQuestionAnswerText();
                    }

                    setShowButtonEnable();
                    return true;
                case R.id.action_previous:

                    if (currentQuestion != 0) {
                        currentQuestion--;
                        setQuestionAnswerText();
                        setNbPracticeCards();
                    } else if (previousCard != null && !showingPrevious && !showingUndo) {
                        showingPrevious = true;
                        getActivity().invalidateOptionsMenu();
                        setQuestionAnswerText();
                        setNbPracticeCards();
                    } else {
                        Toast.makeText(getContext(), "Cannot show previous card", Toast.LENGTH_SHORT).show();
                    }
                    getActivity().invalidateOptionsMenu();
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
                        toggleFromReview(Database.mUserDao.fetchUser().getQuickToggleHours());
                    } else {
                        Toast.makeText(getContext(), "Cannot toggle from review", Toast.LENGTH_SHORT).show();
                    }
                    return true;

                case R.id.action_modify_card_decks:
                    if (mCardList != null && !mCardList.isEmpty()) {
                        if (showingUndo) {
                            showModifyCardDecksDialog(undoCard);
                        } else if (showingPrevious) {
                            showModifyCardDecksDialog(previousCard);
                        } else {
                            showModifyCardDecksDialog(mCardList.get(questionOrder.get(currentQuestion)));
                        }
                    } else {
                        Toast.makeText(getContext(), "Please select a deck", Toast.LENGTH_SHORT).show();
                    }

                    return true;
                case R.id.action_quick_info:
                    if (mCardList != null && !mCardList.isEmpty()) {
                        if (!(isSelectedDeckAll() && !(Database.mUserDao.fetchUser().isAllowPracticeAll()))) {

                            Deck deckAssociated;
                            if (!isSelectedDeckAll())
                                deckAssociated = deckList.get(selectedSpinnerPosition - 1);
                            else
                                deckAssociated = null;

                            if (showingUndo)
                                CardUtilities.showQuickInfoCard(getActivity(), undoCard, deckAssociated);
                            else if (showingPrevious)
                                CardUtilities.showQuickInfoCard(getActivity(), previousCard, deckAssociated);
                            else
                                CardUtilities.showQuickInfoCard(getActivity(), mCardList.get(questionOrder.get(currentQuestion)), deckAssociated);
                        }
                    }
                    return true;

                case R.id.action_shuffle:
                    previousCard = undoCard = null;
                    showingUndo = showingPrevious = false;
                    getActivity().invalidateOptionsMenu();
                    loadNewData = true;
                    switchPracticeCards();

                    return true;
                case R.id.undo_toggle_review:
                    if (undoCard != null && !showingUndo) {
                        undoLastToggledClicked();
                    } else {
                        Toast.makeText(getContext(), "Cannot undo toggle review", Toast.LENGTH_SHORT).show();
                    }

                    return true;
            }

        }

        return super.

                onOptionsItemSelected(item);
    }

    private void undoLastToggledClicked() {
        int deckPosition = selectedSpinnerPosition - 1;
        Database.mCardDeckDao.changeCardReviewTime(undoCard.getCardId(),
                deckList.get(deckPosition).getDeckId(), REVIEW_TOGGLE_ID);
        mCardList.add(undoCard);
        questionList.add(undoCard.getQuestion());
        answerList.add(undoCard.getAnswer());
        questionOrder.add(findCardIndex(undoCard));
        showingPrevious = false;
        showingUndo = true;
        getActivity().invalidateOptionsMenu();
        setQuestionAnswerText();
        setNbPracticeCards();
        String cardInfo = "\'" + undoCard.getQuestion() + "/" + undoCard.getAnswer() + "\'";
        Toast.makeText(getContext(), cardInfo + " added back to review", Toast.LENGTH_SHORT).show();
        setButtonsEnable();
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
        if (showingUndo)
            nbCardsString = "Undo / ";
        else if (showingPrevious)
            nbCardsString = "Previous / ";
        else if (mCardList.size() != 0)
            nbCardsString = (currentQuestion + 1) + " / ";
        nbCardsString += mCardList.size() + " ";

        if (mCardList.size() == 1)
            nbCardsString += "Review Card";
        else
            nbCardsString += "Review Cards";

        reviewNbCards.setText(nbCardsString);
    }

    private void initSpinner() {
        if (deckArrayAdapter != null)
            deckArrayAdapter.clear();
        if (deckList != null)
            deckList.clear();

        deckList = Database.mDeckDao.fetchAllDecks();
        final List<String> deckListString = new ArrayList<>();


        if (Database.mUserDao.fetchUser().isAllowPracticeAll())
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

                if (position == SELECT_ALL_CARDS && !(Database.mUserDao.fetchUser().isAllowPracticeAll())) {
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
                    if (!(Database.mUserDao.fetchUser().isAllowPracticeAll())) {
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
                if (position == SELECT_ALL_CARDS && !(Database.mUserDao.fetchUser().isAllowPracticeAll())) {
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

    private class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

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

        if (isSelectedDeckAll() && !(Database.mUserDao.fetchUser().isAllowPracticeAll())) {

            questionTextView.setText("Select Deck");
            answerTextView.setText("");
            reviewNbCards.setText("0 Review Cards");
            progressBar.setVisibility(View.GONE);

        } else {
            if (loadNewData || mCardList.isEmpty()) {
                progressBar.setVisibility(View.VISIBLE);
                questionTextView.setVisibility(View.GONE);
                answerTextView.setText("");
                reviewNbCards.setVisibility(View.GONE);
                ((MainActivityManager) getActivity()).enableDrawerSwipe(false);

                previousCard = null;
                showingUndo = showingPrevious = false;
                getActivity().invalidateOptionsMenu();

                int selectedDeck = selectedSpinnerPosition;
                spinner.setEnabled(false);

                currentQuestion = 0;
                new LoadData().execute(selectedDeck);
            } else {
                progressBar.setVisibility(View.GONE);

                setNbPracticeCards();
                setQuestionAnswerText();
            }

            questionTextView.setTextIsSelectable(true);
            answerTextView.setTextIsSelectable(true);
        }

    }

    // helpers
    private void nextButtonClicked() {

        if (questionOrder.size() < 2 && !showingUndo && !showingPrevious) {
            String message = "";
            if (questionOrder.size() == 0) {
                message = "No Review Cards";
            } else if (questionOrder.size() == 1) {
                message = "Only one Review card in deck";
            }
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        } else {

            if (showingPrevious || showingUndo) {
                if(showingUndo)
                    undoCard = null;
                showingUndo = showingPrevious = false;
                getActivity().invalidateOptionsMenu();
                setQuestionAnswerText();
            } else {
                showingUndo = showingPrevious = false;
                getActivity().invalidateOptionsMenu();
                answerTextView.scrollTo(0, 0);
                questionTextView.scrollTo(0, 0);

                if ((currentQuestion + 1) < questionOrder.size()) {
                    currentQuestion++;
                    setQuestionAnswerText();
                } else {
                    // reset
                    previousCard = mCardList.get(questionOrder.get(currentQuestion));

                    randomizeQuestionOrder();
                    setQuestionAnswerText();
                    Toast.makeText(getContext(), "Order reset", Toast.LENGTH_SHORT).show();
                }
            }

            resetShowButtonLabel();
        }

        setNbPracticeCards();
    }

    private void showButtonClicked() {

        if (questionOrder.size() == 0) {
            Toast.makeText(getContext(), "No Review Cards", Toast.LENGTH_SHORT).show();
        } else {

            if (answerHidden) {

                if (showingUndo) {
                    if (isReverseOrder)
                        answerTextView.setText(undoCard.getQuestion());
                    else
                        answerTextView.setText(undoCard.getAnswer());
                } else if (showingPrevious) {
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
                    if (showingUndo) {
                        answerTextView.setText(undoCard.getAnswer());
                    }
                    if (showingPrevious) {
                        answerTextView.setText(previousCard.getAnswer());
                    } else {
                        answerTextView.setText(answerList.get(questionOrder.get(currentQuestion)));
                    }
                }

                answerHidden = true;
                resetShowButtonLabel();
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


        if (showingUndo) {
            if (isReverseOrder) {
                questionTextView.setText(undoCard.getAnswer());
                if (!alwaysShowAnswer)
                    answerTextView.setText(answerHiddenString);
                else
                    answerTextView.setText(undoCard.getQuestion());
            } else {
                questionTextView.setText(undoCard.getQuestion());
                if (!alwaysShowAnswer)
                    answerTextView.setText(answerHiddenString);
                else
                    answerTextView.setText(undoCard.getAnswer());
            }
            return;
        }


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
                if (showingUndo)
                    answerTextView.setText(undoCard.getAnswer());
                else if (showingPrevious)
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
        String[] options = new String[]{helper.convertPracticeToggleToString(ReviewToggle.ALWAYS), helper.convertPracticeToggleToString(ReviewToggle.HOURS_3),
                helper.convertPracticeToggleToString(ReviewToggle.HOURS_12),
                helper.convertPracticeToggleToString(ReviewToggle.HOURS_24), helper.convertPracticeToggleToString(ReviewToggle.HOURS_48),
                helper.convertPracticeToggleToString(ReviewToggle.DAYS_3), helper.convertPracticeToggleToString(ReviewToggle.DAYS_5)};

        Card currentCard = mCardList.get(currentQuestion);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Toggle From Review: " + currentCard.getQuestion() + " / " + currentCard.getAnswer())
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                toggleFromReview(TOGGLE_FOREVER);
                                break;
                            case 1:
                                toggleFromReview(3);
                                break;
                            case 2:
                                toggleFromReview(12);
                                break;
                            case 3:
                                toggleFromReview(24);
                                break;
                            case 4:
                                toggleFromReview(48);
                                break;
                            case 5:
                                toggleFromReview(72);
                                break;
                            case 6:
                                toggleFromReview(120);
                                break;

                        }
                    }
                });
        builder.create().show();
    }

    private void toggleFromReview(int hours) {
        String cardInfo = "";

        if (showingUndo) {
            int deckPosition = selectedSpinnerPosition - 1;

            showingUndo = false;

            Database.mCardDeckDao.changeCardReviewTime(undoCard.getCardId(),
                    deckList.get(deckPosition).getDeckId(), hours);

            cardInfo = "\'" + undoCard.getQuestion() + "/" + undoCard.getAnswer() + "\'";

            int undoCardIndex = findCardIndex(undoCard);

            if(undoCardIndex != -1) {
                int previousCardOrderIndex = findQuestionIndex(undoCardIndex);
                mCardList.remove(undoCardIndex);
                questionList.remove(undoCardIndex);
                answerList.remove(undoCardIndex);
                questionOrder.remove(previousCardOrderIndex);

                for (int i = 0; i < questionOrder.size(); i++) {
                    int temp = questionOrder.get(i);
                    if (questionOrder.get(i) > undoCardIndex)
                        questionOrder.set(i, temp - 1);
                }

                if (previousCardOrderIndex < currentQuestion)
                    currentQuestion--;
            }
        } else if (showingPrevious) {

            undoCard = previousCard;

            int deckPosition = selectedSpinnerPosition - 1;

            showingPrevious = false;
            getActivity().invalidateOptionsMenu();


            Database.mCardDeckDao.changeCardReviewTime(previousCard.getCardId(),
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

            previousCard = null;

        } else {

            int deckPosition = selectedSpinnerPosition - 1;
            int indexRemoved = questionOrder.get(currentQuestion);

            Card removeCard = mCardList.get(indexRemoved);

            undoCard = removeCard;

            Database.mCardDeckDao.changeCardReviewTime(removeCard.getCardId(),
                    deckList.get(deckPosition).getDeckId(), hours);


            cardInfo = "\'" + removeCard.getQuestion() + "/" + removeCard.getAnswer() + "\'";


            mCardList.remove(indexRemoved);
            questionList.remove(indexRemoved);
            answerList.remove(indexRemoved);

            questionOrder.remove(currentQuestion);

            for (int i = 0; i < questionOrder.size(); i++) {
                int temp = questionOrder.get(i);
                if (questionOrder.get(i) > indexRemoved)
                    questionOrder.set(i, temp - 1);
            }

            if (previousCard != null && removeCard.getCardId() == previousCard.getCardId()) {
                previousCard = null;
                getActivity().invalidateOptionsMenu();
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

        setNbPracticeCards();
        resetShowButtonLabel();
        setButtonsEnable();
        getActivity().invalidateOptionsMenu();

        String lengthMessage = "";
        if (hours == TOGGLE_FOREVER)
            lengthMessage = "until manual toggle";
        else
            lengthMessage = "for " + hours + " hours";

        if (hours != TOGGLE_REMOVE)
            Toast.makeText(getContext(), cardInfo + " toggled from review " + lengthMessage, Toast.LENGTH_SHORT).show();
    }

    private void showModifyCardDecksDialog(final Card card) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Modify " + card.getQuestion() + "/" + card.getAnswer() + " decks");

        List<Deck> cardSpecificDeck = Database.mCardDeckDao.fetchDecksByCardId(card.getCardId());

        isPartOfDeckList = new boolean[deckList.size()];
        for (int counter = 0; counter < isPartOfDeckList.length; counter++) {
            for (int i = 0; i < cardSpecificDeck.size(); i++) {
                if (deckList.get(counter).getDeckId() == (cardSpecificDeck.get(i).getDeckId()))
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
        builder.setMultiChoiceItems(deckListDisplayName, isPartOfDeckList, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {


            }

        });

        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (deckListIsDifferent()) {


                    for (int i = 0; i < tempPartOfDeckList.length; i++) {
                        if (tempPartOfDeckList[i] != isPartOfDeckList[i]) {
                            if (tempPartOfDeckList[i]) {
                                // remove

                                if (i == deckPosition) {
                                    // put the card in a temp position, not reviewing
                                    toggleFromReview(TOGGLE_REMOVE);
                                }

                                Database.mCardDeckDao.deleteCardDeck(card.getCardId(), deckList.get(i).getDeckId());
                            } else {
                                // add
                                Database.mCardDeckDao.createCardDeck(card.getCardId(), deckList.get(i).getDeckId());
                            }
                        }
                    }


                    Toast.makeText(getContext(), "Decks modified", Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });

        final android.app.AlertDialog alert = builder.create();

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
                mCardList = Database.mCardDao.fetchAllCards();
            } else {
                int deckId = deckList.get(selectedDeck - SPINNER_OFFSET).getDeckId();
                Database.mCardDeckDao.revalidateReviewCardsByDeckId(deckId);
                mCardList = Database.mCardDeckDao.fetchReviewCardsByDeckId(deckId);
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

            setNbPracticeCards();
            setQuestionAnswerText();
            setButtonsEnable();


            ((MainActivityManager) getActivity()).enableDrawerSwipe(true);


            loading = false;

            spinner.setEnabled(true);

            resetShowButtonLabel();


            undoCard = previousCard = null;
            showingPrevious = showingUndo = false;
            getActivity().invalidateOptionsMenu();

            progressBar.setVisibility(View.GONE);
            questionTextView.setVisibility(View.VISIBLE);
            answerTextView.setVisibility(View.VISIBLE);
            reviewNbCards.setVisibility(View.VISIBLE);

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

    public void setButtonsEnable() {

        if (isSelectedDeckAll() && !(Database.mUserDao.fetchUser().isAllowPracticeAll()) || mCardList.isEmpty()) {
            nextButton.setEnabled(false);
            showButton.setEnabled(false);
        } else {
            nextButton.setEnabled(true);
            showButton.setEnabled(!alwaysShowAnswer);
        }

        infoButton.setEnabled(!isSelectedDeckAll());
    }

}
