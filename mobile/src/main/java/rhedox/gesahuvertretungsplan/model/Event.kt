package rhedox.gesahuvertretungsplan.model

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

/**
 * Created by robin on 27.12.2016.
 */
data class Event(@SerializedName("Terminbeginn") val begin: DateTime,
                 @SerializedName("Terminende") val end: DateTime,
                 @SerializedName("ganztags") val wholeDay: Boolean,
                 @SerializedName("Ort") val location: String,
                 @SerializedName("Beschreibung") val description: String,
                 @SerializedName("Ersteller") val author: String,
                 @SerializedName("Category") val category: String)