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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
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
public class SubstituteJSoupRequest extends Request<SubstitutesList> {
    private StudentInformation studentInformation;
    private ShortNameResolver shortNameResolver;
    private WeakReference<Response.Listener<SubstitutesList>> listener;
    private Context context;
    private LocalDate date;

    @RequiresPermission(Manifest.permission.INTERNET)
    public SubstituteJSoupRequest(@NonNull Context context, @NonNull LocalDate date, StudentInformation studentInformation, Response.Listener<SubstitutesList> listener, Response.ErrorListener errorListener) {
        super(Method.GET, "http://www.gesahui.de/home/view.php" + "?" + "d=" + Integer.toString(date.getDayOfMonth()) + "&m=" + Integer.toString(date.getMonthOfYear()) + "&y=" + Integer.toString(date.getYear()), errorListener);

        this.date = date;
        this.studentInformation = studentInformation;
        this.shortNameResolver = new ShortNameResolver(context);
        this.listener = new WeakReference<Response.Listener<SubstitutesList>>(listener);
        this.context = context.getApplicationContext();
    }

    @Override
    protected Response<SubstitutesList> parseNetworkResponse(NetworkResponse response) {
        if(response.statusCode == 200) {
            List<Substitute> substitutes = new ArrayList<Substitute>();
            String announcement;

            try {
                String body = new String(response.data, "windows-1252");
                Document document = Jsoup.parse(body);
                announcement = readAnnouncement(document);

                Elements tables = document.getElementsByTag("table");

                if(tables.size() != 5)
                    return Response.success(new SubstitutesList(substitutes, announcement, date), HttpHeaderParser.parseCacheHeaders(response));

                Elements rows = tables.get(2).getElementsByTag("tr");

                String lesson = "";
                String subject = "";
                String teacher = "";
                String substituteTeacher = "";
                String room = "";
                String hint = "";
                for(Element row : rows) {
                    Elements cells = row.getElementsByTag("td");
                    for(int i = 0; i < cells.size(); i++) {
                        Element cell = cells.get(i);
                        String text = cell.text();
                        text = text.replaceAll(((char)160)+"| +", " ").trim();

                        if(!TextUtils.isEmpty(text)) {
                            switch (i) {
                                case 0: {
                                    if(!text.equals(lesson) && !TextUtils.isEmpty(subject)) {
                                        substitutes.add(new Substitute(lesson, subject, teacher, substituteTeacher, room, hint, studentInformation));

                                        subject = "";
                                        teacher = "";
                                        substituteTeacher = "";
                                        room = "";
                                        hint = "";
                                    }

                                    lesson = text;
                                } break;

                                case 1: {
                                    if(!TextUtils.isEmpty(subject)) {
                                        substitutes.add(new Substitute(lesson, subject, teacher, substituteTeacher, room, hint, studentInformation));

                                        teacher = "";
                                        substituteTeacher = "";
                                        room = "";
                                        hint = "";
                                    }

                                    String[] parts = text.split(" ");
                                    if (parts.length > 1)
                                        text = shortNameResolver.resolveSubject(parts[0]) + " " + parts[1];

                                    subject = text;
                                } break;

                                case 2:
                                    text = text.replaceAll(", |; |,+|;+|" + System.getProperty("line.separator"), ",");
                                    String[] teachers = text.split(",");
                                    for (String _teacher : teachers) {
                                        if (!TextUtils.isEmpty(_teacher) && !TextUtils.isEmpty(text)) {

                                            //Semikolon einfügen, wenn schon Lehrer hinzugefügt wurden
                                            if (!TextUtils.isEmpty(teacher)) {
                                                teacher += "; ";
                                            }

                                            teacher += shortNameResolver.resolveTeacher(_teacher.trim());
                                        }
                                    }
                                    break;

                                case 3:
                                    text = text.replaceAll(", |; |,+|;+|" + System.getProperty("line.separator"), ",");
                                    String[] substituteTeachers = text.split(",");
                                    for (String _teacher : substituteTeachers) {
                                        if (!TextUtils.isEmpty(_teacher) && !TextUtils.isEmpty(text)) {

                                            //Semikolon einfügen, wenn schon Lehrer hinzugefügt wurden
                                            if (!TextUtils.isEmpty(substituteTeacher)) {
                                                substituteTeacher += "; ";
                                            }

                                            substituteTeacher += shortNameResolver.resolveTeacher(_teacher.trim());
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
                    }
                }

                if (!TextUtils.isEmpty(subject)) {
                    Substitute substitute = new Substitute(lesson, subject, teacher, substituteTeacher, room, hint, studentInformation);
                    substitutes.add(substitute);
                }
            }
            catch (UnsupportedEncodingException e) {
                Log.d("Catched Exception: ", "" + e.getMessage());
                return Response.error(new VolleyError(response));
            }

            return Response.success(new SubstitutesList(substitutes, announcement, date), HttpHeaderParser.parseCacheHeaders(response));
        }
        return Response.error(new VolleyError(context.getString(R.string.error_server)));
    }

    private String readAnnouncement(Document document) {
        Elements hinweise = document.select("body>div>center>div>p>b>font[size=\"2\"][face=\"Arial\"]");
        if(hinweise.hasText())
            return hinweise.text();

        return "";
    }

    @Override
    protected void deliverResponse(SubstitutesList response) {
        if(listener != null && listener.get() != null)
            listener.get().onResponse(response);
    }
}

