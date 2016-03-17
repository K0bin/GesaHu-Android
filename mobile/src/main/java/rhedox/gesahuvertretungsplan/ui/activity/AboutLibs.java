package rhedox.gesahuvertretungsplan.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
        new LibsBuilder()
                .withFields(R.string.class.getFields())
                .withAboutAppName(context.getResources().getString(R.string.app_name))
                .withActivityTitle(context.getString(R.string.title_activity_about))
                .withAboutIconShown(true)
                .withAboutVersionShown(true)
                .withAboutDescription(Html.fromHtml(context.getString(R.string.about_text)).toString())
                .withVersionShown(true)
                .withActivityStyle(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES ? Libs.ActivityStyle.DARK : Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .withActivityTheme(R.style.GesahuTheme)
                .withLibraries("AppCompat", "MaterialDesignIcons")
                .withAboutSpecial3(context.getString(R.string.special3))
                .withAboutSpecial2(context.getString(R.string.special2))
                .withAboutSpecial2Description(context.getString(R.string.special2_description))
                .withAboutSpecial1(context.getString(R.string.special1))
                .withListener(new LibsConfiguration.LibsListener() {
                    private int goenns = 0;
                    private SharedPreferences prefs;
                    private Toast toast;

                    @Override
                    public void onIconClicked(View v) {

                    }

                    @Override
                    public boolean onLibraryAuthorClicked(View v, Library library) {
                        return false;
                    }

                    @Override
                    public boolean onLibraryContentClicked(View v, Library library) {
                        return false;
                    }

                    @Override
                    public boolean onLibraryBottomClicked(View v, Library library) {
                        return false;
                    }

                    @Override
                    public boolean onExtraClicked(View v, Libs.SpecialButton specialButton) {
                        if(specialButton == null)
                            return false;

                        Log.d("AboutLibs: Special", specialButton.name());

                        if (specialButton.name() == "SPECIAL3") {
                            goenns++;

                            if(toast == null)
                                toast = Toast.makeText(context.getApplicationContext(), String.format(context.getString(R.string.special3_tap), goenns), Toast.LENGTH_SHORT);
                            else
                                toast.setText(String.format(context.getString(R.string.special3_tap), goenns));

                            if (goenns % 15 == 0) {
                                if (prefs == null) {
                                    prefs = PreferenceManager.getDefaultSharedPreferences(context);
                                }
                                boolean goennung = prefs.getBoolean(PreferenceFragment.PREF_SPECIAL_MODE, false);

                                boolean special = (goenns / 15) % 2 != 0 && !goennung;
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean(PreferenceFragment.PREF_SPECIAL_MODE, special);
                                editor.apply();

                                toast.setText(context.getString(special ? R.string.special_mode_activated : R.string.special_mode_deactivated));
                            }
                            toast.show();
                            return true;
                        } else if (specialButton.name() == "SPECIAL1") {
                            String url = "http://www.gesahui.de";
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            return true;
                        }

                        return false;
                    }

                    @Override
                    public boolean onIconLongClicked(View v) {
                        return false;
                    }

                    @Override
                    public boolean onLibraryAuthorLongClicked(View v, Library library) {
                        return false;
                    }

                    @Override
                    public boolean onLibraryContentLongClicked(View v, Library library) {
                        return false;
                    }

                    @Override
                    public boolean onLibraryBottomLongClicked(View v, Library library) {
                        return false;
                    }
                })
                .start(context);
    }
}
