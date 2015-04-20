package rhedox.gesahuvertretungsplan;

import android.content.Context;
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

    private Shorts shorts = new Shorts();

    public ReplacementsList() {
    }

    public void load(Context context, int day, int month, int year, String schoolyear, String schoolclass, OnDownloadedListener listener) {
        replacements.clear();
        ReplacementslistLoader loader = new ReplacementslistLoader(context);
        loader.execute(new ReplacementsListLoaderArgs(day, month, year, schoolyear, schoolclass, context, listener));
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
                String getString = "http://www.gesahui.de/intranet/view.php" + "?" + "d=" + String.valueOf(loaderArgs.getDay()) + "&m=" + String.valueOf(loaderArgs.getMonth()) + "&y=" + String.valueOf(loaderArgs.getYear());
                URL url = new URL(getString);
                Log.d("Plan","Downloading: "+getString);


                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                request.setRequestProperty("User-Agent", "Gesahu Replacementplan Android");
                request.connect();

                int responseCode = request.getResponseCode();
                Log.d("Plan","HTTP Responsecode: "+Integer.toString(responseCode));

                InputStreamReader stream = new InputStreamReader(request.getInputStream(), "windows-1252");
                BufferedReader in = new BufferedReader(stream);
                String line = "";

                //Parsing
                int cellIndex = 0;
                int lineIndex = 0;

                String lesson = "";
                String subject ="";
                String regularTeacher ="";
                String replacementTeacher ="";
                String room ="";
                String hint ="";

                boolean done = false;

                while ((line = in.readLine()) != null && !done) {
                    line = line.replaceAll("&nbsp;|\u00a0|" + System.getProperty("line.separator"), "").replaceAll(" +", " ").trim();
                    if (line.length() > 3) {
                        String tag = line.substring(1, 4).trim().replaceAll(">", "");
                        if (tag.equals("td")) {
                            int textEnd = line.lastIndexOf("</td>");
                            String textEndless = line.substring(0, textEnd).trim();
                            int textStart = textEndless.lastIndexOf(">") + ">".length();
                            String text = textEndless.substring(textStart).trim();
                            switch (cellIndex) {
                                case 0:
                                    if (!text.equals("") && !text.equals(" ")) {
                                        lesson = text.replaceAll(" ", "");
                                    }
                                    break;
                                case 1:
                                    if (lineIndex != 0) {
                                        if (!text.equals("") && !text.equals(" ")) {
                                            //Subjectname isn't empty => add previous lesson
                                            Replacement replacement = new Replacement(lesson, subject, regularTeacher, replacementTeacher, room, hint);
                                            replacement.trim();
                                            replacement.check(loaderArgs.getSchoolyear(), loaderArgs.getSchoolclass());
                                            replacements.add(replacement);

                                            //Resolve subject shortnames
                                            String[] parts = text.split(" ");
                                            if(parts.length > 1) {
                                                text = shorts.resolveSubject(parts[0]) + " " + parts[1];
                                            }

                                            subject=text;
                                            regularTeacher="";
                                            replacementTeacher="";
                                            room="";
                                            hint="";
                                        }
                                    } else {
                                        String[] parts = text.split(" ");
                                        if(parts.length > 1) {
                                            text = shorts.resolveSubject(parts[0]) + " " + parts[1];
                                        }

                                        subject += text;

                                    }
                                    break;
                                case 2:
                                    if (!text.equals("") && !text.equals(" ")) {
                                        //Resolve Teacher short names
                                        text = text.replaceAll(", |; |,+|;+|"+System.getProperty("line.separator"), ",");
                                        String[] teachers = text.split(",");
                                        for (int i = 0; i < teachers.length; i++) {
                                            if (!teachers[i].equals("") && !teachers[i].equals(" ") && !text.equals("") && !text.equals(" ")) {
                                                if(!regularTeacher.equals("") && !regularTeacher.equals(" ")) {
                                                    regularTeacher += "; ";
                                                }
                                                regularTeacher += shorts.resolveTeacher(teachers[i].trim());
                                            }
                                        }
                                    }
                                    break;
                                case 3:
                                    if (!text.equals("") && !text.equals(" ")) {
                                        //Resolve Teacher short names
                                        text = text.replaceAll(", |; |,+|;+|"+System.getProperty("line.separator"), ",");
                                        String[] teachers = text.split(",");
                                        for (int i = 0; i < teachers.length; i++) {
                                            if (!teachers[i].equals("") && !teachers[i].equals(" ") && !text.equals("") && !text.equals(" ")) {
                                                if(!replacementTeacher.equals("") && !replacementTeacher.equals(" ")) {
                                                    replacementTeacher+="; ";
                                                }
                                                replacementTeacher+=shorts.resolveTeacher(teachers[i].trim());
                                            }
                                        }
                                    }
                                    break;
                                case 4:
                                    if (!text.equals("") && !text.equals(" ")) {
                                        room += text + " ";
                                    }
                                    break;
                                case 5:
                                    if (!text.equals("") && !text.equals(" ")) {
                                        hint += text + " ";
                                    }
                                    break;
                            }
                            cellIndex++;
                        }
                        if (tag.equals("/tr")) {
                            cellIndex = 0;
                            lineIndex++;
                        }
                        if (tag.equals("/ta")) {
                            done = true;
                        }
                    }
                }

                if (!subject.equals("") && !subject.equals(" ")) {
                    Replacement replacement = new Replacement(lesson, subject, regularTeacher, replacementTeacher, room, hint);
                    replacement.trim();
                    replacement.check(loaderArgs.getSchoolyear(), loaderArgs.getSchoolclass());
                    replacements.add(replacement);
                }

                if(replacements.size() == 0) {
                    Replacement replacement = new Replacement("1-10", "Keine Vertretungsstunden", "Alles nach Plan", "", "", "");
                    replacements.add(replacement);
                }

                stream.close();
                in.close();

            } catch (Exception e) {
                Log.d("VPException2", "" + e.getMessage());

                Replacement replacement = new Replacement("1-10", "Keine Vertretungsstunden", "Keine Daten", "Überprüfe deine Verbindung!", "", e.getMessage());
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
    }
}
