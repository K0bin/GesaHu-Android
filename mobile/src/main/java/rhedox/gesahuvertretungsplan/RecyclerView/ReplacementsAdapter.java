package rhedox.gesahuvertretungsplan.RecyclerView;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.Replacement;

/**
 * Created by Robin on 28.10.2014.
 */
public class ReplacementsAdapter extends RecyclerView.Adapter<ReplacementsAdapter.ReplacementViewHolder> {
    private List<Replacement> replacements;

    private Drawable highlightBackground;
    private Drawable background;

    public ReplacementsAdapter(Context context) {
        this.replacements = new ArrayList<Replacement>(0);

        highlightBackground = context.getResources().getDrawable(R.drawable.circle_highlight);
        background = context.getTheme().obtainStyledAttributes(new int[]{R.attr.circle}).getDrawable(0);
    }

    @Override
    public void onBindViewHolder(ReplacementViewHolder viewHolder, int i) {
        if(replacements!= null && replacements.size() > i)
            viewHolder.setReplacement(replacements.get(i));
    }

    @Override
    public ReplacementViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        FrameLayout view;
        view = (FrameLayout) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_replacement, viewGroup, false);

        return new ReplacementViewHolder(view, highlightBackground, background);
    }

    @Override
    public int getItemCount() {
        return replacements.size();
    }

    public void setReplacements(List<Replacement> replacements) {
        this.replacements = new ArrayList<Replacement>(replacements);
    }

    public void clear() {
        replacements.clear();
    }
    public void addAll() {
        if(getItemCount() > 0) {
            notifyItemRangeInserted(0, getItemCount());
        }
    }
    public void addAll(List<Replacement> replacements) {
        setReplacements(replacements);
        addAll();
    }
    public void removeAll() {
        if(getItemCount() > 0) {
            int count = getItemCount();
            clear();
            notifyItemRangeRemoved(0, count);
        }
    }


    public class ReplacementViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private AppCompatTextView lesson;
        private AppCompatTextView subjectName;
        private AppCompatTextView regularTeacher;
        private AppCompatTextView replacementTeacher;
        private AppCompatTextView room;
        private AppCompatTextView hint;

        private Drawable highlightBackground;
        private Drawable background;

        public ReplacementViewHolder(ViewGroup view, Drawable highlightBackground, Drawable background) {
            super(view);
            lesson = (AppCompatTextView) view.findViewById(R.id.lesson);
            subjectName = (AppCompatTextView) view.findViewById(R.id.subjectName);
            regularTeacher = (AppCompatTextView) view.findViewById(R.id.regularTeacher);
            replacementTeacher = (AppCompatTextView) view.findViewById(R.id.replacementTeacher);
            room = (AppCompatTextView) view.findViewById(R.id.room);
            hint = (AppCompatTextView) view.findViewById(R.id.hint);

            this.highlightBackground = highlightBackground;
            this.background = background;
        }

        public void setReplacement(Replacement replacement) {
            if(replacement != null) {
                lesson.setText(replacement.getLesson());
                subjectName.setText(replacement.getSubject());
                regularTeacher.setText(replacement.getRegularTeacher());
                replacementTeacher.setText(replacement.getReplacementTeacher());
                room.setText(replacement.getRoom());
                hint.setText(replacement.getHint());
                if (replacement.getImportant()) {
                    lesson.setBackground(highlightBackground);
                    subjectName.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    lesson.setBackground(background);
                    subjectName.setTypeface(Typeface.DEFAULT);
                }
            }
        }
    }
}
