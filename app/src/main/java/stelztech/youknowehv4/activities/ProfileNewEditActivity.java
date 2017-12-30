package stelztech.youknowehv4.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.profile.Profile;
import stelztech.youknowehv4.manager.ThemeManager;
import stelztech.youknowehv4.utilities.Helper;

/**
 * Created by alex on 12/29/2017.
 */

public class ProfileNewEditActivity extends AppCompatActivity {

    public enum PROFILE_MODE {
        EDIT,
        NEW
    }

    private PROFILE_MODE profileMode;

    private TextView profileNameEditText;
    private TextView frontLabelEditText;
    private TextView backLabelEditText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(ThemeManager.getInstance().getCurrentAppThemeValue());
        setContentView(R.layout.activity_profile_new_edit);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // todo change to intent extra
        profileMode = PROFILE_MODE.EDIT;


        // set back button instead of drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        profileNameEditText = (TextView) findViewById(R.id.profile_new_edit_profile_name);
        frontLabelEditText = (TextView) findViewById(R.id.profile_new_edit_front_label);
        backLabelEditText = (TextView) findViewById(R.id.profile_new_edit_back_label);



        if(profileMode == PROFILE_MODE.EDIT){
            initEditFields();
            getSupportActionBar().setTitle("Edit Profile");
        }else{
            initNewFields();
            getSupportActionBar().setTitle("New Profile");
        }

        initComponentsOnClick();

    }

    private void initComponentsOnClick() {

        profileNameEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(MotionEvent.ACTION_UP == motionEvent.getAction())
                    Toast.makeText(ProfileNewEditActivity.this, "name clicked", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        frontLabelEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(MotionEvent.ACTION_UP == motionEvent.getAction())
                    Toast.makeText(ProfileNewEditActivity.this, "front clicked", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        backLabelEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(MotionEvent.ACTION_UP == motionEvent.getAction())
                    Toast.makeText(ProfileNewEditActivity.this, "back clicked", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


    }

    private void initEditFields() {
        // todo fetch from intent extra
        Profile profile = Database.mUserDao.fetchActiveProfile();

        profileNameEditText.setText(profile.getProfileName());
        frontLabelEditText.setText(profile.getQuestionLabel());
        backLabelEditText.setText(profile.getAnswerLabel());

    }

    private void initNewFields() {
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (profileMode == PROFILE_MODE.NEW)
            getMenuInflater().inflate(R.menu.toolbar_profile_new, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        switch (id) {

            // back button
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
