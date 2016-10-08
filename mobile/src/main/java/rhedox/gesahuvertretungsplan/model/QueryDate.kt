package rhedox.gesahuvertretungsplan.model

import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormatterBuilder

/**
 * Created by robin on 01.10.2016.
 */
class QueryDate(date: LocalDate) {
    private val dateString: String;

    init {
        dateString = date.toString("yyyy-MM-dd");
    }

    override fun toString(): String = dateString;
}

fun LocalDate.toQueryDate(): QueryDate {
    return QueryDate(this);
}