package stelztech.youknowehv4.fragments.quiz;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activities.MainActivityManager;
import stelztech.youknowehv4.activities.quiz.QuizActivity;
import stelztech.youknowehv4.activities.quiz.QuizMultipleChoice;
import stelztech.youknowehv4.activities.quiz.QuizReading;
import stelztech.youknowehv4.activities.quiz.QuizWriting;
import stelztech.youknowehv4.components.CustomYesNoDialog;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.card.Card;
import stelztech.youknowehv4.database.deck.Deck;
import stelztech.youknowehv4.database.profile.Profile;
import stelztech.youknowehv4.database.quiz.Quiz;
import stelztech.youknowehv4.database.quizcard.QuizCard;
import stelztech.youknowehv4.fragments.FragmentCommon;
import stelztech.youknowehv4.utilities.BlurBuilder;
import stelztech.youknowehv4.components.CustomProgressDialog;
import stelztech.youknowehv4.manager.FloatingActionButtonManager;
import stelztech.youknowehv4.utilities.QuizCardsUtilities;

/**
 * Created by alex on 2017-04-03.
 */

public class QuizFragment extends FragmentCommon {

    public QuizFragment(int animationLayoutPosition, boolean animationFade) {
        super(animationLayoutPosition, animationFade);
    }

    private enum QuizCreationPhase {
        PHASE_1,
        PHASE_2,
        PHASE_3
    }

    View view;

    private List<Deck> deckList;
    private boolean allDecksEmpty;
    private boolean[] deckSelectedArray;
    private CharSequence[] deckListDisplayName;

    private BlurBuilder blurBuilder;


    // new quiz
    final String[] QUIZ_CHOICES = new String[]{"Reading", "Writing", "Multiple Choice"};
    private QuizCreationPhase quizCreationPhase;

    private int activeQuizId;

    private boolean isReviewOnly = false;
    private boolean isOrientationReversed = false;
    private Quiz.MODE quizMode = Quiz.MODE.READING;
    ArrayList<Integer> selectedDeckIdList = new ArrayList<>();

    private RadioGroup orientationRadioGroup;
    private ListView quizOptionsListview;
    private ListView quizDecksListview;
    private Switch reviewOnlySwitch;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_quiz, container, false);


        activeQuizId = Database.mProfileDao.fetchActiveQuizId();

        initButtons();

        deckList = Database.mDeckDao.fetchAllDecks();
        allDecksEmpty = isAllDecksEmpty();
        deckSelectedArray = new boolean[deckList.size()];

        deckListDisplayName = new CharSequence[deckList.size()];

        for (int i = 0; i < deckList.size(); i++) {
            deckListDisplayName[i] = deckList.get(i).getDeckName();
        }

        if (blurBuilder == null)
            blurBuilder = new BlurBuilder();

        ImageView imageView = (ImageView) view.findViewById(R.id.quiz_intro_background);

        Bitmap bitmap = blurBuilder.blur(getContext(), ((BitmapDrawable) imageView.getDrawable()).getBitmap());
        // change the background image
        imageView.setImageBitmap(bitmap);

        return view;
    }

    private void initButtons() {

        Button newQuizButton = (Button) view.findViewById(R.id.quiz_new_button);

        FloatingActionButtonManager.getInstance().setState(FloatingActionButtonManager.ActionButtonState.GONE, getActivity());
        setHasOptionsMenu(true);

        newQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newQuizButtonClicked();
            }
        });

        updateContinueButton();
    }

    private void updateContinueButton() {

        Button continueQuizButton = (Button) view.findViewById(R.id.quiz_continue_button);
        if (activeQuizId == Profile.NO_QUIZ) {
            continueQuizButton.setEnabled(false);
        } else {
            continueQuizButton.setEnabled(true);
            continueQuizButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    continueQuizButtonClicked();
                }
            });

        }
    }

    private void continueQuizButtonClicked() {

        if(Database.mProfileDao.fetchActiveQuizId() != Profile.NO_PROFILES){
            Intent intent = fetchIntentFromQuizMode(Database.mQuizDao.fetchQuizById(activeQuizId).getMode());
            intent.putExtra(QuizActivity.EXTRA_INTENT_CONTINUE, true);
            getActivity().startActivityForResult(intent, MainActivityManager.RESULT_ANIMATION_RIGHT_TO_LEFT);
        }else{
            Toast.makeText(getContext(), "No active quiz", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isAllDecksEmpty() {

        for (int i = 0; i < deckList.size(); i++) {
            int cardCounter = Database.mCardDeckDao.fetchNumberCardsFromDeckId(deckList.get(i).getDeckId());
            if (cardCounter > 0)
                return false;
        }
        return true;

    }


    private void newQuizButtonClicked() {
        if (activeQuizId != Profile.NO_QUIZ) {


            final CustomYesNoDialog customYesNoDialog = new CustomYesNoDialog(getContext(),
                    "Override Previous Quiz", "A quiz already exists.\nAre you sure you want to override the previous quiz?") {
                @Override
                protected void onNegativeButtonClick() {

                }

                @Override
                protected void onPositiveButtonClick() {
                    removePreviousQuiz();
                }
            };
            customYesNoDialog.show();


        } else {

            if (!deckList.isEmpty() && !allDecksEmpty) {
                quizCreationPhase = QuizCreationPhase.PHASE_1;
                openNewQuizDialog();
            } else {
                Toast.makeText(getContext(), "You need at least 1 non-empty deck to create a Quiz", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void removePreviousQuiz() {
        int numberOfCards = Database.mQuizCardDao.fetchNumberQuizCardFromQuizId(activeQuizId);
        QuizCardsUtilities.setQuizStats(activeQuizId);
        Database.mQuizDao.markQuizAsQuizFinished(activeQuizId);

        CustomProgressDialog customProgressDialog = new CustomProgressDialog("Removing Previous Quiz",
                numberOfCards, getContext(), getActivity()) {

            @Override
            public void loadInformation() {
                List<QuizCard> quizCardList = Database.mQuizCardDao.
                        fetchQuizCardsByQuizId(Database.mProfileDao.fetchActiveQuizId());

                int position = 0;
                for (QuizCard quizCard : quizCardList) {
                    Database.mQuizCardDao.deleteQuizCard(quizCard.getCardId(), quizCard.getQuizId());
                    position++;
                    setDialogProgress(position);
                }

                Database.mProfileDao.setActiveQuizId(Database.mUserDao.fetchActiveProfile().getProfileId(),
                        Profile.NO_QUIZ);

            }

            @Override
            public void informationLoaded() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activeQuizId = Profile.NO_QUIZ;
                        updateContinueButton();

                        // todo move this
                        if (!deckList.isEmpty() && !allDecksEmpty) {
                            quizCreationPhase = QuizCreationPhase.PHASE_1;
                            openNewQuizDialog();
                        } else {
                            Toast.makeText(getContext(), "You need at least 1 non-empty deck to create a Quiz", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        };

        customProgressDialog.startDialog();

    }

    private void openNewQuizDialog() {


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_create_quiz, null);
        builder.setView(dialogView);


        orientationRadioGroup = (RadioGroup) dialogView.findViewById(R.id.custom_dialog_create_quiz_radio_group);
        reviewOnlySwitch = (Switch) dialogView.findViewById(R.id.custom_dialog_create_quiz_review_only_switch);


        String questionLabel = Database.mUserDao.fetchActiveProfile().getQuestionLabel();
        String answerLabel = Database.mUserDao.fetchActiveProfile().getAnswerLabel();

        ((RadioButton) dialogView.findViewById(R.id.custom_dialog_create_quiz_orientation_normal))
                .setText(questionLabel + "\nto\n" + answerLabel);
        ((RadioButton) dialogView.findViewById(R.id.custom_dialog_create_quiz_orientation_reverse))
                .setText(answerLabel + "\nto\n" + questionLabel);


        // LISTVIEW 1
        quizOptionsListview = (ListView) dialogView.findViewById(R.id.custom_dialog_create_quiz_listview_type);
        ArrayAdapter adapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_single_choice, QUIZ_CHOICES);
        quizOptionsListview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        quizOptionsListview.setAdapter(adapter1);

        final LinearLayout quizDialog1 = (LinearLayout) dialogView.findViewById(R.id.create_quiz_dialog_1);
        final LinearLayout quizDialog2 = (LinearLayout) dialogView.findViewById(R.id.create_quiz_dialog_2);
        final LinearLayout quizDialog3 = (LinearLayout) dialogView.findViewById(R.id.create_quiz_dialog_3);
        final TextView titleTextView = (TextView) dialogView.findViewById(R.id.create_quiz_title);
        final TextView numDeckTextView = (TextView) dialogView.findViewById(R.id.create_quiz_deck_counter);

        // Set up the buttons
        builder.setPositiveButton("Next", null);
        // if cancel button is press, close dialog
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNeutralButton("Previous", null);

        titleTextView.setText("New Quiz");

        quizOptionsListview.setItemChecked(0, true);
        quizDialog2.setVisibility(View.GONE);
        quizDialog3.setVisibility(View.GONE);
        numDeckTextView.setVisibility(View.GONE);


        final AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // do nothing
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                dialog.setCanceledOnTouchOutside(false);
                final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                final Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);

                neutralButton.setVisibility(View.GONE);

                positiveButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (quizCreationPhase == QuizCreationPhase.PHASE_1) {
                            quizCreationPhase = QuizCreationPhase.PHASE_2;

                            // buttons logic
                            positiveButton.setText("Next");
                            neutralButton.setVisibility(View.VISIBLE);

                            // view logic
                            quizDialog1.setVisibility(View.GONE);
                            quizDialog2.setVisibility(View.VISIBLE);
                            quizDialog3.setVisibility(View.GONE);
                            numDeckTextView.setVisibility(View.GONE);
                            titleTextView.setText("Quiz Orientation");
                        } else if (quizCreationPhase == QuizCreationPhase.PHASE_2) {
                            quizCreationPhase = QuizCreationPhase.PHASE_3;

                            // buttons logic
                            positiveButton.setText("Start");
                            neutralButton.setVisibility(View.VISIBLE);

                            // view logic
                            quizDialog1.setVisibility(View.GONE);
                            quizDialog2.setVisibility(View.GONE);
                            quizDialog3.setVisibility(View.VISIBLE);
                            numDeckTextView.setVisibility(View.VISIBLE);
                            titleTextView.setText("Quiz Decks");
                        } else {
                            // TODO validation and init info

                            if (quizDecksListview.getCheckedItemPositions().size() != 0) {
                                fetchQuizCreationInformation();
                                dialog.dismiss();
                                createQuiz();


                            } else {
                                Toast.makeText(getContext(), "At least 1 deck must be selected", Toast.LENGTH_SHORT).show();

                            }

                        }

                    }
                });

                neutralButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (quizCreationPhase == QuizCreationPhase.PHASE_3) {
                            quizCreationPhase = QuizCreationPhase.PHASE_2;

                            // buttons logic
                            positiveButton.setText("Next");
                            neutralButton.setVisibility(View.VISIBLE);

                            // view logic
                            quizDialog1.setVisibility(View.GONE);
                            quizDialog2.setVisibility(View.VISIBLE);
                            quizDialog3.setVisibility(View.GONE);
                            numDeckTextView.setVisibility(View.GONE);
                            titleTextView.setText("Quiz Orientation");
                        } else if (quizCreationPhase == QuizCreationPhase.PHASE_2) {
                            quizCreationPhase = QuizCreationPhase.PHASE_1;

                            // buttons logic
                            positiveButton.setText("Next");
                            neutralButton.setVisibility(View.GONE);

                            // view logic
                            quizDialog1.setVisibility(View.VISIBLE);
                            quizDialog2.setVisibility(View.GONE);
                            quizDialog3.setVisibility(View.GONE);
                            numDeckTextView.setVisibility(View.GONE);
                            titleTextView.setText("New Quiz");
                        }
                    }
                });
            }
        });


        quizDecksListview = (ListView) dialogView.findViewById(R.id.custom_dialog_create_quiz_listview_decks);
        ArrayAdapter adapter2 = new ArrayAdapter<CharSequence>(getContext(), android.R.layout.select_dialog_multichoice, deckListDisplayName);
        quizDecksListview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        quizDecksListview.setItemsCanFocus(false);
        quizDecksListview.setAdapter(adapter2);

        quizDecksListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int len = quizDecksListview.getCount();
                int numberSelected = 0;
                for (int counter = 0; counter < len; counter++) {
                    if (quizDecksListview.isItemChecked(counter)) {
                        numberSelected++;
                    }
                }

                numDeckTextView.setText(numberSelected + " Deck(s)");
            }
        });


        dialog.show();
    }

    private void createQuiz() {

        int numberOfCards = 0;
        for (int deckId : selectedDeckIdList) {
            if (isReviewOnly)
                numberOfCards += Database.mCardDeckDao.fetchNumberReviewCardsFromDeck(deckId);
            else
                numberOfCards += Database.mCardDeckDao.fetchNumberCardsFromDeckId(deckId);
        }

        CustomProgressDialog customProgressDialog = new CustomProgressDialog("Creating Quiz",
                numberOfCards, getContext(), getActivity()) {

            @Override
            public void loadInformation() {
                final int quizId = Database.mQuizDao.createQuiz(quizMode, isOrientationReversed, isReviewOnly);
                Database.mProfileDao.setActiveQuizId(Database.mUserDao.fetchActiveProfile().getProfileId(), quizId);
                activeQuizId = quizId;

                int position = 0;
                List<Card> cardList = new ArrayList<>();
                for (int deckId : selectedDeckIdList) {
                    if (isReviewOnly)
                        cardList.addAll(Database.mCardDeckDao.fetchReviewCardsByDeckId(deckId));
                    else
                        cardList.addAll(Database.mCardDeckDao.fetchCardsByDeckId(deckId));
                }

                long seed = System.nanoTime();
                Collections.shuffle(cardList, new Random(seed));

                for (int counter = 0; counter < cardList.size(); counter++) {
                    Card card = cardList.get(counter);
                    Database.mQuizCardDao.createQuizCard(card.getCardId(), quizId, card.getQuestion(),
                            card.getAnswer(), position);
                    position++;

                    setDialogProgress(position);


                }

            }

            @Override
            public void informationLoaded() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateContinueButton();
                        setIntentExtraAndStartQuiz();
                    }
                });
            }
        };

        customProgressDialog.startDialog();

    }

    private void setIntentExtraAndStartQuiz() {


        Intent intent = fetchIntentFromQuizMode(quizMode);

        intent.putExtra(QuizActivity.EXTRA_INTENT_CONTINUE, false);
        intent.putExtra(QuizActivity.EXTRA_INTENT_TYPE, quizMode.toString());
        intent.putExtra(QuizActivity.EXTRA_INTENT_REVERSE, isOrientationReversed);


        getActivity().startActivityForResult(intent, MainActivityManager.RESULT_QUIZ_END);

    }


    private Intent fetchIntentFromQuizMode(Quiz.MODE quizMode) {
        Intent intent = null;
        switch (quizMode) {

            case READING:
                intent = new Intent(getActivity(), QuizReading.class);
                break;
            case WRITING:
                intent = new Intent(getActivity(), QuizWriting.class);
                break;
            case MULTIPLE_CHOICE:
                intent = new Intent(getActivity(), QuizMultipleChoice.class);
                break;
        }
        return intent;
    }

    private void fetchQuizCreationInformation() {
        int orientationId = orientationRadioGroup.getCheckedRadioButtonId();

        isOrientationReversed = orientationId != R.id.custom_dialog_create_quiz_orientation_normal;

        isReviewOnly = reviewOnlySwitch.isChecked();

        int quizOptionsCheckPosition = quizOptionsListview.getCheckedItemPosition();

        quizMode = Quiz.getModeFromPosition(quizOptionsCheckPosition);

        selectedDeckIdList.clear();
        int len = quizDecksListview.getCount();
        SparseBooleanArray checked = quizDecksListview.getCheckedItemPositions();
        for (int i = 0; i < len; i++) {
            if (checked.get(i)) {
                selectedDeckIdList.add(deckList.get(i).getDeckId());
            }
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.toolbar_other, menu);
        ActionBar actionBar = ((MainActivityManager) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        getActivity().findViewById(R.id.spinner_nav_layout).setVisibility(View.GONE);

    }

    public void onQuizFinishResult(){
        activeQuizId = Profile.NO_QUIZ;
        updateContinueButton();
    }
}
