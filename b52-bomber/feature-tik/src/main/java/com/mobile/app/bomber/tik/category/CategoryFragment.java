package com.mobile.app.bomber.tik.category;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.MyBaseFragment;
import com.mobile.app.bomber.data.http.entities.ApiFixedad;
import com.mobile.app.bomber.tik.category.items.TitleVideoItem;
import com.mobile.app.bomber.tik.login.LoginViewModel;
import com.mobile.guava.jvm.domain.Source;
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
    private LinearLayoutManager linearLayoutManager;
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
        linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.recycler.setLayoutManager(linearLayoutManager);
        binding.recycler.setAdapter(adapter);
        // binding.imgSearch.setOnClickListener(this);
        binding.layoutRefresh.setOnRefreshListener(this);
        binding.layoutRefresh.setRefreshing(true);
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
        if (binding.recycler.getLayoutManager() == null) {
            binding.recycler.setLayoutManager(linearLayoutManager);
        }
        adapter.clear();
        load();


        // discoveryPresenter.onRefresh();
//        rankPresenter.onRefresh();
    }

    /**
     * 跳转到 搜索页面
     */
    @SingleClick
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.img_search) {
            RouterKt.newStartActivity(requireActivity(), SearchActivity.class);
            return;
        }
    }

    /**
     * 加载整个页面的数据
     */
    private void load() {
        List<RecyclerItem> list = new ArrayList<>();
        TitlePresenter titlePresenter = new TitlePresenter("火爆活动", R.drawable.fl_huobaohuodong);
        list.add(titlePresenter);
        titleVideoPresenter = new TitleVideoPresenter(this, binding);
        list.add(titleVideoPresenter);
        this.model.fixedAd().observe(this, source -> {
            if (source instanceof Source.Success) {
                ApiFixedad ad = source.requireData();
                ApiFixedad.FixedadObj obj = ad.getFixedadObj();
                List<TitleVideoItem> videoItem = new ArrayList<TitleVideoItem>();
                TitleVideoItem adItem = new TitleVideoItem(obj);
                videoItem.add(adItem);
                if (obj.getResolutionData().length() < 1) {
                    list.remove(titleVideoPresenter);
                    list.remove(titlePresenter);
                    adapter.remove(titlePresenter);
                    adapter.remove(titleVideoPresenter);
                }
            } else {
                Msg.INSTANCE.handleSourceException(source.requireError());
            }
            binding.layoutRefresh.setRefreshing(false);
        });

        // list.add(new TitlePresenter("发现精彩", R.drawable.fl_faxianjingcai));
        // discoveryPresenter = new DiscoveryVideoPresenter(this);
        // list.add(discoveryPresenter);

        list.add(new TitlePresenter("人气榜", R.drawable.fl_renqibang));
        rankPresenter = new RankPresenter(this);
        list.add(rankPresenter);

        list.add(new TitlePresenter("今日最新视频", R.drawable.fl_zuixinshiping));
        newVideoPresenter = new NewVideoPresenter(this, binding);
        list.add(newVideoPresenter);

        list.add(new TitlePresenter("今日最热视频", R.drawable.fl_zuireshiping));
        hotVideoPresenter = new HotVideoPresenter(this, binding);
        list.add(hotVideoPresenter);
        adapter.addAll(list);
    }

    /**
     * 初始化 分类页面 （fragment）
     */
    public static CategoryFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        CategoryFragment fragment = new CategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
