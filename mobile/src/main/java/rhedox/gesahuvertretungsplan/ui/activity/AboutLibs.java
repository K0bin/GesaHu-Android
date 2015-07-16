package rhedox.gesahuvertretungsplan.ui.activity;

import android.content.Context;
import android.text.Html;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import rhedox.gesahuvertretungsplan.R;

/**
 * Created by Robin on 10.07.2015.
 */
public final class AboutLibs {
    private AboutLibs() {}

    public static void start(Context context, boolean darkTheme) {
        new LibsBuilder()
                .withFields(R.string.class.getFields())
                .withAboutAppName(context.getResources().getString(R.string.app_name))
                .withActivityTitle(context.getString(R.string.title_activity_about))
                .withAboutIconShown(true)
                .withAboutVersionShown(true)
                .withAboutDescription(Html.fromHtml(context.getString(R.string.about_text)).toString())
                .withVersionShown(true)
                .withActivityStyle(darkTheme ? Libs.ActivityStyle.DARK : Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .withLibraries("AppCompat", "MaterialDesignIcons", "ACRA", "Volley", "ChangelogLib")
                .start(context);
    }
}
