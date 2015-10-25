package rhedox.gesahuvertretungsplan.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.Substitute;

/**
 * Created by Robin on 09.10.2015.
 */
public final class SubstituteShareHelper {
    private SubstituteShareHelper() {}

    public static PendingIntent makeShareIntent(Substitute substitute, Context context) {
        if(substitute == null)
            return null;

        String text = "";

        if (!TextUtils.isEmpty(substitute.getSubject())) {
            text += substitute.getSubject().trim();
        }
        if (!TextUtils.isEmpty(substitute.getRoom())) {
            text += "; "+context.getString(R.string.room)+": " + substitute.getRoom().trim();
        }
        if (!TextUtils.isEmpty(substitute.getTeacher())) {
            text += System.getProperty("line.separator") + context.getString(R.string.teacher) + ": " + substitute.getTeacher().trim() + "; ";
        }
        if (!TextUtils.isEmpty(substitute.getSubstituteTeacher())) {
            text += context.getString(R.string.substitute_teacher)+": " + substitute.getSubstituteTeacher().trim();
        }
        if (!TextUtils.isEmpty(substitute.getHint())) {
            text += System.getProperty("line.separator") + context.getString(R.string.hint)+": " + substitute.getHint().trim();
        }
        
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_text)+ " " + text);
        share.setType("text/plain");
        return PendingIntent.getActivity(context, 1337, share, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    
    public static String makeShareText(Substitute substitute, Context context) {
        if(substitute == null)
            return null;

        String text = "";

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
