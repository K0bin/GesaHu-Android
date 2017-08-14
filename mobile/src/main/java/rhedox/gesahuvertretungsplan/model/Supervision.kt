package rhedox.gesahuvertretungsplan.model

/**
 * Created by robin on 01.08.17.
 */
data class Supervision(val time: String, val teacher: String, val substitute: String, val location: String, val isRelevant: Boolean, val id: Long? = null)