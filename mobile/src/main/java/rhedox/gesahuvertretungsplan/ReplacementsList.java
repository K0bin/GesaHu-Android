package rhedox.gesahuvertretungsplan;

import android.content.Context;
import android.content.Loader;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReplacementsList {

    private List<Replacement> replacements = new ArrayList<Replacement>();

    private ShortNameResolver shortNameResolver = new ShortNameResolver();

    private ReplacementslistLoader loader;

    public ReplacementsList() {
    }

    public void load(Context context, Date date, StudentInformation studentInformation, OnDownloadedListener listener) {
        replacements.clear();
        loader = new ReplacementslistLoader(context);
        loader.execute(new ReplacementsListLoaderArgs(date, studentInformation, context, listener));
    }

    public void stop() {
        loader.cancel(true);
    }

    public List<Replacement> getReplacements() {
        return replacements;
    }

    class ReplacementslistLoader extends AsyncTask<ReplacementsListLoaderArgs, Void, List<Replacement>> {
        private ReplacementsListLoaderArgs loaderArgs;
        private Context context;

        public ReplacementslistLoader(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Replacement> doInBackground(ReplacementsListLoaderArgs... args) {
            loaderArgs = args[0];

            try {
                String getString = "http://www.gesahui.de/intranet/view.php" + "?" + "d=" + String.valueOf(loaderArgs.getDate().getDay()) + "&m=" + String.valueOf(loaderArgs.getDate().getMonth()) + "&y=" + String.valueOf(loaderArgs.getDate().getYear());
                URL url = new URL(getString);
                Log.d("Plan","Downloading: "+getString);


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
                                                Replacement replacement = new Replacement(lesson, subject, regularTeacher, replacementTeacher, room, hint, loaderArgs.getStudentInformation());
                                                replacements.add(replacement);

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
                                                Replacement replacement = new Replacement(lesson, subject, regularTeacher, replacementTeacher, room, hint, loaderArgs.getStudentInformation());
                                            replacements.add(replacement);

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
                    Replacement replacement = new Replacement(lesson, subject, regularTeacher, replacementTeacher, room, hint, loaderArgs.getStudentInformation());
                    replacements.add(replacement);
                }

                if(replacements.size() == 0) {
                    Replacement replacement = new Replacement("1-10", "Keine Vertretungsstunden", "Alles nach Plan", "", "", "", new StudentInformation("",""));
                    replacements.add(replacement);
                }

                stream.close();
                in.close();

            } catch (Exception e) {
                Log.d("VPException2", "" + e.getMessage());

                Replacement replacement = new Replacement("1-10", "Keine Vertretungsstunden", "Keine Daten", "Überprüfe deine Verbindung!", "", e.getMessage(), new StudentInformation("",""));
                replacements.add(replacement);
            }

            Log.d("Plan", "Plan loadToday!");

            return replacements;
        }

        @Override
        protected void onPostExecute(List<Replacement> Replacements) {
            super.onPostExecute(Replacements);
            Log.d("Plan","PostExecute");
            loaderArgs.getCallback().onDownloaded(context, Replacements);

            /*if(loaderArgs.populate) {
                ((MainActivity)loaderArgs.context).populateList(Replacements);
            } else {
                notifications(loaderArgs.context);
            }*/
        }

        private boolean isEmpty(String string) {
            return string == null || string.isEmpty() || string.trim().length() == 0;
        }
    }
}
