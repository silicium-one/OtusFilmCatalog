package com.silicium.otusfilmcatalog.logic.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.controller.FilmDescriptionStorage;
import com.silicium.otusfilmcatalog.logic.model.IItemTouchHelperAdapter;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FilmItemAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<FilmItemViewHolder> implements IItemTouchHelperAdapter {
    class FilmItemData {
        final String filmID;
        boolean isChecked;

        FilmItemData(String filmID) {
            this.filmID = filmID;
            this.isChecked = false;
        }

        FilmItemData(String filmID, boolean isChecked) {
            this.filmID = filmID;
            this.isChecked = isChecked;
        }

        /**
         * Indicates whether some other object is "equal to" this one.
         * <p>
         * The {@code equals} method implements an equivalence relation
         * on non-null object references:
         * <ul>
         * <li>It is <i>reflexive</i>: for any non-null reference value
         * {@code x}, {@code x.equals(x)} should return
         * {@code true}.
         * <li>It is <i>symmetric</i>: for any non-null reference values
         * {@code x} and {@code y}, {@code x.equals(y)}
         * should return {@code true} if and only if
         * {@code y.equals(x)} returns {@code true}.
         * <li>It is <i>transitive</i>: for any non-null reference values
         * {@code x}, {@code y}, and {@code z}, if
         * {@code x.equals(y)} returns {@code true} and
         * {@code y.equals(z)} returns {@code true}, then
         * {@code x.equals(z)} should return {@code true}.
         * <li>It is <i>consistent</i>: for any non-null reference values
         * {@code x} and {@code y}, multiple invocations of
         * {@code x.equals(y)} consistently return {@code true}
         * or consistently return {@code false}, provided no
         * information used in {@code equals} comparisons on the
         * objects is modified.
         * <li>For any non-null reference value {@code x},
         * {@code x.equals(null)} should return {@code false}.
         * </ul>
         * <p>
         * The {@code equals} method for class {@code Object} implements
         * the most discriminating possible equivalence relation on objects;
         * that is, for any non-null reference values {@code x} and
         * {@code y}, this method returns {@code true} if and only
         * if {@code x} and {@code y} refer to the same object
         * ({@code x == y} has the value {@code true}).
         * <p>
         * Note that it is generally necessary to override the {@code hashCode}
         * method whenever this method is overridden, so as to maintain the
         * general contract for the {@code hashCode} method, which states
         * that equal objects must have equal hash codes.
         *
         * @param obj the reference object with which to compare.
         * @return {@code true} if this object is the same as the obj
         * argument; {@code false} otherwise.
         * @see #hashCode()
         * @see HashMap
         */
        @Contract(value = "null -> false", pure = true)
        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof FilmItemData)
                return filmID.equals(((FilmItemData) obj).filmID);
            return super.equals(obj);
        }
    }
    private final LayoutInflater inflater;
    private final List<FilmItemData> filmsItemsData;
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

    public FilmItemAdapter(LayoutInflater inflater, CompoundButton.OnCheckedChangeListener favoriteStateChangedListener, View.OnClickListener detailBtnClickListener, View.OnLongClickListener itemLongClickListener) { // TODO: нарушение принципа Single Responsibility, исправить
        this.inflater = inflater;
        this.filmsItemsData = new ArrayList<>();
        this.favoriteStateChangedListener = favoriteStateChangedListener;
        this.detailBtnClickListener = detailBtnClickListener;
        this.itemLongClickListener = itemLongClickListener;
    }

    public List<String> getCheckedIDs()
    {
        List<String> ret = new ArrayList<>();
        for (FilmItemData filmItemData: filmsItemsData) {
            if (filmItemData.isChecked)
                ret.add(filmItemData.filmID);
        }
        return ret;
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
    public void onBindViewHolder(@NonNull FilmItemViewHolder holder, final int position) {
        String currentTag = filmsItemsData.get(position).filmID;
        boolean isChecked = filmsItemsData.get(position).isChecked;
        holder.bind(FilmDescriptionStorage.getInstance().getFilmByID(currentTag), currentTag.equals(selectedFilmTag), isMultiselectMode(), isChecked, favoriteStateChangedListener, detailBtnClickListener, itemLongClickListener, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filmsItemsData.get(position).isChecked = isChecked;
            }
        });
    }

    /**
     * Called when a view created by this adapter has been recycled.
     *
     * <p>A view is recycled when a {@link RecyclerView.LayoutManager} decides that it no longer
     * needs to be attached to its parent {@link RecyclerView}. This can be because it has
     * fallen out of visibility or a set of cached views represented by views still
     * attached to the parent RecyclerView. If an item view has large or expensive data
     * bound to it such as large bitmaps, this may be a good place to release those
     * resources.</p>
     * <p>
     * RecyclerView calls this method right before clearing ViewHolder's internal data and
     * sending it to RecycledViewPool. This way, if ViewHolder was holding valid information
     * before being recycled, you can call {@link RecyclerView.ViewHolder#getAdapterPosition()} to get
     * its adapter position.
     *
     * @param holder The ViewHolder for the view being recycled
     */
    @Override
    public void onViewRecycled(@NonNull FilmItemViewHolder holder) {
        try {
            filmsItemsData.get(holder.getAdapterPosition()).isChecked = holder.isChecked();
        }
        catch (IndexOutOfBoundsException ignored)
        {

        }
        super.onViewRecycled(holder);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return filmsItemsData.size();
    }

    public void setSelectedFilmTag(String selectedFilmTag) {
        int prevPosition = filmsItemsData.indexOf(new FilmItemData(this.selectedFilmTag));
        this.selectedFilmTag = selectedFilmTag;
        int currentPosition = filmsItemsData.indexOf(new FilmItemData(this.selectedFilmTag));

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
                Collections.swap(filmsItemsData, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(filmsItemsData, i, i - 1);
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
        filmsItemsData.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(String filmID){
        FilmItemData item = new FilmItemData(filmID, false);
        filmsItemsData.add(item);
        notifyItemInserted(filmsItemsData.size()-1);
    }

    public void removeAllItems() {
        int size = filmsItemsData.size();
        filmsItemsData.clear();
        notifyItemRangeRemoved(0, size);
    }

    public String getFilmIDByPos(int position) {
        return filmsItemsData.get(position).filmID;
    }

    public int getPosByID(String filmID) {
        return filmsItemsData.indexOf(new FilmItemData(filmID));
    }

    public boolean removeItemByID(String filmID) {
        int pos = filmsItemsData.indexOf(new FilmItemData(filmID));
        if (pos == -1)
            return false;

        filmsItemsData.remove(pos);
        notifyItemRemoved(pos);
        return true;
    }
}
