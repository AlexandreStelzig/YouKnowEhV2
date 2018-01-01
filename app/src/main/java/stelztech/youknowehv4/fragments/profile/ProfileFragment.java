package stelztech.youknowehv4.fragments.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activities.ArchivedActivity;
import stelztech.youknowehv4.activities.MainActivityManager;
import stelztech.youknowehv4.activities.ProfileNewEditActivity;
import stelztech.youknowehv4.activities.profilepicker.ProfilePickerActivity;
import stelztech.youknowehv4.components.CustomProgressDialog;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.profile.Profile;
import stelztech.youknowehv4.fragments.FragmentCommon;
import stelztech.youknowehv4.manager.ExportImportManager;
import stelztech.youknowehv4.manager.FloatingActionButtonManager;
import stelztech.youknowehv4.manager.SortingStateManager;
import stelztech.youknowehv4.utilities.CardUtilities;
import stelztech.youknowehv4.utilities.DateUtilities;
import stelztech.youknowehv4.utilities.Helper;

/**
 * Created by alex on 12/24/2017.
 */

public class ProfileFragment extends FragmentCommon {

    public ProfileFragment(int animationLayoutPosition, boolean animationFade) {
        super(animationLayoutPosition, animationFade);
    }

    private View view;


    private TextView profileNameLabel;
    private TextView nbCardsLabel;
    private TextView nbDecksLabel;

    private TextView frontLabel;
    private TextView backLabel;

    private ListView preferencesListView;
    private ListView moreOptionsListView;
    private ListView changeProfileListView;


    private String[] sortingOptions;
    private CustomProfileListviewAdapter preferenceAdapter;
    private String[] preferenceOptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);

        FloatingActionButtonManager.getInstance().setState(FloatingActionButtonManager.ActionButtonState.GONE, getActivity());
        setHasOptionsMenu(true);

        profileNameLabel = (TextView) view.findViewById(R.id.profile_profile_name);
        nbCardsLabel = (TextView) view.findViewById(R.id.profile_number_cards);
        nbDecksLabel = (TextView) view.findViewById(R.id.profile_number_decks);
        frontLabel = (TextView) view.findViewById(R.id.profile_front_label);
        backLabel = (TextView) view.findViewById(R.id.profile_back_label);
        preferencesListView = (ListView) view.findViewById(R.id.profile_preferences_listview);
        moreOptionsListView = (ListView) view.findViewById(R.id.profile_more_options_listview);
        changeProfileListView = (ListView) view.findViewById(R.id.profile_change_profile_listview);

        initHeader();
        initLabelsText();
        initPreferenceListView();
        initMoreOptionsListView();
        initChangeProfileListView();
        initSwitches();

        return view;
    }

    private void initSwitches() {
        Profile activeProfile = Database.mUserDao.fetchActiveProfile();

        final Switch showOnAllSwitch = (Switch) view.findViewById(R.id.settings_show_on_all_switch);
        final Switch showOnSpecificSwitch = (Switch) view.findViewById(R.id.settings_show_on_specific_switch);
        final Switch allowOnQueryChangedSwitch = (Switch) view.findViewById(R.id.settings_allow_on_query_changed_switch);

        showOnAllSwitch.setChecked(activeProfile.isDisplayNbDecksAllCards());
        showOnSpecificSwitch.setChecked(activeProfile.isDisplayNbDecksSpecificCards());
        allowOnQueryChangedSwitch.setChecked(activeProfile.isAllowOnQueryChanged());

        showOnAllSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Database.mProfileDao.toggleDisplayNumDecksAllCards();
                Profile profile = Database.mUserDao.fetchActiveProfile();
                showOnAllSwitch.setChecked(profile.isDisplayNbDecksAllCards());
            }
        });

        showOnSpecificSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database.mProfileDao.toggleDisplayNumDecksSpecificCard();
                Profile profile = Database.mUserDao.fetchActiveProfile();
                showOnSpecificSwitch.setChecked(profile.isDisplayNbDecksSpecificCards());
            }
        });

        allowOnQueryChangedSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database.mProfileDao.toggleAllowSearchOnQueryChanged();
                Profile profile = Database.mUserDao.fetchActiveProfile();
                allowOnQueryChangedSwitch.setChecked(profile.isAllowOnQueryChanged());
            }
        });

    }

    private void initChangeProfileListView() {
        // export import listview
        final String[] changeProfileOptions = getContext().getResources().getStringArray(R.array.profile_change_profile_options);

        CustomCenterProfileListviewAdapter adapter = new CustomCenterProfileListviewAdapter(getContext(), getActivity(), changeProfileOptions);

        changeProfileListView.setAdapter(adapter);
        changeProfileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    // change profile
                    case 0:

                        Database.mProfileDao.updateLastTimeOpened(Database.mUserDao.fetchActiveProfile().getProfileId(), DateUtilities.getDateNowString());
                        Database.mUserDao.setActiveProfile(Profile.NO_PROFILES);

                        Intent i = new Intent(getActivity(), ProfilePickerActivity.class);
                        //  todo open on index
                        //  i.putExtra("OpenOnIndex", profilePickerLastIndex);
                        startActivity(i);
                        getActivity().finish();
                        break;
                }

            }
        });

        Helper.getInstance().setListViewHeightBasedOnChildren(changeProfileListView);

    }


    private void initPreferenceListView() {
        // export import listview
        preferenceOptions = getContext().getResources().getStringArray(R.array.profile_preference_options);

        Profile currentProfile = Database.mUserDao.fetchActiveProfile();
        sortingOptions = getResources().getStringArray(R.array.sort_options);
        sortingOptions[0] = currentProfile.getFrontLabel() + " (A-Z)";
        sortingOptions[1] = currentProfile.getFrontLabel() + " (Z-A)";
        sortingOptions[2] = currentProfile.getBackLabel() + " (A-Z)";
        sortingOptions[3] = currentProfile.getBackLabel() + " (Z-A)";

        preferenceOptions[0] = preferenceOptions[0] + " " + sortingOptions[Database.mUserDao.fetchActiveProfile().getDefaultSortingPosition()];
        preferenceOptions[1] = preferenceOptions[1] + " " + Database.mUserDao.fetchActiveProfile().getQuickToggleHours() + " hours";

        int[] drawableIds = {R.drawable.ic_sort_black_24dp, R.mipmap.ic_toggle_review_black};

        preferenceAdapter = new CustomProfileListviewAdapter(getContext(), getActivity(), preferenceOptions, drawableIds);

        preferencesListView.setAdapter(preferenceAdapter);

        preferencesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    // Default Sorting
                    case 0:
                        showDefaultSortDialog();
                        break;
                    // Quiz toggle Value
                    case 1:
                        showQuickToggleDialog();
                        break;
                }

            }
        });

        Helper.getInstance().setListViewHeightBasedOnChildren(preferencesListView);
    }

    private void initMoreOptionsListView() {
        final String[] moreOptionsStrings = getContext().getResources().getStringArray(R.array.profile_more_options_options);


        int[] drawableIds = {R.drawable.ic_call_merge_black_24dp, R.drawable.ic_archive_black_24dp, R.drawable.ic_action_export, R.drawable.ic_action_export_all};

        CustomProfileListviewAdapter adapter = new CustomProfileListviewAdapter(getContext(), getActivity(), moreOptionsStrings, drawableIds);

        moreOptionsListView.setAdapter(adapter);
        moreOptionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    // merge duplicates
                    case 0:
                        final CustomProgressDialog customProgressDialog = new CustomProgressDialog("Merging Duplicates", 100, getContext(), getActivity()) {
                            @Override
                            public void loadInformation() {
                                CardUtilities.mergeDuplicates(this, -1);
                            }

                            @Override
                            public void informationLoaded() {

                            }
                        };
                        customProgressDialog.startDialog();
                        break;
                    // archived
                    case 1:
                        Intent intent = new Intent(getActivity(), ArchivedActivity.class);
                        getActivity().startActivityForResult(intent, MainActivityManager.RESULT_ANIMATION_RIGHT_TO_LEFT);
                        break;
                    // export profile
                    case 2:
                        ExportImportManager.exportAllToEmail(getContext());
                        break;
                    // export all profiles
                    case 3:
                        ExportImportManager.exportAllProfilesToEmail(getContext());
                        break;
                }

            }
        });

        Helper.getInstance().setListViewHeightBasedOnChildren(moreOptionsListView);

    }

    private void initLabelsText() {

        Profile profile = Database.mUserDao.fetchActiveProfile();
        String frontString = profile.getFrontLabel();
        String backString = profile.getBackLabel();

        frontLabel.setText(frontString);
        backLabel.setText(backString);

    }

    private void initHeader() {

        Profile activeProfile = Database.mUserDao.fetchActiveProfile();

        profileNameLabel.setText(activeProfile.getProfileName());

        int numberCards = Database.mCardDao.fetchNumberOfCardsByProfileId(activeProfile.getProfileId());

        String numberCardsString = numberCards + " ";
        if (numberCards == 1)
            numberCardsString += "Card";
        else
            numberCardsString += "Cards";
        nbCardsLabel.setText(numberCardsString);

        int numberDecks = Database.mDeckDao.fetchNumberOfDecksByProfileId(activeProfile.getProfileId());

        String numberDecksString = numberDecks + " ";
        if (numberDecks == 1)
            numberDecksString += "Deck";
        else
            numberDecksString += "Decks";
        nbDecksLabel.setText(numberDecksString);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.toolbar_profile, menu);
        ActionBar actionBar = ((MainActivityManager) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        getActivity().findViewById(R.id.spinner_nav_layout).setVisibility(View.GONE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent i = new Intent(getContext(), ProfileNewEditActivity.class);
                i.putExtra("ProfileId", Database.mUserDao.fetchActiveProfile().getProfileId());
                i.putExtra("ReturnLocation", ProfileNewEditActivity.RETURN_LOCATION.MAIN_ACTIVITY.toString());

                getActivity().startActivity(i);
                getActivity().finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDefaultSortDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Default Sorting:");

        builder.setSingleChoiceItems(sortingOptions, SortingStateManager.getInstance().getDefaultSort(),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Database.mProfileDao.updateDefaultSortPosition(which);
                        dialog.dismiss();
                        initPreferenceListView();
                        Toast.makeText(getContext(), "Default sort by: " + sortingOptions[which], Toast.LENGTH_SHORT).show();


                    }
                });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alert = builder.create();
        Helper.getInstance().hideKeyboard(getActivity());

        alert.show();
    }

    public void showQuickToggleDialog() {

        View viewPicker = View.inflate(getContext(), R.layout.custom_number_picker_dialog, null);
        AlertDialog.Builder db = new AlertDialog.Builder(getContext());
        db.setView(viewPicker);
        db.setTitle("Quick Toggle Review");

        final NumberPicker np = (NumberPicker) viewPicker.findViewById(R.id.numberPicker1);
        np.setMaxValue(100); // max value 100
        np.setMinValue(1);   // min value 1
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });

        np.setValue(Database.mUserDao.fetchActiveProfile().getQuickToggleHours());


        db.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Database.mProfileDao.updateQuickToggleReviewHours(np.getValue());
                initPreferenceListView();
                Toast.makeText(getContext(), "Default Quick Review Value changed to " + np.getValue(), Toast.LENGTH_SHORT).show();
            }
        });

        db.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        db.setCancelable(false);

        db.show();

    }


}
