package com.silicium.otusfilmcatalog.logic.view;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.controller.FilmDescriptionStorage;
import com.silicium.otusfilmcatalog.logic.model.IItemTouchHelperAdapter;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilmItemAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<FilmItemViewHolder> implements IItemTouchHelperAdapter {
    @NonNull
    private final LayoutInflater inflater;
    @NonNull
    private final List<FilmItemData> filmsItemsData;
    @Nullable
    private final CompoundButton.OnCheckedChangeListener favoriteStateChangedListener;
    @Nullable
    private final View.OnClickListener detailBtnClickListener;
    @Nullable
    private final View.OnLongClickListener itemLongClickListener;
    @NonNull
    private String selectedFilmTag = "";
    private boolean isMultiselectMode = false;

    public FilmItemAdapter(@NonNull LayoutInflater inflater, @Nullable CompoundButton.OnCheckedChangeListener favoriteStateChangedListener, @Nullable View.OnClickListener detailBtnClickListener, @Nullable View.OnLongClickListener itemLongClickListener) { // TODO: нарушение принципа Single Responsibility, исправить
        this.inflater = inflater;
        this.filmsItemsData = new ArrayList<>();
        this.favoriteStateChangedListener = favoriteStateChangedListener;
        this.detailBtnClickListener = detailBtnClickListener;
        this.itemLongClickListener = itemLongClickListener;
    }

    public boolean isMultiselectMode() {
        return isMultiselectMode;
    }

    public void setMultiselectMode(boolean multiselectMode) {
        isMultiselectMode = multiselectMode;
        notifyDataSetChanged();
    }

    @NonNull
    public List<String> getCheckedIDs() {
        List<String> ret = new ArrayList<>();
        for (FilmItemData filmItemData : filmsItemsData) {
            if (filmItemData.isChecked)
                ret.add(filmItemData.filmID);
        }
        return ret;
    }

    @NonNull
    @Override
    public FilmItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FilmItemViewHolder(inflater.inflate(R.layout.item_film, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FilmItemViewHolder holder, final int position) {
        String currentTag = filmsItemsData.get(position).filmID;
        boolean isChecked = filmsItemsData.get(position).isChecked;
        holder.bind(FilmDescriptionStorage.getInstance().getFilmByID(currentTag), currentTag.equals(selectedFilmTag), isMultiselectMode(), isChecked, favoriteStateChangedListener, detailBtnClickListener, itemLongClickListener, new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("SyntheticAccessor")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filmsItemsData.get(position).isChecked = isChecked;
            }
        });
    }

    @Override
    public void onViewRecycled(@NonNull FilmItemViewHolder holder) {
        try {
            filmsItemsData.get(holder.getAdapterPosition()).isChecked = holder.isChecked();
        } catch (IndexOutOfBoundsException ignored) {

        }
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return filmsItemsData.size();
    }

    public void setSelectedFilmTag(@NonNull String selectedFilmTag) {
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

    public void addItem(@NonNull String filmID) {
        FilmItemData item = new FilmItemData(filmID);
        filmsItemsData.add(item);
        notifyItemInserted(filmsItemsData.size() - 1);
    }

    public void removeAllItems() {
        int size = filmsItemsData.size();
        filmsItemsData.clear();
        notifyItemRangeRemoved(0, size);
    }

    @NonNull
    public String getFilmIDByPos(int position) {
        return filmsItemsData.get(position).filmID;
    }

    public int getPosByID(@NonNull String filmID) {
        return filmsItemsData.indexOf(new FilmItemData(filmID));
    }

    public void removeItemByID(@NonNull String filmID) {
        int pos = filmsItemsData.indexOf(new FilmItemData(filmID));
        if (pos == -1)
            return;

        filmsItemsData.remove(pos);
        notifyItemRemoved(pos);
    }

    class FilmItemData {
        @NonNull
        final String filmID;
        boolean isChecked;

        FilmItemData(@NonNull String filmID) {
            this.filmID = filmID;
            this.isChecked = false;
        }

        @Contract(value = "null -> false", pure = true)
        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof FilmItemData)
                return filmID.equals(((FilmItemData) obj).filmID);
            return super.equals(obj);
        }
    }
}
