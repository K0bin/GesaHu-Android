package rhedox.gesahuvertretungsplan.RecyclerView;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import rhedox.gesahuvertretungsplan.R;

/**
 * Created by Robin on 22.03.2015.
 */
public class ReplacementViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case
    private TextView lesson;
    private TextView subjectName;
    private TextView regularTeacher;
    private TextView replacementTeacher;
    private TextView room;
    private TextView hint;

    private static Drawable highlightBackground;
    private static Drawable background;

    public ReplacementViewHolder(ViewGroup view) {
        super(view);
        lesson = (TextView) view.findViewById(R.id.lesson);
        subjectName = (TextView) view.findViewById(R.id.subjectName);
        regularTeacher = (TextView) view.findViewById(R.id.regularTeacher);
        replacementTeacher = (TextView) view.findViewById(R.id.replacementTeacher);
        room = (TextView) view.findViewById(R.id.room);
        hint = (TextView) view.findViewById(R.id.hint);
    }

    public void setLesson(String text) {
        lesson.setText(text);
    }
    public void setSubjectName(String text) {
        subjectName.setText(text);
    }
    public void setRegularTeacher(String text) {
        regularTeacher.setText(text);
    }
    public void setReplacementTeacher(String text) {
        replacementTeacher.setText(text);
    }
    public void setRoom(String text) {
        room.setText(text);
    }
    public void setHint(String text) {
        lesson.setText(text);
    }
    public void setImportant(boolean important) {
        if(important) {
            lesson.setBackground(highlightBackground);
            subjectName.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            lesson.setBackground(background);
            subjectName.setTypeface(Typeface.DEFAULT);
        }
    }

    public static void load(Context context) {
        highlightBackground = context.getResources().getDrawable(R.drawable.circle_highlight);
        background = context.getTheme().obtainStyledAttributes(new int[]{R.attr.circle}).getDrawable(0);
    }
}

