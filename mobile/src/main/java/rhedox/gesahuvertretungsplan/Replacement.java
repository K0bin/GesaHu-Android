package rhedox.gesahuvertretungsplan;

public class Replacement {
    private final String lesson, subject, regularTeacher, replacementTeacher, room, hint;
    private final boolean important;

    public Replacement(String lesson, String subject, String regularTeacher, String replacementTeacher, String room, String hint, StudentInformation information) {
        this.lesson = lesson.trim();
        this.subject = subject.trim();
        this.regularTeacher = regularTeacher.trim();
        this.replacementTeacher = replacementTeacher.trim();
        this.room = room.trim();
        this.hint = hint.trim();

        String[] classes = subject.split(" ");
        String _class = classes[classes.length - 1];
        if (_class.contains(information.getSchoolYear()) && _class.contains(information.getSchoolClass()))
            important = true;
        else
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
}