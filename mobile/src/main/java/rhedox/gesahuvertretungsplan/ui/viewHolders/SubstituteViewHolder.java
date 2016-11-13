package rhedox.gesahuvertretungsplan.ui.viewHolders;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

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

    @BindView(R.id.lesson) AppCompatTextView lesson;
    @BindView(R.id.subject) AppCompatTextView subject;
    @BindView(R.id.teacher)  AppCompatTextView teacher;
    @BindView(R.id.substituteTeacher)  AppCompatTextView substituteTeacher;
    @BindView(R.id.room)  AppCompatTextView room;
    @BindView(R.id.hint)  AppCompatTextView hint;
    private Unbinder unbinder;

    @BindDrawable(R.drawable.circle) Drawable circleRelevantBackground;
    @BindDrawable(R.drawable.circle)  Drawable circleBackground;
    @ColorInt private int textColor;
    @ColorInt private int textColorRelevant;

	private SubstitutesContract.Presenter presenter;
	private int pagerPosition;

	private Animator backgroundAnimatorSelect;
	private Animator backgroundAnimatorUnselect;

	@SuppressWarnings("ResourceType")
    public SubstituteViewHolder(ViewGroup view, SubstitutesContract.Presenter presenter, int pagerPosition) {
        super(view);

	    TypedArray typedArray = view.getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.circleColor, R.attr.circleImportantColor, R.attr.circleTextColor, R.attr.circleImportantTextColor, R.attr.textPrimary, R.attr.textSecondary});
	    textColor = typedArray.getColor(2, 0);
	    textColorRelevant = typedArray.getColor(3, 0);
	    int circleColorRelevant = typedArray.getColor(1, 0);
	    int circleColor = typedArray.getColor(0, 0);
	    typedArray.recycle();

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

    public void setSelected(boolean selected) {

	    if (view != null) {
		    view.setActivated(selected);
		    if(selected) {
			    backgroundAnimatorUnselect.cancel();
			    backgroundAnimatorSelect.start();
		    }
		    else {
			    backgroundAnimatorSelect.cancel();
			    backgroundAnimatorUnselect.start();
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
