package rhedox.gesahuvertretungsplan.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.Substitute;

/**
 * Created by Robin on 09.10.2015.
 */
public final class SubstituteShareHelper {
    public static final int REQUEST_CODE = 2;

    private SubstituteShareHelper() {}

    public static Intent makeShareIntent(@Nullable LocalDate date, Substitute substitute, Context context) {
        if(substitute == null)
            return null;

        String text = makeShareText(date, substitute, context);
        
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_text) + " " + text);
        share.setType("text/plain");
        return share;
    }

    public static PendingIntent makePendingShareIntent(@Nullable LocalDate date,  Substitute substitute, Context context) {
        return PendingIntent.getActivity(context.getApplicationContext(), REQUEST_CODE, makeShareIntent(date, substitute, context), PendingIntent.FLAG_UPDATE_CURRENT);
    }
    
    public static String makeShareText(@Nullable LocalDate date, Substitute substitute, Context context) {
        if(substitute == null)
            return null;

        String text = "";

        if(date != null) {
            if (date.equals(LocalDate.now()))
                text += context.getString(R.string.today) + " ";
            else if (date.equals(LocalDate.now().plusDays(1)))
                text += context.getString(R.string.tomorrow) + " ";
            else {

                if(date.getWeekOfWeekyear() == LocalDate.now().getWeekOfWeekyear()) {
                    DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE");
                    text += context.getString(R.string.date_prefix) + " " + date.toString(fmt) + " ";
                }
                else if(date.getWeekOfWeekyear() == LocalDate.now().getWeekOfWeekyear() + 1) {
                    DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE");
                    text += context.getString(R.string.next_week) + " " + date.toString(fmt) + " ";
                }
                else {
                    DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.");
                    text += context.getString(R.string.date_prefix) + " " + date.toString(fmt) + " ";
                }
            }
        }

        if (!TextUtils.isEmpty(substitute.getSubject())) {
            text += substitute.getSubject().trim();
        }
        if (!TextUtils.isEmpty(substitute.getRoom())) {
            text += "; "+context.getString(R.string.room)+": " + substitute.getRoom().trim();
        }
        if (!TextUtils.isEmpty(substitute.getTeacher())) {
            text += System.getProperty("line.separator") + context.getString(R.string.teacher) + ": " + substitute.getTeacher().trim() + "; ";
        }
        if (!TextUtils.isEmpty(substitute.getSubstituteTeacher()) && !TextUtils.isEmpty(substitute.getSubstituteTeacher())) {
            text += context.getString(R.string.substitute_teacher)+": " + substitute.getSubstituteTeacher().trim();
        }
        if (!TextUtils.isEmpty(substitute.getHint())) {
            text += System.getProperty("line.separator") + context.getString(R.string.hint)+": " + substitute.getHint().trim();
        }
        
        return text;
    }
}
