package com.silicium.otusfilmcatalog.logic.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.controller.FilmDescriptionStorage;
import com.silicium.otusfilmcatalog.logic.model.IItemTouchHelperAdapter;

import java.util.Collections;
import java.util.List;

public class FilmItemAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<FilmItemViewHolder> implements IItemTouchHelperAdapter {
    private final LayoutInflater inflater;
    private final List<String> filmIDs;
    private final CompoundButton.OnCheckedChangeListener favoriteStateChangedListener;
    private final View.OnClickListener detailBtnClickListener;
    private final View.OnLongClickListener itemLongClickListener;

    private String selectedFilmTag = "";

    public boolean isMultiselectMode() {
        return isMultiselectMode;
    }

    public void setMultiselectMode(boolean multiselectMode) {
        isMultiselectMode = multiselectMode;
        notifyDataSetChanged();
    }

    private boolean isMultiselectMode = false;

    public FilmItemAdapter(LayoutInflater inflater, List<String> filmIDs, CompoundButton.OnCheckedChangeListener favoriteStateChangedListener, View.OnClickListener detailBtnClickListener, View.OnLongClickListener itemLongClickListener) { // TODO: нарушение принципа Single Responsibility, исправить
        this.inflater = inflater;
        this.filmIDs = filmIDs;
        this.favoriteStateChangedListener = favoriteStateChangedListener;
        this.detailBtnClickListener = detailBtnClickListener;
        this.itemLongClickListener = itemLongClickListener;
    }

    /**
     * Called when RecyclerView needs a new {@link RecyclerView.ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)
     */
    @NonNull
    @Override
    public FilmItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FilmItemViewHolder(inflater.inflate(R.layout.item_film, parent, false));
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link RecyclerView.ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link RecyclerView.ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int, List)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull FilmItemViewHolder holder, int position) {
        String currentTag = filmIDs.get(position);
        holder.bind(FilmDescriptionStorage.getInstance().GetFilmByID(currentTag), currentTag.equals(selectedFilmTag), isMultiselectMode(), favoriteStateChangedListener, detailBtnClickListener, itemLongClickListener);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return filmIDs.size();
    }

    public void setSelectedFilmTag(String selectedFilmTag) {
        int prevPosition = filmIDs.indexOf(this.selectedFilmTag);
        this.selectedFilmTag = selectedFilmTag;
        int currentPosition = filmIDs.indexOf(this.selectedFilmTag);

        if (prevPosition != -1)
            notifyItemChanged(prevPosition); // снимаем выделение

        if (currentPosition != -1)
            notifyItemChanged(currentPosition); // ставим выделение
    }

    /**
     * Перестановка элементов в адаптере местами (поддержка drag-n-drop)
     *
     * @param fromPosition откуда
     * @param toPosition   куда
     */
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(filmIDs, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(filmIDs, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * Удаление элемента
     *
     * @param position откуда
     */
    @Override
    public void onItemDismiss(int position) {
        filmIDs.remove(position);
        notifyItemRemoved(position);
    }
}
