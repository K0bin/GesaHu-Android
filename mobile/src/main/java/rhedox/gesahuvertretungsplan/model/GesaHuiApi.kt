package rhedox.gesahuvertretungsplan.model

import android.content.Context
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.joda.time.LocalDate
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import rhedox.gesahuvertretungsplan.BuildConfig

/**
 * Created by robin on 01.10.2016.
 */
interface GesaHuiApi {
    @GET("/home/mobil/appscripts/getboards.php")
    fun boards(@Query("username") username: String, @Query("pw") password: String): Call<Boards>

    @GET("/home/mobil/appscripts/getvplan.php")
    fun substitutesForStudent(@Query("day") date: QueryDate, @Query("klasse") _class: String): Call<SubstitutesList>

    @GET("/home/mobil/appscripts/getvplan.php")
    fun substitutesForTeacher(@Query("day") date: QueryDate, @Query("kuerzel") teacher: String): Call<SubstitutesList>

    @GET("/home/mobil/appscripts/getvplan.php")
    fun substitutes(@Query("day") date: QueryDate): Call<SubstitutesList>

    companion object {
        fun create(context: Context): GesaHuiApi {
            //Init Retrofit
            val builder = OkHttpClient.Builder()

            if (BuildConfig.DEBUG)
                builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

            val gson = GsonBuilder()
                    .registerTypeAdapter(Substitute::class.java, Substitute.Deserializer(context))
                    .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
                    .create()

            val client = builder.build()

            val retrofit = Retrofit.Builder().baseUrl("http://gesahui.de").addConverterFactory(GsonConverterFactory.create(gson)).client(client).build()

            return retrofit.create(GesaHuiApi::class.java)
        }
    }
}