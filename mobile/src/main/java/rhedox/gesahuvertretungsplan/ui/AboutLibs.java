package rhedox.gesahuvertretungsplan.ui;

import android.content.Context;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import rhedox.gesahuvertretungsplan.R;

/**
 * Created by Robin on 10.07.2015.
 */
public class AboutLibs {
    private AboutLibs() {

    }

    public static void start(Context context, boolean darkTheme) {
        new LibsBuilder()
                .withFields(R.string.class.getFields())
                .withAboutAppName(context.getResources().getString(R.string.app_name))
                .withActivityTitle("Über")
                .withAboutIconShown(true)
                .withAboutVersionShown(true)
                .withAboutDescription("Zeigt den <b>Gesahu Vertretungsplan</b> in einem für Smartphones optimierten Layout an.<br>Entwickelt von Robin Kertels<br>Feedback von Felix Bastian<br><i>Wollen unbedingt erwähnt werden: Jonas Dietz, Heidi Meyer, Robin Möbus</i>")
                .withVersionShown(true)
                .withActivityStyle(darkTheme ? Libs.ActivityStyle.DARK : Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .withLibraries("AppCompat","MaterialDesignIcons","ACRA")
                .start(context);
    }
}
