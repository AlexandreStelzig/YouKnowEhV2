package stelztech.youknowehv4.fragmentpackage;


import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activitypackage.MainActivityManager;
import stelztech.youknowehv4.database.DatabaseManager;
import stelztech.youknowehv4.manager.ActionButtonManager;
import stelztech.youknowehv4.model.Card;
import stelztech.youknowehv4.model.Deck;
import stelztech.youknowehv4.model.Profile;

/**
 * Created by alex on 2017-04-03.
 */

public class PracticeFragment extends Fragment {

    View view;

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
    private List<String> questionList;
    private List<String> answerList;
    private int[] questionOrder;
    private int currentQuestion;

    // hidden text
    private boolean answerHidden;
    private String answerHiddenString = "----";

    // reverse order
    private boolean isReverseOrder;

    private List<Card> mCardList;

    private final int SELECT_ALL_CARDS = 0;
    private final int SPINNER_OFFSET = 1;

    private boolean alwaysShowAnswer = false;

    // orientation labels
    private String orientationQuestionAnswer;
    private String orientationAnswerQuestion;
    private String questionLabel;
    private String answerLabel;

    // show previous
    private boolean showPreviousIsOkay;
    private boolean showingPrevious;
    private Card previousCard;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_practice, container, false);

        ActionButtonManager.getInstance().setState(ActionButtonManager.ActionButtonState.GONE, getActivity());
        setHasOptionsMenu(true);


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

        questionList = new ArrayList<>();
        answerList = new ArrayList<>();

        currentQuestion = 0;
        answerHidden = true;
        isReverseOrder = false;
        showPreviousIsOkay = false;
        showingPrevious = false;

        // init orientation text holders
        Profile currentProfile = dbManager.getActiveProfile();
        questionLabel = currentProfile.getQuestionLabel();
        answerLabel = currentProfile.getAnswerLabel();

        orientationQuestionAnswer = questionLabel + " - " + answerLabel;
        orientationAnswerQuestion = answerLabel + " - " + questionLabel;

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showButtonClicked();

            }
        });

        questionTextView.setMovementMethod(new ScrollingMovementMethod());
        answerTextView.setMovementMethod(new ScrollingMovementMethod());


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nextButtonClicked();

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
                    ((MainActivityManager) getActivity())
                            .displayDeckInfo(deckList.get(spinner.getSelectedItemPosition() - SPINNER_OFFSET).getDeckId());
                }

            }
        });


        reverseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reserveButtonClicked();
            }
        });

//        reverseLayout.setVisibility(View.GONE);

        setOrientationText();
        initSpinner();
        switchPracticeCards();

        return view;
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
        switch (id) {
            case R.id.action_always_show_answer:
                alwaysShowAnswer = !alwaysShowAnswer;
                if (alwaysShowAnswer)
                    item.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_check_box_black_24dp));
                else
                    item.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_check_box_outline_blank_black_24dp));

                setQuestionAnswerText();

                return true;
            case R.id.action_previous:
                if (showPreviousIsOkay) {
                    showPreviousIsOkay = false;
                    showingPrevious = true;
                    setQuestionAnswerText();
                } else {
                    Toast.makeText(getContext(), "Cannot show previous card", Toast.LENGTH_SHORT).show();
                }


                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initSpinner() {
        if (deckArrayAdapter != null)
            deckArrayAdapter.clear();
        if (deckList != null)
            deckList.clear();

        deckList = dbManager.getDecks();
        final List<String> deckListString = new ArrayList<>();

        deckListString.add("All Cards");
        spinner.setEnabled(true);


        for (int counter = 0; counter < deckList.size(); counter++) {
            deckListString.add(deckList.get(counter).getDeckName());
        }

        deckArrayAdapter = new ArrayAdapter<String>(
                getContext(), R.layout.custom_spinner_item, deckListString) {


            @NonNull
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;

                mTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);


                if (position == SELECT_ALL_CARDS) {
                    mTextView.setTypeface(null, Typeface.BOLD);

                } else {
                    mTextView.setTypeface(null, Typeface.NORMAL);
                }

                mTextView.setTextColor(Color.BLACK);
                if (position == spinner.getSelectedItemPosition())
                    mView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ripple_grey));
                else
                    mView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ripple_normal));


                return mTextView;
            }
        };


        spinner.setAdapter(deckArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switchPracticeCards();
                return;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });
    }

    private void switchPracticeCards() {
        showPreviousIsOkay = false;
        previousCard = null;
        showingPrevious = false;
        currentQuestion = 0;
        int selectedDeck = spinner.getSelectedItemPosition();


        if (isSelectedDeckAll()) {
            mCardList = dbManager.getCards();
        } else {
            String deckId = deckList.get(selectedDeck - SPINNER_OFFSET).getDeckId();
            mCardList = dbManager.getDeckPracticeCards(deckId);
        }


        int nbCardsPractice = mCardList.size();
        String nbCardsString = nbCardsPractice + " ";

        if (nbCardsPractice == 1)
            nbCardsString += "Review Card";
        else
            nbCardsString += "Review Cards";

        praticeNbCards.setText(nbCardsString);

        questionOrder = new int[mCardList.size()];

        // init order
        for (int i = 0; i < questionOrder.length; i++) {
            questionOrder[i] = i;
        }

        setQuestionAnswerOrder();
        randomizeQuestionOrder();
        setQuestionAnswerText();

    }

    // helpers
    private void nextButtonClicked() {

        if (questionOrder.length < 2) {
            String message = "";
            if (questionOrder.length == 0) {
                message = "No Review Cards";
            } else if (questionOrder.length == 1) {
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
                previousCard = mCardList.get(questionOrder[currentQuestion]);

                answerTextView.scrollTo(0, 0);
                questionTextView.scrollTo(0, 0);

                if ((currentQuestion + 1) < questionOrder.length) {
                    currentQuestion++;
                    setQuestionAnswerText();
                } else {
                    // reset
                    randomizeQuestionOrder();
                    setQuestionAnswerText();
                    Toast.makeText(getContext(), "Order reset", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void showButtonClicked() {

        if (questionOrder.length == 0) {
            Toast.makeText(getContext(), "No Review Cards", Toast.LENGTH_SHORT).show();
        } else {

            if (answerHidden) {
                if (showingPrevious) {
                    answerTextView.setText(previousCard.getAnswer());
                } else {
                    answerTextView.setText(answerList.get(questionOrder[currentQuestion]));
                }

                answerHidden = false;
                showButton.setText("hide");
            } else {
                if (!alwaysShowAnswer)
                    answerTextView.setText(answerHiddenString);
                else {
                    if (showingPrevious) {
                        answerTextView.setText(previousCard.getAnswer());
                    } else {
                        answerTextView.setText(answerList.get(questionOrder[currentQuestion]));
                    }
                }

                answerHidden = true;
                showButton.setText("show");
            }
        }
    }

    private void randomizeQuestionOrder() {

        if (questionOrder.length > 1) {

            int lastQuestion = questionOrder[currentQuestion];


            for (int i = 0; i < questionOrder.length; i++) {
                int random = (int) (Math.random() * questionOrder.length);

                int temp = questionOrder[random];
                questionOrder[random] = questionOrder[i];
                questionOrder[i] = temp;
            }

            if (questionOrder[0] == lastQuestion && currentQuestion != 0) {
                int random = (int) (Math.random() * (questionOrder.length - 1));
                int temp = questionOrder[random + 1];
                questionOrder[random + 1] = questionOrder[0];
                questionOrder[0] = temp;
            }

        }
        currentQuestion = 0;
    }

    private void setQuestionAnswerText() {

        answerTextView.scrollTo(0, 0);
        questionTextView.scrollTo(0, 0);

        if (questionOrder.length == 0) {
            questionTextView.setText("No Review Cards");
            answerTextView.setText("");
        } else {
            if (showingPrevious) {
                questionTextView.setText(previousCard.getQuestion());
                if (!alwaysShowAnswer)
                    answerTextView.setText(answerHiddenString);
                else
                    answerTextView.setText(previousCard.getAnswer());
            } else {
                questionTextView.setText(questionList.get(questionOrder[currentQuestion]));
                if (!alwaysShowAnswer)
                    answerTextView.setText(answerHiddenString);
                else
                    answerTextView.setText(answerList.get(questionOrder[currentQuestion]));
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
            for (int counter = 0; counter < questionOrder.length; counter++) {
                questionList.add(mCardList.get(counter).getQuestion());
                answerList.add(mCardList.get(counter).getAnswer());
            }
        } else {
            for (int counter = 0; counter < questionOrder.length; counter++) {
                answerList.add(mCardList.get(counter).getQuestion());
                questionList.add(mCardList.get(counter).getAnswer());
            }
        }
    }

    private boolean isSelectedDeckAll() {
        return spinner.getSelectedItemPosition() == SELECT_ALL_CARDS;
    }

    private void reserveButtonClicked() {


        ObjectAnimator.ofFloat(reverseButton, "rotation", 0, 180).start();
        isReverseOrder = !isReverseOrder;

        if (!questionList.isEmpty()) {
            boolean temp = answerHidden;
            setQuestionAnswerOrder();
            setQuestionAnswerText();

            if (!temp) {
                answerTextView.setText(answerList.get(questionOrder[currentQuestion]));
                answerHidden = false;
            }
        }


        setOrientationText();
        Toast.makeText(getContext(), "Orientation: " + orientationTextView.getText().toString(), Toast.LENGTH_SHORT).show();
    }
}
