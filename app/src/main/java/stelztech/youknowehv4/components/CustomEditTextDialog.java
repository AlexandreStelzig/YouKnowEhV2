package stelztech.youknowehv4.components;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.fragments.profile.Old_ProfileFragment;
import stelztech.youknowehv4.utilities.Helper;

/**
 * Created by alex on 1/1/2018.
 */

public abstract class CustomEditTextDialog  {

    private AlertDialog alertDialog;
    private boolean validateIfSame;
    private String initialData;
    private Context context;

    public CustomEditTextDialog(final Activity activity, final Context context, final String initialData, String title, final boolean validateIfSame){

        this.context = context;
        this.initialData = initialData;
        this.validateIfSame = validateIfSame;

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final EditText input = new EditText(activity);
        input.setSelectAllOnFocus(true);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setSingleLine();

        FrameLayout container = new FrameLayout(activity);
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = activity.getResources().getDimensionPixelSize(R.dimen.default_padding);
        params.rightMargin = activity.getResources().getDimensionPixelSize(R.dimen.default_padding);
        input.setLayoutParams(params);
        input.setText(initialData);
        container.addView(input);

        builder.setCustomTitle(Helper.getInstance().customTitle(title));
        builder.setCancelable(false);

        builder.setView(container);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            // when button OK is press
            public void onClick(DialogInterface dialog, int which) {
                // nothing, will initiate it later
            }
        });

        // if cancel button is press, close dialog
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog = builder.create();

        builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        validateInformation(input.getText().toString());
                    }
                });

                Button negativeButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                        onNegativeButtonClicked();
                    }
                });
            }
        });

        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    validateInformation(input.getText().toString());
                }
                return true;
            }
        });

        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        input.setSelection(input.getText().length());
    }

    private void validateInformation(String data){

        if(validateIfSame){
            if(Objects.equals(initialData.toLowerCase(), data.toLowerCase())){
                Toast.makeText(context, "The labels must be different", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        onPositiveButtonClicked(data);
        alertDialog.dismiss();
    }

    public void showDialog(){
        alertDialog.show();
    }

    public abstract void onPositiveButtonClicked(String newData);
    public abstract void onNegativeButtonClicked();

}
