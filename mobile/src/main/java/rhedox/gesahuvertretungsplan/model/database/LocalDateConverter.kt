package rhedox.gesahuvertretungsplan.model.database

import androidx.room.TypeConverter
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.util.localDateFromUnix
import rhedox.gesahuvertretungsplan.util.unixTimeStamp

/**
 * Created by robin on 08.03.2018.
 */
object LocalDateConverter {
    @JvmStatic
    @TypeConverter
    fun toDate(value: Int): LocalDate? = if (value == 0) null else localDateFromUnix(value)

    @JvmStatic
    @TypeConverter
    fun dateToInt(date: LocalDate?): Int = date.unixTimeStamp
}