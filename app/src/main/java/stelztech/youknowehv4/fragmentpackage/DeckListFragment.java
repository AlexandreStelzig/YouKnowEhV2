package stelztech.youknowehv4.fragmentpackage;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activitypackage.ApplicationManager;
import stelztech.youknowehv4.database.DatabaseManager;
import stelztech.youknowehv4.model.Deck;
import stelztech.youknowehv4.state.ActionButtonStateManager;
import stelztech.youknowehv4.state.ToolbarStateManager;

/**
 * Created by alex on 2017-04-03.
 */

public class DeckListFragment extends Fragment {

    // views
    private View view;
    private ListView listView;

    // database
    private DatabaseManager dbManager;

    // list
    private List<Deck> deckList;
    private List<String> adapterDeckName;
    private ArrayAdapter<String> adapter;

    // dialog
    private String deckNameHolder;
    private int indexSelected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);

        ActionButtonStateManager.getInstance().setState(ActionButtonStateManager.actionButtonState.DECK, getActivity());
        setHasOptionsMenu(true);

        // init variables
        deckNameHolder = "";
        indexSelected = -1;
        listView = (ListView) view.findViewById(R.id.listview);
        dbManager = DatabaseManager.getInstance(getActivity());

        deckList = new ArrayList<Deck>();
        adapterDeckName = new ArrayList<String>();

        populateListView();

        return view;
    }


    private void populateListView() {

        if (deckList != null)
            deckList.clear();
        if (adapterDeckName != null)
            adapterDeckName.clear();

        deckList = dbManager.getDecks();

        for (int i = 0; i < deckList.size(); i++) {
            adapterDeckName.add(deckList.get(i).getDeckName());
        }
        adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.custom_deck_item, adapterDeckName);

        listView.setAdapter(adapter);
        registerForContextMenu(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                openPackageInfo(position);
                String deckId = "";

                deckId = deckList.get(position).getIdDeck();

                ((ApplicationManager) getActivity()).displayDeckInfo(deckId);
            }
        });
    }

    ////// HOLD MENU //////

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        indexSelected = info.position;

        if (v.getId() == R.id.listview) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.hold_menu_deck, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                updateDeck();
                populateListView();
                return true;
            case R.id.delete:
                deleteFromDatabase();
                populateListView();
                return true;
            default:
                indexSelected = -1;
                return super.onContextItemSelected(item);
        }
    }


    public void createDeck() {
        AlertDialog alertDialog = createUpdateDeckDialog(DeckDialogOption.NEW);
        alertDialog.show();
    }

    public void updateDeck() {
        AlertDialog alertDialog = createUpdateDeckDialog(DeckDialogOption.UPDATE);
        alertDialog.show();
    }


    // DIALOG
    private AlertDialog createUpdateDeckDialog(final DeckDialogOption dialogType) {
        deckNameHolder = "";

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        switch (dialogType) {
            case NEW:
                input.setHint("Package name");
                builder.setTitle("New Package");
                break;
            case UPDATE:
                input.setText((String) adapterDeckName.get(indexSelected));
                builder.setTitle("Updating \'" + adapterDeckName.get(indexSelected) + "\'");
                break;
            default:
                Toast.makeText(getContext(), "Error in deck dialog - wrong type", Toast.LENGTH_SHORT).show();
                break;
        }

        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            // when button OK is press
            public void onClick(DialogInterface dialog, int which) {

                deckNameHolder = input.getText().toString();
                // check if valid name
                if (deckNameHolder.equals("")) {
                    Toast.makeText(getContext(), "invalid name", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    switch (dialogType) {
                        case NEW:
                            addToDatabase();
                            dialog.dismiss();
                            Toast.makeText(getContext(), "package added", Toast.LENGTH_SHORT).show();
                            populateListView();
                            break;
                        case UPDATE:
                            updateDatabase();
                            dialog.dismiss();
                            Toast.makeText(getContext(), "package updated", Toast.LENGTH_SHORT).show();
                            populateListView();
                            break;
                        default:
                            Toast.makeText(getContext(), "Error in deck dialog while adding", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
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
        AlertDialog dialog = builder.create();

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        input.setSelection(input.getText().length());
        return dialog;


    }


    // DATABASE HANDLING

    private void addToDatabase() {
        dbManager.createDeck(deckNameHolder);
    }

    private void deleteFromDatabase() {
        dbManager.deleteDeck((String) deckList.get(indexSelected).getIdDeck());
        Toast.makeText(getContext(), "package deleted", Toast.LENGTH_SHORT).show();
    }

    private void updateDatabase() {

        if (!deckNameHolder.equals((String) adapterDeckName.get(indexSelected))) {
            dbManager.updateDeck((String) deckList.get(indexSelected).getIdDeck(), deckNameHolder);
            Toast.makeText(getContext(), "package name changed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "invalid: same name", Toast.LENGTH_SHORT).show();

        }
    }

    public enum DeckDialogOption {
        NEW,
        UPDATE
    }

    public void onPrepareOptionsMenu(Menu menu) {
        ToolbarStateManager.getInstance().setState(ToolbarStateManager.toolbarState.DECK, menu, getActivity());
    }

}
