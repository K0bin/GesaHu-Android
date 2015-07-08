package rhedox.gesahuvertretungsplan.ui;

import android.animation.Animator;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Robin on 08.11.2014.
 */
public class SlideAnimator extends RecyclerView.ItemAnimator {

    private RecyclerView recyclerView;

    private final int addDuration = 200;
    private final int removeDuration = 200;

    public SlideAnimator(RecyclerView recyclerView) {
        setAddDuration(addDuration);
        setRemoveDuration(removeDuration);
        setMoveDuration(0);

        this.recyclerView=recyclerView;
    }

    @Override
    public void runPendingAnimations() {
    }

    private void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
        holder.itemView.animate().x(recyclerView.getWidth()+1).setDuration(removeDuration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                dispatchRemoveStarting(holder);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dispatchRemoveFinished(holder);
                holder.itemView.setX(0f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    private void animateAddImpl(final RecyclerView.ViewHolder holder) {
        holder.itemView.animate().x(0).setDuration(addDuration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                dispatchAddStarting(holder);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dispatchAddFinished(holder);
                holder.itemView.setX(0f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder viewHolder, int i, int i2, int i3, int i4) {
        dispatchMoveStarting(viewHolder);
        dispatchMoveFinished(viewHolder);
        return false;
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder viewHolder) {
    }

    @Override
    public void endAnimations() {
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder viewHolder) {
        final RecyclerView.ViewHolder holder = viewHolder;

        holder.itemView.setX(recyclerView.getWidth()+1);

        animateAddImpl(viewHolder);

        return true;
    }

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder viewHolder) {
        final RecyclerView.ViewHolder holder = viewHolder;

        animateRemoveImpl(viewHolder);

        return true;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2, int i, int i2, int i3, int i4) {
        return false;
    }

    @Override
    public void onAddFinished(RecyclerView.ViewHolder item) {
        super.onAddFinished(item);
    }
}
