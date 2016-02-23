package rhedox.gesahuvertretungsplan.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.ParseException;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.util.TextUtils;

public class Substitute implements Comparable<Substitute> {
    private final String lesson, subject, teacher, replacementTeacher, room, hint;
    private final boolean isImportant;
    private int startingLesson;

    public Substitute(String lesson, String subject, String teacher, String replacementTeacher, String room, String hint, @Nullable StudentInformation information) {
        this.lesson = lesson.trim();
        this.subject = subject.trim();
        this.teacher = teacher.trim();
        this.replacementTeacher = replacementTeacher.trim();
        this.room = room.trim();
        this.hint = hint.trim();

        String[] classes = subject.split(" ");
        if(classes.length > 0 && information != null && !information.getIsEmpty()) {
            String _class = classes[classes.length - 1];
            isImportant = _class.contains(information.getSchoolYear()) && _class.contains(information.getSchoolClass());
        } else
            isImportant = false;

        if(!TextUtils.isEmpty(lesson)) {
            String[] lessonParts = lesson.split("-");
            if(lessonParts.length > 0) {

                try {
                    startingLesson = Integer.parseInt(lessonParts[0], 10);
                }
                catch(NumberFormatException e) {
                    startingLesson = -1;
                }
            }
        }
    }

    public String getLesson() {
        return lesson;
    }

    public String getSubject() {
        return subject;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getSubstituteTeacher() {
        return replacementTeacher;
    }

    public String getRoom() {
        return room;
    }

    public String getHint() {
        return hint;
    }

    public int getStartingLesson() {
        return startingLesson;
    }

    public boolean getIsImportant() {
        return isImportant;
    }

    public static Substitute makeEmptyListSubstitute(@NonNull Context context) {
        return new Substitute("1-10", context.getString(R.string.no_substitutes), context.getString(R.string.no_substitutes_hint), "", "", "", null);
    }

    @Override
    public int compareTo(Substitute another) {
        if(another == null)
            return -1;

        if(getIsImportant()) {
            if (!another.getIsImportant())
                return -1;
            else {
                if(getStartingLesson() < another.getStartingLesson())
                    return -1;
                else if(getStartingLesson() == another.getStartingLesson())
                    return 0;
                else if(getStartingLesson() > another.getStartingLesson())
                    return 1;
            }
        } else {
            if (another.getIsImportant())
                return 1;
            else {
                if(getStartingLesson() < another.getStartingLesson())
                    return -1;
                else if(getStartingLesson() == another.getStartingLesson())
                    return 0;
                else if(getStartingLesson() > another.getStartingLesson())
                    return 1;
            }
        }

        return 1;
    }
}