package rhedox.gesahuvertretungsplan.model

import org.joda.time.*

/**
 * Created by Robin on 02.03.2015.
 */
object SchoolWeek {
    val startHours = intArrayOf(  8,  8,  9, 10, 11, 12, 14, 14, 15, 16)
    val startMinutes = intArrayOf(0, 50, 50, 40, 40, 30,  0, 45, 45, 30)
    val endHours = intArrayOf(    8,  9, 10, 11, 12, 13, 14, 15, 16, 17)
    val endMinutes = intArrayOf( 45, 35, 35, 25, 35, 15, 45, 30, 25, 15)

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

    @JvmStatic
    fun lessonStart(lessonIndex: Int): LocalTime {
        return LocalTime(startHours[lessonIndex - 1], startMinutes[lessonIndex - 1])
    }
    @JvmStatic
    fun lessonEnd(lessonIndex: Int): LocalTime {
        return LocalTime(endHours[lessonIndex - 1], endMinutes[lessonIndex - 1])
    }
}

val LocalDate.dayOfWeekIndex: Int
    get() = this.dayOfWeek - DateTimeConstants.MONDAY;