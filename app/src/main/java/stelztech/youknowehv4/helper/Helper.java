package stelztech.youknowehv4.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import stelztech.youknowehv4.fragmentpackage.review.ReviewFragment;
import stelztech.youknowehv4.database.deck.Deck;

/**
 * Created by alex on 2017-04-10.
 */

public class Helper {

    private static Helper instance;
    private Context context;

    private Helper() {

    }

    public static Helper getInstance() {
        if (instance == null)
            instance = new Helper();
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(context.INPUT_METHOD_SERVICE);
            if (imm.isAcceptingText()) { // verify if the soft keyboard is open
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }


    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public TextView customTitle(String title) {
        TextView titleTV = new TextView(context);
        titleTV.setTypeface(Typeface.DEFAULT_BOLD);
        titleTV.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        titleTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        titleTV.setLayoutParams(lp);
        titleTV.setText(title);
        titleTV.setPadding(35, 35, 0, 0);
        return titleTV;
    }



    public ReviewFragment.ReviewToggle convertStringToPracticeToggle(String practiceToggleString){

        switch (practiceToggleString){
            case "HOURS_3":
                return ReviewFragment.ReviewToggle.HOURS_3;
            case "HOURS_12":
                return ReviewFragment.ReviewToggle.HOURS_12;
            case "HOURS_24":
                return ReviewFragment.ReviewToggle.HOURS_24;
            case "HOURS_48":
                return ReviewFragment.ReviewToggle.HOURS_48;
            case "DAYS_3":
                return ReviewFragment.ReviewToggle.DAYS_3;
            case "DAYS_5":
                return ReviewFragment.ReviewToggle.DAYS_5;
            case "Forever":
                return ReviewFragment.ReviewToggle.ALWAYS;
        }
        return ReviewFragment.ReviewToggle.HOURS_12;
    }

    public String convertPracticeToggleToString(ReviewFragment.ReviewToggle reviewToggle){

        switch (reviewToggle){

            case HOURS_3:
                return "3 Hours";
            case HOURS_12:
                return "12 Hours";
            case HOURS_24:
                return "24 Hours";
            case HOURS_48:
                return "48 Hours";
            case DAYS_3:
                return "3 days";
            case DAYS_5:
                return "5 days";
            case ALWAYS:
                return "Forever";
        }
        return "";
    }

}
