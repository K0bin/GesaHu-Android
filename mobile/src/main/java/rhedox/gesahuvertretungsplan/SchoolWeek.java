package rhedox.gesahuvertretungsplan;

import java.util.GregorianCalendar;

/**
 * Created by Robin on 02.03.2015.
 */
public class SchoolWeek {
    public static Date next() {
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();

        if(calendar.get(GregorianCalendar.HOUR_OF_DAY) > 18)
            calendar.add(GregorianCalendar.DAY_OF_MONTH,1);

        if(calendar.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY)
            calendar.add(GregorianCalendar.DAY_OF_MONTH,1);
        else if(calendar.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY)
            calendar.add(GregorianCalendar.DAY_OF_MONTH,2);

        return Date.fromCalendar(calendar);
    }

    public static Date nextDay(Date date) {
        GregorianCalendar calendar = (GregorianCalendar) date.toCalendar();

        if (calendar.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY)
            calendar.add(GregorianCalendar.DAY_OF_MONTH, 1);
        else if (calendar.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY)
            calendar.add(GregorianCalendar.DAY_OF_MONTH, 2);

        return Date.fromCalendar(calendar);
    }
}
