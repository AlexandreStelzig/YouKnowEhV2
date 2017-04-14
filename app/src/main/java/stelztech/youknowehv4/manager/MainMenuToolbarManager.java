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
        WORD,
        DECK,
        DEFAULT
    }

    private static MainMenuToolbarManager instance;
    private Context context;


    private MainMenuToolbarManager(){

    }

    public void setContext(Context context){
        this.context = context;
    }

    public static MainMenuToolbarManager getInstance(){
        if(instance == null){
            instance = new MainMenuToolbarManager();
        }
        return instance;
    }

    public void setState(MainMenuToolbarState state, Menu menu, final Activity activity) {


        switch (state){
            case WORD:
                setMenuItemVisibility(activity, menu, true, true,true,true);
                break;
            case DECK:
                setMenuItemVisibility(activity, menu, false, true,true,true);
                break;
            case DEFAULT:
                // same
            default:
                setMenuItemVisibility(activity, menu, false, false,false,true);
                break;
        }
    }

    private void setMenuItemVisibility(Activity activity, Menu menu, boolean isDropdownVisible, boolean isSearchVisible,
                                       boolean isSortVisible, boolean isSettingsVisible){

        ActionBar actionBar = ((MainActivityManager) activity).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(!isDropdownVisible);

        if(isDropdownVisible){
            activity.findViewById(R.id.spinner_nav_layout).setVisibility(View.VISIBLE);
        } else{
            activity.findViewById(R.id.spinner_nav_layout).setVisibility(View.GONE);
        }
        menu.findItem(R.id.action_search).setVisible(isSearchVisible);
        menu.findItem(R.id.action_sort).setVisible(isSortVisible);
        menu.findItem(R.id.action_settings).setVisible(isSettingsVisible);


    }


}
