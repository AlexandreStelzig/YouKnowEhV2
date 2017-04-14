package stelztech.youknowehv4.fragmentpackage;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activitypackage.MainActivityManager;
import stelztech.youknowehv4.database.DatabaseManager;
import stelztech.youknowehv4.model.Card;
import stelztech.youknowehv4.model.Deck;
import stelztech.youknowehv4.manager.ActionButtonManager;
import stelztech.youknowehv4.manager.MainMenuToolbarManager;

/**
 * Created by alex on 2017-04-03.
 */

public class CardListFragment extends Fragment {


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
    private List<String> mCardListString;
    private CustomListAdapter customListAdapter;
    private ListView listView;
    private TextView textView;

    // dialog
    private String wordNameHolder;
    private int indexSelected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);

        ActionButtonManager.getInstance().setState(ActionButtonManager.ActionButtonState.ADD_WORD, getActivity());
        setHasOptionsMenu(true);

        // init
        wordNameHolder = "";
        indexSelected = -1;
        spinner = ((Spinner) getActivity().findViewById(R.id.spinner_nav));
        dbManager = DatabaseManager.getInstance(getActivity());
        listView = (ListView) view.findViewById(R.id.listview);
        textView = (TextView) view.findViewById(R.id.list_text);
        textView.setText("NO CARDS");

        cardList = new ArrayList<Card>();
        mCardListString = new ArrayList<String>();

        populateSpinner();
        populateListView(ALL_DECKS_ITEM);

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MainMenuToolbarManager.getInstance().setState(MainMenuToolbarManager.MainMenuToolbarState.WORD, menu, getActivity());
    }

    ////// HOLD MENU //////

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        indexSelected = info.position;

        if (v.getId() == R.id.listview) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.hold_menu_word, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_word:
                editWord();
                populateListView(getCurrentDeckIdSelected());
                return true;
            case R.id.delete_word:
                displayDeleteConfirmationDialog();
                return true;
            case R.id.info_word:
                showQuickInfoWord();
                populateListView(getCurrentDeckIdSelected());
                return true;
            default:
                indexSelected = -1;
                return super.onContextItemSelected(item);
        }
    }

    private void showQuickInfoWord() {
    }

    private void deleteWordFromDatabase() {
        dbManager.deleteCard((String) cardList.get(indexSelected).getCardId());
        Toast.makeText(getContext(), "word deleted", Toast.LENGTH_SHORT).show();
        populateListView(getCurrentDeckIdSelected());
    }

    private void editWord() {

    }

    private void displayDeleteConfirmationDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setMessage("Are you sure you want to delete:\n \'" +
                cardList.get(indexSelected).getQuestion() + "\'?");
        alertDialog.setTitle("Delete Card");

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                onOkClick();
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

    private void onOkClick() {
        deleteWordFromDatabase();
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
                getContext(), R.layout.custom_spinner_item, deckListString);
        deckArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
            toSelectDeckId = "-1";
        }
    }

    public void populateListView(String idDeck) {
        if (cardList != null)
            cardList.clear();
        if (mCardListString != null)
            mCardListString.clear();


        if (idDeck.equals("-1"))
            cardList = dbManager.getCards();
        else
            cardList = dbManager.getCardsFromDeck(idDeck);

        if (!cardList.isEmpty()) {
            textView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            for (int i = 0; i < cardList.size(); i++) {
                mCardListString.add(cardList.get(i).getQuestion());
            }

            customListAdapter = new CustomListAdapter(getContext());

            listView.setAdapter(customListAdapter);
            registerForContextMenu(listView);

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
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            Holder holder = new Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.custom_card_item, null);
            holder.questionHolder = (TextView) rowView.findViewById(R.id.custom_card_item_question);
            holder.answerHolder = (TextView) rowView.findViewById(R.id.custom_card_item_answer);
            holder.questionHolder.setText(cardList.get(position).getQuestion());
            holder.answerHolder.setText(cardList.get(position).getAnswer());

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    String wordId = cardList.get(position).getCardId();
                    ((MainActivityManager) getActivity()).startActivityViewCard(wordId);

                }
            });

            rowView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    indexSelected = position;
                    return false;
                }
            });

            return rowView;

        }

    }
}
