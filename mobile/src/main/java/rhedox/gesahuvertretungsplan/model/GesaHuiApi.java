package rhedox.gesahuvertretungsplan.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by robin on 28.09.2016.
 */

public interface GesaHuiApi {
	@GET("/home/mobil/appscripts/getboards.php")
	Call<Boards> boards(@Query("username") String username, @Query("pw") String password);

	@GET("/home/mobil/appscripts/getvplan.php")
	Call<SubstitutesList> substitutes(@Query("d") int day, @Query("m") int month, @Query("y") int year);
}
