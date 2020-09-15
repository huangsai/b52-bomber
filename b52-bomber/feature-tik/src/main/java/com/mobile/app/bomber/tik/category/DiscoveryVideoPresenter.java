package com.mobile.app.bomber.tik.category;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.pacific.adapter.AdapterUtils;
import com.pacific.adapter.AdapterViewHolder;
import com.mobile.guava.jvm.domain.Source;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.common.base.GlideApp;
import com.mobile.app.bomber.tik.base.GlideExtKt;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.tik.category.items.DiscoveryVideoItem;
import com.mobile.app.bomber.tik.category.items.TheEndItem;
import com.mobile.app.bomber.tik.databinding.ItemCategoryDiscoveryBinding;
import com.mobile.app.bomber.tik.home.PlayListActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class DiscoveryVideoPresenter extends BaseVideoPresenter {

    public DiscoveryVideoPresenter(CategoryFragment fragment) {
        super(fragment);
    }

    @Override
    public int getLayout() {
        return R.layout.item_category_discovery;
    }

    @Override
    public void load(@NotNull ImageView imageView, @NotNull AdapterViewHolder holder) {
        DiscoveryVideoItem item = holder.item();
        GlideApp.with(fragment)
                .load(GlideExtKt.decodeImgUrl(item.data.getCoverImageUrl()))
                .placeholder(R.drawable.nearby_absent)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.item_category_discovery_video) {
            int position = AdapterUtils.INSTANCE.getHolder(v).getBindingAdapterPosition();
            PlayListActivity.start(fragment.requireActivity(), videos, position);
            return;
        }
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return viewHolder.binding(ItemCategoryDiscoveryBinding::bind).recycler;
    }

    @Override
    protected View getEmptyView() {
        return viewHolder.binding(ItemCategoryDiscoveryBinding::bind).txtEmpty;
    }

    @Override
    protected void load() {
        if (!pager.isAvailable()) return;

        fragment.model.videosOfLabel(pager, "独家原创").observe(fragment, source -> {
            if (source instanceof Source.Success) {
                List<DiscoveryVideoItem> items = source.requireData()
                        .stream()
                        .map(o -> new DiscoveryVideoItem(o))
                        .collect(Collectors.toList());

                if (!items.isEmpty()) {
                    if (pager.isFirstPage(2)) {
                        videos.clear();
                        adapter.replaceAll(items);
                    } else {
                        adapter.addAll(items);
                    }
                    videos.addAll(source.requireData());
                }
                if (pager.isReachedTheEnd() && adapter.getItemCount() > 3) {
                    adapter.add(new TheEndItem());
                }
            }
        });
    }
}
