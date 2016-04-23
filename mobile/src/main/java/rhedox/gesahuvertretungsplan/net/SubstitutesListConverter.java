package rhedox.gesahuvertretungsplan.net;

/**
 * Created by Robin on 17.02.2016.
 */

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import rhedox.gesahuvertretungsplan.model.ShortNameResolver;
import rhedox.gesahuvertretungsplan.model.Student;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;
import rhedox.gesahuvertretungsplan.util.TextUtils;

public class SubstitutesListConverter implements Converter<ResponseBody, SubstitutesList> {

    private Student student;
    private ShortNameResolver shortNameResolver;

    public SubstitutesListConverter(ShortNameResolver resolver, Student student)
    {
        this.student = student;
        this.shortNameResolver = resolver;
    }

    @Override
    public SubstitutesList convert(ResponseBody value) throws IOException {

        try {
            List<Substitute> substitutes = new ArrayList<Substitute>();
            String announcement;
            LocalDate date;

            String body = new String(value.bytes(), "windows-1252");
            body = body.replaceAll("\\s+", " ");
            Document document = Jsoup.parse(body);
            date = readDate(document);
            announcement = readAnnouncement(document);

            Elements tables = document.getElementsByTag("table");

            if (tables.size() != 5)
                return new SubstitutesList(substitutes, announcement, date);

            Elements rows = tables.get(2).getElementsByTag("tr");

            String lesson = "";
            String subject = "";
            StringBuilder teacher = new StringBuilder();
            StringBuilder substituteTeacher = new StringBuilder();
            StringBuilder room = new StringBuilder();
            StringBuilder hint = new StringBuilder();
            for (Element row : rows) {
                Elements cells = row.getElementsByTag("td");
                for (int i = 0; i < cells.size(); i++) {
                    Element cell = cells.get(i);
                    String text = cell.text();
                    text = text.replaceAll(((char) 160) + "| +", " ").trim();

                    if (!TextUtils.isEmpty(text)) {
                        switch (i) {
                            case 0: {
                                if (!text.equals(lesson) && !TextUtils.isEmpty(subject) && !TextUtils.isEmpty(lesson)) {
                                    Substitute substitute = new Substitute(lesson.trim(), subject.trim(), teacher.toString().trim(), substituteTeacher.toString().trim(), room.toString().trim(), hint.toString().trim(), student);
                                    substitutes.add(substitute);

                                    subject = "";
                                    teacher.delete(0, teacher.length());
                                    substituteTeacher.delete(0, substituteTeacher.length());
                                    room.delete(0, room.length());
                                    hint.delete(0, hint.length());
                                }

                                lesson = text.replaceAll(" ", "");
                            }
                            break;

                            case 1: {
                                if (!TextUtils.isEmpty(subject)) {
                                    Substitute substitute = new Substitute(lesson.trim(), subject.trim(), teacher.toString().trim(), substituteTeacher.toString().trim(), room.toString().trim(), hint.toString().trim(), student);
                                    substitutes.add(substitute);

                                    subject = "";
                                    teacher.delete(0, teacher.length());
                                    substituteTeacher.delete(0, substituteTeacher.length());
                                    room.delete(0, room.length());
                                    hint.delete(0, hint.length());
                                }

                                String[] parts = text.split(" ");
                                if (parts.length > 1)
                                    text = shortNameResolver.resolveSubject(parts[0]) + " " + parts[1];

                                subject = text;
                            }
                            break;

                            case 2: {
                                text = text.replaceAll(", |; |,+|;+|" + System.getProperty("line.separator"), ",");
                                String[] teachers = text.split(",");
                                for (String _teacher : teachers) {
                                    if (!TextUtils.isEmpty(_teacher) && !"---".equals(_teacher)) {

                                        //Semikolon einfügen, wenn schon Lehrer hinzugefügt wurden
                                        if (!TextUtils.isEmpty(teacher.toString()))
                                            teacher.append("; ");

                                        teacher.append(shortNameResolver.resolveTeacher(_teacher.trim()));
                                    }
                                }
                            }
                            break;

                            case 3: {
                                text = text.replaceAll(", |; |,+|;+|" + System.getProperty("line.separator"), ",");
                                String[] substituteTeachers = text.split(",");
                                for (String _teacher : substituteTeachers) {
                                    if (!TextUtils.isEmpty(_teacher) && !"---".equals(_teacher)) {

                                        //Semikolon einfügen, wenn schon Lehrer hinzugefügt wurden
                                        if (!TextUtils.isEmpty(substituteTeacher.toString()))
                                            substituteTeacher.append("; ");

                                        substituteTeacher.append(shortNameResolver.resolveTeacher(_teacher.trim()));
                                    }
                                }
                            }
                            break;

                            case 4:
                                if(!"---".equals(text.toString()))
                                    room.append(text + " ");
                                break;

                            case 5:
                                if(!"---".equals(text.toString()))
                                    hint.append(text + " ");
                                break;
                        }
                    }
                }
            }

            if (!TextUtils.isEmpty(subject)) {
                Substitute substitute = new Substitute(lesson.trim(), subject.trim(), teacher.toString().trim(), substituteTeacher.toString().trim(), room.toString().trim(), hint.toString().trim(), student);
                substitutes.add(substitute);
            }

            if (substitutes.isEmpty() && TextUtils.isEmpty(announcement) && (date == null || date.equals(new LocalDate())))
                return null;

            return new SubstitutesList(substitutes, announcement, date);
        }
        finally {
            value.close();
        }
    }

    private LocalDate readDate(Document document) {
        Elements dateElement = document.select("body>div>center>div>div>center>table[id=\"AutoNumber1\"]>tbody>tr>td>p>font[size=\"4\"][face=\"Arial\"]");
        if(!dateElement.hasText())
            return null;

        String[] strings = dateElement.text().split(" ");
        if(strings.length < 3)
            return null;

        String date = strings[2];

        return LocalDate.parse(date, DateTimeFormat.forPattern("dd.MM.YYYY"));
    }

    private String readAnnouncement(Document document) {
        Elements announcementElement = document.select("body>div>center>div>p>b>font[size=\"2\"][face=\"Arial\"]");
        if(announcementElement.hasText())
            return announcementElement.text();

        return "";
    }

}
