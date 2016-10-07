package rhedox.gesahuvertretungsplan.model

/**
 * Created by robin on 01.10.2016.
 */
data class Substitute(val lessonBegin: Int, val lessonEnd: Int, val subject: String, val course: String, val teacher: String, val substitute: String, val room: String, val hint: String, val isRelevant: Boolean) {
    val lesson: String
        get() = lessonBegin.toString() + "-" + lessonEnd.toString();

    val title: String
        get() = course + " " + subject;
}