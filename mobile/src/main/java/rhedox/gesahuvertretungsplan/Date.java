package rhedox.gesahuvertretungsplan;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Robin on 03.05.2015.
 */
public class Date {
    private final int day;
    private final int month;
    private final int year;

    public Date(int day, int month, int year) {
        this.day= day;
        this.month = month;
        this.year = year;
    }

    public static Date fromCalendar(Calendar calendar) {
        return new Date(calendar.get(GregorianCalendar.DAY_OF_MONTH), calendar.get(GregorianCalendar.MONTH) + 1, calendar.get(GregorianCalendar.YEAR));
    }
    public static Date fromJavaDate(int day, int month, int year) {
        return new Date(day, month + 1, year);
    }

    public Calendar toCalendar() {
        return new GregorianCalendar(getYear(), getJavaMonth(), getDay());
    }


    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }
    public int getJavaMonth() {
        return month-1;
    }

    public int getYear() {
        return year;
    }

    public String toString() {
        String dateString = "";

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(GregorianCalendar.DAY_OF_MONTH, day);
        calendar.set(GregorianCalendar.MONTH, month - 1);
        calendar.set(GregorianCalendar.YEAR, year);
        calendar.set(GregorianCalendar.HOUR, 0);
        calendar.set(GregorianCalendar.MINUTE, 0);
        calendar.set(GregorianCalendar.SECOND, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);

        int dayOfWeek = calendar.get(GregorianCalendar.DAY_OF_WEEK);

        String[] daysOfWeek = new String[]{"", "So.", "Mo.", "Di.", "Mi.", "Do.", "Fr.", "Sa."};

        dateString += daysOfWeek[dayOfWeek] + ", ";

        if (day < 10) {
            dateString += "0";
        }
        dateString += Integer.toString(day) + ".";
        if (month < 10) {
            dateString += "0";
        }
        dateString += Integer.toString(month) + ".";
        dateString += Integer.toString(year);
        return dateString;
    }
}
