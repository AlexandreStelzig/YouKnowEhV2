package stelztech.youknowehv4.fragments.statistics;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by alex on 2/5/2018.
 */

public class StatsMultiSelectionSpinner extends android.support.v7.widget.AppCompatSpinner implements
        DialogInterface.OnMultiChoiceClickListener {
    String[] _items = new String[0];
    boolean[] mSelection = new boolean[0];

    ArrayAdapter<String> simple_adapter;

    private StatisticsFragment statisticsFragment = null;
    AlertDialog alertDialog;

    public void setStatisticsFragment(StatisticsFragment statisticsFragment) {
        this.statisticsFragment = statisticsFragment;
    }

    public StatsMultiSelectionSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        simple_adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(simple_adapter);
    }

    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (mSelection != null && which < mSelection.length) {
            mSelection[which] = isChecked;
            if (which == 0) {
                if (!mSelection[0])
                    for (int i = 0; i < mSelection.length; i++)
                        mSelection[i] = false;
                else
                    for (int i = 0; i < mSelection.length; i++)
                        mSelection[i] = true;
                performClick();
            }
            setAdapterTitle();
        } else {
            throw new IllegalArgumentException(
                    "Argument 'which' is out of bounds.");
        }
    }

    @Override
    public boolean performClick() {
        if(alertDialog != null && alertDialog.isShowing())
            alertDialog.dismiss();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(_items, mSelection, this);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (statisticsFragment != null) {
                    statisticsFragment.onDeckSpinnerPositiveButtonClicked();
                }
            }
        });


        alertDialog = builder.create();
        alertDialog.show();
        return true;
    }


    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException(
                "setAdapter is not supported by MultiSelectSpinner.");
    }

    public void setItems(List<String> items) {
        _items = items.toArray(new String[items.size()]);
        mSelection = new boolean[_items.length];
        simple_adapter.clear();
        simple_adapter.add(_items[0]);
        Arrays.fill(mSelection, false);
    }


    public void setSelection(int index) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        if (index >= 0 && index < mSelection.length) {
            mSelection[index] = true;
        } else {
            throw new IllegalArgumentException("Index " + index
                    + " is out of bounds.");
        }
        setAdapterTitle();
    }



    public List<Integer> getSelectedIndicies() {
        List<Integer> selection = new LinkedList<Integer>();
        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                selection.add(i);
            }
        }
        return selection;
    }

    private String buildSelectedItemString() {
        String selectedItemString;
        int nbFound = 0;

        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                nbFound++;
            }
        }

        if (nbFound == 0) {
            selectedItemString = "No decks";
        } else if (nbFound == 1) {
            selectedItemString = "1 deck";
        } else if (nbFound == mSelection.length) {
            selectedItemString = "All decks";
        } else {
            selectedItemString = nbFound + " decks";
        }

        return selectedItemString;
    }

    public void setAdapterTitle() {

        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

}