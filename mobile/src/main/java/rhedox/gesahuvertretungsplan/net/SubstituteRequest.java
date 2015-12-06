package rhedox.gesahuvertretungsplan.net;

import android.Manifest;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
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
import rhedox.gesahuvertretungsplan.model.SubstitutesList;
import rhedox.gesahuvertretungsplan.util.TextUtils;

/**
 * Created by Robin on 12.07.2015.
 */
@Deprecated
public class SubstituteRequest extends Request<SubstitutesList> {
    private StudentInformation studentInformation;
    private ShortNameResolver shortNameResolver;
    private Response.Listener<SubstitutesList> listener;
    private Context context;
    private LocalDate date;

    @RequiresPermission(Manifest.permission.INTERNET)
    public SubstituteRequest(@NonNull Context context, @NonNull LocalDate date, StudentInformation studentInformation, Response.Listener<SubstitutesList> listener, Response.ErrorListener errorListener) {
        super(Method.GET, "http://www.gesahui.de/home/view.php" + "?" + "d=" + Integer.toString(date.getDayOfMonth()) + "&m=" + Integer.toString(date.getMonthOfYear()) + "&y=" + Integer.toString(date.getYear()), errorListener);

        this.date = date;
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
                int cellCounter = 0;

                String lesson = "";
                String subject = "";
                String teacher = "";
                String substituteTeacher = "";
                String room = "";
                String hint = "";

                while ((line = in.readLine()) != null) {
                    //Dopplete Leerzeichen, HTML Zeichen und Newline entfernen
                    line = line.replaceAll("&nbsp;|\u00a0|" + System.getProperty("line.separator"), "").replaceAll(" +", " ").trim();

                    if(line.length() <= 3)
                        continue;

                    if (announcement == null)
                        announcement = readAnnouncement(line);
                    else {
                        //HTML Tag auslesen
                        String tag = line.substring(1, 4).trim().replaceAll(">", "");

                        //Tabellenzelle
                        if (tag.equals("td")) {
                            //Text bis Zellenindex ausschneiden
                            String text = readElementContent(line);

                            if(TextUtils.isEmpty(text)) {
                                cellCounter++;
                                continue;
                            }

                            switch (cellCounter) {
                                case 0:
                                    String lessonText = text.replaceAll(" ", "");

                                    if (!lesson.equals(lessonText) && !TextUtils.isEmpty(subject) && !TextUtils.isEmpty(lessonText)) {
                                        Substitute substitute = new Substitute(lesson, subject, teacher, substituteTeacher, room, hint, studentInformation);
                                        substitutes.add(substitute);

                                        subject = "";
                                        teacher = "";
                                        substituteTeacher = "";
                                        room = "";
                                        hint = "";
                                    }
                                    lesson = lessonText;
                                    break;
                                case 1:
                                    if (!TextUtils.isEmpty(subject)) {
                                        //Subjectname isn't empty => add previous lesson
                                        Substitute substitute = new Substitute(lesson, subject, teacher, substituteTeacher, room, hint, studentInformation);
                                        substitutes.add(substitute);

                                        subject = "";
                                        teacher = "";
                                        substituteTeacher = "";
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
                                        if (!TextUtils.isEmpty(regularTeachers[i]) && !TextUtils.isEmpty(text)) {

                                            //Semikolon einfügen, wenn schon Lehrer hinzugefügt wurden
                                            if (!TextUtils.isEmpty(teacher)) {
                                                teacher += "; ";
                                            }

                                            teacher += shortNameResolver.resolveTeacher(regularTeachers[i].trim());
                                        }
                                    }

                                    break;
                                case 3:
                                    //Lehrerkürzel auflesen
                                    text = text.replaceAll(", |; |,+|;+|" + System.getProperty("line.separator"), ",");
                                    String[] replacementTeachers = text.split(",");
                                    for (int i = 0; i < replacementTeachers.length; i++) {
                                        if (!TextUtils.isEmpty(replacementTeachers[i]) && !TextUtils.isEmpty(text)) {

                                            //Semikolon einfügen, wenn schon Lehrer hinzugefügt wurden
                                            if (!TextUtils.isEmpty(substituteTeacher)) {
                                                substituteTeacher += "; ";
                                            }

                                            substituteTeacher += shortNameResolver.resolveTeacher(replacementTeachers[i].trim());
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

                            cellCounter++;
                        }

                        //Tabellenzeilenende
                        if (tag.equals("/tr"))
                            cellCounter = 0;

                        //Tabellende
                        if (tag.equals("/ta"))
                            break;
                    }
                }

                if (!TextUtils.isEmpty(subject)) {
                    Substitute substitute = new Substitute(lesson, subject, teacher, substituteTeacher, room, hint, studentInformation);
                    substitutes.add(substitute);
                }

                stream.close();
                in.close();

            } catch (IOException e) {
                Log.d("Catched Exception: ", "" + e.getMessage());
                return Response.error(new VolleyError(response));
            }

            return Response.success(new SubstitutesList(substitutes, announcement, date), HttpHeaderParser.parseCacheHeaders(response));
        }
        else
            return Response.error(new VolleyError(context.getString(R.string.error_server)));
    }

    private String readAnnouncement(String line) {
        String announcement;

        int start = line.indexOf("<b><font face=Arial size=2>");

        if (start == -1)
            return "";

        start += "<b><font face=Arial size=2>".length();
        if(line.length() <= start)
            return "";

        announcement = line.substring(start);

        int end = announcement.indexOf("</font>");
        if (end == -1)
            return "";

        announcement = announcement.substring(0, end);

        return announcement;
    }

    private String readElementContent(String line) {
        int textEnd = line.lastIndexOf("</td>");
        if(textEnd == -1)
            return "";

        String textCut = line.substring(0, textEnd).trim();

        int tagEnd = textCut.lastIndexOf(">");
        if(tagEnd == -1)
            return "";

        int textStart = textCut.lastIndexOf(">") + ">".length();
        return textCut.substring(textStart).trim();
    }




    @Override
    protected void deliverResponse(SubstitutesList response) {
        if(listener != null)
            listener.onResponse(response);
    }
}

