package rhedox.gesahuvertretungsplan.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.Substitute;

/**
 * Created by Robin on 05.05.2016.
 */
public class SubstituteFormatter {

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
            case Substitute.KIND_DROPPED:
                return String.format(context.getString(R.string.share_dropped), dateText, substitute.getLesson(), substitute.getSubject());

            case Substitute.KIND_ROOM_CHANGE:
                return String.format(context.getString(R.string.share_room_change), dateText, substitute.getLesson(), substitute.getSubject(), substitute.getRoom());

            case Substitute.KIND_TEST:
                return String.format(context.getString(R.string.share_test), dateText, substitute.getLesson(), substitute.getSubject(), substitute.getRoom());

            case Substitute.KIND_REGULAR:
                return String.format(context.getString(R.string.share_regular), substitute.getSubject(), dateText, substitute.getLesson());

            case Substitute.KIND_SUBSTITUTE:
            default:
                return String.format(context.getString(R.string.share_subsitute), dateText, substitute.getLesson(), substitute.getSubject(), substitute.getSubstituteTeacher(), substitute.getRoom());
        }
    }

    public static String makeSubstituteKindText(Context context, @Substitute.SubstituteKind int kind) {
        switch(kind) {
            case Substitute.KIND_ROOM_CHANGE:
                return context.getString(R.string.roomchange);

            case Substitute.KIND_DROPPED:
                return context.getString(R.string.dropped);

            case Substitute.KIND_TEST:
                return context.getString(R.string.test);

            case Substitute.KIND_REGULAR:
                return context.getString(R.string.regular);

            default:
            case Substitute.KIND_SUBSTITUTE:
                return context.getString(R.string.substitute);
        }
    }

    public static String makeNotificationText(Context context, Substitute substitute) {
        String text = substitute.getSubject();

        if(!TextUtils.isEmpty(substitute.getRoom()))
            text += "; "+context.getString(R.string.room)+": "+substitute.getRoom();

        text += System.getProperty("line.separator");

        if(!TextUtils.isEmpty(substitute.getTeacher()))
            text += context.getString(R.string.teacher)+": "+ substitute.getTeacher()+"; ";

        if(!TextUtils.isEmpty(substitute.getSubstituteTeacher()))
            text += context.getString(R.string.substitute_teacher)+": "+ substitute.getSubstituteTeacher();

        text += System.getProperty("line.separator");

        if(!TextUtils.isEmpty(substitute.getHint()))
            text += context.getString(R.string.hint)+": "+ substitute.getHint();

        return text;
    }
}
