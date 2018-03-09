@file:JvmName("")

package rhedox.gesahuvertretungsplan.util

import rhedox.gesahuvertretungsplan.model.database.entity.Substitute

/**
 * Created by robin on 19.01.2017.
 */
/**
 * Returns a layoutManager that only contains relevant substitutes
 * @param substitutes the substitutes to put into the layoutManager
 * @param removeDoubles whether or not it should also remove redundant entries
 */
fun List<Substitute>.filterRelevant(removeDoubles: Boolean = false): List<Substitute> {
    val list = mutableListOf<Substitute>();
    for (substitute in this) {
        if(substitute.isRelevant && (!removeDoubles || !list.contains(substitute)))
            list.add(substitute);
    }

    return list;
}

/**
 * Counts the amount of relevant substitutes on the given layoutManager
 * @param substitutes the substitutes to put into the layoutManager
 * @return the amount of relevant substitutes
 */
fun List<Substitute>.countRelevant(): Int {
    var count = 0;
    for (substitute in this) {
        if(substitute.isRelevant)
            count++;
    }
    return count;
}

/**
 * Returns a new list without redundant entries
 * @param substitutes the substitutes to put into the layoutManager
 */
fun List<Substitute>.removeDoubles(): List<Substitute> {
    val list = mutableListOf<Substitute>();
    for (substitute in this) {
        if(!list.contains(substitute))
            list.add(substitute);
    }
    return list;
}