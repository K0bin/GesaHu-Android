package rhedox.gesahuvertretungsplan.RecyclerView;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
        viewHolder.setLesson(replacements.get(i).getLesson());
        viewHolder.setSubjectName(replacements.get(i).getSubject());
        viewHolder.setRegularTeacher(replacements.get(i).getRegularTeacher());
        viewHolder.setReplacementTeacher(replacements.get(i).getReplacementTeacher());
        viewHolder.setRoom(replacements.get(i).getRoom());
        viewHolder.setHint(replacements.get(i).getHint());
        viewHolder.setImportant(replacements.get(i).getImportant());
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
        private TextView lesson;
        private TextView subjectName;
        private TextView regularTeacher;
        private TextView replacementTeacher;
        private TextView room;
        private TextView hint;

        private Drawable highlightBackground;
        private Drawable background;

        public ReplacementViewHolder(ViewGroup view, Drawable highlightBackground, Drawable background) {
            super(view);
            lesson = (TextView) view.findViewById(R.id.lesson);
            subjectName = (TextView) view.findViewById(R.id.subjectName);
            regularTeacher = (TextView) view.findViewById(R.id.regularTeacher);
            replacementTeacher = (TextView) view.findViewById(R.id.replacementTeacher);
            room = (TextView) view.findViewById(R.id.room);
            hint = (TextView) view.findViewById(R.id.hint);

            this.highlightBackground = highlightBackground;
            this.background = background;
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
            hint.setText(text);
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
    }
}
