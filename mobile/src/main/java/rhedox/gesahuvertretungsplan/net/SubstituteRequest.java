package rhedox.gesahuvertretungsplan.net;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.joda.time.LocalDate;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.ShortNameResolver;
import rhedox.gesahuvertretungsplan.model.StudentInformation;
import rhedox.gesahuvertretungsplan.model.Substitute;

/**
 * Created by Robin on 12.07.2015.
 */
public class SubstituteRequest extends Request<SubstitutesList> {
    private StudentInformation studentInformation;
    private ShortNameResolver shortNameResolver;
    private Response.Listener<SubstitutesList> listener;
    private Context context;

        public SubstituteRequest(@NonNull Context context, @NonNull LocalDate date, StudentInformation studentInformation, Response.Listener<SubstitutesList> listener, Response.ErrorListener errorListener) {
            super(Method.GET, "http://www.gesahui.de/intranet/view.php" + "?" + "d=" + Integer.toString(date.getDayOfMonth()) + "&m=" + Integer.toString(date.getMonthOfYear()) + "&y=" + Integer.toString(date.getYear()), errorListener);

            this.studentInformation = studentInformation;
            this.shortNameResolver = new ShortNameResolver(context);
            this.listener = listener;
            this.context = context.getApplicationContext();
        }

        @Override
        protected Response<SubstitutesList> parseNetworkResponse(NetworkResponse response) {
            if(response.statusCode == 200) {
                List<Substitute> substitutes = new ArrayList<Substitute>();
                String announcement = null;

                try {
                    ByteArrayInputStream stream = new ByteArrayInputStream(response.data);
                    InputStreamReader streamReader = new InputStreamReader(stream, "windows-1252");
                    BufferedReader in = new BufferedReader(streamReader);
                    String line;

                    //Parsing
                    int cellIndex = 0;

                    String lesson = "";
                    String subject = "";
                    String regularTeacher = "";
                    String replacementTeacher = "";
                    String room = "";
                    String hint = "";

                    while ((line = in.readLine()) != null) {
                        //Dopplete Leerzeichen, HTML Zeichen und Newline entfernen
                        line = line.replaceAll("&nbsp;|\u00a0|" + System.getProperty("line.separator"), "").replaceAll(" +", " ").trim();

                        if (announcement == null) {
                            int start = line.indexOf("<b><font face=Arial size=2>");
                            start += "<b><font face=Arial size=2>".length();
                            if (start != -1 && line.length() > start) {
                                announcement = line.substring(start);

                                int end = announcement.indexOf("</font>");
                                if (end != -1)
                                    announcement = announcement.substring(0, end);
                                else
                                    announcement = "";
                            } else
                                announcement = "";
                        } else if (line.length() > 3) {
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
                                                Substitute substitute = new Substitute(lesson, subject, regularTeacher, replacementTeacher, room, hint, studentInformation);
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
                                                Substitute substitute = new Substitute(lesson, subject, regularTeacher, replacementTeacher, room, hint, studentInformation);
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
                        Substitute substitute = new Substitute(lesson, subject, regularTeacher, replacementTeacher, room, hint, studentInformation);
                        substitutes.add(substitute);
                    }

                    if (substitutes.size() == 0) {
                        Substitute substitute = new Substitute("1-10", context.getString(R.string.no_substitutes), context.getString(R.string.no_substitutes_hint), "", "", "", new StudentInformation("", ""));
                        substitutes.add(substitute);
                    }

                    stream.close();
                    in.close();

                } catch (IOException e) {
                    Log.d("Catched Exception: ", "" + e.getMessage());
                    return Response.error(new VolleyError(response));
                }

                return Response.success(new SubstitutesList(substitutes, announcement), HttpHeaderParser.parseCacheHeaders(response));
            }
            else
                return Response.error(new VolleyError(context.getString(R.string.error_server)));
        }

        @Override
        protected void deliverResponse(SubstitutesList response) {
            if(listener != null)
                listener.onResponse(response);
        }

    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty() || string.trim().length() == 0;
    }
}

