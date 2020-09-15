package com.mobile.app.bomber.tik.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mobile.app.bomber.runner.RunnerLib;
import com.mobile.guava.android.mvvm.AndroidX;
import com.pacific.adapter.AdapterUtils;
import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.RecyclerAdapter;
import com.mobile.guava.android.context.ActivityExtKt;
import com.mobile.guava.android.ui.view.recyclerview.LinearItemDecoration;
import com.mobile.guava.jvm.coroutines.Bus;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.AppRouterUtils;
import com.mobile.app.bomber.common.base.MyBaseActivity;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.tik.databinding.ActivitySearchBinding;
import com.mobile.app.bomber.tik.search.items.SearchHistoryItem;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class SearchActivity extends MyBaseActivity
        implements View.OnClickListener, SearchTitleBarPresenter.Callback {

    private ActivitySearchBinding binding;
    private RecyclerAdapter recyclerAdapter = new RecyclerAdapter();
    private List<Fragment> fragmentsList;
    private List<String> indexTitles;
    private String[] hotWords;
    private SearchTitleBarPresenter searchTitleBarPresenter;
    private SearchViewModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        model = AppRouterUtils.viewModels(this, SearchViewModel.class);
        recyclerAdapter.setOnClickListener(this);
        binding.searchHistoryRecycler.setAdapter(recyclerAdapter);
        binding.searchHistoryRecycler.addItemDecoration(
                LinearItemDecoration.builder(this)
                        .bottomMargin(R.dimen.size_2dp)
                        .build()
        );

        binding.searchHistoryRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchTitleBarPresenter = new SearchTitleBarPresenter(binding.toolbar, this);
        binding.tvHistoryClear.setOnClickListener(this);
        initHotSearchData();
        initSearchHistoryData();
        initSearchResultView();
    }

    /**
     * 初始化搜索结果view
     */
    private void initSearchResultView() {
        if (fragmentsList == null) {
            fragmentsList = new ArrayList<>();
            fragmentsList.add(new FragmentSearchVideo());
            fragmentsList.add(new FragmentSearchUser());
        }
        if (indexTitles == null) {
            indexTitles = new ArrayList<>();
            indexTitles.add("视频");
            indexTitles.add("用户");
        }
        FragmentTabAdapter fragmentTabAdapter = new FragmentTabAdapter(this, fragmentsList);
        binding.searchViewpager.setAdapter(fragmentTabAdapter);
        binding.searchViewpager.setOffscreenPageLimit(2);
        binding.searchViewpager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.searchViewpager.setCurrentItem(0, false);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(binding.searchLayoutTab, binding.searchViewpager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(indexTitles.get(position));
            }
        });
        tabLayoutMediator.attach();
    }


    /**
     * 初始化搜索历史数据
     */
    private void initSearchHistoryData() {
        model.getKeys().observe(this, dbSearchKeys -> {
            if (dbSearchKeys.size() > 0) {
                binding.tvHistoryClear.setVisibility(View.VISIBLE);
                List<SearchHistoryItem> items = new ArrayList();
                for (int i = 0; i < dbSearchKeys.size(); i++) {
                    Timber.tag("db").d(dbSearchKeys.get(i).getName());
                    SearchHistoryItem searchHistoryItem = new SearchHistoryItem(dbSearchKeys.get(i));
                    items.add(searchHistoryItem);
                }
                recyclerAdapter.replaceAll(items);
            }
        });
    }

    /**
     * 初始化热门搜索词汇
     */
    private void initHotSearchData() {
        hotWords = getResources().getStringArray(R.array.search_hot_labels);
        for (String hotWord : hotWords) {
            TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.item_tag_tv, binding.mShowBtnLayout, false);
            textView.setText(hotWord);
            textView.setTag(hotWord);
            textView.setOnClickListener(view -> {
                String keyword = (String) view.getTag();
                searchTitleBarPresenter.selectKeyword(keyword);
            });
            binding.mShowBtnLayout.addView(textView);
        }
    }

    /**
     * 处理搜索结果
     */
    public void handleSearchResult(String keyword) {
        //后期把data传过去,刷新搜索结果Fragment
        Bus.INSTANCE.offer(RunnerLib.BUS_SEARCH_RESULT);
        binding.layoutResultView.setVisibility(View.VISIBLE);
        binding.layoutSearchView.setVisibility(View.INVISIBLE);
        model.addKey(keyword).observe(
                this,
                obj -> Timber.tag("db").d("增加成功%s", obj.getName())
        );
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        AdapterViewHolder holder;
        SearchHistoryItem item;
        int position;
        int id = v.getId();
        if (id == R.id.tv_history_clear) {
            clearSearchHistory();
        } else if (id == R.id.delete) {  // 删除当前搜索历史记录
            holder = AdapterUtils.INSTANCE.getHolder(v);
            position = holder.getBindingAdapterPosition();
            item = holder.item();
            recyclerAdapter.remove(position);
            model.deleteKey(item.data).observe(
                    this,
                    count -> Timber.tag("db").d("删除成功%s", item.data.getName())
            );
        } else if (id == R.id.layout_item) {
            holder = AdapterUtils.INSTANCE.getHolder(v);
            item = holder.item();
            searchTitleBarPresenter.selectKeyword(item.data.getName());
        }
    }

    /**
     * 清空搜索历史记录
     */
    private void clearSearchHistory() {
        model.clearKeys().observe(this, integer -> {
            binding.tvHistoryClear.setVisibility(View.INVISIBLE);
            if (recyclerAdapter != null)
                recyclerAdapter.clear();
        });
    }

    @Override
    public void doSearch(String keyword) {
        ActivityExtKt.hideSoftInput(this);
        handleSearchResult(keyword);
    }

    @Override
    public void onBack() {
        if (binding.layoutResultView.getVisibility() == View.VISIBLE) {
            binding.layoutResultView.setVisibility(View.INVISIBLE);
            binding.layoutSearchView.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    private static class FragmentTabAdapter extends FragmentStateAdapter {

        private final List<Fragment> mFragments;

        public FragmentTabAdapter(FragmentActivity activity, List<Fragment> fragments) {
            super(activity);
            this.mFragments = fragments;
        }

        @Override
        public Fragment createFragment(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getItemCount() {
            return mFragments.size();
        }
    }
}
