package com.mobile.app.bomber.tik.category;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.util.Preconditions;
import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.MyBaseActivity;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.data.http.entities.ApiRank;
import com.mobile.app.bomber.data.http.entities.Pager;
import com.mobile.app.bomber.runner.base.PrefsManager;
import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.AppRouterUtils;
import com.mobile.app.bomber.tik.base.GlideExtKt;
import com.mobile.app.bomber.tik.category.items.RankItem;
import com.mobile.app.bomber.tik.databinding.ActivityRankBinding;
import com.mobile.app.bomber.tik.home.PlayListActivity;
import com.mobile.app.bomber.tik.login.LoginActivity;
import com.mobile.app.bomber.tik.mine.UserDetailActivity;
import com.mobile.ext.glide.GlideApp;
import com.mobile.guava.android.mvvm.RouterKt;
import com.mobile.guava.android.ui.view.recyclerview.EndlessRecyclerViewScrollListener;
import com.mobile.guava.android.ui.view.recyclerview.RecyclerViewUtilsKt;
import com.mobile.guava.data.Values;
import com.mobile.guava.jvm.date.Java8TimeKt;
import com.mobile.guava.jvm.domain.Source;
import com.pacific.adapter.AdapterImageLoader;
import com.pacific.adapter.AdapterUtils;
import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.OnDataSetChanged;
import com.pacific.adapter.RecyclerAdapter;

import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RankActivity extends MyBaseActivity implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, AdapterImageLoader, OnDataSetChanged,
        CompoundButton.OnCheckedChangeListener {

    private final Pager pager = new Pager();
    private final RecyclerAdapter adapter = new RecyclerAdapter();
    private ActivityRankBinding binding;
    private EndlessRecyclerViewScrollListener endless;
    private CategoryViewModel model;
    private int type;
    private long time;
    private ZonedDateTime now;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRankBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        type = Values.INSTANCE.take("RankActivity");
        Preconditions.checkArgument(type == 1 || type == 2, "Unknown type");
        model = AppRouterUtils.viewModels(this, CategoryViewModel.class);
        now = ZonedDateTime.now(Java8TimeKt.getJdk8Zone());
        binding.imgBack.setOnClickListener(this);
        binding.rbWeek.setOnCheckedChangeListener(this);
        binding.rbMonth.setOnCheckedChangeListener(this);
        binding.rbYear.setOnCheckedChangeListener(this);
        adapter.setOnClickListener(this);
        adapter.setImageLoader(this);
        adapter.setOnDataSetChanged(this);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setAdapter(adapter);
        endless = new EndlessRecyclerViewScrollListener(
                binding.recycler.getLayoutManager(),
                (count, view) -> {
                    if (pager.isAvailable()) {
                        load();
                    }
                    return null;
                }
        );
        binding.recycler.addOnScrollListener(endless);
        binding.layoutRefresh.setOnRefreshListener(this);

        GlideApp.with(this)
                .load(type == 1 ? R.drawable.height_play : R.drawable.height_like)
                .into(binding.imgType);

        binding.rbWeek.setChecked(true);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (buttonView == binding.rbWeek) {
                time = now.minusDays(7).toEpochSecond();
            } else if (buttonView == binding.rbMonth) {
                time = 0;
            } else {
                time = now.minusDays(90).toEpochSecond();
            }
            binding.layoutRefresh.setRefreshing(true);
            onRefresh();
        }
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.img_back) {
            finish();
            return;
        }
        if (id == R.id.btn_follow) {
            if (!PrefsManager.INSTANCE.isLogin()) {
                RouterKt.newStartActivity(this, LoginActivity.class);
                return;
            }
            AdapterViewHolder holder = AdapterUtils.INSTANCE.getHolder(v);
            RankItem item = holder.item();
            follow(item.data, holder.getBindingAdapterPosition());
            return;
        }
        if (id == R.id.img_profile) {
            AdapterViewHolder holder = AdapterUtils.INSTANCE.getHolder(v);
            RankItem item = holder.item();
            UserDetailActivity.start(this,item.data.getUId());
            return;
        }
    }

    @Override
    public void onRefresh() {
        pager.reset();
        endless.reset();
        load();
    }

    private void load() {
        if (!pager.isAvailable()) return;

        (type == 1 ? model.ranksOfPlay(time, pager) : model.ranksOfLike(time, pager)
        ).observe(this, source -> {
            if (source instanceof Source.Success) {
                List<ApiRank.Rank> firstData = source.requireData();
                List<RankItem> data = source.requireData().stream()
                        .map(o -> new RankItem(o, firstData.get(0), type))
                        .collect(Collectors.toList());
                if (pager.isFirstPage(2)) {
                    adapter.replaceAll(data);
                } else {
                    adapter.addAll(data);
                }
                if (pager.isReachedTheEnd()) {
                    Msg.INSTANCE.toast("已加载完数据");
                }
            } else {
                Msg.INSTANCE.handleSourceException(source.requireError());
            }
            RecyclerViewUtilsKt.cancelRefreshing(binding.layoutRefresh, 500L);
            binding.txtTime.setText(("更新于 " + Java8TimeKt.yyyy_mm_dd_hh_mm_ss(System.currentTimeMillis())));
        });
    }

    private void follow(final ApiRank.Rank rank, final int position) {
        final boolean oldIsFollowing = rank.isFollowing();
        rank.setFollowing(!oldIsFollowing);
        adapter.notifyItemChanged(position, 0);
        model.follow(rank.getUId(), oldIsFollowing ? 1 : 0).observe(this, source -> {
            if (source instanceof Source.Error) {
                Msg.INSTANCE.handleSourceException(source.requireError());
                rank.setFollowing(oldIsFollowing);
                adapter.notifyItemChanged(position, 0);
            }
        });
    }

    @Override
    public void load(@NotNull ImageView imageView, @NotNull AdapterViewHolder holder) {
        int id = imageView.getId();
        RankItem item = holder.item();
        if (id == R.id.img_profile) {
            GlideExtKt.loadProfile(this, item.data.getPicUrl(), imageView);
            return;
        }
    }

    @Override
    public void apply(int count) {
        if (count > 0) {
            binding.txtEmpty.setVisibility(View.INVISIBLE);
        } else {
            binding.txtEmpty.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @param activity upstream Context
     * @param type     1 for playCount, 2 for like
     */
    public static void start(Activity activity, int type) {
        Preconditions.checkArgument(type == 1 || type == 2, "Unknown type");
        Values.INSTANCE.put("RankActivity", type);
        RouterKt.newStartActivity(activity, RankActivity.class);
    }
}
