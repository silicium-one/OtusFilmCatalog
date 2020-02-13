package com.silicium.otusfilmcatalog.ui.cuctomcomponents;

import android.graphics.Canvas;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.model.IItemTouchHelperAdapter;

public class SwipeProcessor extends ItemTouchHelper.Callback {
    @NonNull
    private final IItemTouchHelperAdapter itemAdapter;
    private boolean isDelBySwipePossible = false;
    private boolean swipeBack = false;
    @Nullable
    private RecyclerView.ViewHolder lastProcessedViewHolder = null;
    public SwipeProcessor(@NonNull IItemTouchHelperAdapter itemAdapter) {
        this.itemAdapter = itemAdapter;
    }

    public void setSwipeDeletionPossible(boolean isPossible) {
        isDelBySwipePossible = isPossible;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (swipeBack) {
            swipeBack = false;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        itemAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && !isDelBySwipePossible)
            swipeBack = true;
    }

    @Override
    public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
        if (animationType == ItemTouchHelper.ANIMATION_TYPE_SWIPE_CANCEL && !isDelBySwipePossible && lastProcessedViewHolder != null) {
            Animation shakeAnimation = AnimationUtils.loadAnimation(recyclerView.getContext(), R.anim.shake);
            lastProcessedViewHolder.itemView.startAnimation(shakeAnimation);
        }
        return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (viewHolder != null)
            lastProcessedViewHolder = viewHolder;
    }
}
