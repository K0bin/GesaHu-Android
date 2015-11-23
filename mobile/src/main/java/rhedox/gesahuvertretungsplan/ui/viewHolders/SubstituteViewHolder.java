package rhedox.gesahuvertretungsplan.ui.viewHolders;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.BindDrawable;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.ui.adapters.SubstitutesAdapter;

/**
 * Created by Robin on 21.11.2015.
 */


public class SubstituteViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case
    private View view;

    @Bind(R.id.lesson) AppCompatTextView lesson;
    @Bind(R.id.subject) AppCompatTextView subject;
    @Bind(R.id.teacher)  AppCompatTextView teacher;
    @Bind(R.id.substituteTeacher)  AppCompatTextView substituteTeacher;
    @Bind(R.id.room)  AppCompatTextView room;
    @Bind(R.id.hint)  AppCompatTextView hint;
    @Bind(R.id.backgroundFrame)  FrameLayout backgroundFrame;

    @BindDrawable(R.drawable.circle) Drawable circleHighlightedBackground;
    @BindDrawable(R.drawable.circle)  Drawable circleBackground;
    @ColorInt private int textColor;
    @ColorInt private int highlightedTextColor;

    @ColorInt private int activatedBackgroundColor;

    private WeakReference<SubstitutesAdapter> adapter;

    public SubstituteViewHolder(ViewGroup view, SubstitutesAdapter adapter, @ColorInt int circleColor, @ColorInt int circleHighlightedColor, @ColorInt int textColor, @ColorInt int highlightedTextColor, @ColorInt int activatedBackgroundColor) {
        super(view);

        this.view = view;
        ButterKnife.bind(this, view);

        view.setClickable(true);
        //view.setOnClickListener(this);
        //view.setOnTouchListener(this);

        circleBackground = DrawableCompat.wrap(circleBackground);
        DrawableCompat.setTint(circleBackground, circleColor);
        circleHighlightedBackground = DrawableCompat.wrap(circleHighlightedBackground);
        DrawableCompat.setTint(circleHighlightedBackground, circleHighlightedColor);

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
                lesson.setBackground(circleHighlightedBackground);
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

    @OnClick(R.id.rootFrame)
    public void Click(View view) {
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

    @OnTouch(R.id.rootFrame)
    public boolean onTouch(View view, MotionEvent motionEvent) {
        backgroundFrame.onTouchEvent(motionEvent);

        return view.onTouchEvent(motionEvent);
    }

    public void destroy() {
        ButterKnife.unbind(this);
    }
}
