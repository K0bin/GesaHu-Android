package rhedox.gesahuvertretungsplan;

public class Replacement {
    private String lesson, subject, regularTeacher, replacementTeacher, room, hint;
    private boolean important;

    public Replacement(String lesson, String subject) {
        this.lesson = lesson;
        this.subject = subject;
        regularTeacher = "";
        replacementTeacher = "";
        room = "";
        hint = "";
        important = false;
    }

    public String getLesson() {
        return lesson;
    }
    public void setLesson(String _lesson) {
        lesson = _lesson;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String _subject) {
        subject = _subject;
    }

    public String getRegularTeacher() {
        return regularTeacher;
    }
    public void setRegularTeacher(String _regularTeacher) {
        regularTeacher = _regularTeacher;
    }

    public String getReplacementTeacher() {
        return replacementTeacher;
    }
    public void setReplacementTeacher(String _replacementTeacher) {
        replacementTeacher = _replacementTeacher;
    }

    public String getRoom() {
        return room;
    }
    public void setRoom(String _room) {
        room = _room;
    }

    public String getHint() {
        return hint;
    }
    public void setHint(String _note) {
        hint = _note;
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