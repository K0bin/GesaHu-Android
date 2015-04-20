package rhedox.gesahuvertretungsplan;

public class Replacement {
    private String lesson, subject, regularTeacher, replacementTeacher, room, hint;
    private boolean important;

    public Replacement(String lesson, String subject, String regularTeacher, String replacementTeacher, String room, String hint) {
        this.lesson = lesson;
        this.subject = subject;
        this.regularTeacher = replacementTeacher;
        this.replacementTeacher = replacementTeacher;
        this.room = room;
        this.hint = hint;
        important = false;
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

    public boolean getImportant() {
        return important;
    }


    public void trim() {
        lesson = lesson.trim();
        subject = subject.trim();
        regularTeacher=regularTeacher.trim();
        replacementTeacher = replacementTeacher.trim();
        room=room.trim();
        hint = hint.trim();
    }

    public boolean check(String schoolyear, String schoolclass) {
        important = false;
        String[] classes = subject.split(" ");
        int index = classes.length - 1;
        String _class = classes[index];
        if (_class.contains(schoolyear) && _class.contains(schoolclass)) {
            important = true;
        }

        return important;
    }
}