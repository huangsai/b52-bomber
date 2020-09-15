package com.mobile.app.bomber.tik.category.items;

import android.graphics.Color;

import androidx.annotation.ColorInt;

import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.ItemCategoryTheEndBinding;

import org.jetbrains.annotations.NotNull;

public class TheEndItem extends SimpleRecyclerItem {

    public final int textColor;

    public TheEndItem() {
        this(Color.WHITE);
    }

    public TheEndItem(@ColorInt int textColor) {
        this.textColor = textColor;
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        ItemCategoryTheEndBinding binding = holder.binding(ItemCategoryTheEndBinding::bind);
        binding.itemCategoryTheEnd.setTextColor(textColor);
    }

    @Override
    public int getLayout() {
        return R.layout.item_category_the_end;
    }
}
