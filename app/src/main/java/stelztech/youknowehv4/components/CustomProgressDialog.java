package stelztech.youknowehv4.components;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by alex on 2017-12-05.
 */

public abstract class CustomProgressDialog extends ProgressDialog {

    private Activity activity;

    public CustomProgressDialog(String title, int numToLoad, Context context, Activity activity) {
        super(context);
        this.activity = activity;
        setTitle(title);
        setMax(numToLoad);
        setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        setIndeterminate(false);
        setCancelable(false);
        setProgress(0);
    }

    public void startDialog() {
        show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // todo dialog
                loadInformation();
                informationLoaded();
            }
        }).start();
    }

    public abstract void loadInformation();

    public abstract void informationLoaded();

    public void setDialogProgress(final int progressValue) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setProgress(progressValue);
            }
        });
    }

}
