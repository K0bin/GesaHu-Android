package rhedox.gesahuvertretungsplan.ui.viewHolders;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.Substitute;
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract;

/**
 * Created by Robin on 21.11.2015.
 */


public class SubstituteViewHolder extends RecyclerView.ViewHolder {
    private View view;

    @BindView(R.id.lesson) TextView lesson;
    @BindView(R.id.subject) TextView subject;
    @BindView(R.id.teacher)  TextView teacher;
    @BindView(R.id.substituteTeacher)  TextView substituteTeacher;
    @BindView(R.id.room)  TextView room;
    @BindView(R.id.hint)  TextView hint;
    private Unbinder unbinder;

    @BindDrawable(R.drawable.circle) Drawable circleRelevantBackground;
    @BindDrawable(R.drawable.circle) Drawable circleBackground;
    @ColorInt private int textColor;
    @ColorInt private int textColorRelevant;
	@ColorInt private int windowColor;
	@ColorInt private int selectedColor;
	@Dimension private float selectedElevation;

	private SubstitutesContract.Presenter presenter;
	private int pagerPosition;

	private Animator backgroundAnimatorSelect;
	private Animator backgroundAnimatorUnselect;

	@SuppressWarnings("ResourceType")
    public SubstituteViewHolder(View view, SubstitutesContract.Presenter presenter, int pagerPosition, int textColor, int textColorRelevant, int circleColor, int circleColorRelevant, float selectedElevation) {
        super(view);

		this.textColor = textColor;
		this.textColorRelevant = textColorRelevant;
		this.selectedElevation = selectedElevation;

		windowColor = ContextCompat.getColor(view.getContext(), R.color.windowBackground);
		selectedColor = ContextCompat.getColor(view.getContext(), R.color.selected);

        this.view = view;
        unbinder = ButterKnife.bind(this, view);

        view.setClickable(true);

        circleBackground = DrawableCompat.wrap(circleBackground);
        DrawableCompat.setTint(circleBackground, circleColor);
        circleRelevantBackground = DrawableCompat.wrap(circleRelevantBackground);
        DrawableCompat.setTint(circleRelevantBackground, circleColorRelevant);

	    this.presenter = presenter;
	    this.pagerPosition = pagerPosition;

		backgroundAnimatorSelect = AnimatorInflater.loadAnimator(view.getContext(), R.animator.substitute_select);
		backgroundAnimatorSelect.setTarget(view);
		backgroundAnimatorUnselect = AnimatorInflater.loadAnimator(view.getContext(), R.animator.substitute_unselect);
		backgroundAnimatorUnselect.setTarget(view);
    }

    public void setSubstitute(Substitute substitute) {
        if(substitute != null) {
            lesson.setText(substitute.getLessonText());
            subject.setText(substitute.getTitle());
            teacher.setText(substitute.getTeacher());
            substituteTeacher.setText(substitute.getSubstitute());
            room.setText(substitute.getRoom());
            hint.setText(substitute.getHint());
            if (substitute.isRelevant()) {
                lesson.setBackground(circleRelevantBackground);
                subject.setTypeface(Typeface.DEFAULT_BOLD);
                lesson.setTextColor(textColorRelevant);
            } else {
                lesson.setBackground(circleBackground);
                subject.setTypeface(Typeface.DEFAULT);
                lesson.setTextColor(textColor);
            }
        }
    }

    public void setSelected(boolean selected, boolean animate) {
	    if (view != null) {
		    view.setActivated(selected);
		    if(animate) {
			    if (selected) {
				    backgroundAnimatorUnselect.cancel();
				    backgroundAnimatorSelect.start();
			    } else {
				    backgroundAnimatorSelect.cancel();
				    backgroundAnimatorUnselect.start();
			    }
		    } else {
			    if(selected) {
				    view.setBackgroundColor(selectedColor);
				    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					    view.setElevation(0);
				    }
			    } else {
				    view.setBackgroundColor(windowColor);
				    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					    view.setElevation(selectedElevation);
				    }
			    }
		    }
	    }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.rootFrame)
    public void Click(View view) {
		if(presenter != null)
			presenter.onListItemSelected(pagerPosition, this.getAdapterPosition());
    }

    public void destroy() {
        unbinder.unbind();
    }
}
