package com.mobile.app.bomber.tik.category;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.databinding.ItemCategoryTitleBinding;

import org.jetbrains.annotations.NotNull;

public class TitlePresenter extends SimpleRecyclerItem {

    @NonNull
    public final String name;

    @DrawableRes
    public final int icon;

    public TitlePresenter(@NonNull String name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        ItemCategoryTitleBinding binding = holder.binding(ItemCategoryTitleBinding::bind);
        binding.itemCategoryTitle.setText(name);
        binding.itemCategoryTitle.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
    }

    @Override
    public int getLayout() {
        return R.layout.item_category_title;
    }
}
