package rhedox.gesahuvertretungsplan.ui.viewHolders;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.ui.adapters.SubstitutesAdapter;

/**
 * Created by Robin on 21.11.2015.
 */


public class SubstituteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener {
    // each data item is just a string in this case
    private View view;
    private AppCompatTextView lesson;
    private AppCompatTextView subject;
    private AppCompatTextView teacher;
    private AppCompatTextView substituteTeacher;
    private AppCompatTextView room;
    private AppCompatTextView hint;
    private FrameLayout backgroundFrame;

    private Drawable circleHighlightBackground;
    private Drawable circleBackground;
    @ColorInt private int textColor;
    @ColorInt private int highlightedTextColor;

    @ColorInt private int activatedBackgroundColor;

    private WeakReference<SubstitutesAdapter> adapter;

    public SubstituteViewHolder(ViewGroup view, SubstitutesAdapter adapter, Drawable circleBackground, Drawable circleHighlightBackground, @ColorInt int textColor, @ColorInt int highlightedTextColor, @ColorInt int activatedBackgroundColor) {
        super(view);

        this.view = view;
        lesson = (AppCompatTextView) view.findViewById(R.id.lesson);
        subject = (AppCompatTextView) view.findViewById(R.id.subject);
        teacher = (AppCompatTextView) view.findViewById(R.id.teacher);
        substituteTeacher = (AppCompatTextView) view.findViewById(R.id.substituteTeacher);
        room = (AppCompatTextView) view.findViewById(R.id.room);
        hint = (AppCompatTextView) view.findViewById(R.id.hint);
        backgroundFrame = (FrameLayout) view.findViewById(R.id.backgroundFrame);

        view.setClickable(true);
        view.setOnClickListener(this);
        view.setOnTouchListener(this);

        this.circleBackground = circleBackground;
        this.circleHighlightBackground = circleHighlightBackground;
        this.textColor = textColor;
        this.highlightedTextColor = highlightedTextColor;

        this.activatedBackgroundColor = activatedBackgroundColor;

        this.adapter = new WeakReference<SubstitutesAdapter>(adapter);
    }

    public void setSubstitute(Substitute substitute) {
        if(substitute != null) {
            lesson.setText(substitute.getLesson());
            subject.setText(substitute.getSubject());
            teacher.setText(substitute.getTeacher());
            substituteTeacher.setText(substitute.getSubstituteTeacher());
            room.setText(substitute.getRoom());
            hint.setText(substitute.getHint());
            if (substitute.getIsImportant()) {
                lesson.setBackground(circleHighlightBackground);
                subject.setTypeface(Typeface.DEFAULT_BOLD);
                lesson.setTextColor(highlightedTextColor);
            } else {
                lesson.setBackground(circleBackground);
                subject.setTypeface(Typeface.DEFAULT);
                lesson.setTextColor(textColor);
            }
        }
    }

    public void setSelected(boolean selected) {
        if(view != null)
            view.setActivated(selected);

        if(!selected)
            view.setBackgroundColor(0x0);
        else
            view.setBackgroundColor(activatedBackgroundColor);
    }

    @Override
    public void onClick(View view) {
        if(adapter != null && adapter.get() != null) {
            if (adapter.get().getSelectedIndex() == getAdapterPosition()) {
                setSelected(false);
                adapter.get().clearSelection(false);
            } else {
                setSelected(true);
                adapter.get().setSelected(this);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        backgroundFrame.onTouchEvent(motionEvent);

        return view.onTouchEvent(motionEvent);
    }
}
