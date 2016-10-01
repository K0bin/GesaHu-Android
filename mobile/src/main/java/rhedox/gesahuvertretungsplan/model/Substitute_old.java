package rhedox.gesahuvertretungsplan.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.moshi.FromJson;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.util.TextUtils;

public class Substitute_old implements Comparable<Substitute_old>, Parcelable {
    public static final int KIND_SUBSTITUTE = 0;
    public static final int KIND_DROPPED = 1;
    public static final int KIND_ROOM_CHANGE = 2;
    public static final int KIND_TEST = 3;
    public static final int KIND_REGULAR = 4;

    @IntDef({KIND_SUBSTITUTE, KIND_DROPPED, KIND_ROOM_CHANGE, KIND_TEST, KIND_REGULAR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SubstituteKind {}

    private final String lesson, subject, teacher, substituteTeacher, room, hint;
    private final boolean isImportant;
    private int startingLesson;
    private final @SubstituteKind int kind;

    public Substitute_old(@NonNull String lesson, @NonNull String subject, @NonNull String teacher, @NonNull String substituteTeacher, @NonNull String room, @NonNull String hint, @Nullable Student information) {
        this.lesson = lesson.trim();
        this.subject = subject.trim();
        this.teacher = teacher.trim();
        this.substituteTeacher = substituteTeacher.trim();
        this.room = room.trim();
        this.hint = hint.trim();

        String[] classes = subject.split(" ");
        if(classes.length > 0 && information != null && !information.getIsEmpty()) {
            String _class = classes[classes.length - 1];
            isImportant = (_class.contains(information.getSchoolYear()) || ("13".equals(information.getSchoolYear()) && "abi".equals(_class.toLowerCase()))) && _class.contains(information.getSchoolClass());
        } else
            isImportant = false;

        if(!TextUtils.isEmpty(lesson)) {
            String[] lessonParts = lesson.split("-");
            if(lessonParts.length > 0) {
                try {
                    startingLesson = Integer.parseInt(lessonParts[0].trim(), 10);
                }
                catch(NumberFormatException e) {
                    startingLesson = -1;
                }
            }
        }

        String lowerSubstitute = substituteTeacher.toLowerCase();
        String lowerHint = hint.toLowerCase();
        if("eigv. lernen".equals(lowerSubstitute) || lowerHint.contains("eigenverantwortliches arbeiten") || lowerHint.contains("entfällt"))
            kind = KIND_DROPPED;
        else if((TextUtils.isEmpty(substituteTeacher) || substituteTeacher.equals(teacher)) && "raumänderung".equals(lowerHint))
            kind = KIND_ROOM_CHANGE;
        else if(lowerHint.contains("klausur"))
            kind = KIND_TEST;
        else if(lowerHint.contains("findet statt"))
            kind = KIND_REGULAR;
        else
            kind = KIND_SUBSTITUTE;
    }

	private Substitute_old(@NonNull String lesson, @NonNull String subject, @NonNull String teacher, @NonNull String substituteTeacher, @NonNull String room, @NonNull String hint, boolean isImportant) {
		this.lesson = lesson;
		this.subject = subject;
		this.teacher = teacher;
		this.substituteTeacher = substituteTeacher;
		this.room = room;
		this.hint = hint;
		this.isImportant = isImportant;

		if(!TextUtils.isEmpty(lesson)) {
			String[] lessonParts = lesson.split("-");
			if(lessonParts.length > 0) {
				try {
					startingLesson = Integer.parseInt(lessonParts[0].trim(), 10);
				}
				catch(NumberFormatException e) {
					startingLesson = -1;
				}
			}
		}

		String lowerSubstitute = substituteTeacher.toLowerCase();
		String lowerHint = hint.toLowerCase();
		if("eigv. lernen".equals(lowerSubstitute) || lowerHint.contains("eigenverantwortliches arbeiten") || lowerHint.contains("entfällt") || lowerHint.contains("frei"))
			kind = KIND_DROPPED;
		else if((TextUtils.isEmpty(substituteTeacher) || substituteTeacher.equals(teacher)) && "raumänderung".equals(lowerHint))
			kind = KIND_ROOM_CHANGE;
		else if(lowerHint.contains("klausur"))
			kind = KIND_TEST;
		else if(lowerHint.contains("findet statt"))
			kind = KIND_REGULAR;
		else
			kind = KIND_SUBSTITUTE;
	}

	public Substitute_old(@NonNull String startingLesson, @NonNull String endLesson, @NonNull String _class, @NonNull String subject, @NonNull String teacher, @NonNull String substituteTeacher, @NonNull String room, @NonNull String hint) {
		this.lesson = startingLesson + "-" + endLesson;
		this.subject = _class + " " +subject;
		this.teacher = teacher;
		this.substituteTeacher = substituteTeacher;
		this.hint = hint;
		this.room = room;
		this.isImportant = true;

		try {
			this.startingLesson = Integer.parseInt(startingLesson.trim(), 10);
		}
		catch(NumberFormatException e) {
			this.startingLesson = -1;
		}

		String lowerSubstitute = substituteTeacher.toLowerCase();
		String lowerHint = hint.toLowerCase();
		if("eigv. lernen".equals(lowerSubstitute) || lowerHint.contains("eigenverantwortliches arbeiten") || lowerHint.contains("entfällt") || lowerHint.contains("frei"))
			kind = KIND_DROPPED;
		else if((TextUtils.isEmpty(substituteTeacher) || substituteTeacher.equals(teacher)) && "raumänderung".equals(lowerHint))
			kind = KIND_ROOM_CHANGE;
		else if(lowerHint.contains("klausur"))
			kind = KIND_TEST;
		else if(lowerHint.contains("findet statt"))
			kind = KIND_REGULAR;
		else
			kind = KIND_SUBSTITUTE;
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
        return substituteTeacher;
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

    public @SubstituteKind int getKind() {
        return kind;
    }

    public static Substitute_old makeEmptyListSubstitute(@NonNull Context context) {
        return new Substitute_old("1-10", context.getString(R.string.no_substitutes), context.getString(R.string.no_substitutes_hint), "", "", "", null);
    }

	public boolean equals(Substitute_old another) {
		return another != null &&
				((lesson == null && another.lesson == null) || (lesson != null && lesson.equals(another.lesson))) &&
				((subject == null && another.subject == null) || (subject != null && subject.equals(another.subject))) &&
				((teacher == null && another.teacher == null) || (teacher != null && teacher.equals(another.teacher))) &&
				((substituteTeacher == null && another.substituteTeacher == null) || (substituteTeacher != null && another.substituteTeacher.equals(substituteTeacher))) &&
				((room == null && another.room == null) || (room != null && room.equals(another.room))) &&
				((hint == null && another.hint == null) || (hint != null && hint.equals(another.hint)));
	}

    @Override
    public int compareTo(Substitute_old another) {
        if(another == null)
            return -1;

        if(getIsImportant()) {
            if (!another.getIsImportant())
                return -1;
            else {
                if (getStartingLesson() - another.getStartingLesson() == 0) {
                    if (getLesson().length() - another.getLesson().length() == 0)
                        return getSubject().compareTo(another.getSubject());
                    else
                        return getLesson().length() - another.getLesson().length();
                }

                return getStartingLesson() - another.getStartingLesson();
            }
        } else {
            if (another.getIsImportant())
                return 1;
            else {
                if (getStartingLesson() - another.getStartingLesson() == 0) {
                    if (getLesson().length() - another.getLesson().length() == 0)
                        return getSubject().compareTo(another.getSubject());
                    else
                        return getLesson().length() - another.getLesson().length();
                }

                return getStartingLesson() - another.getStartingLesson();
            }
        }
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
	    dest.writeString(lesson);
	    dest.writeString(subject);
	    dest.writeString(teacher);
	    dest.writeString(substituteTeacher);
	    dest.writeString(room);
	    dest.writeString(hint);
	    dest.writeByte((byte)(isImportant ? 1 : 0));
    }

    public static final Parcelable.Creator<Substitute_old> CREATOR =
            new Parcelable.Creator<Substitute_old>(){

                @Override
                public Substitute_old createFromParcel(Parcel source) {
	                String lesson = source.readString();
	                String subject = source.readString();
	                String teacher = source.readString();
	                String substitute = source.readString();
	                String room = source.readString();
	                String hint = source.readString();

	                byte isImportantByte = source.readByte();
	                boolean isImportant = isImportantByte == 1;

	                if(lesson == null || subject == null || teacher == null || substitute == null || room == null || hint == null)
		                return null;

                    return new Substitute_old(lesson, subject, teacher, substitute, room, hint, isImportant);
                }

                @Override
                public Substitute_old[] newArray(int size) {
                    return new Substitute_old[size];
                }
    };

	public static class Adapter {
		@FromJson public Substitute_old fromJson(String stundeAnfang, String stundeEnde, String fach, String klasse, String lehrer, String vertretungslehrer, String raum, String hinweis) {
			return new Substitute_old(stundeAnfang, stundeEnde, klasse, fach, lehrer, vertretungslehrer, raum, hinweis);
		}
	}
}