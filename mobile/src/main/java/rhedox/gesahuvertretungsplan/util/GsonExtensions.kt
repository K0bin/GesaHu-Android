package rhedox.gesahuvertretungsplan.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer

/**
 * Created by robin on 16.01.2017.
 */
inline fun <reified T: Any>GsonBuilder.registerTypeAdapter(deserializer: JsonDeserializer<T>): GsonBuilder {
    return this.registerTypeAdapter(T::class.java, deserializer)
}