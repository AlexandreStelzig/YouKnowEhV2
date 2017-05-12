package stelztech.youknowehv4.fragmentpackage;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activitypackage.ArchivedActivity;
import stelztech.youknowehv4.activitypackage.MainActivityManager;
import stelztech.youknowehv4.database.DatabaseManager;
import stelztech.youknowehv4.helper.Helper;
import stelztech.youknowehv4.manager.ActionButtonManager;
import stelztech.youknowehv4.manager.ExportImportManager;
import stelztech.youknowehv4.manager.SortingStateManager;
import stelztech.youknowehv4.model.User;

/**
 * Created by alex on 2017-04-03.
 */

public class SettingsFragment extends Fragment {

    View view;

    private ListView exportImportLV;
    private ListView deletedCardsLV;
    private ListView sortingLV;

    private CheckBox allowProfileDeletionCheckbox;
    private CheckBox showOnAllCheckbox;
    private CheckBox showOnSpecificCheckbox;

    private TextView allowProfileDeletionTextView;
    private TextView showOnAllTextView;
    private TextView showOnSpecificTextView;

    private DatabaseManager dbManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        ActionButtonManager.getInstance().setState(ActionButtonManager.ActionButtonState.GONE, getActivity());
        setHasOptionsMenu(true);

        dbManager = DatabaseManager.getInstance(getContext());
        User user = dbManager.getUser();

        exportImportLV = (ListView) view.findViewById(R.id.settings_export_import_lv);
        deletedCardsLV = (ListView) view.findViewById(R.id.settings_deleted_cards_lv);
        sortingLV = (ListView) view.findViewById(R.id.settings_sorting_lv);

        allowProfileDeletionCheckbox = (CheckBox) view.findViewById(R.id.settings_profile_checkbox);
        showOnAllCheckbox = (CheckBox) view.findViewById(R.id.settings_show_on_all_checkbox);
        showOnSpecificCheckbox = (CheckBox) view.findViewById(R.id.settings_show_on_specific_checkbox);

        allowProfileDeletionTextView = (TextView) view.findViewById(R.id.settings_profile_textview);
        showOnAllTextView = (TextView) view.findViewById(R.id.settings_show_on_all_textview);
        showOnSpecificTextView = (TextView) view.findViewById(R.id.settings_show_on_specific_textview);


        allowProfileDeletionCheckbox.setChecked(user.isAllowProfileDeletion());
        showOnAllCheckbox.setChecked(user.isDisplayNbDecksAllCards());
        showOnSpecificCheckbox.setChecked(user.isDisplayNbDecksSpecificCards());

        allowProfileDeletionCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowProfileDeletionClicked();
            }
        });
        allowProfileDeletionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowProfileDeletionClicked();
            }
        });

        showOnAllCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOnAllCardsClicked();
            }
        });
        showOnAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOnAllCardsClicked();
            }
        });

        showOnSpecificCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOnSpecificDeckClicked();
            }
        });
        showOnSpecificTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOnSpecificDeckClicked();
            }
        });


        setupExportImportLV();
        setupDeletedCardsLV();
        setupSortingLV();

        return view;
    }


    private void allowProfileDeletionClicked() {
        dbManager.toggleAllowProfileDeletion();
        User user = dbManager.getUser();
        allowProfileDeletionCheckbox.setChecked(user.isAllowProfileDeletion());
    }

    private void showOnAllCardsClicked() {
        dbManager.toggleDisplayNumberDecksAll();
        User user = dbManager.getUser();
        showOnAllCheckbox.setChecked(user.isDisplayNbDecksAllCards());
    }

    private void showOnSpecificDeckClicked() {
        dbManager.toggleDisplayNumberDecksSpecific();
        User user = dbManager.getUser();
        showOnSpecificCheckbox.setChecked(user.isDisplayNbDecksSpecificCards());
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
                    // Import decks
                    case 0:
                        ExportImportManager.importDeck(getContext(), getActivity());
                        break;
                    // Export all decks
                    case 1:
                        ExportImportManager.exportAllToEmail(getContext());
                        break;
                    case 2:

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
                        defaultSortDialog().show();
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


    private AlertDialog defaultSortDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        final String[] sortingChoices = getResources().getStringArray(R.array.sort_options);
        builder.setTitle("Sort by");

        builder.setSingleChoiceItems(sortingChoices, SortingStateManager.getInstance().getDefaultSort(),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SortingStateManager sortingStateManager = SortingStateManager.getInstance();

                        DatabaseManager.getInstance(getContext()).updateDefaultSortPosition(which);
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Default sort by: " + sortingChoices[which], Toast.LENGTH_SHORT).show();
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
}
