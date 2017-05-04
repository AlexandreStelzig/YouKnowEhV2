package stelztech.youknowehv4.fragmentpackage;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
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
import stelztech.youknowehv4.model.Card;
import stelztech.youknowehv4.model.Deck;

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
    private TextView textView;
    private boolean[] isPartOfList;
    private boolean[] isPartOfListTemp;
    private boolean[] isPracticeList;
    private boolean[] isPracticeListTemp;

    // dialog
    private int indexSelected;

    // state
    private CardListState currentState;

    // reverse
    private boolean isReverseOrder;

    // quick create holder
    private String questionHolder;
    private String answerHolder;

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
        textView = (TextView) view.findViewById(R.id.list_text);
        textView.setText("NO CARDS");
        allCardsListSearch = new ArrayList<>();


        cardList = new ArrayList<Card>();

        isReverseOrder = false;

        currentState = CardListState.VIEW;

        populateSpinner();
        populateListView(getCurrentDeckIdSelected());

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
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();

        searchView.setQueryHint("Search Cards...");

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

        MenuItemCompat.setOnActionExpandListener(myActionMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                currentState = CardListState.SEARCH;

                MainMenuToolbarManager.getInstance().setState(MainMenuToolbarManager.MainMenuToolbarState.SEARCH, menu, getActivity());
                ActionButtonManager.getInstance().setState(ActionButtonManager.ActionButtonState.GONE, getActivity());

                ((MainActivityManager) getActivity()).enableDrawerSwipe(false);
                allCardsListSearch = dbManager.getCards();
                isPracticeList = null;
                populateSearchListView("");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
//                ActionButtonManager.getInstance().setState(ActionButtonManager.ActionButtonState.ADD_CARD, getActivity());
                changeStateToView();
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
                if (deckList.size() != 0) {
                    isReverseOrder = !isReverseOrder;
                    populateListView(getCurrentDeckIdSelected());
                }
                return true;
            case R.id.action_edit_deck_cards:
                if (deckList.size() == 0) {
                    Toast.makeText(getContext(), "No cards in deck", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (getCurrentDeckIdSelected() != ALL_DECKS_ITEM) {
                    currentState = CardListState.EDIT_DECK;
                    populateListView(getCurrentDeckIdSelected());
                    getActivity().invalidateOptionsMenu();
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
                    currentState = CardListState.PRACTICE_TOGGLE;
                    populateListView(getCurrentDeckIdSelected());
                    getActivity().invalidateOptionsMenu();
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
                changeStateToView();
                return true;
            case R.id.action_cancel:
                if (currentState == CardListState.EDIT_DECK && areDecksDifferent()) {
                    displayConfirmationDialog();

                } else if (currentState == CardListState.PRACTICE_TOGGLE && arePracticeCardsDifferent()) {
                    displayConfirmationDialog();

                } else {
                    changeStateToView();
                }

                return true;
            case R.id.action_sort:
                sortDialog().show();
                return true;
            case R.id.quick_create:
                quickCreateUpdateDialog(CardQuickDialogOption.QUICK_NEW).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
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

        deckListAlertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        // TODO array -> menu for sorting choices

        deckListAlertDialog.setTitle("Sort");


        final String[] sortingChoices = getContext().getResources().getStringArray(R.array.sort_options);

        deckListAlertDialog.setSingleChoiceItems(sortingChoices, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // todo add temp variable for selected
                switch (which) {
                    // Question: A-Z
                    case 0:

                        break;
                    // Question: Z-A
                    case 1:

                        break;
                    // Answer: A-Z
                    case 2:

                        break;
                    // Answer: Z-A
                    case 3:

                        break;
                    // Date Created
                    case 4:

                        break;
                    // Date Modified
                    case 5:

                        break;
                }
            }
        });

        AlertDialog alert = deckListAlertDialog.create();
        Helper.getInstance().hideKeyboard(getActivity());
        return alert;
    }


    private AlertDialog quickCreateUpdateDialog(final CardQuickDialogOption dialogType) {

        questionHolder = "";
        answerHolder = "";

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        switch (dialogType) {
            case QUICK_NEW:
                builder.setTitle("Quick Create Card");
                break;
            case QUICK_UPDATE:

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

        final String currentSelectedDeckId = getCurrentDeckIdSelected();


        if (dialogType == CardQuickDialogOption.QUICK_NEW) {
            if (currentSelectedDeckId == ALL_DECKS_ITEM)
                deckPlaceHolder.setText("No Deck");
            else
                deckPlaceHolder.setText(dbManager.getDeckFromId(currentSelectedDeckId).getDeckName());
        } else {
            // todo number of deck
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

        builder.setNeutralButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        final AlertDialog aDialog = builder.create();

        aDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button okButton = aDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                okButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
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
                                aDialog.dismiss();
                            } else {
                                // todo update function
                            }

                        }
                    }
                });
            }
        });

        aDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        input.setSelection(input.getText().length());
        return aDialog;


    }


    ////// HOLD MENU //////

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        indexSelected = info.position;

        if (v.getId() == R.id.listview) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.hold_menu_card, menu);
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
                populateListView(getCurrentDeckIdSelected());
                return true;
            case R.id.toggle_practice:
                if (getCurrentDeckIdSelected() != ALL_DECKS_ITEM) {
                    toggleCardFromPractice();
                    populateListView(getCurrentDeckIdSelected());
                    Toast.makeText(getContext(), "Card toggle from practice", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), "Please select a deck", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.remove_from_deck:
                if (getCurrentDeckIdSelected() != ALL_DECKS_ITEM) {
                    dbManager.deleteCardDeck(cardList.get(indexSelected).getCardId(), getCurrentDeckIdSelected());
                    Toast.makeText(getContext(), "Card removed from deck", Toast.LENGTH_SHORT).show();
                    populateListView(getCurrentDeckIdSelected());
                } else {
                    Toast.makeText(getContext(), "Please select a deck", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                indexSelected = -1;
                return super.onContextItemSelected(item);
        }
    }


    private void showQuickInfoCard() {
        Card card = cardList.get(indexSelected);
        String cardQuestion = "Card question: " + card.getQuestion();
        String cardAnswer = "Card answer: " + card.getAnswer();

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
        dbManager.deleteCard((String) cardList.get(indexSelected).getCardId());
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
            changeStateToView();
        }
    }

    private void changeStateToView() {
        currentState = CardListState.VIEW;
        populateListView(getCurrentDeckIdSelected());
        getActivity().invalidateOptionsMenu();
    }

    private void populateSpinner() {
        if (deckArrayAdapter != null)
            deckArrayAdapter.clear();
        if (deckList != null)
            deckList.clear();

        deckList = dbManager.getDecks();
        final List<String> deckListString = new ArrayList<>();


        deckListString.add(new String("All Cards"));
        for (int counter = 0; counter < deckList.size(); counter++) {
            deckListString.add(deckList.get(counter).getDeckName());
        }

        deckArrayAdapter = new ArrayAdapter<String>(
                getContext(), R.layout.custom_spinner_item_card, deckListString);
        deckArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
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
        if (!toSelectDeckId.isEmpty()) {
            for (int counter = 0; counter < deckList.size(); counter++) {
                if (deckList.get(counter).getDeckId().equals(toSelectDeckId)) {
                    spinner.setSelection(counter + SPINNER_OFFSET);
                    return;
                }
            }
            toSelectDeckId = ALL_DECKS_ITEM;
        }
    }

    public void populateListView(String idDeck) {
        if (cardList != null)
            cardList.clear();

        customListAdapter = new CustomListAdapter(getContext());


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
            }

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


        if (!cardList.isEmpty()) {
            textView.setVisibility(View.GONE);
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
            textView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

    }

    private void populateSearchListView(String containsString) {


        cardList.clear();

        for (int counter = 0; counter < allCardsListSearch.size(); counter++) {
            if (allCardsListSearch.get(counter).getAnswer().contains(containsString) ||
                    allCardsListSearch.get(counter).getQuestion().contains(containsString)) {
                cardList.add(allCardsListSearch.get(counter));
            }
        }


        customListAdapter = new CustomListAdapter(getContext());

        if (!cardList.isEmpty()) {
            textView.setVisibility(View.GONE);
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
            textView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
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
                            holder.cardLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorNotPractice));
                        else
                            holder.cardLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

                    }
                });

            }


            if (currentState == CardListState.VIEW) {
                rowView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (currentState == CardListState.VIEW) {
                            indexSelected = position;
                        }
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
                    } else if (currentState == CardListState.PRACTICE_TOGGLE) {
                        isPracticeList[position] = !isPracticeList[position];
                        if (isPracticeList[position]) {
                            holder.checkBox.setChecked(true);
                            holder.cardLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                        } else {
                            holder.cardLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorNotPractice));
                            holder.checkBox.setChecked(false);
                        }
                    }

                }
            });


            if (isPracticeList != null && isPracticeList.length >= position && !isPracticeList[position] && currentState != CardListState.EDIT_DECK) {
                holder.cardLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorNotPractice));
            } else {
                holder.cardLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            }

            rowView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    return false;
                }
            });


            return rowView;

        }

    }
}
