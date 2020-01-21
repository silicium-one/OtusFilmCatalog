package com.silicium.otusfilmcatalog.logic.view;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.model.FilmDescription;

class FilmItemViewHolder extends RecyclerView.ViewHolder {
    private ImageView film_cover_preview_imageView;
    private TextView film_name_TextView;
    private TextView film_description_TextView;
    private CheckBox film_favorite_CheckBox;
    private Button film_detail_Button;
    private View itemView;

    FilmItemViewHolder(@NonNull View itemView) {
        super(itemView);

        film_cover_preview_imageView = itemView.findViewById(R.id.film_cover_preview_imageView);
        film_name_TextView = itemView.findViewById(R.id.film_name_TextView);
        film_description_TextView = itemView.findViewById(R.id.film_description_TextView);
        film_favorite_CheckBox = itemView.findViewById(R.id.film_favorite_CheckBox);
        film_detail_Button = itemView.findViewById(R.id.film_detail_Button);
        this.itemView = itemView;
    }

    void bind(@NonNull FilmDescription item, boolean isSelected)
    {
        film_cover_preview_imageView.setImageBitmap(item.CoverPreview);
        film_name_TextView.setText(item.Name);
        film_description_TextView.setText(item.Description);
        film_favorite_CheckBox.setChecked(item.ifFavorite);
        film_detail_Button.setTag(item.ID);
        itemView.setTag(item.ID);

        if (isSelected) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = itemView.getContext().getTheme();
            theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, typedValue, true);
            int color = typedValue.data;
            itemView.setBackgroundColor(color);
        }
    }
}
