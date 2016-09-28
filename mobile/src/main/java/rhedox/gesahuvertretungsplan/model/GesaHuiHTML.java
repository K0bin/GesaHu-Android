package rhedox.gesahuvertretungsplan.model;

import org.joda.time.LocalDate;

import retrofit2.*;
import retrofit2.http.*;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;

/**
 * Created by Robin on 17.02.2016.
 */
public interface GesaHuiHtml {
    @GET("/home/view.php")
    Call<SubstitutesList> getSubstitutesList(@Query("y") int year, @Query("m") int month, @Query("d") int day);
}
