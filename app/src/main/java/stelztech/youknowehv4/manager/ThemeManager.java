package stelztech.youknowehv4.manager;

import android.content.res.Resources;

import stelztech.youknowehv4.R;

/**
 * Created by alex on 11/19/2017.
 */

public class ThemeManager {

    /*
    to add new colors:
        add the colors in colors.xml
        add the new theme in styles.xml
        create a button in fragment_profile.xml
        hookup the button in ProfileFragment.xml
        add a THEME_COLORS and hook it up to the get functions
     */


    public enum THEME_COLORS{
        BLUE,
        GREEN,
        RED,
        PURPLE,
        GREY,
        PINK,
        ORANGE,
        INDIGO
    }

    private THEME_COLORS currentTheme;

    private static ThemeManager instance;


    private ThemeManager(){
        currentTheme = THEME_COLORS.BLUE;
    }

    public static ThemeManager getInstance(){
        if(instance == null){
            instance = new ThemeManager();
        }
        return instance;
    }

    public void changeTheme(THEME_COLORS theme){
        currentTheme = theme;
    }


    public int getCurrentAppThemeNoActionBarValue(){

        int theme = R.style.AppThemeBlue_NoActionBar;
        switch (currentTheme){
            case BLUE:
                theme = R.style.AppThemeBlue_NoActionBar;
                break;
            case GREEN:
                theme = R.style.AppThemeGreen_NoActionBar;
                break;
            case RED:
                theme = R.style.AppThemeRed_NoActionBar;
                break;
            case PURPLE:
                theme = R.style.AppThemePurple_NoActionBar;
                break;
            case GREY:
                theme = R.style.AppThemeGrey_NoActionBar;
                break;
            case PINK:
                theme = R.style.AppThemePink_NoActionBar;
                break;
            case ORANGE:
                theme = R.style.AppThemeOrange_NoActionBar;
                break;
            case INDIGO:
                theme = R.style.AppThemeIndigo_NoActionBar;
                break;
        }

        return theme;
    }

    public int getCurrentAppThemeValue(){

        int theme = R.style.AppThemeBlue;
        switch (currentTheme){
            case BLUE:
                theme = R.style.AppThemeBlue;
                break;
            case GREEN:
                theme = R.style.AppThemeGreen;
                break;
            case RED:
                theme = R.style.AppThemeRed;
                break;
            case PURPLE:
                theme = R.style.AppThemePurple;
                break;
            case GREY:
                theme = R.style.AppThemeGrey;
                break;
            case PINK:
                theme = R.style.AppThemePink;
                break;
            case ORANGE:
                theme = R.style.AppThemeOrange;
                break;
            case INDIGO:
                theme = R.style.AppThemeIndigo;
                break;
        }

        return theme;
    }

    public THEME_COLORS getCurrentTheme(){
        return currentTheme;
    }

}
