package rhedox.gesahuvertretungsplan.ui.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.Substitute;

/**
 * Created by Robin on 28.10.2014.
 */
public class SubstitutesAdapter extends RecyclerView.Adapter<SubstitutesAdapter.SubstituteViewHolder> {
    private List<Substitute> substitutes;

    private Drawable highlightedBackground;
    private Drawable background;
    private int textColor;
    private int highlightedTextColor;

    public SubstitutesAdapter(@NonNull Context context) {
        this.substitutes = new ArrayList<Substitute>(0);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{R.attr.circleColor, R.attr.circleHighlightedColor, R.attr.circleTextColor, R.attr.circleHighlightedTextColor});
        textColor = typedArray.getColor(2, 0);
        highlightedTextColor = typedArray.getColor(3, 0);
        int circleColorImportant= typedArray.getColor(1, 0);
        int circleColor = typedArray.getColor(0, 0);
        typedArray.recycle();

        highlightedBackground = ContextCompat.getDrawable(context, R.drawable.circle);
        GradientDrawable highlightedGradientDrawable = (GradientDrawable) highlightedBackground;
        highlightedGradientDrawable.setColor(circleColorImportant);
        highlightedBackground = highlightedGradientDrawable;

        background = ContextCompat.getDrawable(context, R.drawable.circle);
        GradientDrawable gradientDrawable = (GradientDrawable) background;
        gradientDrawable.setColor(circleColor);
        background = gradientDrawable;
    }

    @Override
    public void onBindViewHolder(@NonNull SubstituteViewHolder viewHolder, int i) {
        if(substitutes != null && substitutes.size() > i)
            viewHolder.setSubstitute(substitutes.get(i));
    }

    @Override
    public SubstituteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        FrameLayout view;
        view = (FrameLayout) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_replacement, viewGroup, false);

        return new SubstituteViewHolder(view, background, highlightedBackground, textColor, highlightedTextColor);
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
        if(getItemCount() > 0) {
            int count = getItemCount();
            substitutes.clear();
            notifyItemRangeRemoved(0, count);
        }
    }


    public static class SubstituteViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private AppCompatTextView lesson;
        private AppCompatTextView subject;
        private AppCompatTextView teacher;
        private AppCompatTextView substituteTeacher;
        private AppCompatTextView room;
        private AppCompatTextView hint;

        private Drawable highlightBackground;
        private Drawable background;
        @ColorInt private int textColor;
        @ColorInt private int highlightedTextColor;

        public SubstituteViewHolder(ViewGroup view, Drawable background, Drawable highlightBackground, @ColorInt int textColor, @ColorInt int highlightedTextColor) {
            super(view);
            lesson = (AppCompatTextView) view.findViewById(R.id.lesson);
            subject = (AppCompatTextView) view.findViewById(R.id.subject);
            teacher = (AppCompatTextView) view.findViewById(R.id.teacher);
            substituteTeacher = (AppCompatTextView) view.findViewById(R.id.substituteTeacher);
            room = (AppCompatTextView) view.findViewById(R.id.room);
            hint = (AppCompatTextView) view.findViewById(R.id.hint);

            this.background = background;
            this.highlightBackground = highlightBackground;
            this.textColor = textColor;
            this.highlightedTextColor = highlightedTextColor;
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
                    lesson.setBackground(highlightBackground);
                    subject.setTypeface(Typeface.DEFAULT_BOLD);
                    lesson.setTextColor(highlightedTextColor);
                } else {
                    lesson.setBackground(background);
                    subject.setTypeface(Typeface.DEFAULT);
                    lesson.setTextColor(textColor);
                }
            }
        }
    }
}
