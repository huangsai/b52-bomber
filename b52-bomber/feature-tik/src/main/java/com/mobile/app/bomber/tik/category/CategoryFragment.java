package com.mobile.app.bomber.tik.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mobile.app.bomber.common.base.MyBaseFragment;
import com.mobile.app.bomber.tik.login.LoginViewModel;
import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerItem;
import com.mobile.guava.android.mvvm.RouterKt;
import com.mobile.guava.android.ui.view.recyclerview.RecyclerViewUtilsKt;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.AppRouterUtils;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.tik.databinding.FragmentCatetoryBinding;
import com.mobile.app.bomber.tik.search.SearchActivity;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends MyBaseFragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private final RecyclerAdapter adapter = new RecyclerAdapter();
    private FragmentCatetoryBinding binding;

    private NewVideoPresenter newVideoPresenter;
    private HotVideoPresenter hotVideoPresenter;
    private DiscoveryVideoPresenter discoveryPresenter;
    private RankPresenter rankPresenter;
    private TitleVideoPresenter titleVideoPresenter;

    public CategoryViewModel model;
    public LoginViewModel loginViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = AppRouterUtils.viewModels(this, CategoryViewModel.class);
        loginViewModel = AppRouterUtils.viewModels(this, LoginViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCatetoryBinding.inflate(inflater, container, false);
        binding.recycler.setItemViewCacheSize(16);
        binding.recycler.setHasFixedSize(true);
        binding.recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recycler.setAdapter(adapter);
        // binding.imgSearch.setOnClickListener(this);
        binding.layoutRefresh.setOnRefreshListener(this);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getOnResumeCount() == 1) {
            load();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.recycler.setAdapter(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        RecyclerViewUtilsKt.cancelRefreshing(binding.layoutRefresh, 1000);
        newVideoPresenter.onRefresh();
        hotVideoPresenter.onRefresh();
        titleVideoPresenter.onRefresh();

        // discoveryPresenter.onRefresh();
        rankPresenter.onRefresh();
    }


    @SingleClick
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.img_search) {
            RouterKt.newStartActivity(requireActivity(), SearchActivity.class);
            return;
        }
    }

    private void load() {
        List<RecyclerItem> list = new ArrayList<>();
        list.add(new TitlePresenter("火爆活动", R.drawable.fl_huobaohuodong));
        titleVideoPresenter = new TitleVideoPresenter(this);
        list.add(titleVideoPresenter);

        // list.add(new TitlePresenter("发现精彩", R.drawable.fl_faxianjingcai));
        // discoveryPresenter = new DiscoveryVideoPresenter(this);
        // list.add(discoveryPresenter);

        list.add(new TitlePresenter("人气榜", R.drawable.fl_renqibang));
        rankPresenter = new RankPresenter(this);
        list.add(rankPresenter);

        list.add(new TitlePresenter("今日最新视频", R.drawable.fl_zuixinshiping));
        newVideoPresenter = new NewVideoPresenter(this);
        list.add(newVideoPresenter);

        list.add(new TitlePresenter("今日最热视频", R.drawable.fl_zuireshiping));
        hotVideoPresenter = new HotVideoPresenter(this);
        list.add(hotVideoPresenter);

        adapter.addAll(list);
    }

    public static CategoryFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        CategoryFragment fragment = new CategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
