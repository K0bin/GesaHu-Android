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

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import butterknife.Unbinder;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.ui.adapters.SelectableAdapter;

/**
 * Created by Robin on 21.11.2015.
 */


public class SubstituteViewHolder extends RecyclerView.ViewHolder {
    private View view;

    @BindView(R.id.lesson) AppCompatTextView lesson;
    @BindView(R.id.subject) AppCompatTextView subject;
    @BindView(R.id.teacher)  AppCompatTextView teacher;
    @BindView(R.id.substituteTeacher)  AppCompatTextView substituteTeacher;
    @BindView(R.id.room)  AppCompatTextView room;
    @BindView(R.id.hint)  AppCompatTextView hint;
    private Unbinder unbinder;

    @BindDrawable(R.drawable.circle) Drawable circleHighlightedBackground;
    @BindDrawable(R.drawable.circle)  Drawable circleBackground;
    @ColorInt private int textColor;
    @ColorInt private int highlightedTextColor;

    @ColorInt private int activatedBackgroundColor;

    //Prevent memory leak that could be the result of keeping an adapter reference
    private WeakReference<SelectableAdapter<Substitute, RecyclerView.ViewHolder>> adapter;

    public SubstituteViewHolder(ViewGroup view, SelectableAdapter<Substitute, RecyclerView.ViewHolder> adapter, @ColorInt int circleColor, @ColorInt int circleHighlightedColor, @ColorInt int textColor, @ColorInt int highlightedTextColor, @ColorInt int activatedBackgroundColor) {
        super(view);

        this.view = view;
        unbinder = ButterKnife.bind(this, view);

        view.setClickable(true);

        circleBackground = DrawableCompat.wrap(circleBackground);
        DrawableCompat.setTint(circleBackground, circleColor);
        circleHighlightedBackground = DrawableCompat.wrap(circleHighlightedBackground);
        DrawableCompat.setTint(circleHighlightedBackground, circleHighlightedColor);

        this.textColor = textColor;
        this.highlightedTextColor = highlightedTextColor;

        this.activatedBackgroundColor = activatedBackgroundColor;

        this.adapter = new WeakReference<SelectableAdapter<Substitute, RecyclerView.ViewHolder>>(adapter);
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
        if(view != null) {
            view.setActivated(selected);

            if (!selected)
                view.setBackgroundColor(0x0);
            else
                view.setBackgroundColor(activatedBackgroundColor);
        }
    }


    @SuppressWarnings("unused")
    @OnClick(R.id.rootFrame)
    public void Click(View view) {
        if(adapter != null && adapter.get() != null) {
            if (adapter.get().getSelectedIndex() == getAdapterPosition()) {
                adapter.get().clearSelection(false);
            } else {
                adapter.get().setSelected(this);
            }
        }
    }

    public void destroy() {
        unbinder.unbind();
    }
}
