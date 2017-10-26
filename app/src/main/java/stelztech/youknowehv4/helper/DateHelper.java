package stelztech.youknowehv4.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by alex on 2017-10-25.
 */

public class DateHelper {

    private final static String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";


    public static String getDateNow() {
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        Date now = Calendar.getInstance().getTime();
        return df.format(now);
    }

}
