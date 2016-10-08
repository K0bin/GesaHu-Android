package rhedox.gesahuvertretungsplan.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import org.joda.time.LocalDate
import java.lang.reflect.Type

/**
 * Created by robin on 01.10.2016.
 */

data class SubstitutesList(@SerializedName("Hinweise") val announcement: String, @SerializedName("Stunden") val substitutes: List<Substitute>, @SerializedName("Datum") val date: LocalDate) {
    val hasSubstitutes: Boolean
        @JvmName("hasSubstitutes")
        get() = substitutes.size > 0;

    val hasAnnouncement: Boolean
        @JvmName("hasAnnouncement")
        get() = announcement.trim().length > 0 && announcement.trim() != "keine";

    companion object {
        /**
         * Returns a list that only contains relevant substitutes
         * @param substitutes the substitutes to put into the list
         * @param removeDoubles whether or not it should also remove redundant entries
         */
        @JvmStatic
        fun filterRelevant(substitutes: List<Substitute>, removeDoubles: Boolean = false): List<Substitute> {
            val list = mutableListOf<Substitute>();
            for (substitute in substitutes) {
                if(substitute.isRelevant && (!removeDoubles || !list.contains(substitute)))
                    list.add(substitute);
            }

            return list;
        }


        /**
         * Returns a list that is sorted so that relevant entries are on top
         * @param substitutes the substitutes to put into the list
         */
        @JvmStatic
        fun sort(substitutes: List<Substitute>): List<Substitute> {
            return substitutes.sorted();
        }


        /**
         * Counts the amount of relevant substitutes on the given list
         * @param substitutes the substitutes to put into the list
         * @return the amount of relevant substitutes
         */
        @JvmStatic
        fun countRelevant(substitutes: List<Substitute>): Int {
            var count = 0;
            for (substitute in substitutes) {
                if(substitute.isRelevant)
                    count++;
            }
            return count;
        }

        /**
         * Returns a new list without redundant entries
         * @param substitutes the substitutes to put into the list
         */
        @JvmStatic
        fun removeDoubles(substitutes: List<Substitute>): List<Substitute> {
            val list = mutableListOf<Substitute>();
            for (substitute in substitutes) {
                if(!list.contains(substitute))
                    list.add(substitute);
            }
            return list;
        }
    }
}
