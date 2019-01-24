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
import rhedox.gesahuvertretungsplan.model.api.deserializer.*
import rhedox.gesahuvertretungsplan.util.registerTypeAdapter

/**
 * Created by robin on 01.10.2016.
 */
class GesaHu(context: Context) {
    private val api: GesaHuApi

    init {
        //Init Retrofit
        val builder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG)
            builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))

        val gson = GsonBuilder()
                .registerTypeAdapter(SubstitutesListDeserializer(context))
                .registerTypeAdapter(TestDeserializer(context))
                .registerTypeAdapter(ExamDeserializer(context))
                .registerTypeAdapter(DateTime::class.java, DateTimeDeserializer())
                .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
                .registerTypeAdapter(LocalTimeDeserializer())
                .registerTypeAdapter(BoardDeserializer())
                .create()

        val client = builder.build()

        val retrofit = Retrofit.Builder().baseUrl("https://www.gesahui.de/home/mobil/appscripts/").addConverterFactory(GsonConverterFactory.create(gson)).client(client).build()

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

    fun boards(username: String, password: String): Call<List<BoardInfo>> {
        return api.boards(username, password)
    }

    fun boardNames(username: String, password: String): Call<List<BoardName>> {
        return api.boardNames(username, password)
    }

    fun publicSubstitutes(date: LocalDate): Call<SubstitutesList> {
        return api.substitutes(date.toString(localDateFormatter))
    }

    fun substitutes(username: String, date: LocalDate): Call<SubstitutesList> {
        return api.substitutes(username, date.toString(localDateFormatter))
    }

    fun events(username: String, password: String, begin: DateTime, end: DateTime): Call<List<Event>> {
        return api.events(username, password, begin.toString(dateTimeFormatter), end.toString(dateTimeFormatter))
    }

    fun tests(username: String, begin: DateTime): Call<List<Test>> {
        return api.tests(username, begin.toString(dateTimeFormatter))
    }

    fun exams(username: String, begin: DateTime): Call<List<Exam>> {
        return api.exams(username, begin.toString(dateTimeFormatter))
    }

    private interface GesaHuApi {
        @GET("getboards.php")
        fun boardNames(@Query("username") username: String, @Query("pw") password: String): Call<List<BoardName>>

        @GET("getvplan.php")
        fun substitutes(@Query("username") username: String, @Query("day") date: String): Call<SubstitutesList>

        @GET("getvplan.php")
        fun substitutes(@Query("day") date: String): Call<SubstitutesList>

        @GET("getdates.php")
        fun events(@Query("username") username: String, @Query("pw") password: String, @Query("start") begin: String, @Query("end") end: String): Call<List<Event>>

        @GET("gettestdates.php")
        fun tests(@Query("username") username: String, @Query("beginn") begin: String): Call<List<Test>>

        @GET("getexamdates.php")
        fun exams(@Query("username") username: String, @Query("beginn") begin: String): Call<List<Exam>>

        @GET("getkursboardinfos.php")
        fun boards(@Query("username") username: String, @Query("pw") password: String): Call<List<BoardInfo>>
    }
}
