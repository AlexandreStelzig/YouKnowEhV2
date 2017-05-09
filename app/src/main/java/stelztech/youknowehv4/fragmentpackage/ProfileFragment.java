package stelztech.youknowehv4.fragmentpackage;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.database.DatabaseManager;
import stelztech.youknowehv4.helper.Helper;
import stelztech.youknowehv4.manager.ActionButtonManager;
import stelztech.youknowehv4.manager.MainMenuToolbarManager;
import stelztech.youknowehv4.model.Profile;

/**
 * Created by alex on 2017-04-03.
 */

public class ProfileFragment extends Fragment {

    public enum ProfileDialogOptions {
        CREATE_PROFILE,
        UPDATE_PROFILE,
        UPDATE_QUESTION,
        UPDATE_ANSWER
    }


    private View view;

    private final int NO_PROFILES = -1;

    private Spinner profileSpinner;
    private Button deleteButton;
    private Button editButton;
    private Button newButton;
    private LinearLayout questionLabelEditLayout;
    private LinearLayout answerLabelEditLayout;

    private EditText questionLabel;
    private EditText answerLabel;

    private DatabaseManager dbManager;

    private String dialogTextHolder;

    // spinner
    private List<Profile> profileList;
    private ArrayAdapter arrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);

        ActionButtonManager.getInstance().setState(ActionButtonManager.ActionButtonState.GONE, getActivity());
        setHasOptionsMenu(true);

        dbManager = DatabaseManager.getInstance(getContext());
        dialogTextHolder = "";

        // init
        profileSpinner = (Spinner) view.findViewById(R.id.profile_spinner);
        deleteButton = (Button) view.findViewById(R.id.profile_delete);
        editButton = (Button) view.findViewById(R.id.profile_edit);
        newButton = (Button) view.findViewById(R.id.profile_new);
        questionLabel = (EditText) view.findViewById(R.id.profile_question);
        answerLabel = (EditText) view.findViewById(R.id.profile_answer);

        questionLabelEditLayout = (LinearLayout) view.findViewById(R.id.profile_question_edit);
        answerLabelEditLayout = (LinearLayout) view.findViewById(R.id.profile_answer_edit);

        questionLabelEditLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile profile = profileList.get(getCurrentlySelectedProfilePosition());
                createDialog(ProfileDialogOptions.UPDATE_QUESTION, profile.getQuestionLabel()).show();
            }
        });

        answerLabelEditLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile profile = profileList.get(getCurrentlySelectedProfilePosition());
                createDialog(ProfileDialogOptions.UPDATE_ANSWER, profile.getAnswerLabel()).show();
            }
        });

        questionLabel.setKeyListener(null);
        answerLabel.setKeyListener(null);
        questionLabel.setFocusable(false);
        answerLabel.setFocusable(false);

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProfile();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProfile();
            }
        });

        populateInformation();

        return view;
    }

    private void setLabelText() {

        Profile profile = dbManager.getActiveProfile();
        String questionText = profile.getQuestionLabel();
        String answerText = profile.getAnswerLabel();

        questionLabel.setText(questionText);
        answerLabel.setText(answerText);

    }


    private void populateInformation() {

        if (arrayAdapter != null)
            arrayAdapter.clear();

        profileList = dbManager.getProfiles();

        List<String> profileNameArray = new ArrayList<>();
        int activeProfilePosition = 1;

        for (int i = 0; i < profileList.size(); i++) {
            profileNameArray.add(profileList.get(i).getProfileName());
            if (profileList.get(i).isActive())
                activeProfilePosition = i;
        }

        if (profileList.isEmpty()) {
            profileNameArray.add("- No Profiles -");
        }

        arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.custom_spinner_item, profileNameArray) {

            @NonNull
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;

                mTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                if (position == profileSpinner.getSelectedItemPosition())
                    mView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorNotPractice));
                else
                    mView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));


                return mTextView;
            }
        };

//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);


        profileSpinner.setAdapter(arrayAdapter);

        profileSpinner.setSelection(activeProfilePosition);

        profileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!profileList.isEmpty()) {
                    changeProfile(profileList.get(position));
                    setLabelText();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        setLabelText();


    }


    private void changeProfile(Profile profile) {
        dbManager.setActiveProfile(profile.getProfileId());
    }


    private void createProfile() {
        AlertDialog alertDialog = createDialog(ProfileDialogOptions.CREATE_PROFILE, "");
        alertDialog.show();
    }

    private void editProfile() {
        if (getCurrentlySelectedProfilePosition() == NO_PROFILES) {
            Toast.makeText(getContext(), "No profiles", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog alertDialog = createDialog(ProfileDialogOptions.UPDATE_PROFILE, profileList.get(
                getCurrentlySelectedProfilePosition()).getProfileName());
        alertDialog.show();
    }

    private void deleteProfile() {
        if (profileList.size() <= 1) {
            Toast.makeText(getContext(), "You need to have at least another profile to delete this profile",
                    Toast.LENGTH_LONG).show();
            return;
        } else {

            deleteConfirmationDialog(profileList.get(
                    getCurrentlySelectedProfilePosition())).show();

        }
    }

    private AlertDialog createDialog(final ProfileDialogOptions dialogType, String text) {

        dialogTextHolder = text;

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setSingleLine();

        FrameLayout container = new FrameLayout(getActivity());
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.default_padding);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.default_padding);
        input.setLayoutParams(params);
        input.setText(dialogTextHolder);
        container.addView(input);

        switch (dialogType) {

            case CREATE_PROFILE:
                input.setHint("Profile name");
                builder.setTitle("New Profile");
                break;
            case UPDATE_PROFILE:
                input.setHint("Profile Name");
                builder.setTitle("Update Profile Name");
                break;
            case UPDATE_QUESTION:
                input.setHint("Question label");
                builder.setTitle("Update Question Label");
                break;
            case UPDATE_ANSWER:
                input.setHint("Answer label");
                builder.setTitle("Update Answer Label");
                break;
        }

        builder.setView(container);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            // when button OK is press
            public void onClick(DialogInterface dialog, int which) {
                // nothing, will initiate it later
            }
        });

        // if cancel button is press, close dialog
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String temp = dialogTextHolder;
                        dialogTextHolder = input.getText().toString();
                        // check if valid name
                        if (dialogTextHolder.trim().isEmpty()) {
                            Toast.makeText(getContext(), "invalid name", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (temp.equals(dialogTextHolder)) {
                            Toast.makeText(getContext(), "same name", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            switch (dialogType) {
                                case CREATE_PROFILE:
                                    dbManager.createProfile(dialogTextHolder);
                                    populateInformation();
                                    break;
                                case UPDATE_PROFILE:
                                    dbManager.updateProfile(profileList.get(
                                            getCurrentlySelectedProfilePosition()).getProfileId(), dialogTextHolder);
                                    populateInformation();
                                    break;
                                case UPDATE_QUESTION:
                                    dbManager.updateProfileQuestionLabel(profileList.get(
                                            getCurrentlySelectedProfilePosition()).getProfileId(), dialogTextHolder);
                                    populateInformation();
                                    break;
                                case UPDATE_ANSWER:
                                    dbManager.updateProfileAnswerLabel(profileList.get(
                                            getCurrentlySelectedProfilePosition()).getProfileId(), dialogTextHolder);
                                    populateInformation();
                                    break;
                            }
                            Helper.getInstance().hideKeyboard(getActivity());
                            alertDialog.dismiss();
                        }
                    }
                });

                Button negativeButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                        Helper.getInstance().hideKeyboard(getActivity());
                    }
                });
            }
        });

        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        input.setSelection(input.getText().length());
        return alertDialog;

    }

    private AlertDialog.Builder deleteConfirmationDialog(final Profile profileToDelete) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setMessage("Are you sure you want to delete:\n \'" +
                profileToDelete.getProfileName() + "\'?");
        alertDialog.setTitle("Delete Profile");

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < profileList.size(); i++) {
                    if (profileList.get(i).getProfileId().equals(profileToDelete.getProfileId())) {
                        profileList.remove(i);
                        break;
                    }
                }
                dbManager.deleteProfile(profileToDelete.getProfileId());
                dbManager.setActiveProfile(profileList.get(0).getProfileId());
                populateInformation();
            }
        });

        // Setting cancel Button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        });
        // Showing Alert Message

        return alertDialog;
    }


    public int getCurrentlySelectedProfilePosition() {

        if (profileList.isEmpty())
            return NO_PROFILES;
        else
            return profileSpinner.getSelectedItemPosition();
    }

    public void onPrepareOptionsMenu(Menu menu) {
        MainMenuToolbarManager.getInstance().setState(MainMenuToolbarManager.MainMenuToolbarState.DEFAULT, menu, getActivity());
    }
}
