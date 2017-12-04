package stelztech.youknowehv4.fragments.statistics;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activities.MainActivityManager;

/**
 * Created by alex on 2017-04-03.
 */

public class StatisticsFragment extends Fragment {

    View view;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_statistics, container, false);

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.toolbar_other, menu);
        ActionBar actionBar = ((MainActivityManager) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        getActivity().findViewById(R.id.spinner_nav_layout).setVisibility(View.GONE);

    }
}
