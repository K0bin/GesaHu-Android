package rhedox.gesahuvertretungsplan.model.database

import android.arch.persistence.room.TypeConverter
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.util.localDateFromUnix
import rhedox.gesahuvertretungsplan.util.unixTimeStamp

/**
 * Created by robin on 08.03.2018.
 */
object TypeConverters {
    @JvmStatic
    @TypeConverter
    fun toDate(value: Int): LocalDate = localDateFromUnix(value)

    @JvmStatic
    @TypeConverter
    fun toInt(date: LocalDate): Int = date.unixTimeStamp
}