package com.silicium.otusfilmcatalog.logic.view;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.model.FilmDescription;

class FilmItemViewHolder extends RecyclerView.ViewHolder {
    private ImageView film_cover_preview_imageView;
    private CheckBox item_selected_CheckBox;
    private TextView film_name_TextView;
    private TextView film_description_TextView;
    private CheckBox film_favorite_CheckBox;
    private Button film_detail_Button;
    private boolean isSelected = false;

    FilmItemViewHolder(@NonNull final View itemView) {
        super(itemView);

        film_cover_preview_imageView = itemView.findViewById(R.id.film_cover_preview_imageView);
        item_selected_CheckBox = itemView.findViewById(R.id.item_selected_CheckBox);
        film_name_TextView = itemView.findViewById(R.id.film_name_TextView);
        film_description_TextView = itemView.findViewById(R.id.film_description_TextView);
        film_favorite_CheckBox = itemView.findViewById(R.id.film_favorite_CheckBox);
        film_detail_Button = itemView.findViewById(R.id.film_detail_Button);

        item_selected_CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                resolveRootItemBackgroundColor();
            }
        });
    }

    void bind(@NonNull FilmDescription item, boolean isSelected, boolean isMultiselectMode, CompoundButton.OnCheckedChangeListener favoriteStateChangedListener, View.OnClickListener detailBtnClickListener, View.OnLongClickListener itemLongClickListener) {
        film_cover_preview_imageView.setImageBitmap(item.CoverPreview);
        film_name_TextView.setText(item.Name);
        film_description_TextView.setText(item.Description);
        film_favorite_CheckBox.setChecked(item.isFavorite());

        film_favorite_CheckBox.setTag(item.ID);
        film_detail_Button.setTag(item.ID);
        itemView.setTag(item.ID);

        film_favorite_CheckBox.setOnCheckedChangeListener(favoriteStateChangedListener);
        film_detail_Button.setOnClickListener(detailBtnClickListener);
        itemView.setOnLongClickListener(itemLongClickListener);

        this.isSelected = isSelected;
        if (isMultiselectMode)
            item_selected_CheckBox.setVisibility(View.VISIBLE);
        else {
            item_selected_CheckBox.setVisibility(View.GONE);
            item_selected_CheckBox.setChecked(false);
        }
        resolveRootItemBackgroundColor();
    }

    private void resolveRootItemBackgroundColor() {
        boolean isChecked = item_selected_CheckBox.isChecked();
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = itemView.getContext().getTheme();
        if (isSelected)
            theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, typedValue, true);
        else if (isChecked)
            theme.resolveAttribute(R.attr.multipleSelectionBackgroundColor, typedValue, true);
        else
            theme.resolveAttribute(R.attr.background, typedValue, true);
        int color = typedValue.data;
        itemView.setBackgroundColor(color);
    }
}
