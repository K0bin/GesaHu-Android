package rhedox.gesahuvertretungsplan.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.joda.time.LocalDate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.model.ShortNameResolver;
import rhedox.gesahuvertretungsplan.model.StudentInformation;

public class SubstitutesList {

    private ShortNameResolver shortNameResolver = new ShortNameResolver();

    private SubstitutesListLoader loader;
    private boolean isLoading = false;

    public void load(Context context, LocalDate date, StudentInformation studentInformation, OnDownloadedListener listener) {
        if(isNetworkConnected(context)) {
            isLoading = true;
            loader = new SubstitutesListLoader();
            loader.execute(new ReplacementsListLoaderArgs(date, studentInformation, listener));
        } else {
            if(listener != null)
                listener.onDownloadFailed(Error.NO_CONNECTION);
        }
    }

    // Check network connection
    private boolean isNetworkConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void stop() {
        if(loader != null && !loader.isCancelled())
            loader.cancel(true);

        isLoading = false;
    }

    public boolean isLoading() {
        return isLoading;
    }

    class SubstitutesListLoader extends AsyncTask<ReplacementsListLoaderArgs, Void, List<Substitute>> {
        private ReplacementsListLoaderArgs loaderArgs;

        public SubstitutesListLoader() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Substitute> doInBackground(ReplacementsListLoaderArgs... args) {
            loaderArgs = args[0];
            List<Substitute> substitutes = new ArrayList<Substitute>();

            try {
                String getString = "http://www.gesahui.de/intranet/view.php" + "?" + "d=" + String.valueOf(loaderArgs.getDate().getDayOfMonth()) + "&m=" + String.valueOf(loaderArgs.getDate().getMonthOfYear()) + "&y=" + String.valueOf(loaderArgs.getDate().getYear());
                URL url = new URL(getString);
                Log.d("Plan", "Downloading: " + getString);

                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                request.setRequestProperty("User-Agent", "Gesahu Replacementplan Android");
                request.connect();

                int responseCode = request.getResponseCode();
                Log.d("Plan","HTTP Responsecode: "+Integer.toString(responseCode));

                InputStreamReader stream = new InputStreamReader(request.getInputStream(), "windows-1252");
                BufferedReader in = new BufferedReader(stream);
                String line;

                //Parsing
                int cellIndex = 0;

                String lesson = "";
                String subject ="";
                String regularTeacher ="";
                String replacementTeacher ="";
                String room ="";
                String hint ="";

                while ((line = in.readLine()) != null) {
                    //Dopplete Leerzeichen, HTML Zeichen und Newline entfernen
                    line = line.replaceAll("&nbsp;|\u00a0|" + System.getProperty("line.separator"), "").replaceAll(" +", " ").trim();
                    if (line.length() > 3) {
                        //HTML Tag auslesen
                        String tag = line.substring(1, 4).trim().replaceAll(">", "");

                        //Tabellenzelle
                        if (tag.equals("td")) {
                            //Text bis Zellenindex ausschneiden
                            int textEnd = line.lastIndexOf("</td>");
                            String textCut = line.substring(0, textEnd).trim();
                            int textStart = textCut.lastIndexOf(">") + ">".length();
                            String text = textCut.substring(textStart).trim();

                            if (!text.equals("") && !text.equals(" ")) {
                                switch (cellIndex) {
                                    case 0:
                                            String lessonText = text.replaceAll(" ", "");

                                            if (!lesson.equals(lessonText) && !subject.equals("")) {
                                                Substitute substitute = new Substitute(lesson, subject, regularTeacher, replacementTeacher, room, hint, loaderArgs.getStudentInformation());
                                                substitutes.add(substitute);

                                                subject = "";
                                                regularTeacher = "";
                                                replacementTeacher = "";
                                                room = "";
                                                hint = "";
                                            }
                                        lesson = lessonText;
                                        break;
                                    case 1:
                                        if (!subject.equals("")) {
                                            //Subjectname isn't empty => add previous lesson
                                            Substitute substitute = new Substitute(lesson, subject, regularTeacher, replacementTeacher, room, hint, loaderArgs.getStudentInformation());
                                            substitutes.add(substitute);

                                            subject = "";
                                            regularTeacher = "";
                                            replacementTeacher = "";
                                            room = "";
                                            hint = "";
                                        }

                                        String[] parts = text.split(" ");
                                        if (parts.length > 1)
                                            text = shortNameResolver.resolveSubject(parts[0]) + " " + parts[1];

                                        subject += text;
                                        break;
                                    case 2:
                                        //Lehrerkürzel auflesen
                                        text = text.replaceAll(", |; |,+|;+|" + System.getProperty("line.separator"), ",");
                                        String[] regularTeachers = text.split(",");
                                        for (int i = 0; i < regularTeachers.length; i++) {
                                            if (!isEmpty(regularTeachers[i]) && !isEmpty(text)) {

                                                //Semikolon einfügen, wenn schon Lehrer hinzugefügt wurden
                                                if (!isEmpty(regularTeacher)) {
                                                    regularTeacher += "; ";
                                                }

                                                regularTeacher += shortNameResolver.resolveTeacher(regularTeachers[i].trim());
                                            }
                                        }

                                        break;
                                    case 3:
                                        //Lehrerkürzel auflesen
                                        text = text.replaceAll(", |; |,+|;+|" + System.getProperty("line.separator"), ",");
                                        String[] replacementTeachers = text.split(",");
                                        for (int i = 0; i < replacementTeachers.length; i++) {
                                            if (!isEmpty(replacementTeachers[i]) && !isEmpty(text)) {

                                                //Semikolon einfügen, wenn schon Lehrer hinzugefügt wurden
                                                if (!isEmpty(replacementTeacher)) {
                                                    replacementTeacher += "; ";
                                                }

                                                replacementTeacher += shortNameResolver.resolveTeacher(replacementTeachers[i].trim());
                                            }

                                        }
                                        break;
                                    case 4:
                                        room += text + " ";
                                        break;
                                    case 5:
                                        hint += text + " ";
                                        break;
                                }
                            }
                            cellIndex++;
                        }

                        //Tabellenzeilenende
                        if (tag.equals("/tr"))
                            cellIndex = 0;

                        //Tabellende
                        if (tag.equals("/ta"))
                            break;
                    }
                }

                if (!isEmpty(subject)) {
                    Substitute substitute = new Substitute(lesson, subject, regularTeacher, replacementTeacher, room, hint, loaderArgs.getStudentInformation());
                    substitutes.add(substitute);
                }

                if(substitutes.size() == 0) {
                    Substitute substitute = new Substitute("1-10", "Keine Vertretungsstunden", "Alles nach Plan", "", "", "", new StudentInformation("",""));
                    substitutes.add(substitute);
                }

                stream.close();
                in.close();

            } catch (IOException e) {
                Log.d("VPException", "" + e.getMessage());
            }

            return substitutes;
        }

        @Override
        protected void onPostExecute(List<Substitute> substitutes) {
            super.onPostExecute(substitutes);

            SubstitutesList.this.isLoading = false;

            if(loaderArgs.getCallback() != null)
                if(substitutes != null && substitutes.size() > 0) {
                        loaderArgs.getCallback().onDownloaded(substitutes);
                } else {
                    loaderArgs.getCallback().onDownloadFailed(Error.NO_DATA);
                }
        }

        private boolean isEmpty(String string) {
            return string == null || string.isEmpty() || string.trim().length() == 0;
        }
    }
}
