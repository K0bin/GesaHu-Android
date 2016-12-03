package rhedox.gesahuvertretungsplan.model

import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime

/**
 * Created by robin on 30.10.2016.
 */
val DateTime.unixTimeStamp: Int
    get() = (this.millis / 1000).toInt();

val LocalDate.unixTimeStamp: Int
    get() = this.toDateTime(LocalTime(0)).unixTimeStamp

fun localDateFromUnix(seconds: Int): LocalDate {
    return DateTime(seconds * 1000L).toLocalDate()
}
fun localDateFromUnix(seconds: Int?): LocalDate? {
    if(seconds == null || seconds == 0)
        return null
    else
        return DateTime(seconds * 1000L).toLocalDate()
}
fun dateTimeFromUnix(seconds: Int): DateTime {
    return DateTime(seconds * 1000L)
}
fun dateTimeFromUnix(seconds: Int?): DateTime? {
    if(seconds == null || seconds == 0)
        return null;
    else
        return DateTime(seconds * 1000L)
}