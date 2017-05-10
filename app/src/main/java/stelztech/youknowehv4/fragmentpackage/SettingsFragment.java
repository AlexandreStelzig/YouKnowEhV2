package stelztech.youknowehv4.fragmentpackage;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activitypackage.ArchivedActivity;
import stelztech.youknowehv4.activitypackage.MainActivityManager;
import stelztech.youknowehv4.helper.Helper;
import stelztech.youknowehv4.manager.ActionButtonManager;
import stelztech.youknowehv4.manager.ExportImportManager;

/**
 * Created by alex on 2017-04-03.
 */

public class SettingsFragment extends Fragment {

    View view;

    private ListView exportImportLV;
    private ListView deletedCardsLV;
    private ListView sortingLV;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        ActionButtonManager.getInstance().setState(ActionButtonManager.ActionButtonState.GONE, getActivity());
        setHasOptionsMenu(true);


        exportImportLV = (ListView) view.findViewById(R.id.settings_export_import_lv);
        deletedCardsLV = (ListView) view.findViewById(R.id.settings_deleted_cards_lv);
        sortingLV = (ListView) view.findViewById(R.id.settings_sorting_lv);

        setupExportImportLV();
        setupDeletedCardsLV();
        setupSortingLV();

        return view;
    }

    private void setupDeletedCardsLV() {
        final String[] deletedCardsChoices = getContext().getResources().getStringArray(R.array.settings_deleted_cards_options);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, deletedCardsChoices);
        deletedCardsLV.setAdapter(adapter);
        deletedCardsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    // view deleted cards
                    case 0:
                        startArchivedActivity();
                        break;
                }

            }
        });

        Helper.getInstance().setListViewHeightBasedOnChildren(deletedCardsLV);

    }

    private void startArchivedActivity() {
        Intent intent = new Intent(getActivity(), ArchivedActivity.class);
        getActivity().startActivityForResult(intent, MainActivityManager.ARCHIVED_RESULT);
    }


    private void setupExportImportLV() {

        // export import listview
        final String[] exportImportChoices = getContext().getResources().getStringArray(R.array.settings_export_import_options);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, exportImportChoices);
        exportImportLV.setAdapter(adapter);
        exportImportLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    // Export all decks
                    case 0:

                        break;
                    // Import decks
                    case 1:
                        ExportImportManager.importDeck(getContext(), getActivity());
                        break;
                }

            }
        });

        Helper.getInstance().setListViewHeightBasedOnChildren(exportImportLV);

    }

    private void setupSortingLV() {

        // export import listview
        final String[] exportImportChoices = getContext().getResources().getStringArray(R.array.settings_sorting_options);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, exportImportChoices);
        sortingLV.setAdapter(adapter);
        sortingLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    // Default sorting
                    case 0:

                        break;
                }

            }
        });

        Helper.getInstance().setListViewHeightBasedOnChildren(sortingLV);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.toolbar_other, menu);
        ActionBar actionBar = ((MainActivityManager) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        getActivity().findViewById(R.id.spinner_nav_layout).setVisibility(View.GONE);

    }
}
