package rhedox.gesahuvertretungsplan.model;

import retrofit2.*;
import retrofit2.http.*;

/**
 * Created by Robin on 17.02.2016.
 */
public interface GesaHuiHtml {
    @GET("/home/view.php")
    Call<SubstitutesList_old> getSubstitutesList(@Query("y") int year, @Query("m") int month, @Query("d") int day);
}
