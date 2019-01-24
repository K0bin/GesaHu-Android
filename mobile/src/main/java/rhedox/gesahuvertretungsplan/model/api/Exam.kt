package rhedox.gesahuvertretungsplan.model.api

import org.joda.time.Duration
import org.joda.time.LocalDate
import org.joda.time.LocalTime

/**
 * Created by robin on 29.12.2016.
 */
data class Exam(val date: LocalDate,
                val subject: String,
                val course: String,
                val recorder: String,
                val examiner: String,
                val examinee: String,
                val room: String,
                val chair: String,
                val time: LocalTime,
                val duration: Duration?,
                val allowAudience: Boolean)
