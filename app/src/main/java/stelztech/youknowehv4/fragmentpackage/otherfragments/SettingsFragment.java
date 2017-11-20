package stelztech.youknowehv4.fragmentpackage.otherfragments;


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
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activitypackage.ArchivedActivity;
import stelztech.youknowehv4.activitypackage.MainActivityManager;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.helper.Helper;
import stelztech.youknowehv4.manager.FloatingActionButtonManager;
import stelztech.youknowehv4.manager.ExportImportManager;
import stelztech.youknowehv4.manager.SortingStateManager;
import stelztech.youknowehv4.database.card.Card;
import stelztech.youknowehv4.database.deck.Deck;
import stelztech.youknowehv4.database.profile.Profile;
import stelztech.youknowehv4.database.user.User;

/**
 * Created by alex on 2017-04-03.
 */

public class SettingsFragment extends Fragment{

    View view;

    private ListView exportImportLV;
    private ListView deletedCardsLV;
    private ListView sortingLV;
    private ListView reviewLV;
    private ListView otherLV;

    private Switch showOnAllSwitch;
    private Switch showOnSpecificSwitch;
    private Switch allowPracticeAllSwitch;
    private Switch allowOnQueryChangedSwitch;

    // sort
    private String[] sortChoices;
    private ArrayAdapter sortAdapter;
    private String[] sortingOptions;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        FloatingActionButtonManager.getInstance().setState(FloatingActionButtonManager.ActionButtonState.GONE, getActivity());
        setHasOptionsMenu(true);

        User user = Database.mUserDao.fetchUser();

        exportImportLV = (ListView) view.findViewById(R.id.settings_export_import_lv);
        deletedCardsLV = (ListView) view.findViewById(R.id.settings_deleted_cards_lv);
        sortingLV = (ListView) view.findViewById(R.id.settings_sorting_lv);
        reviewLV = (ListView) view.findViewById(R.id.settings_review_lv);
        otherLV = (ListView) view.findViewById(R.id.settings_other_lv);

        showOnAllSwitch = (Switch) view.findViewById(R.id.settings_show_on_all_switch);
        showOnSpecificSwitch = (Switch) view.findViewById(R.id.settings_show_on_specific_switch);
        allowPracticeAllSwitch = (Switch) view.findViewById(R.id.settings_practice_all_switch);
        allowOnQueryChangedSwitch = (Switch) view.findViewById(R.id.settings_allow_on_query_changed_switch);

        showOnAllSwitch.setChecked(user.isDisplayNbDecksAllCards());
        showOnSpecificSwitch.setChecked(user.isDisplayNbDecksSpecificCards());
        allowPracticeAllSwitch.setChecked(user.isAllowPracticeAll());
        allowOnQueryChangedSwitch.setChecked(user.isAllowOnQueryChanged());


        showOnAllSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOnAllCardsClicked();
            }
        });

        showOnSpecificSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOnSpecificDeckClicked();
            }
        });


        allowPracticeAllSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowPracticeAllClicked();
            }
        });


        allowOnQueryChangedSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowOnQueryChangedClicked();
            }
        });


        setupExportImportLV();
        setupDeletedCardsLV();
        setupSortingLV();
        setupReviewLV();
        setupOtherLV();

        return view;
    }

    private void allowOnQueryChangedClicked() {
        Database.mUserDao.toggleAllowSearchOnQueryChanged();
        User user = Database.mUserDao.fetchUser();
        allowOnQueryChangedSwitch.setChecked(user.isAllowOnQueryChanged());
    }


    private void showOnAllCardsClicked() {
        Database.mUserDao.toggleDisplayNumDecksAllCards();
        User user = Database.mUserDao.fetchUser();
        showOnAllSwitch.setChecked(user.isDisplayNbDecksAllCards());
    }

    private void showOnSpecificDeckClicked() {
        Database.mUserDao.toggleDisplayNumDecksSpecificCard();
        User user = Database.mUserDao.fetchUser();
        showOnSpecificSwitch.setChecked(user.isDisplayNbDecksSpecificCards());
    }

    private void allowPracticeAllClicked() {
        Database.mUserDao.toggleReviewAllCards();
        User user = Database.mUserDao.fetchUser();
        allowPracticeAllSwitch.setChecked(user.isAllowPracticeAll());
        ((MainActivityManager) getActivity()).resetFragmentPractice();
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
        getActivity().startActivityForResult(intent, MainActivityManager.RESULT_ANIMATION_RIGHT_TO_LEFT);
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
                        ExportImportManager.importAllDecks(getContext(), getActivity());
                        break;
                    case 3:
                        ExportImportManager.exportAllProfilesToEmail(getContext());
                        break;
                }

            }
        });

        Helper.getInstance().setListViewHeightBasedOnChildren(exportImportLV);

    }

    private void setupSortingLV() {

        // export import listview
        sortChoices = getContext().getResources().getStringArray(R.array.settings_sorting_options);


        Profile currentProfile = Database.mUserDao.fetchActiveProfile();
        sortingOptions = getResources().getStringArray(R.array.sort_options);
        sortingOptions[0] = currentProfile.getQuestionLabel() + " (A-Z)";
        sortingOptions[1] = currentProfile.getQuestionLabel() + " (Z-A)";
        sortingOptions[2] = currentProfile.getAnswerLabel() + " (A-Z)";
        sortingOptions[3] = currentProfile.getAnswerLabel() + " (Z-A)";

        sortChoices[0] = "Default sorting: " + sortingOptions[SortingStateManager.getInstance().getDefaultSort()];


        sortAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, sortChoices);
        sortingLV.setAdapter(sortAdapter);
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

    private void setupOtherLV() {

        // other import listview
        final String[] otherChoices = getContext().getResources().getStringArray(R.array.settings_other_options);

        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, otherChoices);
        otherLV.setAdapter(adapter);
        otherLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        mergeDuplicates();
                        break;
                }

            }
        });

        Helper.getInstance().setListViewHeightBasedOnChildren(otherLV);


    }

    private void setupReviewLV() {

        // export import listview
        final String[] exportImportChoices = getContext().getResources().getStringArray(R.array.settings_review_options);

        exportImportChoices[0] = exportImportChoices[0] + " " + Database.mUserDao.fetchUser().getQuickToggleHours() + " hours";

        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, exportImportChoices);
        reviewLV.setAdapter(adapter);
        reviewLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    // Default quick toggle
                    case 0:
                        showQuickToggleDialog();
                        break;
                }

            }
        });

        Helper.getInstance().setListViewHeightBasedOnChildren(reviewLV);

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

        builder.setTitle("Sort by");

        builder.setSingleChoiceItems(sortingOptions, SortingStateManager.getInstance().getDefaultSort(),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Database.mUserDao.updateDefaultSortPosition(which);
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Default sort by: " + sortingOptions[which], Toast.LENGTH_SHORT).show();


                        sortChoices[0] = "Default sorting: " + sortingOptions[which];
                        sortAdapter.notifyDataSetChanged();

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

    public void showQuickToggleDialog() {

        View viewPicker = View.inflate(getContext(), R.layout.custom_number_picker_dialog, null);
        AlertDialog.Builder db = new AlertDialog.Builder(getContext());
        db.setView(viewPicker);
        db.setTitle("Quick Toggle Review");

        final NumberPicker np = (NumberPicker) viewPicker.findViewById(R.id.numberPicker1);
        np.setMaxValue(1000); // max value 100
        np.setMinValue(1);   // min value 0
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });

        np.setValue(Database.mUserDao.fetchUser().getQuickToggleHours());


        db.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Database.mUserDao.updateQuickToggleReviewHours(np.getValue());
                setupReviewLV();
                Toast.makeText(getContext(), "Default Quick Review Value changed to " + np.getValue(), Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = db.show();

    }


    private void mergeDuplicates() {

        List<Card> allCards = Database.mCardDao.fetchAllCards();

        for (int counterOne = 0; counterOne < allCards.size(); counterOne++) {

            Card cardOne = allCards.get(counterOne);
            List<Integer> removedCardsIndex = new ArrayList<>();

            int counterTwo = counterOne + 1;
            while (counterTwo < allCards.size()) {

                Card cardTwo = allCards.get(counterTwo);

                if (Objects.equals(cardOne.getQuestion(), cardTwo.getQuestion()) &&
                        Objects.equals(cardOne.getAnswer(), cardTwo.getAnswer()) &&
                        Objects.equals(cardOne.getMoreInfo(), cardTwo.getMoreInfo())) {

                    List<Deck> cardOneDecks = Database.mCardDeckDao.fetchDecksByCardId(cardOne.getCardId());
                    List<Deck> cardTwoDecks = Database.mCardDeckDao.fetchDecksByCardId(cardTwo.getCardId());

                    boolean isInSameDeck = false;

                    for (int cardOneCounter = 0; cardOneCounter < cardOneDecks.size(); cardOneCounter++) {
                        for (int cardTwoDecksCounter = 0; cardTwoDecksCounter < cardTwoDecks.size(); cardTwoDecksCounter++) {
                            if (cardOneDecks.get(cardOneCounter).getDeckId() == (cardTwoDecks.get(cardTwoDecksCounter).getDeckId())) {
                                isInSameDeck = true;
                                break;
                            }
                        }
                    }

                    if (!isInSameDeck) {
                        for (int i = 0; i < cardTwoDecks.size(); i++) {
                            Database.mCardDeckDao.createCardDeck(cardOne.getCardId(), cardTwoDecks.get(i).getDeckId());
                        }
                    }

                    Database.mCardDao.deleteCard(cardTwo.getCardId());
                    allCards.remove(counterTwo);
                } else {
                    counterTwo++;
                }


            }


        }

        Toast.makeText(getContext(), "Merge completed", Toast.LENGTH_SHORT).show();


    }


}
