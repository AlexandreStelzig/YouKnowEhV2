package stelztech.youknowehv4.manager;


import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activitypackage.CardInfoActivity;

/**
 * Created by alex on 4/14/2017.
 */

public class CardInfoToolbarManager {

    private static CardInfoToolbarManager instance;
    private Context context;


    private CardInfoToolbarManager() {

    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static CardInfoToolbarManager getInstance() {
        if (instance == null) {
            instance = new CardInfoToolbarManager();
        }
        return instance;
    }

    public void setState(CardInfoActivity.CardInfoState state, Menu menu) {


        switch (state) {
            case NEW:
                setMenuItemVisibility(menu, false, true, true, false, false);
                break;
            case EDIT:
                setMenuItemVisibility(menu, true, true, false, false, true);
                break;
            case VIEW:
                setMenuItemVisibility(menu, false, false, false, true, false);
                break;
            default:
                Log.e("CardInfoToolbarManager", "Changing state error");
                break;
        }
    }

    private void setMenuItemVisibility( Menu menu, boolean isCancelVisible, boolean isDoneVisible,
                                       boolean isNextVisable, boolean isEditVisible, boolean isSwitchVisible) {

        menu.findItem(R.id.action_cancel_card_info).setVisible(isCancelVisible);
        menu.findItem(R.id.action_done_card_info).setVisible(isDoneVisible);
        menu.findItem(R.id.action_next_card_info).setVisible(isNextVisable);
        menu.findItem(R.id.action_edit_card_info).setVisible(isEditVisible);
        menu.findItem(R.id.action_switch_card_info).setVisible(isSwitchVisible);

    }

}
