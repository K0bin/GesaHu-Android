package rhedox.gesahuvertretungsplan.model.api

import android.content.Context
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormatterBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import rhedox.gesahuvertretungsplan.BuildConfig
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.model.Event
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.model.SubstitutesList
import rhedox.gesahuvertretungsplan.model.api.json.DateTimeDeserializer
import rhedox.gesahuvertretungsplan.model.api.json.LocalDateDeserializer
import rhedox.gesahuvertretungsplan.model.api.json.SubstituteDeserializer

/**
 * Created by robin on 01.10.2016.
 */
class GesaHu(context: Context) {
    private val api: GesaHuApi;

    init {
        //Init Retrofit
        val builder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG)
            builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))

        val gson = GsonBuilder()
                .registerTypeAdapter(Substitute::class.java, SubstituteDeserializer(context))
                .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
                .registerTypeAdapter(DateTime::class.java, DateTimeDeserializer())
                .create()

        val client = builder.build()

        val retrofit = Retrofit.Builder().baseUrl("http://gesahui.de/home/mobil/appscripts/").addConverterFactory(GsonConverterFactory.create(gson)).client(client).build()

        api = retrofit.create(GesaHuApi::class.java)
    }

    private val dateTimeFormatter = DateTimeFormatterBuilder()
            .appendYear(4,4)
            .appendLiteral('-')
            .appendMonthOfYear(2)
            .appendLiteral('-')
            .appendDayOfMonth(2)
            .appendLiteral(' ')
            .appendHourOfDay(2)
            .appendLiteral(':')
            .appendMinuteOfHour(2)
            .appendLiteral(':')
            .appendSecondOfMinute(2)
            .toFormatter()

    private val localDateFormatter = DateTimeFormatterBuilder()
            .appendYear(4,4)
            .appendLiteral('-')
            .appendMonthOfYear(2)
            .appendLiteral('-')
            .appendDayOfMonth(2)
            .toFormatter()

    fun boards(username: String, password: String): Call<List<Board>> {
        return api.boards(username, password)
    }

    fun publicSubstitutes(username: String, password: String, date: LocalDate): Call<SubstitutesList> {
        return api.substitutes(date.toString(localDateFormatter))
    }

    fun substitutes(username: String, date: LocalDate): Call<SubstitutesList> {
        return api.substitutes(username, date.toString(localDateFormatter))
    }

    fun events(username: String, password: String, begin: DateTime, end: DateTime): Call<List<Event>> {
        return api.events(username, password, begin.toString(dateTimeFormatter), end.toString(dateTimeFormatter))
    }

    private interface GesaHuApi {
        @GET("getboards.php")
        fun boards(@Query("username") username: String, @Query("pw") password: String): Call<List<Board>>

        @GET("getvplan.php")
        fun substitutes(@Query("username") username: String, @Query("day") date: String): Call<SubstitutesList>

        @GET("getvplan.php")
        fun substitutes(@Query("day") date: String): Call<SubstitutesList>

        @GET("getdates.php")
        fun events(@Query("username") username: String, @Query("pw") password: String, @Query("start") begin: String, @Query("end") end: String): Call<List<Event>>
    }
}
