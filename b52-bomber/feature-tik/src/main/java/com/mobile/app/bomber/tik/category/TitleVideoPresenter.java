package com.mobile.app.bomber.tik.category;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.data.http.entities.ApiAd;
import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.AppRouterKt;
import com.mobile.app.bomber.tik.base.GlideExtKt;
import com.mobile.app.bomber.tik.category.items.DiscoveryVideoItem;
import com.mobile.app.bomber.tik.category.items.TheEndItem;
import com.mobile.app.bomber.tik.category.items.TitleVideoItem;
import com.mobile.app.bomber.tik.databinding.ItemCategoryTitleVideoBinding;
import com.mobile.app.bomber.tik.home.PlayListActivity;
import com.mobile.ext.glide.GlideApp;
import com.mobile.guava.jvm.domain.Source;
import com.pacific.adapter.AdapterUtils;
import com.pacific.adapter.AdapterViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TitleVideoPresenter extends BaseVideoPresenter {

    private TitleVideoItem videoItem;

    public TitleVideoPresenter(CategoryFragment fragment) {
        super(fragment);
    }

    @Override
    public int getLayout() {
        return R.layout.item_category_title_video;
    }

    @Override
    public void load(@NotNull ImageView imageView, @NotNull AdapterViewHolder holder) {
        TitleVideoItem item = holder.item();
        videoItem = item;
        GlideApp.with(fragment)
                .load(GlideExtKt.decodeImgUrl(item.data.getImage()))
                .placeholder(R.drawable.nearby_absent)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.img_title) {
            AppRouterKt.chrome(fragment, videoItem.data.getUrl());
            return;
        }
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return viewHolder.binding(ItemCategoryTitleVideoBinding::bind).recycler;
    }

    @Override
    protected View getEmptyView() {
        return viewHolder.binding(ItemCategoryTitleVideoBinding::bind).txtEmpty;
    }

    @Override
    protected void load() {
        if (!pager.isAvailable()) return;
        fragment.loginViewModel.ad(5).observe(fragment, source -> {
            if (source instanceof Source.Success) {
                ApiAd ad = source.requireData();
                List<TitleVideoItem> videoItem = new LinkedList<TitleVideoItem>();
                TitleVideoItem adItem = new TitleVideoItem(ad);
                videoItem.add(adItem);
                if (ad.getCode() == 0) {
                    adapter.replaceAll(videoItem);
                }
            }
        });
    }
}
