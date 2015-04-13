package rhedox.gesahuvertretungsplan;

import java.util.GregorianCalendar;

/**
 * Created by Robin on 02.03.2015.
 */
public class SchoolWeek {
    public static GregorianCalendar next() {
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();

        if(calendar.get(GregorianCalendar.HOUR_OF_DAY) > 18) {
            calendar.add(GregorianCalendar.DAY_OF_MONTH,1);
        }

        if(calendar.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY) {
            calendar.add(GregorianCalendar.DAY_OF_MONTH,2);
        }

        if(calendar.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY) {
            calendar.add(GregorianCalendar.DAY_OF_MONTH,1);
        }

        calendar.set(GregorianCalendar.HOUR,0);
        calendar.set(GregorianCalendar.MINUTE,0);
        calendar.set(GregorianCalendar.SECOND,0);
        calendar.set(GregorianCalendar.MILLISECOND,0);

        return calendar;
    }

    public static GregorianCalendar next(int day, int month, int year) {
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();

        calendar.set(GregorianCalendar.DAY_OF_MONTH, day);
        calendar.set(GregorianCalendar.MONTH, month - 1);
        calendar.set(GregorianCalendar.YEAR, year);
        calendar.set(GregorianCalendar.HOUR,0);
        calendar.set(GregorianCalendar.MINUTE,0);
        calendar.set(GregorianCalendar.SECOND,0);
        calendar.set(GregorianCalendar.MILLISECOND,0);

        if(calendar.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY) {
            calendar.add(GregorianCalendar.DAY_OF_MONTH,2);
        }

        if(calendar.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY) {
            calendar.add(GregorianCalendar.DAY_OF_MONTH,1);
        }

        return calendar;
    }


    public static String getDateString(int day, int month, int year) {
        String dateString = "";

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(GregorianCalendar.DAY_OF_MONTH,day);
        calendar.set(GregorianCalendar.MONTH,month-1);
        calendar.set(GregorianCalendar.YEAR,year);
        calendar.set(GregorianCalendar.HOUR,0);
        calendar.set(GregorianCalendar.MINUTE,0);
        calendar.set(GregorianCalendar.SECOND,0);
        calendar.set(GregorianCalendar.MILLISECOND,0);

        int dayOfWeek = calendar.get(GregorianCalendar.DAY_OF_WEEK);

        String[] daysOfWeek = new String[] {"","So.","Mo.", "Di.","Mi.","Do.","Fr.","Sa."};

        dateString +=daysOfWeek[dayOfWeek] + ", ";

        if(day < 10) {
            dateString+="0";
        }
        dateString+=Integer.toString(day)+".";
        if(month < 10) {
            dateString+="0";
        }
        dateString+=Integer.toString(month)+".";
        dateString+=Integer.toString(year);
        return dateString;
    }
}
