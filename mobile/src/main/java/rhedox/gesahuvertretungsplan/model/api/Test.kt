package rhedox.gesahuvertretungsplan.model.api

import org.joda.time.LocalDate

/**
 * Created by robin on 29.12.2016.
 */
data class Test(val remark: String,
                val date: LocalDate,
                val subject: String,
                val course: String,
                val year: Int,
                val teacher: String,
                val lessonStart: Int,
                val duration: Int)
