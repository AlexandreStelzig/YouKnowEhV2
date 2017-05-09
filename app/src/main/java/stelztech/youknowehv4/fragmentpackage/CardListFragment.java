package stelztech.youknowehv4.fragmentpackage;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activitypackage.MainActivityManager;
import stelztech.youknowehv4.database.DatabaseManager;
import stelztech.youknowehv4.helper.Helper;
import stelztech.youknowehv4.manager.ActionButtonManager;
import stelztech.youknowehv4.manager.MainMenuToolbarManager;
import stelztech.youknowehv4.manager.SortingStateManager;
import stelztech.youknowehv4.model.Card;
import stelztech.youknowehv4.model.Deck;
import stelztech.youknowehv4.model.Profile;

/**
 * Created by alex on 2017-04-03.
 */

public class CardListFragment extends Fragment {


    public enum CardQuickDialogOption {
        QUICK_NEW,
        QUICK_UPDATE
    }


    public enum CardListState {
        VIEW,
        EDIT_DECK,
        PRACTICE_TOGGLE,
        SEARCH
    }


    public final String ALL_DECKS_ITEM = "" + -1;
    private final int SPINNER_OFFSET = 1;

    // components
    private View view;

    // Spinner
    private Spinner spinner;
    private ArrayAdapter<String> deckArrayAdapter;
    private List<Deck> deckList;
    private String toSelectDeckId = ALL_DECKS_ITEM;

    // database
    private DatabaseManager dbManager;

    // list
    private List<Card> cardList;
    private List<Card> allCardsListSearch;
    private CustomListAdapter customListAdapter;
    private ListView listView;
    private TextView placeholderTextView;
    private TextView orientation;
    private TextView nbCards;
    private TextView nbCardsPractice;

    private boolean[] isPartOfList;
    private boolean[] isPartOfListTemp;
    private boolean[] isPracticeList;
    private boolean[] isPracticeListTemp;

    private SearchView searchView;

    private int indexSelected;

    // current profile
    private Profile currentProfile;


    // state
    private CardListState currentState;

    // reverse
    private boolean isReverseOrder = false;

    // quick create holder
    private String questionHolder;
    private String answerHolder;

    // orientation labels
    private String orientationQuestionAnswer;
    private String orientationAnswerQuestion;
    private String questionLabel;
    private String answerLabel;

    // loading indicator
    private ProgressBar progressBar;

    private boolean rebuild;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_list, container, false);

//        ActionButtonManager.getInstance().setState(ActionButtonManager.ActionButtonState.ADD_CARD, getActivity());
        setHasOptionsMenu(true);

        // init
        indexSelected = -1;
        spinner = ((Spinner) getActivity().findViewById(R.id.spinner_nav));
        dbManager = DatabaseManager.getInstance(getActivity());
        listView = (ListView) view.findViewById(R.id.listview);
        placeholderTextView = (TextView) view.findViewById(R.id.list_text);
        progressBar = (ProgressBar) view.findViewById(R.id.list_loading_indicator);

        placeholderTextView.setText("Empty");

        orientation = (TextView) view.findViewById(R.id.listview_card_orientation);
        nbCards = (TextView) view.findViewById(R.id.listview_number_cards);
        nbCardsPractice = (TextView) view.findViewById(R.id.listview_number_cards_practice);

        orientation.setVisibility(View.VISIBLE);
        nbCards.setVisibility(View.VISIBLE);
        nbCardsPractice.setVisibility(View.VISIBLE);

        rebuild = true;

        currentProfile = dbManager.getActiveProfile();

        questionLabel = currentProfile.getQuestionLabel();
        answerLabel = currentProfile.getAnswerLabel();

        orientationQuestionAnswer = questionLabel + " - " + answerLabel;
        orientationAnswerQuestion = answerLabel + " - " + questionLabel;

        allCardsListSearch = new ArrayList<>();


        cardList = new ArrayList<Card>();

        currentState = CardListState.VIEW;

        setOrientationText();

        populateSpinner();

        hideListView();

        populateListView(getCurrentDeckIdSelected());

        delayListViewShow();

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {


        if (currentState == CardListState.VIEW) {
            MainMenuToolbarManager.getInstance().setState(MainMenuToolbarManager.MainMenuToolbarState.CARD, menu, getActivity());
            ActionButtonManager.getInstance().setState(ActionButtonManager.ActionButtonState.ADD_CARD, getActivity());
        } else if (currentState == CardListState.SEARCH) {
            MainMenuToolbarManager.getInstance().setState(MainMenuToolbarManager.MainMenuToolbarState.SEARCH, menu, getActivity());
            ActionButtonManager.getInstance().setState(ActionButtonManager.ActionButtonState.GONE, getActivity());
        } else {
            MainMenuToolbarManager.getInstance().setState(MainMenuToolbarManager.MainMenuToolbarState.CARD_LIST_EDIT, menu, getActivity());
            ActionButtonManager.getInstance().setState(ActionButtonManager.ActionButtonState.GONE, getActivity());

        }


        final MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print

                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                searchView.setQuery(query, false);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                populateSearchListView(s);

                return false;
            }
        });

//        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//                MenuItemCompat.collapseActionView(myActionMenuItem);
//                return false;
//            }
//        });


        MenuItemCompat.setOnActionExpandListener(myActionMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                if (getCurrentDeckIdSelected() == ALL_DECKS_ITEM)
                    searchView.setQueryHint("Search All Cards...");
                else {
                    String deckName = dbManager.getDeckFromId(getCurrentDeckIdSelected()).getDeckName();
                    searchView.setQueryHint("Search " + deckName + " Cards...");
                }

                changeState(CardListState.SEARCH);

                MainMenuToolbarManager.getInstance().setState(MainMenuToolbarManager.MainMenuToolbarState.SEARCH, menu, getActivity());


                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
//                ActionButtonManager.getInstance().setState(ActionButtonManager.ActionButtonState.ADD_CARD, getActivity());
                changeState(CardListState.VIEW);
                ((MainActivityManager) getActivity()).enableDrawerSwipe(true);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        switch (id) {
            case R.id.action_reverse:

                isReverseOrder = !isReverseOrder;
                setOrientationText();
                Toast.makeText(getContext(), "Orientation " + orientation.getText().toString(), Toast.LENGTH_SHORT).show();
                populateListView(getCurrentDeckIdSelected());
                listViewShow();

                return true;
            case R.id.action_edit_deck_cards:
                if (deckList.isEmpty()) {
                    Toast.makeText(getContext(), "No cards in deck", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (getCurrentDeckIdSelected() != ALL_DECKS_ITEM) {
                    hideListView();
                    changeState(CardListState.EDIT_DECK);
                    delayListViewShow();
                } else {
                    Toast.makeText(getContext(), "Please select a deck", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_practice_toggle:
                if (deckList.size() == 0) {
                    Toast.makeText(getContext(), "No cards in deck", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (getCurrentDeckIdSelected() != ALL_DECKS_ITEM) {
                    if (cardList.isEmpty()) {
                        Toast.makeText(getContext(), "No cards in deck", Toast.LENGTH_SHORT).show();
                    } else {
                        changeState(CardListState.PRACTICE_TOGGLE);
                    }
                } else {
                    Toast.makeText(getContext(), "Please select a deck", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.action_done:
                if (currentState == CardListState.EDIT_DECK) {
                    if (areDecksDifferent()) {
                        modifyDeckCards();
                    }

                } else if (currentState == CardListState.PRACTICE_TOGGLE) {
                    if (arePracticeCardsDifferent())
                        toggleCardsFromPractice();
                }
                changeState(CardListState.VIEW);
                return true;
            case R.id.action_cancel:
                cancelClicked();
                return true;
            case R.id.action_sort:
                sortDialog().show();
                return true;
            case R.id.quick_create:
                quickCreateUpdateDialog(CardQuickDialogOption.QUICK_NEW, null).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setOrientationText() {
        if (isReverseOrder) {
            orientation.setText(orientationAnswerQuestion);
        } else {
            orientation.setText(orientationQuestionAnswer);
        }

    }

    private void setNumberOfCards() {
        int numberOfCards = cardList.size();
        String message = "";
        if (numberOfCards == 0) {
            message = "No cards";
        } else if (numberOfCards == 1) {
            message = "1 card";
        } else {
            message = numberOfCards + " cards";
        }

        nbCards.setText(message);

    }


    private void setNumberCardsOther(int nbCards) {
        String practiceLabelMessage = "";
        switch (currentState) {

            case VIEW:
                nbCardsPractice.setText(nbCards + " review");
                break;
            case EDIT_DECK:

                nbCardsPractice.setText(nbCards + " included");
                break;
            case PRACTICE_TOGGLE:
                nbCardsPractice.setText(nbCards + " review");
                break;

        }

    }

    private void changeState(CardListState state) {

        MainActivityManager mainActivityManager = ((MainActivityManager) getActivity());
        switch (state) {
            case VIEW:
                currentState = CardListState.VIEW;
                mainActivityManager.getSupportActionBar().setTitle("");
                populateListView(getCurrentDeckIdSelected());
                listViewShow();
                getActivity().invalidateOptionsMenu();
                break;
            case EDIT_DECK:
                currentState = CardListState.EDIT_DECK;
                mainActivityManager.getSupportActionBar().setTitle(dbManager.getDeckFromId(getCurrentDeckIdSelected()).getDeckName() + " Cards");
                populateListView(getCurrentDeckIdSelected());
                getActivity().invalidateOptionsMenu();
                break;
            case PRACTICE_TOGGLE:
                currentState = CardListState.PRACTICE_TOGGLE;
                mainActivityManager.getSupportActionBar().setTitle("Toggle Review");
                populateListView(getCurrentDeckIdSelected());
                listViewShow();
                getActivity().invalidateOptionsMenu();
                break;
            case SEARCH:
                currentState = CardListState.SEARCH;
                ((MainActivityManager) getActivity()).enableDrawerSwipe(false);
                isPracticeList = null;

                populateSearchListView("");

                ActionButtonManager.getInstance().setState(ActionButtonManager.ActionButtonState.GONE, getActivity());
                break;
        }
    }

    private void cancelClicked() {
        if (currentState == CardListState.EDIT_DECK && areDecksDifferent()) {
            displayConfirmationDialog();

        } else if (currentState == CardListState.PRACTICE_TOGGLE && arePracticeCardsDifferent()) {
            displayConfirmationDialog();

        } else {
            changeState(CardListState.VIEW);
        }
    }

    // DIALOG
    private AlertDialog sortDialog() {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.custom_scrollable_dialog_list, null, false);

        AlertDialog.Builder deckListAlertDialog = new AlertDialog.Builder(getContext());

        deckListAlertDialog.setView(dialogView);
        deckListAlertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        deckListAlertDialog.setTitle("Sort");


        final String[] sortingChoices = getContext().getResources().getStringArray(R.array.sort_options);

        deckListAlertDialog.setSingleChoiceItems(sortingChoices,
                SortingStateManager.getInstance().getSelectedPosition(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SortingStateManager sortingStateManager = SortingStateManager.getInstance();
                        sortingStateManager.changeStateByPosition(which);

                        if (currentState == CardListState.SEARCH)
                            populateSearchListView(searchView.getQuery().toString());
                        else
                            populateListView(getCurrentDeckIdSelected());
                        listViewShow();
                        dialog.dismiss();
                        listView.smoothScrollToPosition(0);
                    }
                });

        AlertDialog alert = deckListAlertDialog.create();
        Helper.getInstance().hideKeyboard(getActivity());
        return alert;
    }


    private AlertDialog quickCreateUpdateDialog(final CardQuickDialogOption dialogType, final Card card) {

        if (card == (null)) {
            this.questionHolder = "";
            this.answerHolder = "";
        } else {
            this.questionHolder = card.getQuestion();
            this.answerHolder = card.getAnswer();
        }


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        switch (dialogType) {
            case QUICK_NEW:
                builder.setTitle("Quick Create Card");
                break;
            case QUICK_UPDATE:
                builder.setTitle("Quick Update Card");
                break;
            default:
                Toast.makeText(getContext(), "Error in deck dialog - wrong type", Toast.LENGTH_SHORT).show();
                break;
        }


        LayoutInflater inflater = getActivity().getLayoutInflater();


        View dialogView = inflater.inflate(R.layout.custom_dialog_quick_create_card, null);


        builder.setView(dialogView);


        final EditText questionEditTextView = (EditText) dialogView.findViewById(R.id.quick_create_question);
        final EditText answerEditTextView = (EditText) dialogView.findViewById(R.id.quick_create_answer);
        final TextView deckPlaceHolder = (TextView) dialogView.findViewById(R.id.quick_create_deck_placeholder);

        questionEditTextView.setHint(questionLabel);
        answerEditTextView.setHint(answerLabel);

        final String currentSelectedDeckId = getCurrentDeckIdSelected();

        questionEditTextView.setText(questionHolder);
        answerEditTextView.setText(answerHolder);
        questionEditTextView.setSelection(questionEditTextView.getText().length());


        if (dialogType == CardQuickDialogOption.QUICK_NEW) {
            if (currentSelectedDeckId == ALL_DECKS_ITEM)
                deckPlaceHolder.setText("No Deck");
            else
                deckPlaceHolder.setText(dbManager.getDeckFromId(currentSelectedDeckId).getDeckName());
        } else {
            int numberDecks = dbManager.getDecksFromCard(card.getCardId()).size();
            if (numberDecks == 0) {
                deckPlaceHolder.setText("No Deck");
            } else if (numberDecks == 1) {
                deckPlaceHolder.setText("1 Deck");
            } else {
                deckPlaceHolder.setText(numberDecks + " Decks");
            }
        }


        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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

        if (dialogType == CardQuickDialogOption.QUICK_NEW) {
            builder.setNeutralButton("Next", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }


        final AlertDialog aDialog = builder.create();

        aDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // do nothing
            }
        });

        aDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button okButton = aDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                okButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        String answerTemp = answerHolder;
                        String questionTemp = questionHolder;

                        answerHolder = answerEditTextView.getText().toString();
                        questionHolder = questionEditTextView.getText().toString();


                        boolean answerEmpty = answerHolder.trim().isEmpty();
                        boolean questionEmpty = questionHolder.trim().isEmpty();

                        if (answerEmpty || questionEmpty) {
                            String message = "";
                            if (answerEmpty && questionEmpty) {
                                message = "Error - Question and Answer cannot be empty";
                            } else if (!answerEmpty && questionEmpty) {
                                message = "Error - Question cannot be empty";
                            } else {
                                message = "Error - Answer cannot be empty";
                            }
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        } else {

                            if (dialogType == CardQuickDialogOption.QUICK_NEW) {
                                String newCardId = dbManager.createCard(questionHolder, answerHolder, "");
                                if (currentSelectedDeckId != ALL_DECKS_ITEM) {
                                    dbManager.createCardDeck(newCardId, currentSelectedDeckId);
                                }
                                populateListView(getCurrentDeckIdSelected());
                                listViewShow();
                                Toast.makeText(getContext(), "Card created", Toast.LENGTH_SHORT).show();
                                aDialog.dismiss();
                            } else {
                                if (answerTemp.equals(answerHolder) && questionTemp.equals(questionHolder)) {
                                    Toast.makeText(getContext(), "Question and Answer are the same", Toast.LENGTH_SHORT).show();
                                } else {
                                    dbManager.updateCard(card.getCardId(), questionHolder, answerHolder, card.getMoreInfo());
                                    populateListView(getCurrentDeckIdSelected());
                                    listViewShow();
                                    Toast.makeText(getContext(), "Card updated", Toast.LENGTH_SHORT).show();
                                    aDialog.dismiss();
                                }
                            }

                        }
                    }
                });

                if (dialogType == CardQuickDialogOption.QUICK_NEW) {
                    Button neutralButton = aDialog.getButton(AlertDialog.BUTTON_NEUTRAL);

                    neutralButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            answerHolder = answerEditTextView.getText().toString();
                            questionHolder = questionEditTextView.getText().toString();

                            boolean answerEmpty = answerHolder.trim().isEmpty();
                            boolean questionEmpty = questionHolder.trim().isEmpty();

                            if (answerEmpty || questionEmpty) {
                                String message = "";
                                if (answerEmpty && questionEmpty) {
                                    message = "Error - Question and Answer cannot be empty";
                                } else if (!answerEmpty && questionEmpty) {
                                    message = "Error - Question cannot be empty";
                                } else {
                                    message = "Error - Answer cannot be empty";
                                }
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            } else {

                                if (dialogType == CardQuickDialogOption.QUICK_NEW) {
                                    String newCardId = dbManager.createCard(questionHolder, answerHolder, "");
                                    if (currentSelectedDeckId != ALL_DECKS_ITEM) {
                                        dbManager.createCardDeck(newCardId, currentSelectedDeckId);
                                    }

                                    Toast.makeText(getContext(), "Card created", Toast.LENGTH_SHORT).show();
                                    populateListView(getCurrentDeckIdSelected());
                                    listViewShow();
                                    answerEditTextView.setText("");
                                    questionEditTextView.setText("");
                                    questionEditTextView.requestFocus();

                                }
                            }
                        }
                    });
                }

            }
        });

        aDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        input.setSelection(input.getText().length());
        return aDialog;


    }


    ////// HOLD MENU //////

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.listview) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.hold_menu_card, menu);

            MenuItem practice = menu.findItem(R.id.toggle_practice);
            MenuItem rmDeck = menu.findItem(R.id.remove_from_deck);

            if (getCurrentDeckIdSelected() == ALL_DECKS_ITEM) {
                practice.setVisible(false);
                rmDeck.setVisible(false);
            } else {
                practice.setVisible(true);
                rmDeck.setVisible(true);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_card:
                editCard();
                return true;
            case R.id.delete_card:
                displayConfirmationDialog();
                return true;
            case R.id.info_card:
                showQuickInfoCard();
                return true;
            case R.id.toggle_practice:
                if (getCurrentDeckIdSelected() != ALL_DECKS_ITEM) {
                    toggleCardFromPractice();
                    populateListView(getCurrentDeckIdSelected());
                    listViewShow();
                    Toast.makeText(getContext(), "Card toggle from practice", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), "Please select a deck", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.remove_from_deck:
                if (getCurrentDeckIdSelected() != ALL_DECKS_ITEM) {
                    dbManager.deleteCardDeck(cardList.get(indexSelected).getCardId(), getCurrentDeckIdSelected());
                    String cardRemoved = cardList.get(indexSelected).getQuestion() + "/" + cardList.get(indexSelected).getAnswer();
                    Toast.makeText(getContext(), "\'" + cardRemoved + "\' removed from deck", Toast.LENGTH_SHORT).show();
                    populateListView(getCurrentDeckIdSelected());
                    listViewShow();
                } else {
                    Toast.makeText(getContext(), "Please select a deck", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.quick_edit:
                Card card = cardList.get(indexSelected);
                quickCreateUpdateDialog(CardQuickDialogOption.QUICK_UPDATE, card).show();
                return true;
            default:
                indexSelected = -1;
                return super.onContextItemSelected(item);
        }
    }


    private void showQuickInfoCard() {
        Card card = cardList.get(indexSelected);
        String cardQuestion = questionLabel + ": " + card.getQuestion();
        String cardAnswer = answerLabel + ": " + card.getAnswer();

        List<Deck> decks = dbManager.getDecksFromCard(card.getCardId());
        String numberOfDecks = "Number of Decks: " + decks.size();
        String cardDateCreated = "Date created: " + card.getDateCreated();
        String cardDateModified = "Date modified: " + card.getDateModified();

        String message = cardQuestion + "\n" + cardAnswer + "\n" + numberOfDecks + "\n"
                + cardDateCreated + "\n" + cardDateModified;
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setMessage(message).setPositiveButton("done", null).show();
    }

    private void deleteCardFromDatabase() {
        dbManager.toggleArchiveCard((String) cardList.get(indexSelected).getCardId());
        Toast.makeText(getContext(), "Card deleted", Toast.LENGTH_SHORT).show();
        populateListView(getCurrentDeckIdSelected());
    }

    private void editCard() {
        String cardId = cardList.get(indexSelected).getCardId();
        ((MainActivityManager) getActivity()).startActivityEditCard(cardId);
    }

    private void toggleCardFromPractice() {
        dbManager.togglePractice_Card(cardList.get(indexSelected).getCardId(), getCurrentDeckIdSelected());
    }

    private void toggleCardsFromPractice() {
        for (int counter = 0; counter < isPracticeList.length; counter++) {
            if (isPracticeList[counter] != isPracticeListTemp[counter]) {
                dbManager.togglePractice_Card(cardList.get(counter).getCardId(), getCurrentDeckIdSelected());
            }
        }
    }

    private void displayConfirmationDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        if (currentState == CardListState.VIEW) {
            alertDialog.setMessage("Are you sure you want to delete:\n\'" +
                    cardList.get(indexSelected).getQuestion() + "\'?");
            alertDialog.setTitle("Delete Card");
        } else {
            alertDialog.setMessage("Are you sure you want to cancel?\n" +
                    "All changes will be discarded.");
            alertDialog.setTitle("Discard Changes");
        }


        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                onButtonConfirmation();
            }
        });

        // Setting cancel Button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    private void onButtonConfirmation() {
        if (currentState == CardListState.VIEW) {
            deleteCardFromDatabase();
        } else {
            changeState(CardListState.VIEW);
        }
    }

    public void listViewShow() {
        if (!cardList.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    private void delayListViewShow() {
        if (!cardList.isEmpty()) {
            // forced loading indicator for better transition
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    listView.setSelection(0);
                }
            }, (long) (Math.random() * 250) + 400);
        }
    }

    private void hideListView() {
        progressBar.setRotation((float) (Math.random() * 360));
        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
    }


    private void populateSpinner() {

        if (deckArrayAdapter != null) {
            deckArrayAdapter.clear();
        }

        if (deckList != null)
            deckList.clear();


        cardList = dbManager.getCards();
        deckList = dbManager.getDecks();
        final List<String> deckListString = new ArrayList<>();

        if (cardList.isEmpty()) {
            deckListString.add(new String("No Cards"));
        } else {
            deckListString.add(new String("All Cards"));
        }

        if (cardList.isEmpty() || deckList.isEmpty()) {
            spinner.setEnabled(false);
        } else {
            spinner.setEnabled(true);
        }

        for (int counter = 0; counter < deckList.size(); counter++) {
            deckListString.add(deckList.get(counter).getDeckName());
        }

        deckArrayAdapter = new ArrayAdapter<String>(
                getContext(), R.layout.custom_spinner_item_card, deckListString) {

            @NonNull
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                // set specific spinner dropdown visuals
                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;

                mTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.textPrimary));
                mTextView.setEllipsize(TextUtils.TruncateAt.END);

                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int _width = size.x;

                mTextView.setMinimumWidth(_width / 2);

                if (position == spinner.getSelectedItemPosition())
                    mView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorNotPractice));
                else
                    mView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));


                return mTextView;
            }
        };

        deckArrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        spinner.setAdapter(deckArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String deckId;
                if (position == 0) {
                    deckId = ALL_DECKS_ITEM;
                } else {
                    deckId = deckList.get(position - SPINNER_OFFSET).getDeckId();
                }

                hideListView();

                populateListView(deckId);

                toSelectDeckId = getCurrentDeckIdSelected();

                delayListViewShow();

                return;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        setSpinnerSelected();


    }

    public void setSpinnerSelected() {
        if (!toSelectDeckId.isEmpty()) {
            for (int counter = 0; counter < deckList.size(); counter++) {
                if (deckList.get(counter).getDeckId().equals(toSelectDeckId)) {
                    spinner.setSelection(counter + SPINNER_OFFSET);
                    return;
                }
            }
        }
    }

    public void populateListView(String idDeck) {


        if (getCurrentDeckIdSelected().equals(ALL_DECKS_ITEM)) {
            nbCardsPractice.setVisibility(View.GONE);
        } else {
            nbCardsPractice.setVisibility(View.VISIBLE);
        }

        if (cardList != null) {
            cardList.clear();
        }


        if (idDeck.equals(ALL_DECKS_ITEM)) {
            cardList = dbManager.getCards();
            isPracticeList = null;
        } else {
            if (currentState == CardListState.EDIT_DECK) {
                // Edit
                cardList = dbManager.getCards();
                isPartOfList = new boolean[cardList.size()];


                for (int i = 0; i < isPartOfList.length; i++)
                    isPartOfList[i] = false;

                List<Card> deckCards = dbManager.getCardsFromDeck(idDeck);

                for (int counterAll = 0; counterAll < cardList.size(); counterAll++) {
                    for (int counterCard = 0; counterCard < deckCards.size(); counterCard++) {
                        if (cardList.get(counterAll).getCardId().equals(deckCards.get(counterCard).getCardId())) {
                            isPartOfList[counterAll] = true;
                            break;
                        }
                    }
                }
                isPartOfListTemp = isPartOfList.clone();
                setNumberCardsOther(numberCardsIncludedInDeck());
            } else {
                cardList = dbManager.getCardsFromDeck(idDeck);
            }

            // setup practice list
            isPracticeList = new boolean[cardList.size()];
            List<Card> practiceCards = dbManager.getDeckPracticeCards(getCurrentDeckIdSelected());

            for (int counterAll = 0; counterAll < cardList.size(); counterAll++) {
                for (int counterPractice = 0; counterPractice < practiceCards.size(); counterPractice++) {
                    if (cardList.get(counterAll).getCardId().equals(practiceCards.get(counterPractice).getCardId())) {
                        isPracticeList[counterAll] = true;
                        break;
                    }
                }
            }

            isPracticeListTemp = isPracticeList.clone();

            // show number of practice cards in VIEW or practice toggle
            if (currentState == CardListState.VIEW || currentState == CardListState.PRACTICE_TOGGLE) {
                setNumberCardsOther(numberCardsIncludedInPractice());
            }

        }


        if (!cardList.isEmpty()) {
            placeholderTextView.setVisibility(View.GONE);

            if (rebuild) {
                customListAdapter = new CustomListAdapter(getContext());
                listView.setAdapter(customListAdapter);
                rebuild = false;
            } else {
                customListAdapter.notifyDataSetChanged();
            }


            if (currentState == CardListState.VIEW) {
                registerForContextMenu(listView);
            } else {
                unregisterForContextMenu(listView);
            }


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                }
            });

        } else {
            progressBar.setVisibility(View.GONE);
            placeholderTextView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

        setNumberOfCards();


    }

    public void populateSearchListView(String containsString) {


        nbCardsPractice.setVisibility(View.GONE);

        if (getCurrentDeckIdSelected() == ALL_DECKS_ITEM)
            allCardsListSearch = dbManager.getCards();
        else
            allCardsListSearch = dbManager.getCardsFromDeck(getCurrentDeckIdSelected());

        cardList.clear();

        for (int counter = 0; counter < allCardsListSearch.size(); counter++) {
            if (allCardsListSearch.get(counter).getAnswer().toLowerCase().contains(containsString.toLowerCase()) ||
                    allCardsListSearch.get(counter).getQuestion().toLowerCase().contains(containsString.toLowerCase())) {
                cardList.add(allCardsListSearch.get(counter));
            }
        }


        customListAdapter = new CustomListAdapter(getContext());

        if (!cardList.isEmpty()) {
            placeholderTextView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            listView.setAdapter(customListAdapter);

            if (currentState == CardListState.VIEW) {
                registerForContextMenu(listView);
            } else {
                unregisterForContextMenu(listView);
            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                }
            });

        } else {
            placeholderTextView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
        setNumberOfCards();
    }


    private boolean areDecksDifferent() {
        for (int counter = 0; counter < isPartOfList.length; counter++) {
            if (isPartOfList[counter] != isPartOfListTemp[counter])
                return true;
        }
        return false;
    }

    private boolean arePracticeCardsDifferent() {
        for (int counter = 0; counter < isPracticeList.length; counter++) {
            if (isPracticeList[counter] != isPracticeListTemp[counter]) {
                return true;
            }
        }
        return false;
    }

    private void modifyDeckCards() {
        for (int counter = 0; counter < isPartOfList.length; counter++) {
            if (isPartOfList[counter] != isPartOfListTemp[counter]) {
                if (isPartOfList[counter]) {
                    dbManager.createCardDeck(cardList.get(counter).getCardId(), getCurrentDeckIdSelected());
                } else {
                    dbManager.deleteCardDeck(cardList.get(counter).getCardId(), getCurrentDeckIdSelected());
                }
            }
        }
    }


    public void setToSelectDeckId(String deckId) {
        toSelectDeckId = deckId;
    }

    public String getCurrentDeckIdSelected() {
        if (spinner.getSelectedItemPosition() == 0)
            return ALL_DECKS_ITEM;
        else
            return deckList.get(spinner.getSelectedItemPosition() - SPINNER_OFFSET).getDeckId();
    }

    private int numberCardsIncludedInDeck() {
        int nbPracticeCards = 0;
        for (int i = 0; i < isPartOfList.length; i++) {
            if (isPartOfList[i])
                nbPracticeCards++;
        }
        return nbPracticeCards;
    }


    public SearchView getSearchView() {
        return searchView;
    }


    private int numberCardsIncludedInPractice() {
        int nbPracticeCards = 0;
        for (int i = 0; i < isPracticeList.length; i++) {
            if (isPracticeList[i])
                nbPracticeCards++;
        }
        return nbPracticeCards;
    }

    public CardListState getCurrentState() {
        return currentState;
    }

    ////// CUSTOM ADAPTER //////

    private class CustomListAdapter extends BaseAdapter {
        Context context;

        private LayoutInflater inflater = null;

        public CustomListAdapter(Context context) {
            // TODO Auto-generated constructor stub
            super();
            this.context = context;
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return cardList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public class Holder {
            TextView questionHolder;
            TextView answerHolder;
            CheckBox checkBox;
            LinearLayout checkboxLayout;
            LinearLayout cardLayout;
            LinearLayout cardOptionLayout;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final Holder holder = new Holder();
            final View rowView;
            rowView = inflater.inflate(R.layout.custom_card_item, null);


            holder.questionHolder = (TextView) rowView.findViewById(R.id.custom_card_item_question);
            holder.answerHolder = (TextView) rowView.findViewById(R.id.custom_card_item_answer);
            holder.checkBox = (CheckBox) rowView.findViewById(R.id.custom_card_item_checkbox);
            holder.checkboxLayout = (LinearLayout) rowView.findViewById(R.id.checkbox_layout);
            holder.cardLayout = (LinearLayout) rowView.findViewById(R.id.custom_card_item_layout);
            holder.cardOptionLayout = (LinearLayout) rowView.findViewById(R.id.custom_card_option_layout);


            if (currentState == CardListState.VIEW) {
                holder.cardOptionLayout.setVisibility(View.VISIBLE);
                holder.cardOptionLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        indexSelected = position;
                    }
                });
            } else {
                holder.cardOptionLayout.setVisibility(View.GONE);
                holder.cardOptionLayout.setOnClickListener(null);
            }

            holder.cardOptionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentState == CardListState.VIEW) {
                        indexSelected = position;
                        getActivity().openContextMenu(listView);
                    }

                }
            });

            if (!isReverseOrder) {
                holder.questionHolder.setText(cardList.get(position).getQuestion());
                holder.answerHolder.setText(cardList.get(position).getAnswer());
            } else {
                holder.answerHolder.setText(cardList.get(position).getQuestion());
                holder.questionHolder.setText(cardList.get(position).getAnswer());
            }


            if (currentState == CardListState.VIEW || currentState == CardListState.SEARCH) {
                holder.checkboxLayout.setVisibility(View.GONE);
            } else if (currentState == CardListState.EDIT_DECK) {
                holder.checkboxLayout.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(isPartOfList[position]);

                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        isPartOfList[position] = isChecked;
                    }
                });

            } else if (currentState == CardListState.PRACTICE_TOGGLE) {
                holder.checkboxLayout.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(isPracticeList[position]);


                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        isPracticeList[position] = isChecked;
                        if (!isPracticeList[position])
                            holder.cardLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_not_selected));
                        else
                            holder.cardLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_normal));

                    }
                });

            }


            if (currentState == CardListState.VIEW) {
                rowView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        indexSelected = position;

                        return false;
                    }
                });
            } else {
                rowView.setOnLongClickListener(null);
            }


            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // TODO Auto-generated method stub
                    if (currentState == CardListState.VIEW || currentState == CardListState.SEARCH) {
                        String cardId = cardList.get(position).getCardId();
                        ((MainActivityManager) getActivity()).startActivityViewCard(cardId);
                    } else if (currentState == CardListState.EDIT_DECK) {
                        isPartOfList[position] = !isPartOfList[position];
                        holder.checkBox.setChecked(isPartOfList[position]);

                        if (isPartOfList[position]) {
                            holder.cardLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_normal));
                        } else {
                            holder.cardLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_not_selected));
                        }

                        setNumberCardsOther(numberCardsIncludedInDeck());
                    } else if (currentState == CardListState.PRACTICE_TOGGLE) {
                        isPracticeList[position] = !isPracticeList[position];

                        if (isPracticeList[position]) {
                            holder.checkBox.setChecked(true);
                            holder.cardLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_normal));
                        } else {
                            holder.cardLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_not_selected));
                            holder.checkBox.setChecked(false);
                        }
                        setNumberCardsOther(numberCardsIncludedInPractice());
                    }

                }
            });


            if (currentState == CardListState.EDIT_DECK) {
                if (isPartOfList[position])
                    holder.cardLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_normal));
                else
                    holder.cardLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_not_selected));
            } else if (isPracticeList != null && isPracticeList.length >= position && !isPracticeList[position]) {
                holder.cardLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_not_selected));
            } else {

                holder.cardLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_normal));
            }


            rowView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    holder.cardLayout.getBackground().setHotspot(event.getX(), event.getY());


                    return false;
                }
            });


            return rowView;

        }

    }
}
