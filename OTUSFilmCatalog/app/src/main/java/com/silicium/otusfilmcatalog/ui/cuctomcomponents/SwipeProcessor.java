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

    public void setSwipeDeletionPossible(boolean isPossible) {
        isDelBySwipePossible = isPossible;
    }

    private boolean isDelBySwipePossible = false;
    private boolean swipeBack = false;
    @Nullable
    private RecyclerView.ViewHolder lastProcessedViewHolder = null;

    public SwipeProcessor(@NonNull IItemTouchHelperAdapter itemAdapter) {
        this.itemAdapter = itemAdapter;
    }

    /**
     * Should return a composite flag which defines the enabled move directions in each state
     * (idle, swiping, dragging).
     * <p>
     * Instead of composing this flag manually, you can use {@link #makeMovementFlags(int,
     * int)}
     * or {@link #makeFlag(int, int)}.
     * <p>
     * This flag is composed of 3 sets of 8 bits, where first 8 bits are for IDLE state, next
     * 8 bits are for SWIPE state and third 8 bits are for DRAG state.
     * Each 8 bit sections can be constructed by simply OR'ing direction flags defined in
     * {@link ItemTouchHelper}.
     * <p>
     * For example, if you want it to allow swiping LEFT and RIGHT but only allow starting to
     * swipe by swiping RIGHT, you can return:
     * <pre>
     *      makeFlag(ACTION_STATE_IDLE, RIGHT) | makeFlag(ACTION_STATE_SWIPE, LEFT | RIGHT);
     * </pre>
     * This means, allow right movement while IDLE and allow right and left movement while
     * swiping.
     *
     * @param recyclerView The RecyclerView to which ItemTouchHelper is attached.
     * @param viewHolder   The ViewHolder for which the movement information is necessary.
     * @return flags specifying which movements are allowed on this ViewHolder.
     * @see #makeMovementFlags(int, int)
     * @see #makeFlag(int, int)
     */
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    /**
     * Converts a given set of flags to absolution direction which means {@link ItemTouchHelper#START} and
     * {@link ItemTouchHelper#END} are replaced with {@link ItemTouchHelper#LEFT} and {@link ItemTouchHelper#RIGHT} depending on the layout
     * direction.
     *
     * @param flags           The flag value that include any number of movement flags.
     * @param layoutDirection The layout direction of the RecyclerView.
     * @return Updated flags which includes only absolute direction values.
     */
    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (swipeBack) {
            swipeBack = false;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    /**
     * Called when ItemTouchHelper wants to move the dragged item from its old position to
     * the new position.
     * <p>
     * If this method returns true, ItemTouchHelper assumes {@code viewHolder} has been moved
     * to the adapter position of {@code target} ViewHolder
     * ({@link RecyclerView.ViewHolder#getAdapterPosition()
     * ViewHolder#getAdapterPosition()}).
     * <p>
     * If you don't support drag & drop, this method will never be called.
     *
     * @param recyclerView The RecyclerView to which ItemTouchHelper is attached to.
     * @param viewHolder   The ViewHolder which is being dragged by the user.
     * @param target       The ViewHolder over which the currently active item is being
     *                     dragged.
     * @return True if the {@code viewHolder} has been moved to the adapter position of
     * {@code target}.
     * @see ItemTouchHelper.Callback#onMoved(RecyclerView, RecyclerView.ViewHolder, int, RecyclerView.ViewHolder, int, int, int)
     */
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    /**
     * Returns whether ItemTouchHelper should start a drag and drop operation if an item is
     * long pressed.
     * <p>
     * Default value returns true but you may want to disable this if you want to start
     * dragging on a custom view touch using {@link ItemTouchHelper#startDrag(RecyclerView.ViewHolder)}.
     *
     * @return True if ItemTouchHelper should start dragging an item when it is long pressed,
     * false otherwise. Default value is <code>true</code>.
     * @see ItemTouchHelper#startDrag(RecyclerView.ViewHolder)
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    /**
     * Returns whether ItemTouchHelper should start a swipe operation if a pointer is swiped
     * over the View.
     * <p>
     * Default value returns true but you may want to disable this if you want to start
     * swiping on a custom view touch using {@link ItemTouchHelper#startSwipe(RecyclerView.ViewHolder)}.
     *
     * @return True if ItemTouchHelper should start swiping an item when user swipes a pointer
     * over the View, false otherwise. Default value is <code>true</code>.
     * @see ItemTouchHelper#startSwipe(RecyclerView.ViewHolder)
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    /**
     * Called when a ViewHolder is swiped by the user.
     * <p>
     * If you are returning relative directions ({@link ItemTouchHelper#START} , {@link ItemTouchHelper#END}) from the
     * {@link ItemTouchHelper.Callback#getMovementFlags(RecyclerView, RecyclerView.ViewHolder)} method, this method
     * will also use relative directions. Otherwise, it will use absolute directions.
     * <p>
     * If you don't support swiping, this method will never be called.
     * <p>
     * ItemTouchHelper will keep a reference to the View until it is detached from
     * RecyclerView.
     * As soon as it is detached, ItemTouchHelper will call
     * {@link ItemTouchHelper.Callback#clearView(RecyclerView, RecyclerView.ViewHolder)}.
     *
     * @param viewHolder The ViewHolder which has been swiped by the user.
     * @param direction  The direction to which the ViewHolder is swiped. It is one of
     *                   {@link ItemTouchHelper#UP}, {@link ItemTouchHelper#DOWN},
     *                   {@link ItemTouchHelper#LEFT} or {@link ItemTouchHelper#RIGHT}. If your
     *                   {@link ItemTouchHelper.Callback#getMovementFlags(RecyclerView, RecyclerView.ViewHolder)}
     *                   method
     *                   returned relative flags instead of {@link ItemTouchHelper#LEFT} / {@link ItemTouchHelper#RIGHT};
     *                   `direction` will be relative as well. ({@link ItemTouchHelper#START} or {@link
     *                   ItemTouchHelper#END}).
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        itemAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    /**
     * Called by ItemTouchHelper on RecyclerView's onDraw callback.
     * <p>
     * If you would like to customize how your View's respond to user interactions, this is
     * a good place to override.
     * <p>
     * Default implementation translates the child by the given <code>dX</code>,
     * <code>dY</code>.
     * ItemTouchHelper also takes care of drawing the child after other children if it is being
     * dragged. This is done using child re-ordering mechanism. On platforms prior to L, this
     * is
     * achieved via {android.view.ViewGroup#getChildDrawingOrder(int, int)} and on L
     * and after, it changes View's elevation value to be greater than all other children.)
     *
     * @param c                 The canvas which RecyclerView is drawing its children
     * @param recyclerView      The RecyclerView to which ItemTouchHelper is attached to
     * @param viewHolder        The ViewHolder which is being interacted by the User or it was
     *                          interacted and simply animating to its original position
     * @param dX                The amount of horizontal displacement caused by user's action
     * @param dY                The amount of vertical displacement caused by user's action
     * @param actionState       The type of interaction on the View. Is either {@link
     *                          ItemTouchHelper#ACTION_STATE_DRAG} or {@link ItemTouchHelper#ACTION_STATE_SWIPE}.
     * @param isCurrentlyActive True if this view is currently being controlled by the user or
     *                          false it is simply animating back to its original state.
     * @see ItemTouchHelper.Callback#onChildDraw(Canvas, RecyclerView, RecyclerView.ViewHolder, float, float, int, boolean)
     */
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && !isDelBySwipePossible)
            swipeBack = true;
    }

    /**
     * Called by the ItemTouchHelper when user action finished on a ViewHolder and now the View
     * will be animated to its final position.
     * <p>
     * Default implementation uses ItemAnimator's duration values. If
     * <code>animationType</code> is {@link ItemTouchHelper#ANIMATION_TYPE_DRAG}, it returns
     * {@link RecyclerView.ItemAnimator#getMoveDuration()}, otherwise, it returns
     * {@link RecyclerView.ItemAnimator#getRemoveDuration()}. If RecyclerView does not have
     * any {@link RecyclerView.ItemAnimator} attached, this method returns
     * {@code DEFAULT_DRAG_ANIMATION_DURATION} or {@code DEFAULT_SWIPE_ANIMATION_DURATION}
     * depending on the animation type.
     *
     * @param recyclerView  The RecyclerView to which the ItemTouchHelper is attached to.
     * @param animationType The type of animation. Is one of {@link ItemTouchHelper#ANIMATION_TYPE_DRAG},
     *                      {@link ItemTouchHelper#ANIMATION_TYPE_SWIPE_CANCEL} or
     *                      {@link ItemTouchHelper#ANIMATION_TYPE_SWIPE_SUCCESS}.
     * @param animateDx     The horizontal distance that the animation will offset
     * @param animateDy     The vertical distance that the animation will offset
     * @return The duration for the animation
     */
    @Override
    public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
        if (animationType == ItemTouchHelper.ANIMATION_TYPE_SWIPE_CANCEL && !isDelBySwipePossible && lastProcessedViewHolder != null) {
            Animation shakeAnimation = AnimationUtils.loadAnimation(recyclerView.getContext(), R.anim.shake);
            lastProcessedViewHolder.itemView.startAnimation(shakeAnimation);
        }
        return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
    }

    /**
     * Called when the ViewHolder swiped or dragged by the ItemTouchHelper is changed.
     * <p/>
     * If you override this method, you should call super.
     *
     * @param viewHolder  The new ViewHolder that is being swiped or dragged. Might be null if
     *                    it is cleared.
     * @param actionState One of {@link ItemTouchHelper#ACTION_STATE_IDLE},
     *                    {@link ItemTouchHelper#ACTION_STATE_SWIPE} or
     *                    {@link ItemTouchHelper#ACTION_STATE_DRAG}.
     * @see #clearView(RecyclerView, RecyclerView.ViewHolder)
     */
    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (viewHolder != null)
            lastProcessedViewHolder = viewHolder;
    }
}
