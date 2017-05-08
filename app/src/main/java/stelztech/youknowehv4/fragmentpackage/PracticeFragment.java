package stelztech.youknowehv4.fragmentpackage;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import stelztech.youknowehv4.manager.MainMenuToolbarManager;
import stelztech.youknowehv4.model.Card;
import stelztech.youknowehv4.model.Deck;

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
    private LinearLayout reverseButton;
    private TextView alwaysShowAnswerTV;

    private CheckBox practiceCheckbox;
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

    private int selectedDeck;
    private List<Card> mCardList;

    private final int SELECT_DECK_INDEX = 0;
    private final int SPINNER_OFFSET = 1;


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
        practiceCheckbox = (CheckBox) view.findViewById(R.id.practice_checkbox);
        praticeNbCards = (TextView) view.findViewById(R.id.practice_nb_cards);
        alwaysShowAnswerTV = (TextView) view.findViewById(R.id.always_show_answer_tv);
        reverseButton = (LinearLayout) view.findViewById(R.id.practice_reverse_layout);

        questionList = new ArrayList<>();
        answerList = new ArrayList<>();

        currentQuestion = 0;
        answerHidden = true;
        isReverseOrder = false;

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deckList.size() > 0) {
                    showButtonClicked();
                }
                // TODO add warning toast
            }
        });

        questionTextView.setMovementMethod(new ScrollingMovementMethod());
        answerTextView.setMovementMethod(new ScrollingMovementMethod());

        alwaysShowAnswerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                practiceCheckbox.setChecked(!practiceCheckbox.isChecked());
                if (!isSelectedDeckNothing()) {
                    setQuestionAnswerText();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deckList.size() > 0) {
                    nextButtonClicked();
                }
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelectedDeckNothing()) {
                    Toast.makeText(getContext(), "Select a Deck", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (deckList.size() > 0) {
                    ((MainActivityManager) getActivity())
                            .displayDeckInfo(deckList.get(spinner.getSelectedItemPosition() - SPINNER_OFFSET).getDeckId());
                }

            }
        });

        practiceCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isSelectedDeckNothing()) {
                    setQuestionAnswerText();
                }

            }
        });

        reverseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reserveButtonClicked();
            }
        });

//        reverseButton.setVisibility(View.GONE);

        initSpinner();
        switchPracticeCards();

        return view;
    }

    public void onPrepareOptionsMenu(Menu menu) {
        MainMenuToolbarManager.getInstance().setState(MainMenuToolbarManager.MainMenuToolbarState.PRACTICE, menu, getActivity());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_reverse:
                reserveButtonClicked();

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

        if (deckList.isEmpty()) {
            deckListString.add("- No Decks -");
            spinner.setEnabled(false);
        } else {
            deckListString.add("- Select Deck -");
            spinner.setEnabled(true);
        }
        for (int counter = 0; counter < deckList.size(); counter++) {
            deckListString.add(deckList.get(counter).getDeckName());
        }

        deckArrayAdapter = new ArrayAdapter<String>(
                getContext(), R.layout.custom_spinner_item_practice, deckListString) {
            // disable first field

            @Override
            public boolean isEnabled(int position) {
                if (position == SELECT_DECK_INDEX) {
                    return false;
                }
                return true;
            }

            @NonNull
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;

                if (position == SELECT_DECK_INDEX)
                    mTextView.setTextColor(Color.GRAY);
                else
                    mTextView.setTextColor(Color.BLACK);

                return mTextView;
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;

                if (position == SELECT_DECK_INDEX)
                    mTextView.setTextColor(Color.GRAY);
                else
                    mTextView.setTextColor(Color.BLACK);

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
        int selectedDeck = spinner.getSelectedItemPosition();


        if (isSelectedDeckNothing()) {
            setSelectDeck();
            return;
        }


        if (deckList.size() == 0)
            return;


        String deckId = deckList.get(selectedDeck - SPINNER_OFFSET).getDeckId();

        mCardList = dbManager.getDeckPracticeCards(deckId);

        int nbCardsPractice = mCardList.size();
        String nbCardsString = nbCardsPractice + " ";

        if (nbCardsPractice == 1)
            nbCardsString += "Practice Card";
        else
            nbCardsString += "Practice Cards";

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

    private void setSelectDeck() {
        questionTextView.setText("Select a Deck");
        answerTextView.setText("");
        praticeNbCards.setText("0 Practice Cards");
    }


    // helpers
    private void nextButtonClicked() {
        if (isSelectedDeckNothing()) {
            Toast.makeText(getContext(), "Select a Deck", Toast.LENGTH_SHORT).show();
            return;
        }

        if (questionOrder.length < 2) {
            String message = "";
            if (questionOrder.length == 0) {
                message = "No Practice Cards";
            } else if (questionOrder.length == 1) {
                message = "Only one practice card in deck";
            }
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        } else {
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

    private void showButtonClicked() {

        if (isSelectedDeckNothing()) {
            Toast.makeText(getContext(), "Select a Deck", Toast.LENGTH_SHORT).show();
            return;
        }

        if (questionOrder.length == 0) {
            Toast.makeText(getContext(), "No Cards", Toast.LENGTH_SHORT).show();
        } else {
            if (answerHidden) {
                answerTextView.setText(answerList.get(questionOrder[currentQuestion]));
                answerHidden = false;
                showButton.setText("hide");
            } else {
                if (!practiceCheckbox.isChecked())
                    answerTextView.setText(answerHiddenString);
                else
                    answerTextView.setText(answerList.get(questionOrder[currentQuestion]));
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
            questionTextView.setText("No Cards");
            answerTextView.setText("");
        } else {
            questionTextView.setText(questionList.get(questionOrder[currentQuestion]));
            if (!practiceCheckbox.isChecked())
                answerTextView.setText(answerHiddenString);
            else
                answerTextView.setText(answerList.get(questionOrder[currentQuestion]));
            answerHidden = true;
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

    private boolean isSelectedDeckNothing() {
        return spinner.getSelectedItemPosition() == SELECT_DECK_INDEX;
    }

    private void reserveButtonClicked() {
        if (isSelectedDeckNothing()) {
            Toast.makeText(getContext(), "Select a Deck", Toast.LENGTH_SHORT).show();
            return;
        }


        if (questionOrder.length > 0) {
            boolean temp = answerHidden;
            isReverseOrder = !isReverseOrder;
            setQuestionAnswerOrder();
            setQuestionAnswerText();

            if (!temp) {
                answerTextView.setText(answerList.get(questionOrder[currentQuestion]));
                answerHidden = false;
            }

            Toast.makeText(getContext(), "Order Reversed", Toast.LENGTH_SHORT).show();

        }
    }
}
