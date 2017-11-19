package stelztech.youknowehv4.manager;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.View;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activitypackage.MainActivityManager;

/**
 * Created by alex on 2017-04-06.
 */

public class DeckToolbarManager {

    public enum DeckToolbarState {
        DECK,
        DECK_ORDER
    }

    private static DeckToolbarManager instance;
    private Context context;


    private DeckToolbarManager() {

    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static DeckToolbarManager getInstance() {
        if (instance == null) {
            instance = new DeckToolbarManager();
        }
        return instance;
    }

    public void setState(DeckToolbarState state, Menu menu, final Activity activity) {


        switch (state) {
            case DECK:
                setMenuItemVisibility(activity, menu, false, true);
                break;
            case DECK_ORDER:
                setMenuItemVisibility(activity, menu, true, false);
                break;
            default:
                setMenuItemVisibility(activity, menu, false, false);
                break;
        }
    }

    private void setMenuItemVisibility(Activity activity, Menu menu, boolean isDoneVisible, boolean isDeckOrderVisible) {

        ActionBar actionBar = ((MainActivityManager) activity).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        activity.findViewById(R.id.spinner_nav_layout).setVisibility(View.GONE);

        menu.findItem(R.id.action_done).setVisible(isDoneVisible);
        menu.findItem(R.id.action_deck_order).setVisible(isDeckOrderVisible);
    }


}
