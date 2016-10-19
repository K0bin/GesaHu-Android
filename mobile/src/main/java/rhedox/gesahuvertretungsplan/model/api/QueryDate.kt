package rhedox.gesahuvertretungsplan.model.api

import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormatterBuilder

/**
 * Created by robin on 01.10.2016.
 */
class QueryDate private constructor(private val date: String) {
    constructor(date: LocalDate) : this(date.toString("yyyy-MM-dd")) {
    }

    constructor(year: Int, month: Int, day: Int) : this("$year-$month-$day") {

    }

    override fun toString(): String = date;
}

fun LocalDate.toQueryDate(): QueryDate {
    return QueryDate(this);
}