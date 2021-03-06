package com.mobile.app.bomber.tik.category;

import android.graphics.Point;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.data.http.entities.ApiAd;
import com.mobile.app.bomber.data.http.entities.ApiFixedad;
import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.AppRouterKt;
import com.mobile.app.bomber.tik.base.GlideExtKt;
import com.mobile.app.bomber.tik.category.items.DiscoveryVideoItem;
import com.mobile.app.bomber.tik.category.items.TheEndItem;
import com.mobile.app.bomber.tik.category.items.TitleVideoItem;
import com.mobile.app.bomber.tik.databinding.FragmentCatetoryBinding;
import com.mobile.app.bomber.tik.databinding.ItemCategoryTitleVideoBinding;
import com.mobile.app.bomber.tik.home.PlayListActivity;
import com.mobile.ext.glide.GlideApp;
import com.mobile.guava.android.ui.screen.ScreenUtilsKt;
import com.mobile.guava.jvm.domain.Source;
import com.pacific.adapter.AdapterUtils;
import com.pacific.adapter.AdapterViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TitleVideoPresenter extends BaseVideoPresenter {

    private TitleVideoItem videoItem;
    private FragmentCatetoryBinding binding;

    public TitleVideoPresenter(CategoryFragment fragment, FragmentCatetoryBinding bind) {
        super(fragment);
        this.binding = bind;
    }

    @Override
    public int getLayout() {
        return R.layout.item_category_title_video;
    }

    @Override
    public void load(@NotNull ImageView imageView, @NotNull AdapterViewHolder holder) {
        TitleVideoItem item = holder.item();
        videoItem = item;
        Point a = ScreenUtilsKt.getScreen();
        int result = a.x / a.y;
        String imgeUrl = item.data.getResolutionData();
//        if (result == 9 / 18) { // 9:18分辨率
//            imgeUrl = item.data.getResolutionData();
//        } else if (result == 9 / 21) { //9:21分辨率
//            imgeUrl = item.data.getResolutionData().getTwentyOne();
//        } else {
//            imgeUrl = item.data.getResolutionData().getSixteen();
//        }
        GlideApp.with(fragment)
                .load(GlideExtKt.decodeImgUrl(imgeUrl))
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
        // 1920*1080==16:9的比例
//        Point a = ScreenUtilsKt.getScreen();
//        int result = a.x / a.y;
//        int result1 = 9 / 16;
//        Msg.INSTANCE.toast("==" + String.valueOf(result));

        fragment.model.fixedAd().observe(fragment, source -> {
            if (source instanceof Source.Success) {
                ApiFixedad ad = source.requireData();
                ApiFixedad.FixedadObj obj = ad.getFixedadObj();
                List<TitleVideoItem> videoItem = new ArrayList<TitleVideoItem>();
                TitleVideoItem adItem = new TitleVideoItem(obj);
                videoItem.add(adItem);
                if (ad.getCode() == 0 && videoItem.size() >= 1) {
                    adapter.replaceAll(videoItem);
                } else {
                    adapter.replaceAll(null);
                }
            } else {
                Msg.INSTANCE.handleSourceException(source.requireError());
            }
            binding.layoutRefresh.setRefreshing(false);
        });
    }
}
