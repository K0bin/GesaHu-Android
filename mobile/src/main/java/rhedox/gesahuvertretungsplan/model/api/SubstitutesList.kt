package rhedox.gesahuvertretungsplan.model.api

import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.model.Supervision
import rhedox.gesahuvertretungsplan.model.database.Announcement

/**
 * Created by robin on 01.10.2016.
 */

data class SubstitutesList(val announcement: Announcement, val substitutes: List<Substitute>, val date: LocalDate, val supervisions: List<Supervision>) {
    val hasSubstitutes: Boolean
        @JvmName("hasSubstitutes")
        get() = substitutes.isNotEmpty();

    val hasAnnouncement: Boolean
        @JvmName("hasAnnouncement")
        get() = announcement.text.isNotEmpty() && announcement.text.trim() != "keine";

    val hasSupervisions: Boolean
        @JvmName("hasSupervisions")
        get() = supervisions.isNotEmpty()
}
