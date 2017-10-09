package stelztech.youknowehv4.firsttimeopening;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import stelztech.youknowehv4.R;

/**
 * Created by alex on 2017-05-06.
 */

public class ViewPagerWelcome extends Fragment {


    private View view;
    private Button nextButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.viewpager_welcome, container, false);

        nextButton = (Button) view.findViewById(R.id.intro_next_button);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((FirstTimeOpeningActivity) getActivity())
                        .changeState(FirstTimeOpeningActivity.ViewPagerState.CREATE_PROFILE);
            }
        });

        return view;
    }
}
