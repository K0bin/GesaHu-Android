package rhedox.gesahuvertretungsplan.model;

import java.util.ArrayList;
import java.util.List;

public class Substitute {
    private final String lesson, subject, regularTeacher, replacementTeacher, room, hint;
    private final boolean isImportant;

    public Substitute(String lesson, String subject, String regularTeacher, String replacementTeacher, String room, String hint, StudentInformation information) {
        this.lesson = lesson.trim();
        this.subject = subject.trim();
        this.regularTeacher = regularTeacher.trim();
        this.replacementTeacher = replacementTeacher.trim();
        this.room = room.trim();
        this.hint = hint.trim();

        String[] classes = subject.split(" ");
        if(classes.length > 0 && information != null && !information.isEmpty()) {
            String _class = classes[classes.length - 1];
            isImportant = _class.contains(information.getSchoolYear()) && _class.contains(information.getSchoolClass());
        } else
            isImportant = false;
    }

    public String getLesson() {
        return lesson;
    }

    public String getSubject() {
        return subject;
    }

    public String getRegularTeacher() {
        return regularTeacher;
    }

    public String getReplacementTeacher() {
        return replacementTeacher;
    }

    public String getRoom() {
        return room;
    }

    public String getHint() {
        return hint;
    }

    public boolean getIsImportant() {
        return isImportant;
    }

    public static List<Substitute> filterImportant(List<Substitute> substitutes) {
        List<Substitute> list = new ArrayList<Substitute>();
        for(Substitute substitute : substitutes) {
            if(substitute.getIsImportant())
                list.add(substitute);
        }
        return list;
    }
}