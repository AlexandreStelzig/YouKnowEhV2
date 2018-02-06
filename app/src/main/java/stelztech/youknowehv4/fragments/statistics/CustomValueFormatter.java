package stelztech.youknowehv4.fragments.statistics;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by alex on 2/5/2018.
 */

public class CustomValueFormatter implements IValueFormatter {

    private DecimalFormat mFormat;

    public CustomValueFormatter() {

        // format values to 1 decimal digit
        mFormat = new DecimalFormat("###,###,##0");
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return mFormat.format((int) value);
    }
}