package rhedox.gesahuvertretungsplan.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.LibsConfiguration;
import com.mikepenz.aboutlibraries.entity.Library;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment;

/**
 * Created by Robin on 10.07.2015.
 */
public final class AboutLibs {
    private AboutLibs() {}

    public static void start(final Context context) {
	    boolean isDarkThemeEnabled = (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
	    boolean isAmoledBlackEnabled = prefs.getBoolean(PreferenceFragment.PREF_AMOLED, false);

        new LibsBuilder()
                .withFields(R.string.class.getFields())
                .withAboutAppName(context.getResources().getString(R.string.app_name))
                .withActivityTitle(context.getString(R.string.title_activity_about))
                .withAboutIconShown(true)
                .withAboutVersionShown(true)
                .withAboutDescription(Html.fromHtml(context.getString(R.string.about_text)).toString())
                .withVersionShown(true)
                .withActivityStyle(isDarkThemeEnabled ? Libs.ActivityStyle.DARK : Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .withActivityTheme(!isAmoledBlackEnabled ? R.style.GesahuTheme : R.style.GesahuThemeAmoled)
                .withLibraries("AppCompat", "MaterialDesignIcons")
                .withAboutSpecial2(context.getString(R.string.special2))
                .withAboutSpecial2Description(context.getString(R.string.special2_description))
                .withAboutSpecial1(context.getString(R.string.special1))
                .start(context);
    }
}
