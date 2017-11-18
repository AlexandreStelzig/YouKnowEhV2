package stelztech.youknowehv4.helper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by alex on 2017-10-25.
 */

public class DateHelper {

    public final static String DEFAULT_DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";


    public static String getDateNowString() {
        DateFormat df = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        Date now = Calendar.getInstance().getTime();
        return df.format(now);
    }

    public static Date getDateNow() {
        DateFormat df = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return Calendar.getInstance().getTime();
    }

    public static String dateToString(String date){
        return date.toString();
    }

    public static Date stringToDate(String dateString){
        DateFormat df = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        try {
            return df.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
