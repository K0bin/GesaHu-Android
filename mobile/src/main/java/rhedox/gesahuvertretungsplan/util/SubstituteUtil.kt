@file:JvmName("")

package rhedox.gesahuvertretungsplan.util

import rhedox.gesahuvertretungsplan.model.database.entity.Substitute

/**
 * Created by robin on 19.01.2017.
 */
/**
 * Returns a layoutManager that only contains relevant substitutes
 * @param removeDoubles whether or not it should also remove redundant entries
 */
fun List<Substitute>.filterRelevant(removeDoubles: Boolean = false): List<Substitute> {
    val list = mutableListOf<Substitute>()
    for (substitute in this) {
        if(substitute.isRelevant && (!removeDoubles || !list.contains(substitute)))
            list.add(substitute)
    }

    return list
}

/**
 * Counts the amount of relevant substitutes on the given layoutManager
 * @return the amount of relevant substitutes
 */
fun List<Substitute>.countRelevant(): Int {
    var count = 0
    for (substitute in this) {
        if(substitute.isRelevant)
            count++
    }
    return count
}

/**
 * Returns a new list without redundant entries
 */
fun List<Substitute>.removeDoubles(): List<Substitute> {
    val list = mutableListOf<Substitute>()
    for (substitute in this) {
        if(!list.contains(substitute))
            list.add(substitute)
    }
    return list
}