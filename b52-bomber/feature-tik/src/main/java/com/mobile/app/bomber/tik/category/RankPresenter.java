package com.mobile.app.bomber.tik.category;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.util.Preconditions;
import com.mobile.app.bomber.runner.base.PrefsManager;
import com.mobile.app.bomber.tik.databinding.FragmentCatetoryBinding;
import com.mobile.app.bomber.tik.login.LoginActivity;
import com.mobile.guava.android.mvvm.RouterKt;
import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.SimpleRecyclerItem;
import com.mobile.app.bomber.data.http.entities.ApiRank;
import com.mobile.app.bomber.data.http.entities.Pager;
import com.mobile.guava.jvm.date.Java8TimeKt;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.GlideExtKt;
import com.mobile.app.bomber.tik.databinding.ItemCategoryRankBinding;

import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.List;

public class RankPresenter extends SimpleRecyclerItem implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    @NonNull
    private final CategoryFragment fragment;

    @Nullable
    private AdapterViewHolder viewHolder;
    private FragmentCatetoryBinding binding;


    public RankPresenter(CategoryFragment fragment) {
        Preconditions.checkNotNull(fragment);
        this.fragment = fragment;
//        this.binding = bind;
    }

    @Override
    public void bind(@NotNull AdapterViewHolder holder) {
        Preconditions.checkArgument(viewHolder == null, "viewHolder != null");
        viewHolder = holder;
        ItemCategoryRankBinding binding = holder.binding(ItemCategoryRankBinding::bind);
        binding.layoutPlayCount.setOnClickListener(this);
        binding.layoutLikeCount.setOnClickListener(this);
        onRefresh();
    }

    @Override
    public void unbind(@NotNull AdapterViewHolder holder) {
        this.viewHolder = null;
    }

    @Override
    public int getLayout() {
        return R.layout.item_category_rank;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.layout_play_count) {
            if (PrefsManager.INSTANCE.isLogin()) {
                RankActivity.start(fragment.requireActivity(), 1);
            }else {
                RouterKt.newStartActivity(fragment, LoginActivity.class);
            }
            return;
        }

        if (id == R.id.layout_like_count) {
            if (PrefsManager.INSTANCE.isLogin()) {
                RankActivity.start(fragment.requireActivity(), 2);
            }else{
                RouterKt.newStartActivity(fragment, LoginActivity.class);
            }
            return;
        }
    }

    @Override
    public void onRefresh() {
        final long time = ZonedDateTime.now(Java8TimeKt.getJdk8Zone())
                .minusDays(7)
                .toEpochSecond();

        fragment.model.ranksOfPlay(time, new Pager(1)).observe(fragment, source -> {
            if (viewHolder == null) return;
            ItemCategoryRankBinding binding = viewHolder.binding(ItemCategoryRankBinding::bind);
            List<ApiRank.Rank> list = source.dataOrNull();
            if (list == null || list.isEmpty()) {
                binding.txtPlayName.setText("暂无排行榜");
                binding.imgPlayProfile.setImageResource(R.drawable.default_profile);
            } else {
                ApiRank.Rank obj = list.get(0);
                binding.txtPlayName.setText(obj.getUsername() + "\n" + "Top.1");
                GlideExtKt.loadProfile(fragment, obj.getPicUrl(), binding.imgPlayProfile);
            }
        });

        fragment.model.ranksOfLike(time, new Pager(1)).observe(fragment, source -> {
            if (viewHolder == null) return;
            ItemCategoryRankBinding binding = viewHolder.binding(ItemCategoryRankBinding::bind);
            List<ApiRank.Rank> list = source.dataOrNull();
            if (list == null || list.isEmpty()) {
                binding.txtLikeName.setText("暂无排行榜");
                binding.imgLikeProfile.setImageResource(R.drawable.default_profile);
            } else {
                ApiRank.Rank obj = list.get(0);
                binding.txtLikeName.setText(obj.getUsername() + "\n" + "Top.1");

                GlideExtKt.loadProfile(fragment, obj.getPicUrl(), binding.imgLikeProfile);
            }
//            binding.layoutRefresh.setRefreshing(false);
        });
    }
}
