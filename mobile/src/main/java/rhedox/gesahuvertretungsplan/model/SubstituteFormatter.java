package rhedox.gesahuvertretungsplan.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.util.TextUtils;

/**
 * Created by Robin on 05.05.2016.
 */
public class SubstituteFormatter {

    public static String makeSubstituteKindText(Context context, Substitute.Kind kind) {
        switch(kind) {
	        case RoomChange:
                return context.getString(R.string.roomchange);

	        case Dropped:
                return context.getString(R.string.dropped);

            case Test:
                return context.getString(R.string.test);

	        case Regular:
                return context.getString(R.string.regular);

            default:
	        case Substitute:
                return context.getString(R.string.substitute);
        }
    }

    public static String makeShareText(@NonNull Context context, @Nullable LocalDate date, @NonNull Substitute substitute) {
        String dateText = "";

        if(date != null) {
            if (date.equals(LocalDate.now()))
                dateText = context.getString(R.string.today);
            else if (date.equals(LocalDate.now().plusDays(1)))
                dateText = context.getString(R.string.tomorrow);
            else {

                if(date.getWeekOfWeekyear() == LocalDate.now().getWeekOfWeekyear()) {
                    DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE");
                    dateText = context.getString(R.string.date_prefix) + " " + date.toString(fmt);
                } else if(date.getWeekOfWeekyear() == LocalDate.now().getWeekOfWeekyear() + 1) {
                    DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE");
                    dateText = context.getString(R.string.next_week) + " " + date.toString(fmt);
                } else {
                    DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.");
                    dateText = context.getString(R.string.date_prefix) + " " + date.toString(fmt);
                }
            }
        }


        switch (substitute.getKind()) {
	        case Dropped:
                return String.format(context.getString(R.string.share_dropped), dateText, substitute.getLessonText(), substitute.getSubject());

	        case RoomChange:
                return String.format(context.getString(R.string.share_room_change), dateText, substitute.getLessonText(), substitute.getSubject(), substitute.getRoom());

	        case Test:
                return String.format(context.getString(R.string.share_test), dateText, substitute.getLessonText(), substitute.getSubject(), substitute.getRoom());

	        case Regular:
                return String.format(context.getString(R.string.share_regular), substitute.getSubject(), dateText, substitute.getLessonText());

	        case Substitute:
            default:
                return String.format(context.getString(R.string.share_subsitute), dateText, substitute.getLessonText(), substitute.getSubject(), substitute.getSubstitute(), substitute.getRoom());
        }
    }

    public static String makeNotificationText(Context context, Substitute substitute) {
        String text = substitute.getSubject();

        if(!TextUtils.isEmpty(substitute.getRoom()))
            text += "; " + context.getString(R.string.room) + ": " + substitute.getRoom();

	    if(!TextUtils.isEmpty(substitute.getTeacher()) || !TextUtils.isEmpty(substitute.getSubstitute()))
		    text += System.getProperty("line.separator");

        if(!TextUtils.isEmpty(substitute.getTeacher()))
            text += context.getString(R.string.teacher) + ": " + substitute.getTeacher() + "; ";

        if(!TextUtils.isEmpty(substitute.getSubstitute()))
            text += context.getString(R.string.substitute_teacher)+": "+ substitute.getSubstitute();

        if(!TextUtils.isEmpty(substitute.getHint())) {
            text += System.getProperty("line.separator");
            text += context.getString(R.string.hint) + ": " + substitute.getHint();
        }

        return text;
    }
}
