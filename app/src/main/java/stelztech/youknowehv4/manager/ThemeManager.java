package stelztech.youknowehv4.manager;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import stelztech.youknowehv4.R;

/**
 * Created by alex on 11/19/2017.
 */

public class ThemeManager {

    /*
    to add new colors:
        add the colors in colors.xml
        add the new theme in styles.xml
        create a button in fragment_profile_old_old.xml
        hookup the button in Old_ProfileFragment.xml
        add a THEME_COLORS and hook it up to the get functions
     */


    public enum THEME_COLORS {
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


    private ThemeManager() {
        currentTheme = THEME_COLORS.BLUE;
    }

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    public void changeTheme(THEME_COLORS theme) {
        currentTheme = theme;
    }

    public int getCurrentAppThemeNoActionBarValue() {
        return getAppThemeNoActionBarValue(currentTheme);
    }

    public int getAppThemeNoActionBarValue(THEME_COLORS theme) {

        int themeValue = R.style.AppThemeBlue_NoActionBar;
        switch (theme) {
            case BLUE:
                themeValue = R.style.AppThemeBlue_NoActionBar;
                break;
            case GREEN:
                themeValue = R.style.AppThemeGreen_NoActionBar;
                break;
            case RED:
                themeValue = R.style.AppThemeRed_NoActionBar;
                break;
            case PURPLE:
                themeValue = R.style.AppThemePurple_NoActionBar;
                break;
            case GREY:
                themeValue = R.style.AppThemeGrey_NoActionBar;
                break;
            case PINK:
                themeValue = R.style.AppThemePink_NoActionBar;
                break;
            case ORANGE:
                themeValue = R.style.AppThemeOrange_NoActionBar;
                break;
            case INDIGO:
                themeValue = R.style.AppThemeIndigo_NoActionBar;
                break;
        }

        return themeValue;
    }

    public int getCurrentAppThemeValue() {
        return getAppThemeValue(currentTheme);
    }

    public int getAppThemeValue(THEME_COLORS theme) {

        int themeValue = R.style.AppThemeBlue;
        switch (theme) {
            case BLUE:
                themeValue = R.style.AppThemeBlue;
                break;
            case GREEN:
                themeValue = R.style.AppThemeGreen;
                break;
            case RED:
                themeValue = R.style.AppThemeRed;
                break;
            case PURPLE:
                themeValue = R.style.AppThemePurple;
                break;
            case GREY:
                themeValue = R.style.AppThemeGrey;
                break;
            case PINK:
                themeValue = R.style.AppThemePink;
                break;
            case ORANGE:
                themeValue = R.style.AppThemeOrange;
                break;
            case INDIGO:
                themeValue = R.style.AppThemeIndigo;
                break;
        }

        return themeValue;
    }

    public int getThemePrimaryColor(Context context, THEME_COLORS theme) {

        int color = ContextCompat.getColor(context, R.color.colorPrimary);
        switch (theme) {
            case BLUE:
                color = ContextCompat.getColor(context, R.color.colorPrimary);
                break;
            case GREEN:
                color = ContextCompat.getColor(context, R.color.colorPrimaryGreen);
                break;
            case RED:
                color = ContextCompat.getColor(context, R.color.colorPrimaryRed);
                break;
            case PURPLE:
                color = ContextCompat.getColor(context, R.color.colorPrimaryPurple);
                break;
            case GREY:
                color = ContextCompat.getColor(context, R.color.colorPrimaryGrey);
                break;
            case PINK:
                color = ContextCompat.getColor(context, R.color.colorPrimaryPink);
                break;
            case ORANGE:
                color = ContextCompat.getColor(context, R.color.colorPrimaryOrange);
                break;
            case INDIGO:
                color = ContextCompat.getColor(context, R.color.colorPrimaryIndigo);
                break;
        }

        return color;
    }

    public void setApplicationTheme(Activity activity, boolean themeWithActionBar) {
        if (themeWithActionBar)
            activity.setTheme(ThemeManager.getInstance().getCurrentAppThemeValue());
        else
            activity.setTheme(ThemeManager.getInstance().getCurrentAppThemeNoActionBarValue());
        TypedValue typedValue = new TypedValue();
        TypedArray a = activity.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
        int color = a.getColor(0, 0);
        a.recycle();
        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(activity.getString(R.string.app_name),
                BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher), color);
        (activity).setTaskDescription(taskDescription);
    }

    public THEME_COLORS getCurrentTheme() {
        return currentTheme;
    }

}
