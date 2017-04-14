package stelztech.youknowehv4.helper;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;

/**
 * Created by alex on 2017-04-10.
 */

public class Helper {

    private static Helper instance;
    private Context context;

    private Helper(){

    }

    public static Helper getInstance(){
        if(instance == null)
            instance = new Helper();
        return instance;
    }

    public void setContext(Context context){
        this.context = context;
    }

    public void hideKeyboard(Activity activity){
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
