package stelztech.youknowehv4.fragmentpackage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.database.DatabaseManager;
import stelztech.youknowehv4.model.Deck;
import stelztech.youknowehv4.state.ActionButtonStateManager;
import stelztech.youknowehv4.state.ToolbarStateManager;

/**
 * Created by alex on 2017-04-03.
 */

public class WordListFragment extends Fragment {

    // components
    private View view;

    // Spinner
    private Spinner spinner;
    private ArrayAdapter<String> deckArrayAdapter;
    private List<Deck> deckList;
    private String toSelect = "";

    // database
    private DatabaseManager dbManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);

        ActionButtonStateManager.getInstance().setState(ActionButtonStateManager.actionButtonState.ADD_WORD, getActivity());
        setHasOptionsMenu(true);

        // init
        spinner = ((Spinner) getActivity().findViewById(R.id.spinner_nav));
        dbManager = DatabaseManager.getInstance(getActivity());

        populateSpinner();

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        ToolbarStateManager.getInstance().setState(ToolbarStateManager.toolbarState.WORD, menu, getActivity());
    }

    private void populateSpinner() {
        if (deckArrayAdapter != null)
            deckArrayAdapter.clear();
        if (deckList != null)
            deckList.clear();

        deckList = dbManager.getDecks();
        List<String> deckListString = new ArrayList<>();

        if (deckList.isEmpty()) {
            deckListString.add(new String("NO DECKS"));
            spinner.setEnabled(false);
        } else {
            deckListString.add(new String("All Decks"));
            for (int counter = 0; counter < deckList.size(); counter++) {
                deckListString.add(deckList.get(counter).getDeckName());
            }
            spinner.setEnabled(true);
        }

        deckArrayAdapter = new ArrayAdapter<String>(
                getContext(), R.layout.sprinner_item, deckListString);
        deckArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(deckArrayAdapter);

        if (!toSelect.isEmpty()) {
            for (int counter = 0; counter < deckList.size(); counter++) {
                if (deckList.get(counter).getIdDeck().equals(toSelect)) {
                    spinner.setSelection(counter + 1);
                    return;
                }
            }
            toSelect = "";
        }

    }

    public void displayDeckInfo(String deckId) {

        if (deckId.isEmpty())
            toSelect = "";
        else
            toSelect = deckId;

    }

}
