package rhedox.gesahuvertretungsplan;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDate;

import java.util.GregorianCalendar;

/**
 * Created by Robin on 02.03.2015.
 */
public final class SchoolWeek {
    private SchoolWeek() {}

    public static LocalDate next(LocalDate date){
        if(date.getDayOfWeek() == DateTimeConstants.SUNDAY)
            return date.withFieldAdded(DurationFieldType.days(), 1);
        else if (date.getDayOfWeek() == DateTimeConstants.SATURDAY)
            return date.withFieldAdded(DurationFieldType.days(), 2);

        return date;
    }

    public static LocalDate next(){
        DateTime dateTime = DateTime.now();

        LocalDate date = dateTime.toLocalDate();
        if(dateTime.getHourOfDay() > 18)
            return SchoolWeek.next(date.withFieldAdded(DurationFieldType.days(), 1));
        else
            return SchoolWeek.next(date);
    }
}
