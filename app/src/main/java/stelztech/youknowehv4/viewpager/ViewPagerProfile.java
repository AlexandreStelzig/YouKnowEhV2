package stelztech.youknowehv4.viewpager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activitypackage.MainActivityManager;
import stelztech.youknowehv4.database.DatabaseManager;

/**
 * Created by alex on 2017-05-06.
 */

public class ViewPagerProfile extends Fragment{


    private View view;


    private Button startButton;
    private EditText introProfileEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.viewpager_profile, container, false);


        startButton = (Button) view.findViewById(R.id.intro_start_button);
        introProfileEditText = (EditText) view.findViewById(R.id.intro_profile_edittext);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButtonClicked();
            }
        });

        return view;
    }

    private void startButtonClicked(){

        String profileText = introProfileEditText.getText().toString();

        if(profileText.trim().isEmpty()){
            Toast.makeText(getContext(), "Invalid name: cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String profileId = DatabaseManager.getInstance(getContext()).createProfile(profileText);

        Toast.makeText(getContext(), "Profile successfully created", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(getContext(), MainActivityManager.class);
        getActivity().startActivity(i);
        getActivity().finish();
    }

}
