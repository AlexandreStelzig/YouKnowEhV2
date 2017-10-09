package stelztech.youknowehv4.fragmentpackage.card;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
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
import stelztech.youknowehv4.helper.CardHelper;
import stelztech.youknowehv4.helper.Helper;
import stelztech.youknowehv4.manager.ActionButtonManager;
import stelztech.youknowehv4.manager.CardToolbarManager;
import stelztech.youknowehv4.manager.SortingStateManager;
import stelztech.youknowehv4.model.Card;
import stelztech.youknowehv4.model.Deck;
import stelztech.youknowehv4.model.Profile;
import stelztech.youknowehv4.model.User;

/**
 * Created by alex on 2017-04-03.
 */

public class CardListFragment extends Fragment {


    public void setScrollToTop(boolean scrollToTop) {
        this.scrollToTop = scrollToTop;
    }

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

    private boolean scrollToTop = false;
    private boolean selectionFromActivity = false;

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

    private boolean filterPractice;

    private LoadData dataLoader;
    private boolean isLoading;


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
        isLoading = false;

        currentProfile = dbManager.getActiveProfile();

        questionLabel = currentProfile.getQuestionLabel();
        answerLabel = currentProfile.getAnswerLabel();

        orientationQuestionAnswer = questionLabel + " - " + answerLabel;
        orientationAnswerQuestion = answerLabel + " - " + questionLabel;

        allCardsListSearch = new ArrayList<>();
        dataLoader = new LoadData();

        cardList = new ArrayList<Card>();

        currentState = CardListState.VIEW;

        setOrientationText();

        customListAdapter = new CustomListAdapter(getContext());
        listView.setAdapter(customListAdapter);

        populateSpinner();

//        populateListView(getCurrentDeckIdSelected());

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.toolbar_card_list, menu);

    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {


        if (currentState == CardListState.VIEW) {
            if (getCurrentDeckIdSelected().equals(ALL_DECKS_ITEM))
                CardToolbarManager.getInstance().setState(CardToolbarManager.CardToolbarState.ALL_CARDS, menu, getActivity());
            else
                CardToolbarManager.getInstance().setState(CardToolbarManager.CardToolbarState.CARD, menu, getActivity());
            ActionButtonManager.getInstance().setState(ActionButtonManager.ActionButtonState.ADD_CARD, getActivity());
        } else if (currentState == CardListState.SEARCH) {
            if (getCurrentDeckIdSelected().equals(ALL_DECKS_ITEM))
                CardToolbarManager.getInstance().setState(CardToolbarManager.CardToolbarState.SEARCH_ALL, menu, getActivity());
            else
                CardToolbarManager.getInstance().setState(CardToolbarManager.CardToolbarState.SEARCH, menu, getActivity());
            ActionButtonManager.getInstance().setState(ActionButtonManager.ActionButtonState.GONE, getActivity());
        } else {
            CardToolbarManager.getInstance().setState(CardToolbarManager.CardToolbarState.CARD_LIST_EDIT, menu, getActivity());
            ActionButtonManager.getInstance().setState(ActionButtonManager.ActionButtonState.GONE, getActivity());

        }


        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print

                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                searchView.setQuery(query, false);
                populateSearchListView(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {


                if (dbManager.getUser().isAllowOnQueryChanged())
                    populateSearchListView(s);

                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                if (isLoading) {
                    searchItem.collapseActionView();
                    return true;
                }

                if (getCurrentDeckIdSelected().equals(ALL_DECKS_ITEM))
                    searchView.setQueryHint("Search All Cards...");
                else {
                    String deckName = dbManager.getDeckFromId(getCurrentDeckIdSelected()).getDeckName();
                    searchView.setQueryHint("Search " + deckName + " Cards...");
                }

                changeState(CardListState.SEARCH);

                if (getCurrentDeckIdSelected().equals(ALL_DECKS_ITEM))
                    CardToolbarManager.getInstance().setState(CardToolbarManager.CardToolbarState.SEARCH_ALL, menu, getActivity());
                else
                    CardToolbarManager.getInstance().setState(CardToolbarManager.CardToolbarState.SEARCH, menu, getActivity());

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


        final MenuItem filterPracticeItem = menu.findItem(R.id.action_filter_practice);
        if (filterPractice)
            filterPracticeItem.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_check_box_black_24dp));
        else
            filterPracticeItem.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_check_box_outline_blank_black_24dp));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (!isLoading) {
            switch (id) {
                case R.id.action_reverse:

                    isReverseOrder = !isReverseOrder;
                    setOrientationText();
                    Toast.makeText(getContext(), "Orientation: " + orientation.getText().toString(), Toast.LENGTH_SHORT).show();
                    populateListView(getCurrentDeckIdSelected());

                    return true;
                case R.id.action_edit_deck_cards:
                    if (deckList.isEmpty()) {
                        Toast.makeText(getContext(), "No decks", Toast.LENGTH_SHORT).show();
                        return true;
                    } else if (dbManager.getCards().isEmpty()) {
                        Toast.makeText(getContext(), "No cards", Toast.LENGTH_SHORT).show();
                    }
                    if (getCurrentDeckIdSelected() != ALL_DECKS_ITEM) {
                        changeState(CardListState.EDIT_DECK);
                    } else {
                        Toast.makeText(getContext(), "Please select a deck", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                case R.id.action_practice_toggle:
                    if (deckList.isEmpty()) {
                        Toast.makeText(getContext(), "No decks", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    if (!getCurrentDeckIdSelected().equals(ALL_DECKS_ITEM)) {
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
                    Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_cancel:
                    cancelState();
                    return true;
                case R.id.action_sort:
                    sortDialog().show();
                    return true;
                case R.id.action_quick_create:
                    quickCreateUpdateDialog(CardQuickDialogOption.QUICK_NEW, null).show();
                    return true;
                case R.id.action_filter_practice:
                    filterPractice = !filterPractice;
                    populateListView(getCurrentDeckIdSelected());
                    if (filterPractice)
                        item.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_check_box_black_24dp));
                    else
                        item.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_check_box_outline_blank_black_24dp));
                    return true;
            }
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
        nbCards.setVisibility(View.VISIBLE);
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
        nbCardsPractice.setVisibility(View.VISIBLE);
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
                mainActivityManager.getSupportActionBar().setSubtitle("");
                populateListView(getCurrentDeckIdSelected());
                getActivity().invalidateOptionsMenu();
                break;
            case EDIT_DECK:
                currentState = CardListState.EDIT_DECK;
                mainActivityManager.getSupportActionBar().setTitle("Add/Remove");
                mainActivityManager.getSupportActionBar().setSubtitle(dbManager.getDeckFromId(getCurrentDeckIdSelected()).getDeckName());
                populateListView(getCurrentDeckIdSelected());
                getActivity().invalidateOptionsMenu();
                break;
            case PRACTICE_TOGGLE:
                currentState = CardListState.PRACTICE_TOGGLE;
                mainActivityManager.getSupportActionBar().setTitle("Toggle Review");
                mainActivityManager.getSupportActionBar().setSubtitle(dbManager.getDeckFromId(getCurrentDeckIdSelected()).getDeckName());
                populateListView(getCurrentDeckIdSelected());
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

    public void cancelState() {
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

        final AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

        String[] sortingOptions = getResources().getStringArray(R.array.sort_options);
        sortingOptions[0] = currentProfile.getQuestionLabel() + " (A-Z)";
        sortingOptions[1] = currentProfile.getQuestionLabel() + " (Z-A)";
        sortingOptions[2] = currentProfile.getAnswerLabel() + " (A-Z)";
        sortingOptions[3] = currentProfile.getAnswerLabel() + " (Z-A)";

        final String[] sortingChoices = sortingOptions;
        builder.setTitle("Sort by");

        builder.setSingleChoiceItems(sortingChoices, SortingStateManager.getInstance().getSelectedPosition(),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SortingStateManager sortingStateManager = SortingStateManager.getInstance();
                        sortingStateManager.changeStateByPosition(which);

                        if (currentState == CardListState.SEARCH)
                            populateSearchListView(searchView.getQuery().toString());
                        else
                            populateListView(getCurrentDeckIdSelected());

                        dialog.dismiss();
//                        listView.smoothScrollToPosition(0);

                        int sortingPosition = sortingStateManager.getSelectedPosition();
                        Toast.makeText(getContext(), "Sort by: " + sortingChoices[sortingPosition], Toast.LENGTH_SHORT).show();
                    }
                });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        AlertDialog alert = builder.create();
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

                builder.setCustomTitle(Helper.getInstance().customTitle("Quick Create"));
                break;
            case QUICK_UPDATE:
                builder.setCustomTitle(Helper.getInstance().customTitle("Quick Update Card"));
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

        questionEditTextView.setHint(questionLabel + " (mandatory)");
        answerEditTextView.setHint(answerLabel + " (mandatory)");

        final String currentSelectedDeckId = getCurrentDeckIdSelected();

        questionEditTextView.setText(questionHolder);
        answerEditTextView.setText(answerHolder);
        questionEditTextView.setSelection(questionEditTextView.getText().length());


        if (dialogType == CardQuickDialogOption.QUICK_NEW) {
            if (getCurrentDeckIdSelected().equals(ALL_DECKS_ITEM))
                deckPlaceHolder.setText("No Deck");
            else
                deckPlaceHolder.setText(dbManager.getDeckFromId(currentSelectedDeckId).getDeckName());
        } else {
            int numberDecks = dbManager.numberDecksInCard(card.getCardId());
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
                            String toastMessageError = "";
                            Profile profile = dbManager.getActiveProfile();
                            String questionLabel = profile.getQuestionLabel();
                            String answerLabel = profile.getAnswerLabel();
                            if (answerEmpty && questionEmpty)
                                toastMessageError = questionLabel + " and " + answerLabel + " cannot be empty";
                            else if (questionEmpty && !answerEmpty)
                                toastMessageError = questionLabel + " cannot be empty";
                            else if (answerEmpty && !questionEmpty)
                                toastMessageError = answerLabel + " cannot be empty";

                            Toast.makeText(getContext(), (String) toastMessageError,
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            if (dialogType == CardQuickDialogOption.QUICK_NEW) {
                                String newCardId = dbManager.createCard(questionHolder, answerHolder, "");
                                if (currentSelectedDeckId != ALL_DECKS_ITEM) {
                                    dbManager.createCardDeck(newCardId, currentSelectedDeckId);
                                }
                                populateListView(getCurrentDeckIdSelected());
                                Toast.makeText(getContext(), "Card created", Toast.LENGTH_SHORT).show();
                                aDialog.dismiss();

                            } else {
                                if (answerTemp.equals(answerHolder) && questionTemp.equals(questionHolder)) {

                                    String toastMessageError = "";
                                    Profile profile = dbManager.getActiveProfile();
                                    String questionLabel = profile.getQuestionLabel();
                                    String answerLabel = profile.getAnswerLabel();

                                    Toast.makeText(getContext(), "Card not updated: " + questionLabel + " and " + answerLabel + " are the same", Toast.LENGTH_SHORT).show();
                                } else {
                                    dbManager.updateCard(card.getCardId(), questionHolder, answerHolder, card.getMoreInfo());
                                    populateListView(getCurrentDeckIdSelected());
                                    Toast.makeText(getContext(), "Card updated", Toast.LENGTH_SHORT).show();
                                    aDialog.dismiss();
                                }
                            }

                        }

                        answerHolder = answerTemp;
                        questionHolder = questionTemp;
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
                                String toastMessageError = "";
                                Profile profile = dbManager.getActiveProfile();
                                String questionLabel = profile.getQuestionLabel();
                                String answerLabel = profile.getAnswerLabel();
                                if (answerEmpty && questionEmpty)
                                    toastMessageError = questionLabel + " and " + answerLabel + " cannot be empty";
                                else if (!answerEmpty && questionEmpty)
                                    toastMessageError = questionLabel + " cannot be empty";
                                else if (!questionEmpty && answerEmpty)
                                    toastMessageError = answerLabel + " cannot be empty";

                                Toast.makeText(getContext(), (String) toastMessageError,
                                        Toast.LENGTH_SHORT).show();
                            } else {

                                if (dialogType == CardQuickDialogOption.QUICK_NEW) {
                                    String newCardId = dbManager.createCard(questionHolder, answerHolder, "");
                                    if (currentSelectedDeckId != ALL_DECKS_ITEM) {
                                        dbManager.createCardDeck(newCardId, currentSelectedDeckId);
                                    }

                                    Toast.makeText(getContext(), "Card created", Toast.LENGTH_SHORT).show();
                                    populateListView(getCurrentDeckIdSelected());
                                    answerEditTextView.setText("");
                                    questionEditTextView.setText("");
                                    questionEditTextView.requestFocus();

                                }
                            }


                            answerHolder = "";
                            questionHolder = "";
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
            menu.setHeaderTitle(cardList.get(indexSelected).getQuestion() + " / " +
                    cardList.get(indexSelected).getAnswer());
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.hold_menu_card, menu);

            MenuItem practice = menu.findItem(R.id.toggle_practice);
            MenuItem rmDeck = menu.findItem(R.id.remove_from_deck);

            if (getCurrentDeckIdSelected().equals(ALL_DECKS_ITEM)) {
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
                CardHelper.showQuickInfoCard(getContext(), getActivity(), cardList.get(indexSelected));

                return true;
            case R.id.toggle_practice:
                if (getCurrentDeckIdSelected() != ALL_DECKS_ITEM) {
                    toggleCardFromPractice();
                    populateListView(getCurrentDeckIdSelected());
                    Toast.makeText(getContext(), "Card toggled from review", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), "Please select a deck", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.remove_from_deck:
                if (getCurrentDeckIdSelected() != ALL_DECKS_ITEM) {
                    dbManager.deleteCardDeck(cardList.get(indexSelected).getCardId(), getCurrentDeckIdSelected());
                    String cardRemoved = cardList.get(indexSelected).getQuestion() + " / " + cardList.get(indexSelected).getAnswer();
                    Toast.makeText(getContext(), "\"" + cardRemoved + "\" removed from deck", Toast.LENGTH_SHORT).show();
                    populateListView(getCurrentDeckIdSelected());
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


    private void deleteCardFromDatabase() {
        dbManager.toggleArchiveCard((String) cardList.get(indexSelected).getCardId());
        String question = cardList.get(indexSelected).getQuestion();
        String answer = cardList.get(indexSelected).getAnswer();

        Toast.makeText(getContext(), "\"" + question + " / " + answer + "\"  deleted", Toast.LENGTH_SHORT).show();
        populateListView(getCurrentDeckIdSelected());
    }

    private void editCard() {
        String cardId = cardList.get(indexSelected).getCardId();
        ((MainActivityManager) getActivity()).startActivityEditCard(cardId);
    }

    private void toggleCardFromPractice() {
        dbManager.togglePractice_Card(cardList.get(indexSelected).getCardId(), getCurrentDeckIdSelected(), -1);
    }

    private void toggleCardsFromPractice() {
        for (int counter = 0; counter < isPracticeList.length; counter++) {
            if (isPracticeList[counter] != isPracticeListTemp[counter]) {
                dbManager.togglePractice_Card(cardList.get(counter).getCardId(), getCurrentDeckIdSelected(), -1);
            }
        }
    }

    private void displayConfirmationDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        if (currentState == CardListState.VIEW) {
            alertDialog.setMessage("Are you sure you want to delete:\n\"" +
                    cardList.get(indexSelected).getQuestion() + "\"?");
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
            Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
        }
    }

    public void populateSpinner() {

        if (deckArrayAdapter != null) {
            deckArrayAdapter.clear();
        }

        if (deckList != null)
            deckList.clear();


        deckList = dbManager.getDecks();
        final List<String> deckListString = new ArrayList<>();


        if (deckList.isEmpty()) {
            spinner.setEnabled(false);
            deckListString.add(new String("All Cards\nNo Decks"));

        } else {
            spinner.setEnabled(true);
            deckListString.add(new String("All Cards"));
        }


        if (deckList.isEmpty()) {
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
                    mView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ripple_grey));
                else
                    mView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ripple_normal));

                if (position == 0) {
                    mTextView.setTypeface(null, Typeface.BOLD);
                } else {
                    mTextView.setTypeface(null, Typeface.NORMAL);
                }


                return mTextView;
            }

        };

        deckArrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        spinner.setAdapter(deckArrayAdapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (selectionFromActivity) {
                    selectionFromActivity = false;
                    scrollToTop = false;
                } else
                    scrollToTop = true;


                String deckId;
                if (position == 0) {
                    deckId = ALL_DECKS_ITEM;
                } else {
                    deckId = deckList.get(position - SPINNER_OFFSET).getDeckId();
                }

                populateListView(deckId);
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
        selectionFromActivity = true;
        if (!toSelectDeckId.isEmpty()) {
            for (int counter = 0; counter < deckList.size(); counter++) {
                if (deckList.get(counter).getDeckId().equals(toSelectDeckId)) {
                    spinner.setSelection(counter + SPINNER_OFFSET, false);
                    return;
                }
            }
        }

    }

    public void populateListView(String idDeck) {

        ((MainActivityManager) getActivity()).enableDrawerSwipe(false);

        if (getCurrentDeckIdSelected().equals(ALL_DECKS_ITEM)) {
            nbCardsPractice.setVisibility(View.GONE);
        } else {
            nbCardsPractice.setVisibility(View.VISIBLE);
        }

        spinner.setEnabled(false);
        nbCardsPractice.setVisibility(View.GONE);
        nbCards.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        placeholderTextView.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);

        dataLoader = new LoadData();
        dataLoader.execute(idDeck);
    }

    public void populateSearchListView(String containsString) {

        listView.setSelection(0);

        nbCardsPractice.setVisibility(View.GONE);


        progressBar.setVisibility(View.VISIBLE);
        placeholderTextView.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);


        new LoadSearchData().execute(containsString);

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
            LinearLayout nbDecksLayout;
            TextView nbDecksLabel;
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
            holder.nbDecksLayout = (LinearLayout) rowView.findViewById(R.id.custom_card_item_nb_decks_layout);
            holder.nbDecksLabel = (TextView) rowView.findViewById(R.id.custom_card_item_nb_decks_label);


            User user = dbManager.getUser();

            if ((getCurrentDeckIdSelected().equals(ALL_DECKS_ITEM) && user.isDisplayNbDecksAllCards())
                    || (!getCurrentDeckIdSelected().equals(ALL_DECKS_ITEM) && user.isDisplayNbDecksSpecificCards())) {
                holder.nbDecksLayout.setVisibility(View.VISIBLE);
                int nbDecks = dbManager.getDecksFromCard(cardList.get(position).getCardId()).size();
                String nbDeckString;
                if (nbDecks == 1)
                    nbDeckString = "1 Deck";
                else
                    nbDeckString = nbDecks + " Decks";
                holder.nbDecksLabel.setText(nbDeckString);

            } else {
                holder.nbDecksLayout.setVisibility(View.GONE);
            }


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

                        if (isPartOfList[position]) {
                            holder.cardLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_no_mask_normal));
                        } else {
                            holder.cardLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_no_mask_grey));
                        }
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
                            holder.cardLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_no_mask_grey));
                        else
                            holder.cardLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_no_mask_normal));

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
                            holder.cardLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_no_mask_normal));
                        } else {
                            holder.cardLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_no_mask_grey));
                        }

                        setNumberCardsOther(numberCardsIncludedInDeck());
                    } else if (currentState == CardListState.PRACTICE_TOGGLE) {
                        isPracticeList[position] = !isPracticeList[position];

                        if (isPracticeList[position]) {
                            holder.checkBox.setChecked(true);
                            holder.cardLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_no_mask_normal));
                        } else {
                            holder.cardLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_no_mask_grey));
                            holder.checkBox.setChecked(false);
                        }
                        setNumberCardsOther(numberCardsIncludedInPractice());
                    }

                }
            });


            if (currentState == CardListState.EDIT_DECK) {
                if (isPartOfList[position]) {
                    holder.cardLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_no_mask_normal));
                } else {
                    holder.cardLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_no_mask_grey));
                }

            } else if (isPracticeList != null && isPracticeList.length >= position && !isPracticeList[position]) {
                if (currentState == CardListState.PRACTICE_TOGGLE) {
                    holder.cardLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_no_mask_grey));
                } else {
                    holder.cardLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_grey));
                }
            } else {
                if (currentState == CardListState.PRACTICE_TOGGLE)
                    holder.cardLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_no_mask_normal));
                else

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

    private class LoadData extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {

            String idDeck = params[0];

            isLoading = true;
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

                } else {
                    cardList = dbManager.getCardsFromDeck(idDeck);
                    if (filterPractice) {
                        SortingStateManager.getInstance().sortByPractice(getContext(), cardList, getCurrentDeckIdSelected());
                    }
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

            }


            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressBar.setVisibility(View.GONE);
            placeholderTextView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);


            if (currentState != CardListState.SEARCH) {
                if (getCurrentDeckIdSelected() != ALL_DECKS_ITEM)
                    setNumberCardsOther(numberCardsIncludedInPractice());
            }


            if (!cardList.isEmpty()) {
                placeholderTextView.setVisibility(View.GONE);

                if (currentState == CardListState.VIEW) {
                    registerForContextMenu(listView);
                } else {
                    unregisterForContextMenu(listView);
                }


                placeholderTextView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);


                if (scrollToTop) {
                    listView.smoothScrollToPosition(0);
                    scrollToTop = false;
                }

            } else {
                placeholderTextView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }
            customListAdapter.notifyDataSetChanged();
            toSelectDeckId = getCurrentDeckIdSelected();

            getActivity().invalidateOptionsMenu();


            setNumberOfCards();
            spinner.setEnabled(true);
            isLoading = false;
            ((MainActivityManager) getActivity()).enableDrawerSwipe(true);
        }
    }

    private class LoadSearchData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            isLoading = true;

            String containsString = params[0];

            if (getCurrentDeckIdSelected().equals(ALL_DECKS_ITEM))
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

            return null;
        }

        protected void onPostExecute(String s) {

            progressBar.setVisibility(View.GONE);
            placeholderTextView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            if (!cardList.isEmpty()) {
                placeholderTextView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);


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
                listView.smoothScrollToPosition(0);
            } else {
                placeholderTextView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }


            customListAdapter.notifyDataSetChanged();
            setNumberOfCards();
            isLoading = false;
        }
    }

    public boolean isLoading() {
        return isLoading;
    }
}
