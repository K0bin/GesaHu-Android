package rhedox.gesahuvertretungsplan.ui.adapters;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.afollestad.materialcab.MaterialCab;

import java.util.ArrayList;
import java.util.List;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;
import rhedox.gesahuvertretungsplan.ui.fragment.MainFragment;
import rhedox.gesahuvertretungsplan.util.SubstituteShareHelper;

/**
 * Created by Robin on 28.10.2014.
 */
public class SubstitutesAdapter extends RecyclerView.Adapter<SubstitutesAdapter.SubstituteViewHolder> {
    private List<Substitute> substitutes;

    private Context context;

    @ColorInt private int circleColorImportant;
    @ColorInt private int circleColor;
    @ColorInt private int textColor;
    @ColorInt private int highlightedTextColor;
    @ColorInt private int activatedBackgroundColor;
    private int selected = -1;
    private SubstituteViewHolder selectedViewHolder;

    private MainFragment.MaterialActivity activity;

    public SubstitutesAdapter(@NonNull Activity context) {
        this.substitutes = new ArrayList<Substitute>(0);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{R.attr.circleColor, R.attr.circleHighlightedColor, R.attr.circleTextColor, R.attr.circleHighlightedTextColor, R.attr.activatedColor});
        textColor = typedArray.getColor(2, 0);
        highlightedTextColor = typedArray.getColor(3, 0);
        circleColorImportant= typedArray.getColor(1, 0);
        circleColor = typedArray.getColor(0, 0);
        activatedBackgroundColor = typedArray.getColor(4,0);
        typedArray.recycle();

        this.context = context.getApplicationContext();

        if(context instanceof MainFragment.MaterialActivity)
            activity = (MainFragment.MaterialActivity)context;
    }

    @Override
    public void onBindViewHolder(@NonNull SubstituteViewHolder viewHolder, int i) {
        if(substitutes != null && substitutes.size() > i) {
            viewHolder.setSubstitute(substitutes.get(i));
            viewHolder.setSelected(i == selected && selected != -1);

            if(i == selected && selected != -1)
                selectedViewHolder = viewHolder;
            else if(selectedViewHolder == viewHolder)
                selectedViewHolder = null;
        }
    }

    @Override
    public SubstituteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        FrameLayout view;
        view = (FrameLayout) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_replacement, viewGroup, false);

        Drawable circleBackgroundHighlighted = DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.circle));
        DrawableCompat.setTint(circleBackgroundHighlighted, circleColorImportant);

        Drawable circleBackground = DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.circle));
        DrawableCompat.setTint(circleBackground, circleColor);

        SubstituteViewHolder viewHolder = new SubstituteViewHolder(view, circleBackground, circleBackgroundHighlighted, textColor, highlightedTextColor, activatedBackgroundColor);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return substitutes.size();
    }

    public void addAll(@Nullable List<Substitute> substitutes) {
        if(substitutes == null)
            removeAll();

        this.substitutes = new ArrayList<Substitute>(substitutes);
        if(getItemCount() > 0) {
            notifyItemRangeInserted(0, getItemCount());
        }
    }
    public void removeAll() {
        clearSelection(false);
        if(getItemCount() > 0) {
            int count = getItemCount();
            substitutes.clear();
            notifyItemRangeRemoved(0, count);
        }
    }

    public void setSelected(SubstituteViewHolder viewHolder) {
        if(viewHolder == null || selected == viewHolder.getAdapterPosition())
            return;

        if(selectedViewHolder != null)
            selectedViewHolder.setSelected(false);

        selected = viewHolder.getAdapterPosition();
        selectedViewHolder = viewHolder;

        if(activity != null) {
            activity.setCabVisibility(true);
        }
    }

    public void clearSelection(boolean cabFinished) {
        if(selectedViewHolder != null)
            selectedViewHolder.setSelected(false);

        selected = -1;
        selectedViewHolder = null;

        if(activity != null && !cabFinished)
                activity.setCabVisibility(false);
    }

    public int getSelectedIndex() {
        return selected;
    }

    public Substitute getSelected() {
        if(substitutes == null || selected == -1) return null;

        return substitutes.get(selected);
    }

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

        public SubstituteViewHolder(ViewGroup view, Drawable circleBackground, Drawable circleHighlightBackground, @ColorInt int textColor, @ColorInt int highlightedTextColor, @ColorInt int activatedBackgroundColor) {
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
            if(getSelectedIndex() == getAdapterPosition()) {
                setSelected(false);
                SubstitutesAdapter.this.clearSelection(false);
            }else {
                setSelected(true);
                SubstitutesAdapter.this.setSelected(this);
            }
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            backgroundFrame.onTouchEvent(motionEvent);

            return view.onTouchEvent(motionEvent);
        }
    }
}
