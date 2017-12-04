package stelztech.youknowehv4.fragments.quiz;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.util.List;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activities.MainActivityManager;
import stelztech.youknowehv4.activities.quiz.QuizActivity;
import stelztech.youknowehv4.activities.quiz.QuizReading;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.deck.Deck;
import stelztech.youknowehv4.database.profile.Profile;
import stelztech.youknowehv4.database.quiz.Quiz;
import stelztech.youknowehv4.helper.BlurBuilder;
import stelztech.youknowehv4.manager.FloatingActionButtonManager;

/**
 * Created by alex on 2017-04-03.
 */

public class QuizFragment extends Fragment {

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

    private boolean isRepeat = false;
    private boolean isReviewOnly = false;
    private boolean isOrientationReversed = false;
    private Quiz.MODE quizMode = Quiz.MODE.READING;
    ArrayList<Integer> selectedDeckIdList = new ArrayList<>();

    private RadioGroup orientationRadioGroup;
    private ListView quizOptionsListview;
    private ListView quizDecksListview;
    private Switch reviewOnlySwitch;
    private Switch repeatSwitch;


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
        Button continueQuizButton = (Button) view.findViewById(R.id.quiz_continue_button);

        FloatingActionButtonManager.getInstance().setState(FloatingActionButtonManager.ActionButtonState.GONE, getActivity());
        setHasOptionsMenu(true);

        newQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newQuizButtonClicked();
            }
        });

        if (activeQuizId == Profile.NO_QUIZ) {
            continueQuizButton.setEnabled(false);
        } else {
            continueQuizButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    continueQuizButtonClicked();
                }
            });

        }
    }

    private void continueQuizButtonClicked() {
        Intent intent = new Intent(getActivity(), QuizReading.class);
        getActivity().startActivityForResult(intent, MainActivityManager.RESULT_ANIMATION_RIGHT_TO_LEFT);
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
            // todo delete active quiz
        } else {

            if (!deckList.isEmpty() && !allDecksEmpty) {
                quizCreationPhase = QuizCreationPhase.PHASE_1;
                openNewQuizDialog();
            } else {
                Toast.makeText(getContext(), "You need at least 1 non-empty deck to create a Quiz", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void openNewQuizDialog() {


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_create_quiz, null);
        builder.setView(dialogView);


        orientationRadioGroup = (RadioGroup) dialogView.findViewById(R.id.custom_dialog_create_quiz_radio_group);
        reviewOnlySwitch = (Switch) dialogView.findViewById(R.id.custom_dialog_create_quiz_review_only_switch);
        repeatSwitch = (Switch) dialogView.findViewById(R.id.custom_dialog_create_quiz_repeat_switch);


        String questionLabel = Database.mUserDao.fetchActiveProfile().getQuestionLabel();
        String answerLabel = Database.mUserDao.fetchActiveProfile().getAnswerLabel();

        ((RadioButton) dialogView.findViewById(R.id.custom_dialog_create_quiz_orientation_normal))
                .setText(questionLabel+ "\nto\n" + answerLabel);
        ((RadioButton) dialogView.findViewById(R.id.custom_dialog_create_quiz_orientation_reverse))
                .setText(answerLabel+ "\nto\n" + questionLabel);


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

                            if(quizDecksListview.getCheckedItemPositions().size() != 0){
                                fetchQuizCreationInformation();
                                dialog.dismiss();
                                setIntentExtraAndStartQuiz();

                            }else{
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

    private void setIntentExtraAndStartQuiz() {

        Intent intent = new Intent(getActivity(), QuizReading.class);


        intent.putExtra(QuizActivity.EXTRA_INTENT_CONTINUE, false);
        intent.putExtra(QuizActivity.EXTRA_INTENT_TYPE, quizMode.toString());
        intent.putExtra(QuizActivity.EXTRA_INTENT_REPEAT, isRepeat);
        intent.putExtra(QuizActivity.EXTRA_INTENT_REVIEW_ONLY, isReviewOnly);
        intent.putExtra(QuizActivity.EXTRA_INTENT_REVERSE, isOrientationReversed);
        intent.putIntegerArrayListExtra(QuizActivity.EXTRA_INTENT_DECKS, selectedDeckIdList);


        getActivity().startActivityForResult(intent, MainActivityManager.RESULT_ANIMATION_RIGHT_TO_LEFT);

    }

    private void fetchQuizCreationInformation() {
        int orientationId = orientationRadioGroup.getCheckedRadioButtonId();

        isOrientationReversed = orientationId != R.id.custom_dialog_create_quiz_orientation_normal;

        isRepeat = repeatSwitch.isChecked();
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
}
