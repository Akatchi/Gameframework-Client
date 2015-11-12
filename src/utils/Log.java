package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by akatchi on 11-8-15.
 */
public class Log
{
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public Log(){}

    public static void DEBUG(String message)
    {
        String format = "[%s]: %s";
        System.out.println(String.format(format, getDateTime(), message));
    }

    public static void ERROR(String message)
    {
        String format = "[%s]: %s";
        System.err.println(String.format(format, getDateTime(), message));
    }

    private static String getDateTime()
    {
        return DATE_FORMAT.format(new Date());
    }
}
