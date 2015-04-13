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
public class ReplacementsAdapter extends RecyclerView.Adapter<ReplacementViewHolder> {
    private List<Replacement> replacements;

    public ReplacementsAdapter(Context context) {
        this.replacements = new ArrayList<Replacement>(0);
        ReplacementViewHolder.load(context);
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

        return new ReplacementViewHolder(view);
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
}
