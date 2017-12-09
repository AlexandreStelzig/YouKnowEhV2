package stelztech.youknowehv4.components;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import stelztech.youknowehv4.activities.CardInfoActivity;

/**
 * Created by alex on 2017-12-06.
 */

public abstract class CustomYesNoDialog extends AlertDialog.Builder {

    public CustomYesNoDialog(Context context, String title, String message) {
        super(context);

        setTitle(title);
        setMessage(message);

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        onPositiveButtonClick();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        onNegativeButtonClick();
                        break;
                }
            }
        };


        setPositiveButton("Yes", dialogClickListener);
        setNegativeButton("Cancel", dialogClickListener);
        setCancelable(false);

    }

    protected abstract void onNegativeButtonClick();

    protected abstract void onPositiveButtonClick();


}
