package stelztech.youknowehv4.fragmentpackage;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.manager.ActionButtonManager;
import stelztech.youknowehv4.manager.MainMenuToolbarManager;

/**
 * Created by alex on 2017-04-03.
 */

public class AboutFragment extends Fragment {


    private TextView github;
    private TextView email;
    private TextView website;


    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_about, container, false);

        ActionButtonManager.getInstance().setState(ActionButtonManager.ActionButtonState.GONE, getActivity());
        setHasOptionsMenu(true);

        github = (TextView) view.findViewById(R.id.about_github);
        email = (TextView) view.findViewById(R.id.about_email);
        website = (TextView) view.findViewById(R.id.about_website);

        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/AlexandreStelzig"));
                startActivity(browserIntent);
            }
        });

        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://alexandrestelzig.github.io/"));
                startActivity(browserIntent);
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] emailTo = {"alexandre.stelzig@gmail.com"};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");


                emailIntent.putExtra(Intent.EXTRA_EMAIL, emailTo);
//                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "You Know Eh?");
//                emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

                startActivity(emailIntent);
            }
        });


        return view;
    }

    public void onPrepareOptionsMenu(Menu menu) {
        MainMenuToolbarManager.getInstance().setState(MainMenuToolbarManager.MainMenuToolbarState.DEFAULT, menu, getActivity());
    }
}
