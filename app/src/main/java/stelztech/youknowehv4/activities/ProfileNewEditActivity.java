package stelztech.youknowehv4.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activities.profilepicker.ProfilePickerActivity;
import stelztech.youknowehv4.components.CustomEditTextDialog;
import stelztech.youknowehv4.components.CustomYesNoDialog;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.profile.Profile;
import stelztech.youknowehv4.manager.SortingStateManager;
import stelztech.youknowehv4.manager.ThemeManager;
import stelztech.youknowehv4.utilities.BitmapUtilities;
import stelztech.youknowehv4.utilities.Helper;

/**
 * Created by alex on 12/29/2017.
 */

public class ProfileNewEditActivity extends AppCompatActivity {

    private enum PROFILE_MODE {
        EDIT,
        NEW
    }

    public enum RETURN_LOCATION {
        MAIN_ACTIVITY,
        PROFILE_PICKER
    }

    private PROFILE_MODE profileMode;
    private int profileId;
    private RETURN_LOCATION returnLocation;

    private TextView profileNameEditText;
    private TextView frontLabelEditText;
    private TextView backLabelEditText;

    private ImageView headerImage;

    private String profileNameHolder;
    private String frontLabelHolder;
    private String backLabelHolder;
    private ThemeManager.THEME_COLORS themeColorHolder;
    private String profileTypeHolder;

    private ListView deleteProfileListView;

    private String headerImagePath;

    private Button blueColorButton;
    private Button greenColorButton;
    private Button redColorButton;
    private Button purpleColorButton;
    private Button greyColorButton;
    private Button pinkColorButton;
    private Button orangeColorButton;
    private Button indigoColorButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(ThemeManager.getInstance().getCurrentAppThemeValue());
        setContentView(R.layout.activity_profile_new_edit);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // todo change to intent extra
        profileId = getIntent().getIntExtra("ProfileId", -1);

        if (profileId != -1) {
            profileMode = PROFILE_MODE.EDIT;
            returnLocation = RETURN_LOCATION.valueOf(getIntent().getStringExtra("ReturnLocation"));
        } else {
            profileMode = PROFILE_MODE.NEW;
        }


        // set back button instead of drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        profileNameEditText = (TextView) findViewById(R.id.profile_new_edit_profile_name);
        frontLabelEditText = (TextView) findViewById(R.id.profile_new_edit_front_label);
        backLabelEditText = (TextView) findViewById(R.id.profile_new_edit_back_label);
        deleteProfileListView = (ListView) findViewById(R.id.profile_new_edit_delete_profile_listview);
        headerImage = (ImageView) findViewById(R.id.profile_header);



        profileNameHolder = getIntent().getStringExtra("InitProfileName");
        frontLabelHolder = getIntent().getStringExtra("InitFrontName");
        backLabelHolder = getIntent().getStringExtra("InitBackName");
        themeColorHolder = ThemeManager.getInstance().getCurrentTheme();
        profileTypeHolder = getIntent().getStringExtra("InitType");

        if(profileNameHolder == null)
            profileNameHolder = "Profile";
        if(frontLabelHolder == null)
            frontLabelHolder = "Front";
        if(backLabelHolder == null)
            backLabelHolder = "Back";
        if(profileTypeHolder == null)
            profileTypeHolder = String.valueOf(Profile.PROFILE_TYPE.LANGUAGE);



        LinearLayout deleteProfileLayout = (LinearLayout) findViewById(R.id.profile_new_edit_delete_profile_layout);

        if (profileMode == PROFILE_MODE.EDIT) {
            deleteProfileLayout.setVisibility(View.VISIBLE);
            initDeleteProfileListView();
            setProfileInfoFieldsTextEdit();
            getSupportActionBar().setTitle("Edit Profile");
        } else {
            deleteProfileLayout.setVisibility(View.GONE);
            setProfileInfoFieldsTextNew();
            getSupportActionBar().setTitle("New Profile");
        }


        initComponentsOnClick();
        setupColorChangeButtons();
        initHeaderOnClick();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setColorButtonSelected();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setColorButtonSelected() {

        blueColorButton.setForeground(null);
        greenColorButton.setForeground(null);
        redColorButton.setForeground(null);
        purpleColorButton.setForeground(null);
        greyColorButton.setForeground(null);
        pinkColorButton.setForeground(null);
        orangeColorButton.setForeground(null);
        indigoColorButton.setForeground(null);

        switch (themeColorHolder){
            case BLUE:
                blueColorButton.setForeground(ContextCompat.getDrawable(this, R.drawable.button_selected));
                break;
            case GREEN:
                greenColorButton.setForeground(ContextCompat.getDrawable(this, R.drawable.button_selected));
                break;
            case RED:
                redColorButton.setForeground(ContextCompat.getDrawable(this, R.drawable.button_selected));
                break;
            case PURPLE:
                purpleColorButton.setForeground(ContextCompat.getDrawable(this, R.drawable.button_selected));
                break;
            case GREY:
                greyColorButton.setForeground(ContextCompat.getDrawable(this, R.drawable.button_selected));
                break;
            case PINK:
                pinkColorButton.setForeground(ContextCompat.getDrawable(this, R.drawable.button_selected));
                break;
            case ORANGE:
                orangeColorButton.setForeground(ContextCompat.getDrawable(this, R.drawable.button_selected));
                break;
            case INDIGO:
                indigoColorButton.setForeground(ContextCompat.getDrawable(this, R.drawable.button_selected));
                break;
        }
    }

    private void initHeaderOnClick() {
        ((ImageView) findViewById(R.id.profile_new_edit_header_edit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.create(ProfileNewEditActivity.this).limit(10) // max images can be selected (99 by default)
                        .showCamera(false) // show camera or not (true by default)
                        .folderMode(true)
                        .single()
                        .theme(ThemeManager.getInstance().getCurrentAppThemeNoActionBarValue())
                        .start(1); // start image picker activity with request code

                String path = Environment.getExternalStorageDirectory().toString() + "/Pictures/You Know Eh?";
                Log.d("Files", "Path: " + path);
                File directory = new File(path);
                File[] files = directory.listFiles();
                Log.d("Files", "Size: "+ files.length);
                for (int i = 0; i < files.length; i++)
                {
                    Log.d("Files", "FileName:" + files[i].getName());
                }

                Log.d("TEST", (android.os.Environment.DIRECTORY_DCIM));

            }
        });
    }

    private void initComponentsOnClick() {

        profileNameEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {

                    String label;
                    if (profileMode == PROFILE_MODE.EDIT) {
                        label = Database.mProfileDao.fetchProfileById(profileId).getProfileName();
                    } else {
                        label = profileNameEditText.getText().toString();
                    }

                    CustomEditTextDialog customEditTextDialog = new CustomEditTextDialog(ProfileNewEditActivity.this, ProfileNewEditActivity.this,
                            label, "Edit Profile Name", true) {
                        @Override
                        public void onPositiveButtonClicked(String newProfileName) {

                            if (profileNameExist(newProfileName)) {
                                Toast.makeText(ProfileNewEditActivity.this, "Profile name change failed: Profile name already exists", Toast.LENGTH_SHORT).show();
                            } else {

                                if (profileMode == PROFILE_MODE.EDIT) {
                                    Database.mProfileDao.updateProfileName(profileId, newProfileName);
                                    setProfileInfoFieldsTextEdit();
                                    Toast.makeText(ProfileNewEditActivity.this, "Profile name changed to: " + newProfileName, Toast.LENGTH_SHORT).show();
                                } else {
                                    profileNameHolder = newProfileName;
                                    setProfileInfoFieldsTextNew();
                                }

                            }

                        }

                        @Override
                        public void onNegativeButtonClicked() {
                            Helper.getInstance().hideKeyboard(ProfileNewEditActivity.this);
                        }
                    };
                    customEditTextDialog.showDialog();
                }

                return false;
            }
        });

        frontLabelEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {

                    String label;
                    if (profileMode == PROFILE_MODE.EDIT) {
                        label = Database.mProfileDao.fetchProfileById(profileId).getFrontLabel();
                    } else {
                        label = frontLabelEditText.getText().toString();
                    }

                    CustomEditTextDialog customEditTextDialog = new CustomEditTextDialog(ProfileNewEditActivity.this, ProfileNewEditActivity.this,
                            label, "Edit Front Label", true) {
                        @Override
                        public void onPositiveButtonClicked(String newFrontLabel) {

                            if (profileMode == PROFILE_MODE.EDIT) {
                                Database.mProfileDao.updateProfileFrontLabel(profileId, newFrontLabel);
                                setProfileInfoFieldsTextEdit();
                                Toast.makeText(ProfileNewEditActivity.this, "Front label changed to: " + newFrontLabel, Toast.LENGTH_SHORT).show();
                            } else {
                                frontLabelHolder = newFrontLabel;
                                setProfileInfoFieldsTextNew();
                            }
                        }

                        @Override
                        public void onNegativeButtonClicked() {
                            Helper.getInstance().hideKeyboard(ProfileNewEditActivity.this);
                        }
                    };
                    customEditTextDialog.showDialog();
                }

                return false;
            }
        });

        backLabelEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {

                    String label;
                    if (profileMode == PROFILE_MODE.EDIT) {
                        label = Database.mProfileDao.fetchProfileById(profileId).getBackLabel();
                    } else {
                        label = backLabelEditText.getText().toString();
                    }

                    CustomEditTextDialog customEditTextDialog = new CustomEditTextDialog(ProfileNewEditActivity.this, ProfileNewEditActivity.this,
                            label, "Edit Back Label", true) {
                        @Override
                        public void onPositiveButtonClicked(String newBackLabel) {

                            if (profileMode == PROFILE_MODE.EDIT) {
                                Database.mProfileDao.updateProfileBackLabel(profileId, newBackLabel);
                                setProfileInfoFieldsTextEdit();
                                Toast.makeText(ProfileNewEditActivity.this, "Back label changed to: " + newBackLabel, Toast.LENGTH_SHORT).show();
                            } else {
                                backLabelHolder = newBackLabel;
                                setProfileInfoFieldsTextNew();
                            }

                        }

                        @Override
                        public void onNegativeButtonClicked() {
                            Helper.getInstance().hideKeyboard(ProfileNewEditActivity.this);
                        }
                    };
                    customEditTextDialog.showDialog();
                }

                return false;
            }
        });


    }

    private boolean profileNameExist(String profileName) {

        List<Profile> profileList = Database.mProfileDao.fetchAllProfiles();

        for (int counter = 0; counter < profileList.size(); counter++) {
            if (profileList.get(counter).getProfileName().equals(profileName)) {
                return true;
            }
        }

        return false;
    }

    private void setProfileInfoFieldsTextEdit() {
        Profile profile = Database.mProfileDao.fetchProfileById(profileId);

        profileNameEditText.setText(profile.getProfileName());
        frontLabelEditText.setText(profile.getFrontLabel());
        backLabelEditText.setText(profile.getBackLabel());



        try{
            headerImage.setImageBitmap(BitmapUtilities.getBitmapFromFile(profile.getProfileImagePath()));
        } catch (Exception e){
            headerImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.default1));
        }
    }

    private void setProfileInfoFieldsTextNew() {
        profileNameEditText.setText(profileNameHolder);
        frontLabelEditText.setText(frontLabelHolder);
        backLabelEditText.setText(backLabelHolder);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (profileMode == PROFILE_MODE.NEW)
            getMenuInflater().inflate(R.menu.toolbar_profile_new_edit, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        switch (id) {

            // back button
            case android.R.id.home:
                onActivityBackPressed();
                return true;

            case R.id.action_create_profile:
                createProfile();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createProfile() {

        profileNameHolder = profileNameEditText.getText().toString();
        frontLabelHolder = frontLabelEditText.getText().toString();
        backLabelHolder = backLabelEditText.getText().toString();

        if (profileNameExist(profileNameHolder)) {
            Toast.makeText(ProfileNewEditActivity.this, "Profile creation failed: Profile name already exists", Toast.LENGTH_SHORT).show();
        }
        else{

            int newProfileId = Database.mProfileDao.createProfile(profileNameHolder, frontLabelHolder, backLabelHolder,
                    headerImagePath, themeColorHolder,  Profile.PROFILE_TYPE.valueOf(profileTypeHolder));
            Database.mUserDao.setActiveProfile(newProfileId);
            Database.mProfileDao.changeProfileColor(newProfileId, themeColorHolder);

            Toast.makeText(this, "Profile successfully created", Toast.LENGTH_SHORT).show();


            SortingStateManager.getInstance().setContext(this);
            SortingStateManager.getInstance().changeStateByPosition(Database.mProfileDao.fetchProfileById(newProfileId).getDefaultSortingPosition());

            Intent i = new Intent(this, MainActivityManager.class);
            startActivity(i);
            finish();

        }

    }


    private void setupColorChangeButtons() {

        blueColorButton = (Button) findViewById(R.id.profile_new_edit_change_color_blue_button);
        blueColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColorAndReloadActivity(ThemeManager.THEME_COLORS.BLUE);
            }
        });

        greenColorButton = (Button) findViewById(R.id.profile_new_edit_change_color_green_button);
        greenColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColorAndReloadActivity(ThemeManager.THEME_COLORS.GREEN);
            }
        });

        redColorButton = (Button) findViewById(R.id.profile_new_edit_change_color_red_button);
        redColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColorAndReloadActivity(ThemeManager.THEME_COLORS.RED);
            }
        });

        purpleColorButton = (Button) findViewById(R.id.profile_new_edit_change_color_purple_button);
        purpleColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColorAndReloadActivity(ThemeManager.THEME_COLORS.PURPLE);
            }
        });

        greyColorButton = (Button) findViewById(R.id.profile_new_edit_change_color_grey_button);
        greyColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColorAndReloadActivity(ThemeManager.THEME_COLORS.GREY);
            }
        });

        pinkColorButton = (Button) findViewById(R.id.profile_new_edit_change_color_pink_button);
        pinkColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColorAndReloadActivity(ThemeManager.THEME_COLORS.PINK);
            }
        });

        orangeColorButton = (Button) findViewById(R.id.profile_new_edit_change_color_orange_button);
        orangeColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColorAndReloadActivity(ThemeManager.THEME_COLORS.ORANGE);
            }
        });

        indigoColorButton = (Button) findViewById(R.id.profile_new_edit_change_color_indigo_button);
        indigoColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColorAndReloadActivity(ThemeManager.THEME_COLORS.INDIGO);
            }
        });
    }

    private void changeColorAndReloadActivity(ThemeManager.THEME_COLORS color) {

        themeColorHolder = color;

        ThemeManager themeManager = ThemeManager.getInstance();
        if (themeManager.getCurrentTheme() != color) {
            Intent intent = getIntent();
            if (profileMode == PROFILE_MODE.EDIT) {
                Database.mProfileDao.changeProfileColor(profileId, color);

            } else if (profileMode == PROFILE_MODE.NEW) {
                intent.putExtra("InitProfileName", profileNameHolder);
                intent.putExtra("InitFrontName", frontLabelHolder);
                intent.putExtra("InitBackName", backLabelHolder);
                intent.putExtra("InitType", profileTypeHolder);

            }
            themeManager.changeTheme(color);
            setTheme(ThemeManager.getInstance().getAppThemeValue(color));
            finish();
            startActivity(intent);
        }
    }

    private void initDeleteProfileListView() {
        // export import listview
        final String[] deleteOptions = getResources().getStringArray(R.array.delete_profile_options);

        CustomCenterDeleteProfileListViewAdapter adapter = new CustomCenterDeleteProfileListViewAdapter(this, this, deleteOptions);

        deleteProfileListView.setAdapter(adapter);
        deleteProfileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    // change profile
                    case 0:
                        final Profile profile = Database.mProfileDao.fetchProfileById(profileId);
                        CustomYesNoDialog customYesNoDialog = new CustomYesNoDialog(ProfileNewEditActivity.this, "Delete Profile '" + profile.getProfileName() +"'",
                                "Are you sure you want to delete the profile '" + profile.getProfileName() + "'?\nAll data will be lost forever.") {
                            @Override
                            protected void onNegativeButtonClick() {

                            }

                            @Override
                            protected void onPositiveButtonClick() {
                                Database.mUserDao.setActiveProfile(profileId);
                                Database.mProfileDao.deleteProfile(profileId);
                                Toast.makeText(ProfileNewEditActivity.this, "Profile '" + profile.getProfileName() +"' deleted", Toast.LENGTH_SHORT).show();
                                Database.mUserDao.setActiveProfile(Profile.NO_PROFILES);
                                Intent i = new Intent(ProfileNewEditActivity.this, ProfilePickerActivity.class);
                                startActivity(i);
                                finish();
                            }
                        };
                        customYesNoDialog.show();
                        break;
                }

            }
        });

        Helper.getInstance().setListViewHeightBasedOnChildren(deleteProfileListView);

    }

    @Override
    public void onBackPressed() {
        onActivityBackPressed();
    }

    private void onActivityBackPressed() {

        if (profileMode == PROFILE_MODE.NEW) {
            // todo yesno cancel

            int profilePickerLastIndex = getIntent().getIntExtra("LastIndex", 0);
            Intent i = new Intent(this, ProfilePickerActivity.class);
            i.putExtra("OpenOnIndex", profilePickerLastIndex);
            startActivity(i);
            finish();
        } else {
            if (returnLocation == RETURN_LOCATION.MAIN_ACTIVITY) {
                Intent i = new Intent(this, MainActivityManager.class);
                i.putExtra("StartOnProfileFragment", true);
                startActivity(i);
                finish();
            } else {
                int profilePickerLastIndex = getIntent().getIntExtra("LastIndex", 0);
                Intent i = new Intent(this, ProfilePickerActivity.class);
                i.putExtra("OpenOnIndex", profilePickerLastIndex);
                startActivity(i);
                finish();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = (ArrayList<Image>) ImagePicker.getImages(data);
            File imgFile = new  File(images.get(0).getPath());
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                headerImage.setImageBitmap(myBitmap);
                headerImagePath = imgFile.getAbsolutePath();

                if(profileMode == PROFILE_MODE.EDIT){
                    Database.mProfileDao.updateProfileImage(profileId, imgFile.getAbsolutePath());
                }
            }
        }
    }
}
