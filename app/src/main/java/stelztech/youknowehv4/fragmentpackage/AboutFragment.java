package stelztech.youknowehv4.fragmentpackage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.state.ToolbarStateManager;

/**
 * Created by alex on 2017-04-03.
 */

public class AboutFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_about, container, false);





        return view;
    }

    public void onPrepareOptionsMenu(Menu menu) {
        ToolbarStateManager.getInstance().setState(ToolbarStateManager.toolbarState.WORD, menu, getActivity());
    }
}
