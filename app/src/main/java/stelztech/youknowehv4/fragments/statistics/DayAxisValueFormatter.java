package stelztech.youknowehv4.fragments.statistics;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by alex on 1/20/2018.
 */

public class DayAxisValueFormatter implements IAxisValueFormatter
{

    public static final int START_YEAR = 2016;

    protected String[] mMonths = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    private BarLineChartBase<?> chart;

    public DayAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        int days = (int) value;

        int year = determineYear(days);

        int month = determineMonth(days);
        String monthName = mMonths[month % mMonths.length];
        String yearName = String.valueOf(year);

        if (chart.getVisibleXRange() > 30 * 6) {

            return monthName + " " + yearName;
        } else {

            int dayOfMonth = determineDayOfMonth(days);

            String appendix = "th";

            switch (dayOfMonth) {
                case 1:
                    appendix = "st";
                    break;
                case 2:
                    appendix = "nd";
                    break;
                case 3:
                    appendix = "rd";
                    break;
                case 21:
                    appendix = "st";
                    break;
                case 22:
                    appendix = "nd";
                    break;
                case 23:
                    appendix = "rd";
                    break;
                case 31:
                    appendix = "st";
                    break;
            }

            return dayOfMonth == 0 ? "" : dayOfMonth + appendix + " " + monthName;
        }
    }

    private int determineMonth(int days) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, START_YEAR);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add( Calendar.DAY_OF_YEAR, days);

        return cal.get(Calendar.MONTH );
    }

    private int determineDayOfMonth(int days) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, START_YEAR);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add( Calendar.DAY_OF_YEAR, days);
        return cal.get(Calendar.DAY_OF_MONTH);

    }

    private int determineYear(int days) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, START_YEAR);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add( Calendar.DAY_OF_YEAR, days);
        return cal.get(Calendar.YEAR);

    }
}