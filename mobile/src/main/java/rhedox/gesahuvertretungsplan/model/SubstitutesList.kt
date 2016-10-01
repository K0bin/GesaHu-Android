package rhedox.gesahuvertretungsplan.model

/**
 * Created by robin on 01.10.2016.
 */

data class SubstitutesList(val announcement: String, val substitutes: List<Substitute>) {
    val hasSubstitutes: Boolean
        get() = substitutes.size > 0;

    val hasAnnouncement: Boolean
        get() = announcement.trim().length > 0 && announcement.trim() != "keine";
}
