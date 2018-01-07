package stelztech.youknowehv4.manager;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.View;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activities.MainActivityManager;

/**
 * Created by alex on 2017-04-06.
 */

public class CardToolbarManager {

    public enum CardToolbarState {
        CARD,
        CARD_LIST_EDIT,
        SEARCH,
        SEARCH_ALL,
        ALL_CARDS
    }


    private final boolean DROPDOWN_IS_VISIBLE = true;
    private final boolean SEARCH_IS_VISIBLE = true;
    private final boolean REVERSES_IS_VISIBLE = true;
    private final boolean DONE_IS_VISIBLE = true;
    private final boolean EDIT_DECK_IS_VISIBLE = true;
    private final boolean CANCEL_IS_VISIBLE = true;
    private final boolean TOGGLE_PRACTICE_IS_VISIBLE = true;
    private final boolean SORT_IS_VISIBLE = true;
    private final boolean QUICK_CREATE_IS_VISIBLE = true;
    private final boolean FILTER_PRACTICE_IS_VISIBLE = true;
    private final boolean IMPORT_CARDS_IS_VISIBLE = true;

    private static CardToolbarManager instance;
    private Context context;


    private CardToolbarManager() {

    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static CardToolbarManager getInstance() {
        if (instance == null) {
            instance = new CardToolbarManager();
        }
        return instance;
    }

    public void setState(CardToolbarState state, Menu menu, final Activity activity) {


        switch (state) {
            case CARD:
                setMenuItemVisibility(activity, menu, DROPDOWN_IS_VISIBLE, SEARCH_IS_VISIBLE,
                        REVERSES_IS_VISIBLE, !DONE_IS_VISIBLE, EDIT_DECK_IS_VISIBLE, !CANCEL_IS_VISIBLE,
                        TOGGLE_PRACTICE_IS_VISIBLE, SORT_IS_VISIBLE, QUICK_CREATE_IS_VISIBLE, FILTER_PRACTICE_IS_VISIBLE, IMPORT_CARDS_IS_VISIBLE);
                break;
            case CARD_LIST_EDIT:
                setMenuItemVisibility(activity, menu, !DROPDOWN_IS_VISIBLE, !SEARCH_IS_VISIBLE,
                        REVERSES_IS_VISIBLE, DONE_IS_VISIBLE, !EDIT_DECK_IS_VISIBLE, CANCEL_IS_VISIBLE,
                        !TOGGLE_PRACTICE_IS_VISIBLE, SORT_IS_VISIBLE, !QUICK_CREATE_IS_VISIBLE, !FILTER_PRACTICE_IS_VISIBLE, IMPORT_CARDS_IS_VISIBLE);
                break;
            case SEARCH:
                setMenuItemVisibility(activity, menu, !DROPDOWN_IS_VISIBLE, SEARCH_IS_VISIBLE,
                        REVERSES_IS_VISIBLE, !DONE_IS_VISIBLE, !EDIT_DECK_IS_VISIBLE, !CANCEL_IS_VISIBLE,
                        !TOGGLE_PRACTICE_IS_VISIBLE, SORT_IS_VISIBLE, !QUICK_CREATE_IS_VISIBLE, FILTER_PRACTICE_IS_VISIBLE, !IMPORT_CARDS_IS_VISIBLE);
                break;
            case SEARCH_ALL:
                setMenuItemVisibility(activity, menu, !DROPDOWN_IS_VISIBLE, SEARCH_IS_VISIBLE,
                        REVERSES_IS_VISIBLE, !DONE_IS_VISIBLE, !EDIT_DECK_IS_VISIBLE, !CANCEL_IS_VISIBLE,
                        !TOGGLE_PRACTICE_IS_VISIBLE, SORT_IS_VISIBLE, !QUICK_CREATE_IS_VISIBLE, !FILTER_PRACTICE_IS_VISIBLE, !IMPORT_CARDS_IS_VISIBLE);
                break;
            case ALL_CARDS:
                setMenuItemVisibility(activity, menu, DROPDOWN_IS_VISIBLE, SEARCH_IS_VISIBLE,
                        REVERSES_IS_VISIBLE, !DONE_IS_VISIBLE, !EDIT_DECK_IS_VISIBLE, !CANCEL_IS_VISIBLE,
                        !TOGGLE_PRACTICE_IS_VISIBLE, SORT_IS_VISIBLE, QUICK_CREATE_IS_VISIBLE, !FILTER_PRACTICE_IS_VISIBLE, !IMPORT_CARDS_IS_VISIBLE);
                break;
            default:
                setMenuItemVisibility(activity, menu, !DROPDOWN_IS_VISIBLE, !SEARCH_IS_VISIBLE,
                        !REVERSES_IS_VISIBLE, !DONE_IS_VISIBLE, !EDIT_DECK_IS_VISIBLE, !CANCEL_IS_VISIBLE,
                        !TOGGLE_PRACTICE_IS_VISIBLE, !SORT_IS_VISIBLE, !QUICK_CREATE_IS_VISIBLE, !FILTER_PRACTICE_IS_VISIBLE, !IMPORT_CARDS_IS_VISIBLE);
                break;
        }
    }


    private void setMenuItemVisibility(Activity activity, Menu menu, boolean isDropdownVisible, boolean isSearchVisible,
                                       boolean isReverseVisible, boolean isDoneVisible, boolean isEditDeckCardsVisible,
                                       boolean isCancelVisible, boolean isTogglePracticeVisible, boolean isSortVisible,
                                       boolean isQuickCreateVisible, boolean isFilterPracticeVisible, boolean isImportCardsVisible) {

        ActionBar actionBar = ((MainActivityManager) activity).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(!isDropdownVisible);

        if (activity.findViewById(R.id.spinner_nav_layout) != null) {
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
        menu.findItem(R.id.action_quick_create).setVisible(isQuickCreateVisible);
        menu.findItem(R.id.action_filter_practice).setVisible(isFilterPracticeVisible);
        menu.findItem(R.id.action_import_cards_to_deck).setVisible(isImportCardsVisible);

    }


}
