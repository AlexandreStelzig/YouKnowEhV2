package stelztech.youknowehv4.fragmentpackage;


import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activitypackage.MainActivityManager;
import stelztech.youknowehv4.database.DatabaseManager;
import stelztech.youknowehv4.helper.Helper;
import stelztech.youknowehv4.manager.ActionButtonManager;
import stelztech.youknowehv4.manager.DeckToolbarManager;
import stelztech.youknowehv4.manager.ExportImportManager;
import stelztech.youknowehv4.model.Card;
import stelztech.youknowehv4.model.Deck;

/**
 * Created by alex on 2017-04-03.
 */

public class DeckListFragment extends Fragment {


    private TextView nbDeck;

    public enum DeckDialogOption {
        NEW,
        UPDATE
    }

    // views
    private View view;
    private ListView listView;
    private TextView placerholderTextView;

    // database
    private DatabaseManager dbManager;

    // list
    private List<Deck> deckList;
    private List<Integer> nbCardsInDeckList;
    private CustomListAdapter customListAdapter;

    // dialog
    private String deckNameHolder;
    private int indexSelected;

    // loading indicator
    private ProgressBar progressBar;

    private boolean deckOrdering;
    private int deckOrderingLastElement;

    private boolean scrollToTop = false;


    private boolean loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);

        ActionButtonManager.getInstance().setState(ActionButtonManager.ActionButtonState.DECK, getActivity());
        setHasOptionsMenu(true);

        // init variables
        deckNameHolder = "";
        indexSelected = -1;
        listView = (ListView) view.findViewById(R.id.listview);
        placerholderTextView = (TextView) view.findViewById(R.id.list_text);
        placerholderTextView.setText("Empty");
        dbManager = DatabaseManager.getInstance(getActivity());
        progressBar = (ProgressBar) view.findViewById(R.id.list_loading_indicator);

        nbCardsInDeckList = new ArrayList<>();

        deckOrdering = false;
        loading = false;
        deckOrderingLastElement = -1;


        LinearLayout orientationLayout = (LinearLayout) view.findViewById(R.id.listview_card_orientation_layout);
        orientationLayout.setVisibility(View.GONE);


        TextView nbpractice = (TextView) view.findViewById(R.id.listview_number_cards_practice);
        nbpractice.setVisibility(View.GONE);

        nbDeck = (TextView) view.findViewById(R.id.listview_number_cards);

        deckList = new ArrayList<Deck>();

        customListAdapter = new CustomListAdapter(getContext());
        listView.setAdapter(customListAdapter);

        populateListView();

        if (scrollToTop) {

            // set selection to the first element after oncreate is finish
            listView.post(new Runnable() {
                public void run() {
                    listView.setSelection(0);

                }
            });
            scrollToTop = false;

        }

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (!loading) {
            switch (id) {
                case R.id.action_deck_order:
                    if (deckList.size() < 2) {
                        Toast.makeText(getContext(), "Need more decks to reorder", Toast.LENGTH_SHORT).show();
                    } else {
                        deckOrdering = true;
                        ActionButtonManager.getInstance().setState(ActionButtonManager.ActionButtonState.GONE, getActivity());
                        getActivity().invalidateOptionsMenu();
                        customListAdapter.notifyDataSetChanged(); // valid
                    }
                    return true;
                case R.id.action_done:
                    actionDone();
                    return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    public void actionDone() {
        deckOrdering = false;
        deckOrderingLastElement = -1;
        ActionButtonManager.getInstance().setState(ActionButtonManager.ActionButtonState.DECK, getActivity());
        getActivity().invalidateOptionsMenu();
        customListAdapter.notifyDataSetChanged(); // valid
        Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
    }


    public boolean isDeckOrdering() {
        return deckOrdering;
    }

    private void populateListView() {

        progressBar.setRotation((float) (Math.random() * 360));
        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        placerholderTextView.setVisibility(View.GONE);
        nbDeck.setVisibility(View.GONE);

        ((MainActivityManager) getActivity()).enableDrawerSwipe(false);
        new LoadData().execute("");
    }

    private void setNumberDeckText() {
        nbDeck.setVisibility(View.VISIBLE);
        int numberOfDecks = deckList.size();
        String message;
        if (numberOfDecks == 0)
            message = "No Decks";
        else if (numberOfDecks == 1)
            message = "1 Deck";
        else
            message = numberOfDecks + " Decks";
        nbDeck.setText(message);
    }

    ////// HOLD MENU //////

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

//        indexSelected = info.position;

        if (v.getId() == R.id.listview) {
            menu.setHeaderTitle(deckList.get(indexSelected).getDeckName());
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.hold_menu_deck, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_deck:
                updateDeck();
                return true;
            case R.id.delete_deck:
                displayDeleteConfirmationDialog();
                return true;
            case R.id.info_deck:
                showQuickInfoDeck();
                return true;
            case R.id.export_deck:
                Deck selectedDeck = deckList.get(indexSelected);
                List<Card> cardList = dbManager.getCardsFromDeck(selectedDeck.getDeckId());
                ExportImportManager.exportFileToEmail(getContext(), selectedDeck, cardList);

                return true;
            default:
                indexSelected = -1;
                return super.onContextItemSelected(item);
        }
    }

    private void showQuickInfoDeck() {
        Deck deck = deckList.get(indexSelected);
        String deckName = "Deck name: " + deck.getDeckName();

        int nbCardsNumber = dbManager.getCardsFromDeck(deck.getDeckId()).size();

        String nbCards = "Number of Cards: " + nbCardsNumber;

        String dateCreated = "Date Created: " + deck.getDateCreated();
        String dateModified = "Date Modified: " + deck.getDateModified();
        String deckPosition = "Deck Position: " + deck.getPosition();
        String message = deckName + "\n" + nbCards + "\n" + dateCreated + "\n" + dateModified + "\n" + deckPosition;
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setMessage(message).setPositiveButton("done", null).show();
    }


    public void createDeck() {
        AlertDialog alertDialog = createUpdateDeckDialog(DeckDialogOption.NEW);
        alertDialog.show();
    }

    public void updateDeck() {
        AlertDialog alertDialog = createUpdateDeckDialog(DeckDialogOption.UPDATE);
        alertDialog.show();
    }

    private void deleteDeckFromDatabase() {
        dbManager.deleteDeck((String) deckList.get(indexSelected).getDeckId());
        String deckDeletedName = deckList.get(indexSelected).getDeckName();
        deckList.remove(indexSelected);
        nbCardsInDeckList.remove(indexSelected);
        if (deckList.isEmpty()) {
            placerholderTextView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
        customListAdapter.notifyDataSetChanged(); // valid
        setNumberDeckText();
        Toast.makeText(getContext(), "\"" + deckDeletedName + "\" deck deleted", Toast.LENGTH_SHORT).show();
    }


    // DIALOG
    private AlertDialog createUpdateDeckDialog(final DeckDialogOption dialogType) {
        deckNameHolder = "";

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_CLASS_TEXT);
        input.setSingleLine();

        FrameLayout container = new FrameLayout(getActivity());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.default_padding);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.default_padding);

        input.setLayoutParams(params);
        container.addView(input);

        switch (dialogType) {
            case NEW:
                input.setHint("Deck name");
                builder.setCustomTitle(Helper.getInstance().customTitle("New Deck"));
                break;
            case UPDATE:
                input.setText((String) deckList.get(indexSelected).getDeckName());

                builder.setCustomTitle(Helper.getInstance().customTitle("Edit \"" + deckList.get(indexSelected).getDeckName() + "\""));
                break;
            default:
                Toast.makeText(getContext(), "Error in deck dialog - wrong type", Toast.LENGTH_SHORT).show();
                break;
        }

        builder.setView(container);

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
        // opens keyboard on creation with selection at the end
        final AlertDialog alertDialog = builder.create();


        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deckNameHolder = input.getText().toString();
                        // check if valid name
                        if (deckNameHolder.trim().isEmpty()) {

                            Toast.makeText(getContext(), "Invalid name: cannot be empty", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            switch (dialogType) {
                                case NEW:
                                    Deck deck = addToDatabase(deckNameHolder);
                                    nbCardsInDeckList.add(0);
                                    deckList.add(deck);
                                    customListAdapter.notifyDataSetChanged(); // valid
                                    alertDialog.dismiss();
                                    listView.smoothScrollToPosition(customListAdapter.getCount() - 1);

                                    if (deckList.size() == 1) {
                                        placerholderTextView.setVisibility(View.GONE);
                                        listView.setVisibility(View.VISIBLE);
                                    }
                                    setNumberDeckText();

                                    break;
                                case UPDATE:
                                    if (!deckNameHolder.equals((String) deckList.get(indexSelected).getDeckName())) {
                                        updateDatabase();
                                        deckList.get(indexSelected).setDeckName(deckNameHolder);
                                        customListAdapter.notifyDataSetChanged(); // valid
                                        alertDialog.dismiss();
                                    } else {
                                        Toast.makeText(getContext(), "Invalid: same name", Toast.LENGTH_SHORT).show();
                                    }

                                    break;
                                default:
                                    Toast.makeText(getContext(), "Error in deck dialog while creating dialog", Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                    break;
                            }
                        }
                    }
                });
            }
        });

        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        input.setSelection(input.getText().length());
        return alertDialog;


    }

    private void displayDeleteConfirmationDialog() {

        View checkBoxView = View.inflate(getContext(), R.layout.custom_dialog_checkbox, null);
        final CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.custom_dialog_checkbox_checkbox);
        checkBox.setText("Delete deck's cards as well");

        final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getContext());
        alertDialog.setMessage("Are you sure you want to delete:\n \"" +
                deckList.get(indexSelected).getDeckName() + "\"?");
        alertDialog.setTitle("Delete Deck");

        alertDialog.setView(checkBoxView);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


                if (checkBox.isChecked()) {
                    List<Card> cardList = dbManager.getCardsFromDeck(deckList.get(indexSelected).getDeckId());

                    if (cardList.isEmpty()) {

                    } else {
                        for (int counter = 0; counter < cardList.size(); counter++) {
                            Card temp = cardList.get(counter);
                            List<Deck> tempDeckList = dbManager.getDecksFromCard(temp.getCardId());
                            // only have this deck
                            if (tempDeckList.size() == 1) {
                                dbManager.toggleArchiveCard(temp.getCardId());

                            }
                        }
                    }


                }

                deleteDeckFromDatabase();
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


    // DATABASE HANDLING

    private Deck addToDatabase(String name) {
        String newDeckid = dbManager.createDeck(name);
        Toast.makeText(getContext(), "Deck created", Toast.LENGTH_SHORT).show();
        return dbManager.getDeckFromId(newDeckid);
    }


    private void updateDatabase() {

        dbManager.updateDeck((String) deckList.get(indexSelected).getDeckId(), deckNameHolder);
        Toast.makeText(getContext(), "Deck updated", Toast.LENGTH_SHORT).show();

    }


    public void onPrepareOptionsMenu(Menu menu) {
        if (deckOrdering)
            DeckToolbarManager.getInstance().setState(DeckToolbarManager.DeckToolbarState.DECK_ORDER, menu, getActivity());
        else
            DeckToolbarManager.getInstance().setState(DeckToolbarManager.DeckToolbarState.DECK, menu, getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.toolbar_deck_list, menu);

    }

    public void setScrollToTop(boolean scrollToTop) {
        this.scrollToTop = scrollToTop;
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
            return deckList.size();
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
            TextView deckName;
            TextView numberOfCardsLabel;
            LinearLayout deckLayout;
            LinearLayout deckOptionLayout;
            LinearLayout reorderLayout;
            TextView reorderTextView;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            Holder holder = null;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.custom_deck_item, null);
                holder = new Holder();

                holder.deckName = (TextView) convertView.findViewById(R.id.custom_deck_item_name);
                holder.numberOfCardsLabel = (TextView) convertView.findViewById(R.id.custom_deck_item_nb_cards_label);
                holder.deckLayout = (LinearLayout) convertView.findViewById(R.id.custom_deck_item_layout);
                holder.deckOptionLayout = (LinearLayout) convertView.findViewById(R.id.custom_deck_option_layout);
                holder.reorderLayout = (LinearLayout) convertView.findViewById(R.id.custom_deck_reorder_layout);
                holder.reorderTextView = (TextView) convertView.findViewById(R.id.custom_deck_item_reorder_textview);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }


            // all


            holder.deckOptionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    indexSelected = position;
                    getActivity().openContextMenu(listView);
                }
            });


            holder.deckName.setText(deckList.get(position).getDeckName());

//            int nbCards = dbManager.getCardsFromDeck(deckList.get(position).getDeckId()).size();
            int nbCards = nbCardsInDeckList.get(position);

            if (nbCards == 1)
                holder.numberOfCardsLabel.setText("1 Card");
            else
                holder.numberOfCardsLabel.setText(nbCards + " Cards");


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!deckOrdering) {
                        indexSelected = position;
                        ((MainActivityManager) getActivity()).displayDeckInfo(deckList.get(indexSelected).getDeckId());
                    }
                }
            });


            if (deckOrdering) {
                holder.reorderLayout.setVisibility(View.VISIBLE);
                holder.deckOptionLayout.setVisibility(View.GONE);
                int positionInDeck = deckList.get(position).getPosition();
                String positionString = positionInDeck + "";
                if (positionString.length() == 1)
                    positionString = "0" + positionString;
                holder.reorderTextView.setText(positionString);
            } else {
                holder.reorderLayout.setVisibility(View.GONE);
                holder.deckOptionLayout.setVisibility(View.VISIBLE);
            }


            holder.reorderLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeckOrderingDialog(deckList.get(position));
                }
            });

            if (!deckOrdering) {
                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        indexSelected = position;

                        return false;
                    }
                });
            } else {
                convertView.setOnLongClickListener(null);
            }

            if (deckOrdering && deckOrderingLastElement == position) {
                holder.deckLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_grey));

            } else {
                holder.deckLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_normal));
            }


            return convertView;

        }

        private void showDeckOrderingDialog(final Deck deck) {


            View viewPicker = View.inflate(getContext(), R.layout.custom_number_picker_dialog, null);
            android.app.AlertDialog.Builder db = new android.app.AlertDialog.Builder(getContext());
            db.setView(viewPicker);
            db.setTitle("Deck Order: " + deck.getDeckName());

            final NumberPicker np = (NumberPicker) viewPicker.findViewById(R.id.numberPicker1);
            np.setMaxValue(deckList.size());
            np.setMinValue(1);
            np.setWrapSelectorWheel(false);
            np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                }
            });

            np.setValue(deck.getPosition());


            db.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    int oldValue = deck.getPosition();
                    int newPosition = np.getValue();

                    dbManager.changeDeckPosition(newPosition, deck);

                    deckList = dbManager.getDecks();
                    customListAdapter.notifyDataSetChanged();
                }
            });
            android.app.AlertDialog dialog = db.show();

        }

    }


    private class LoadData extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            loading = true;
            deckList = dbManager.getDecks();

            nbCardsInDeckList.clear();

            for (int i = 0; i < deckList.size(); i++) {
                nbCardsInDeckList.add(dbManager.getCardsFromDeck(deckList.get(i).getDeckId()).size());
            }


            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (!deckList.isEmpty()) {

                placerholderTextView.setVisibility(View.GONE);

                customListAdapter.notifyDataSetChanged();

                registerForContextMenu(listView);

                progressBar.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);

            } else {
                placerholderTextView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }

            loading = false;
            ((MainActivityManager) getActivity()).enableDrawerSwipe(true);
            setNumberDeckText();

        }
    }

    public boolean isLoading() {
        return loading;
    }

}
