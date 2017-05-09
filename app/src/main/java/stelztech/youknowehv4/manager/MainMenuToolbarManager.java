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

public class MainMenuToolbarManager {

    public enum MainMenuToolbarState {
        CARD,
        DECK,
        PRACTICE,
        CARD_LIST_EDIT,
        SEARCH,
        DEFAULT,
        DECK_ORDER
    }

    private static MainMenuToolbarManager instance;
    private Context context;


    private MainMenuToolbarManager() {

    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static MainMenuToolbarManager getInstance() {
        if (instance == null) {
            instance = new MainMenuToolbarManager();
        }
        return instance;
    }

    public void setState(MainMenuToolbarState state, Menu menu, final Activity activity) {


        switch (state) {
            case CARD:
                setMenuItemVisibility(activity, menu, true, true, true, false, true, false, true, true, true, false);
                break;
            case DECK:
                setMenuItemVisibility(activity, menu, false, false, false, false, false, false, false, false, false, true);
                break;
            case DECK_ORDER:
                setMenuItemVisibility(activity, menu, false, false, false, true, false, false, false, false, false, false);
                break;
            case PRACTICE:
                setMenuItemVisibility(activity, menu, false, false, false, false, false, false, false, false, false, false);
                break;
            case CARD_LIST_EDIT:
                setMenuItemVisibility(activity, menu, false, false, true, true, false, true, false, true, false, false);
                break;
            case SEARCH:
                setMenuItemVisibility(activity, menu, false, true, true, false, false, false, false, true, false, false);
                break;
            case DEFAULT:
                // same
            default:
                setMenuItemVisibility(activity, menu, false, false, false, false, false, false, false, false, false, false);
                break;
        }
    }

    private void setMenuItemVisibility(Activity activity, Menu menu, boolean isDropdownVisible, boolean isSearchVisible,
                                       boolean isReverseVisible, boolean isDoneVisible, boolean isEditDeckCardsVisible,
                                       boolean isCancelVisible, boolean isTogglePracticeVisible, boolean isSortVisible,
                                       boolean isQuickCreateVisible, boolean isDeckOrderVisible) {

        ActionBar actionBar = ((MainActivityManager) activity).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(!isDropdownVisible);

        if(activity.findViewById(R.id.spinner_nav_layout) != null){
            if (isDropdownVisible) {
                activity.findViewById(R.id.spinner_nav_layout).setVisibility(View.VISIBLE);
            } else {
                activity.findViewById(R.id.spinner_nav_layout).setVisibility(View.GONE);
            }
        }

        menu.findItem(R.id.action_search).setVisible(isSearchVisible);
        menu.findItem(R.id.action_reverse).setVisible(isReverseVisible);

        menu.findItem(R.id.action_done).setVisible(isDoneVisible);
        menu.findItem(R.id.action_edit_deck_cards).setVisible(isEditDeckCardsVisible);
        menu.findItem(R.id.action_cancel).setVisible(isCancelVisible);
        menu.findItem(R.id.action_practice_toggle).setVisible(isTogglePracticeVisible);

        menu.findItem(R.id.action_sort).setVisible(isSortVisible);
        menu.findItem(R.id.quick_create).setVisible(isQuickCreateVisible);
        menu.findItem(R.id.action_deck_order).setVisible(isDeckOrderVisible);
    }


}
