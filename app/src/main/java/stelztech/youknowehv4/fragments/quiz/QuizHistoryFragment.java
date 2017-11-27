package stelztech.youknowehv4.fragments.quiz;


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

public class QuizHistoryFragment extends Fragment {

    View view;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_quiz_history, container, false);
        ((Button) view.findViewById(R.id.test)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progress = ProgressDialog.show(getContext(), "dialog title",
                        "dialog message", true);

                new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        // do the thing that takes a long time

                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                progress.dismiss();
                            }
                        });
                    }
                }).start();
            }
        });
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
