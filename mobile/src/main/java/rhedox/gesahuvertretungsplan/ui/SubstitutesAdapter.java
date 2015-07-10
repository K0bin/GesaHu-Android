package rhedox.gesahuvertretungsplan.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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

    private Drawable highlightBackground;
    private Drawable background;
    private int textColor;
    private int highlightedTextColor;

    public SubstitutesAdapter(Context context) {
        this.substitutes = new ArrayList<Substitute>(0);

        highlightBackground = ContextCompat.getDrawable(context, R.drawable.circle_highlight);
        background = ContextCompat.getDrawable(context, R.drawable.circle);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{R.attr.circleTextColor, R.attr.circleHighlightedTextColor});
        textColor = typedArray.getColor(0, 0);
        highlightedTextColor = typedArray.getColor(1, 0);
        typedArray.recycle();
    }

    @Override
    public void onBindViewHolder(SubstituteViewHolder viewHolder, int i) {
        if(substitutes != null && substitutes.size() > i)
            viewHolder.setSubstitute(substitutes.get(i));
    }

    @Override
    public SubstituteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        FrameLayout view;
        view = (FrameLayout) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_replacement, viewGroup, false);

        return new SubstituteViewHolder(view, background, highlightBackground, textColor, highlightedTextColor);
    }

    @Override
    public int getItemCount() {
        return substitutes.size();
    }

    public void setSubstitutes(List<Substitute> substitutes) {
        this.substitutes = new ArrayList<Substitute>(substitutes);
    }

    public void addAll(List<Substitute> substitutes) {
        setSubstitutes(substitutes);
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


    public class SubstituteViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private AppCompatTextView lesson;
        private AppCompatTextView subjectName;
        private AppCompatTextView regularTeacher;
        private AppCompatTextView replacementTeacher;
        private AppCompatTextView room;
        private AppCompatTextView hint;

        private Drawable highlightBackground;
        private Drawable background;
        private int textColor;
        private int highlightedTextColor;

        public SubstituteViewHolder(ViewGroup view, Drawable background, Drawable highlightBackground, int textColor, int highlightedTextColor) {
            super(view);
            lesson = (AppCompatTextView) view.findViewById(R.id.lesson);
            subjectName = (AppCompatTextView) view.findViewById(R.id.subjectName);
            regularTeacher = (AppCompatTextView) view.findViewById(R.id.regularTeacher);
            replacementTeacher = (AppCompatTextView) view.findViewById(R.id.replacementTeacher);
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
                subjectName.setText(substitute.getSubject());
                regularTeacher.setText(substitute.getRegularTeacher());
                replacementTeacher.setText(substitute.getReplacementTeacher());
                room.setText(substitute.getRoom());
                hint.setText(substitute.getHint());
                if (substitute.getIsImportant()) {
                    lesson.setBackground(highlightBackground);
                    subjectName.setTypeface(Typeface.DEFAULT_BOLD);
                    lesson.setTextColor(highlightedTextColor);
                } else {
                    lesson.setBackground(background);
                    subjectName.setTypeface(Typeface.DEFAULT);
                    lesson.setTextColor(textColor);
                }
            }
        }
    }
}
