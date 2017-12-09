package stelztech.youknowehv4.fragments.about;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import stelztech.youknowehv4.activities.MainActivityManager;
import stelztech.youknowehv4.fragments.FragmentCommon;
import stelztech.youknowehv4.utilities.Helper;
import stelztech.youknowehv4.manager.FloatingActionButtonManager;

/**
 * Created by alex on 2017-04-03.
 */

public class AboutFragment extends FragmentCommon {


    private ListView contactListView;


    View view;

    public AboutFragment(int animationLayoutPosition, boolean animationFade) {
        super(animationLayoutPosition, animationFade);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_about, container, false);

        FloatingActionButtonManager.getInstance().setState(FloatingActionButtonManager.ActionButtonState.GONE, getActivity());
        setHasOptionsMenu(true);

        contactListView = (ListView) view.findViewById(R.id.about_contact_listview);


        setupContactLV();

        return view;
    }


    private void setupContactLV() {

        // export import listview
        final String[] exportImportChoices = getContext().getResources().getStringArray(R.array.about_contact_options);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, exportImportChoices);
        contactListView.setAdapter(adapter);
        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    // email
                    case 0:
                        String[] emailTo = {"alexandre.stelzig@gmail.com"};
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setData(Uri.parse("mailto:"));
                        emailIntent.setType("text/plain");

                        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailTo);
                        startActivity(emailIntent);

                        break;
                }

            }
        });

        Helper.getInstance().

                setListViewHeightBasedOnChildren(contactListView);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.toolbar_other, menu);
        ActionBar actionBar = ((MainActivityManager) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        getActivity().findViewById(R.id.spinner_nav_layout).setVisibility(View.GONE);

    }
}
