@file:JvmName("JodaTimeUtils")
package rhedox.gesahuvertretungsplan.util

import org.joda.time.DateTime
import org.joda.time.LocalDate

/**
 * Created by robin on 30.10.2016.
 */
val DateTime?.unixTimeStamp: Int
    get() = ((this?.millis ?: 0) / 1000).toInt()

val LocalDate?.unixTimeStamp: Int
    get() = this?.toDateTime(org.joda.time.LocalTime(0)).unixTimeStamp

fun localDateFromUnix(seconds: Int): LocalDate {
    return DateTime(seconds * 1000L).toLocalDate()
}
fun localDateFromUnix(seconds: Int?): LocalDate? {
    return if(seconds == null || seconds == 0)
        null
    else
        DateTime(seconds * 1000L).toLocalDate()
}

fun dateTimeFromUnix(seconds: Int): DateTime {
    return DateTime(seconds * 1000L)
}
fun dateTimeFromUnix(seconds: Int?): DateTime? {
    return if (seconds == null || seconds == 0)
        null
    else
        DateTime(seconds * 1000L)
}