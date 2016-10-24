package rhedox.gesahuvertretungsplan.model

import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.joda.time.DurationFieldType
import org.joda.time.LocalDate

/**
 * Created by Robin on 02.03.2015.
 */
object SchoolWeek {

    /**
     * Finds the next monday
     * @param date the date
     * *
     * @return next monday relative to the given day
     */
    @JvmStatic
    fun nextDate(date: LocalDate): LocalDate {
        if (date.dayOfWeek == DateTimeConstants.SUNDAY)
            return date.withFieldAdded(DurationFieldType.days(), 1)
        else if (date.dayOfWeek == DateTimeConstants.SATURDAY)
            return date.withFieldAdded(DurationFieldType.days(), 2)

        return date
    }

    /**
     * Finds the next school day
     * Skips weekends and adds a day if it's later than 18:00
     * @param dateTime the date and time
     * *
     * @return next school day relative to the given date time
     */
    @JvmStatic
    fun nextFrom(dateTime: DateTime = DateTime.now()): LocalDate {
        val date = dateTime.toLocalDate()
        if (dateTime.hourOfDay > 18)
            return SchoolWeek.nextDate(date.withFieldAdded(DurationFieldType.days(), 1))
        else
            return SchoolWeek.nextDate(date)
    }

    /**
     * Finds the next school day
     * Skips weekends and adds a day if it's later than 18:00
     * @return next school day
     */
    @JvmStatic
    fun nextFromNow(): LocalDate {
        return nextFrom(DateTime.now())
    }
}

val LocalDate.dayOfWeekIndex: Int
    get() = this.dayOfWeek - DateTimeConstants.MONDAY;