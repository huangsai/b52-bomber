package com.mobile.app.bomber.tik.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pacific.adapter.AdapterImageLoader;
import com.pacific.adapter.AdapterUtils;
import com.pacific.adapter.AdapterViewHolder;
import com.mobile.app.bomber.runner.base.PrefsManager;
import com.mobile.app.bomber.data.http.entities.ApiFollow;
import com.mobile.app.bomber.data.http.entities.ApiUserCount;
import com.mobile.guava.data.Values;
import com.mobile.guava.jvm.domain.Source;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.AppRouterUtils;
import com.mobile.app.bomber.tik.base.GlideExtKt;
import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.MyBaseFragment;
import com.mobile.app.bomber.common.base.RecyclerAdapterEmpty;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.tik.databinding.FragmentAttentionFansBinding;
import com.mobile.app.bomber.tik.mine.items.AttentionFansItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ZZ on 2016/9/8.
 */
public class AttentionFansFragment extends MyBaseFragment implements AdapterImageLoader, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private MeViewModel meViewModel;
    private FragmentAttentionFansBinding binding;
    private RecyclerAdapterEmpty recyclerAdapter;
    private int type;
    private long userId;

    public static AttentionFansFragment newInstance(int type, long userId) {
        Values.INSTANCE.put("AttentionFansFragment_type", type);
        Values.INSTANCE.put("AttentionFansFragment_userId", userId);
        return new AttentionFansFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAttentionFansBinding.inflate(inflater, container, false);
        meViewModel = AppRouterUtils.viewModels(this, MeViewModel.class);
        type = Values.INSTANCE.take("AttentionFansFragment_type");
        userId = Values.INSTANCE.take("AttentionFansFragment_userId");
        initRecyclerView();
        return binding.getRoot();

    }

    private void initRecyclerView() {
        recyclerAdapter = new RecyclerAdapterEmpty();
        recyclerAdapter.setImageLoader(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(layoutManager);

        binding.swipeRefresh.setOnRefreshListener(this);
        recyclerAdapter.setOnClickListener(this);

        recyclerAdapter.setEmptyView(binding.layoutEmptyView.NoData, binding.recyclerView);
        binding.recyclerView.setAdapter(recyclerAdapter);

        loadData();
    }

    private void loadData() {
        getUserCount();
        if (type == AttentionFansActivity.TYPE_FOLLOW) {
            getFollowsList();
        } else if (type == AttentionFansActivity.TYPE_FANS) {
            getFansList();
        }
    }

    private void getUserCount() {
        if (PrefsManager.INSTANCE.isLogin()) {
            if (PrefsManager.INSTANCE.isLogin()) {
                meViewModel.getUserCount(userId).observe(getViewLifecycleOwner(), apiUserCountSource -> {
                    if (binding.swipeRefresh.isRefreshing())
                        binding.swipeRefresh.setRefreshing(false);
                    if (apiUserCountSource instanceof Source.Success) {
                        ApiUserCount apiUserCount = apiUserCountSource.requireData();
                        AttentionFansActivity activity = (AttentionFansActivity) getActivity();
                        if (activity != null)
                            activity.setTabTitle(apiUserCount);
                    }
                });
            }
        }
    }

    private void getFollowsList() {
        if (PrefsManager.INSTANCE.isLogin()) {
            meViewModel.followList().observe(getViewLifecycleOwner(), apiFollowSource -> {
                if (binding.swipeRefresh.isRefreshing()) binding.swipeRefresh.setRefreshing(false);
                if (apiFollowSource instanceof Source.Success) {
                    List<ApiFollow.Follow> follows = apiFollowSource.requireData();
                    List<AttentionFansItem> items = new ArrayList();
                    for (ApiFollow.Follow follow : follows) {
                        AttentionFansItem attentionFansItem = new AttentionFansItem(follow);
                        items.add(attentionFansItem);
                    }
                    recyclerAdapter.replaceAll(items);
                }
            });
        }
    }

    private void getFansList() {
        if (PrefsManager.INSTANCE.isLogin()) {
            meViewModel.fanList().observe(getViewLifecycleOwner(), apiFansSource -> {
                if (binding.swipeRefresh.isRefreshing()) binding.swipeRefresh.setRefreshing(false);
                if (apiFansSource instanceof Source.Success) {
                    List<ApiFollow.Follow> fans = apiFansSource.requireData();
                    List<AttentionFansItem> items = new ArrayList();
                    for (ApiFollow.Follow fan : fans) {
                        AttentionFansItem attentionFansItem = new AttentionFansItem(fan);
                        items.add(attentionFansItem);
                    }
                    recyclerAdapter.replaceAll(items);
                }
            });
        }
    }

    public void followedState(Button button) {
        AttentionFansItem item = AdapterUtils.INSTANCE.getHolder(button).item();
        ApiFollow.Follow data = item.data;
        Boolean newIsFollowing = !data.isFollowing();
        meViewModel.follow(data.getFollowUid(), newIsFollowing).observe(this, apiFollowSource -> {
            if (apiFollowSource instanceof Source.Success) {
                data.setFollowing(newIsFollowing);
                recyclerAdapter.replace(item, new AttentionFansItem(data));
            } else {
                Msg.INSTANCE.handleSourceException(apiFollowSource.requireError());
            }
        });
    }

    @Override
    public void load(@NotNull ImageView imageView, @NotNull AdapterViewHolder adapterViewHolder) {
        AttentionFansItem item = adapterViewHolder.item();
        GlideExtKt.loadProfile(this, item.data.getProfile(), imageView);
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.status_btn) {
            followedState((Button) v);
        }
    }

    @Override
    public void onRefresh() {
        loadData();
    }
}
