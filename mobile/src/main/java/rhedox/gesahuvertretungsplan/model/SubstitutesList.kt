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
         * Returns a layoutManager that only contains relevant substitutes
         * @param substitutes the substitutes to put into the layoutManager
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
         * Returns a layoutManager that is sorted so that relevant entries are on top
         * @param substitutes the substitutes to put into the layoutManager
         */
        @JvmStatic
        fun sort(substitutes: List<Substitute>): List<Substitute> {
            return substitutes.sorted();
        }


        /**
         * Counts the amount of relevant substitutes on the given layoutManager
         * @param substitutes the substitutes to put into the layoutManager
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
         * Returns a new layoutManager without redundant entries
         * @param substitutes the substitutes to put into the layoutManager
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
