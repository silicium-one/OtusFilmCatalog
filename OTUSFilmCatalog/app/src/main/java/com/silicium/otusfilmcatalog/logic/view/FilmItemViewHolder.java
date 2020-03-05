package com.silicium.otusfilmcatalog.logic.view;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.silicium.otusfilmcatalog.App;
import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.model.FilmDescription;

class FilmItemViewHolder extends RecyclerView.ViewHolder {
    private final ImageView film_cover_preview_image_view;
    private final CheckBox item_selected_check_box;
    private final TextView film_name_text_view;
    private final TextView film_description_text_view;
    private final CheckBox film_favorite_check_box;
    private final Button film_detail_button;
    private boolean isSelected = false;

    FilmItemViewHolder(@NonNull final View itemView) {
        super(itemView);

        film_cover_preview_image_view = itemView.findViewById(R.id.film_cover_preview_image_view);
        item_selected_check_box = itemView.findViewById(R.id.item_selected_check_box);
        film_name_text_view = itemView.findViewById(R.id.film_name_text_view);
        film_description_text_view = itemView.findViewById(R.id.film_description_text_view);
        film_favorite_check_box = itemView.findViewById(R.id.film_favorite_check_box);
        film_detail_button = itemView.findViewById(R.id.film_detail_button);
    }

    boolean isChecked() {
        return item_selected_check_box.isChecked();
    }

    void bind(@NonNull FilmDescription item, boolean isSelected, boolean isMultiselectMode, boolean isChecked, CompoundButton.OnCheckedChangeListener favoriteStateChangedListener, View.OnClickListener detailBtnClickListener, View.OnLongClickListener itemLongClickListener, @Nullable final CompoundButton.OnCheckedChangeListener checkedStateChangedListener) {

        // во время привязки срабатывание старых callback абсолютно лишнее дело.
        // В частности вызывает срабатывани IllegalStateException в FileItemAdapter.removeItemByID, если элемент ещё трясётся
        // и мы снимаем звёздочку с элемента, а потом сразу переключаемся к виду "Избранное"

        film_favorite_check_box.setOnCheckedChangeListener(null);
        film_detail_button.setOnClickListener(null);
        itemView.setOnLongClickListener(null);
        item_selected_check_box.setOnCheckedChangeListener(null);

        if (item.coverPreviewUrl.isEmpty()) {
            film_cover_preview_image_view.setImageBitmap(item.coverPreview);
        } else {
            //todo: добавить поддержку темы, размеры получать из film_cover_preview_image_view
            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(itemView.getContext());
            circularProgressDrawable.setStrokeWidth(5f);
            circularProgressDrawable.setCenterRadius(30f);
            circularProgressDrawable.start();

            Glide.with(App.getApplication().getApplicationContext())
                    .load(item.coverPreviewUrl)
                    .centerCrop()
                    .placeholder(circularProgressDrawable)
                    .error(R.drawable.ic_error_outline)
                    .into(film_cover_preview_image_view);
        }

        film_name_text_view.setText(item.name);
        film_description_text_view.setText(item.description);
        film_favorite_check_box.setChecked(item.isFavorite());

        film_favorite_check_box.setTag(item.ID);
        film_detail_button.setTag(item.ID);
        itemView.setTag(item.ID);

        this.isSelected = isSelected;
        if (isMultiselectMode) {
            item_selected_check_box.setVisibility(View.VISIBLE);
            item_selected_check_box.setChecked(isChecked);
        } else {
            item_selected_check_box.setVisibility(View.GONE);
            item_selected_check_box.setChecked(false);
        }
        resolveRootItemBackgroundColor();

        film_favorite_check_box.setOnCheckedChangeListener(favoriteStateChangedListener);
        film_detail_button.setOnClickListener(detailBtnClickListener);
        itemView.setOnLongClickListener(itemLongClickListener);

        item_selected_check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("SyntheticAccessor")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                resolveRootItemBackgroundColor();
                if (checkedStateChangedListener != null) {
                    checkedStateChangedListener.onCheckedChanged(buttonView, isChecked);
                }
            }
        });
    }

    private void resolveRootItemBackgroundColor() {
        boolean isChecked = item_selected_check_box.isChecked();
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = itemView.getContext().getTheme();
        if (isSelected)
            theme.resolveAttribute(R.attr.itemSelectedBackgroundColor, typedValue, true);
        else if (isChecked)
            theme.resolveAttribute(R.attr.multipleSelectionBackgroundColor, typedValue, true);
        else
            theme.resolveAttribute(R.attr.background, typedValue, true);
        int color = typedValue.data;
        itemView.setBackgroundColor(color);
    }
}
