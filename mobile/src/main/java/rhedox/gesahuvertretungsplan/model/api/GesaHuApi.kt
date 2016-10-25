package rhedox.gesahuvertretungsplan.model.api

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
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.model.api.QueryDate
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.model.SubstitutesList
import rhedox.gesahuvertretungsplan.model.api.json.LocalDateDeserializer
import rhedox.gesahuvertretungsplan.model.api.json.SubstituteDeserializer

/**
 * Created by robin on 01.10.2016.
 */
interface GesaHuApi {
    @GET("/home/mobil/appscripts/getboards.php")
    fun boards(@Query("username") username: String, @Query("pw") password: String): Call<List<Board>>

    @GET("/home/mobil/appscripts/getvplan.php")
    fun substitutes(@Query("day") date: QueryDate, @Query("username") username: String): Call<SubstitutesList>

    @GET("/home/mobil/appscripts/getvplan.php")
    fun substitutes(@Query("day") date: QueryDate): Call<SubstitutesList>

    companion object {
        fun create(context: Context): GesaHuApi {
            //Init Retrofit
            val builder = OkHttpClient.Builder()

            if (BuildConfig.DEBUG)
                builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))

            val gson = GsonBuilder()
                    .registerTypeAdapter(Substitute::class.java, SubstituteDeserializer(context))
                    .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
                    .create()

            val client = builder.build()

            val retrofit = Retrofit.Builder().baseUrl("http://gesahui.de").addConverterFactory(GsonConverterFactory.create(gson)).client(client).build()

            return retrofit.create(GesaHuApi::class.java)
        }
    }
}