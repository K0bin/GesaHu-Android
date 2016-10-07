package rhedox.gesahuvertretungsplan.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

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
}