package rhedox.gesahuvertretungsplan.service;

import android.content.Intent;

import com.google.android.apps.dashclock.api.ExtensionData;

import org.joda.time.LocalDate;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.api.GesaHuApi;
import rhedox.gesahuvertretungsplan.model.api.QueryDate;
import rhedox.gesahuvertretungsplan.model.SchoolWeek;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity1;

/**
 * Created by Robin on 19.04.2015.
 */
public class DashClockExtension extends com.google.android.apps.dashclock.api.DashClockExtension {

    private GesaHuApi gesahui;

    @Override
    protected void onUpdateData(int reason) {

	    LocalDate date = SchoolWeek.nextFromNow();

	    //Init retro fit for pulling the data
	    gesahui = GesaHuApi.Companion.create(this);

	    Call<SubstitutesList> call = gesahui.substitutes(new QueryDate(date));

	    try {
		    Response<SubstitutesList> response = call.execute();

		    if (response == null || !response.isSuccessful() || response.body() == null)
			    return;

		    List<Substitute> substitutes = response.body().getSubstitutes();

		    List<Substitute> important = SubstitutesList.filterRelevant(substitutes, true);
		    int count = important.size();

		    String body = "";
		    for (Substitute substitute : important) {
			    String title = "";

			    switch ((int)substitute.getKind()) {
				    case (int)Substitute.KIND_SUBSTITUTE:
					    title = getString(R.string.substitute);
					    break;

				    case (int)Substitute.KIND_ROOM_CHANGE:
					    title = getString(R.string.roomchange);
					    break;

				    case (int)Substitute.KIND_DROPPED:
					    title = getString(R.string.dropped);
					    break;

				    case (int)Substitute.KIND_TEST:
					    title = getString(R.string.test);
					    break;
			    }

			    if (!"".equals(body))
				    body += System.getProperty("line.separator");

			    body += String.format(getString(R.string.notification_summary), title, substitute.getLessonText());
		    }

		    if (count > 0) {
			    publishUpdate(new ExtensionData()
					    .visible(true)
					    .icon(R.drawable.ic_notification)
					    .status(Integer.toString(count))
					    .expandedTitle(getString(R.string.app_name))
					    .expandedBody(body)
					    .clickIntent(new Intent(this, MainActivity1.class)));
		    }
		    else
			    publishUpdate(new ExtensionData()
					    .visible(false));

	    } catch (IOException e) { }
    }
}
