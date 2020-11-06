package com.mobile.app.bomber.tik.search;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.views.ShowButtonLayout;
import com.mobile.app.bomber.runner.RunnerX;
import com.mobile.guava.jvm.domain.Source;
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
        implements View.OnClickListener, SearchTitleBarPresenter.Callback, SwipeRefreshLayout.OnRefreshListener, TextView.OnEditorActionListener {

    private ActivitySearchBinding binding;
    private RecyclerAdapter recyclerAdapter = new RecyclerAdapter();
    private List<Fragment> fragmentsList;
    private List<String> indexTitles;
    private String[] hotWords;
    private SearchTitleBarPresenter searchTitleBarPresenter;
    private SearchViewModel model;
    private FragmentSearchVideo fragmentSearchVideo;
    private FragmentSearchUser fragmentSearchUser;
    private Bundle bundle;

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
        fragmentSearchVideo = new FragmentSearchVideo();
        fragmentSearchUser = new FragmentSearchUser();
        bundle = new Bundle();

        binding.searchHistoryRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchTitleBarPresenter = new SearchTitleBarPresenter(binding.toolbar, this);
        binding.tvHistoryClear.setOnClickListener(this);
        binding.swipeRefreshSearch.setOnRefreshListener(this);
        binding.swipeRefreshSearch.setRefreshing(true);
        binding.swipeRefreshSearch.setEnabled(true);
        binding.toolbar.etSearch.setOnEditorActionListener(this);

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
            fragmentsList.add(fragmentSearchVideo);
            fragmentsList.add(fragmentSearchUser);
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
//        recyclerAdapter.setEmptyView(binding.layoutEmptyView.NoData, binding.);

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
                binding.layoutEmptyView.NoData.setVisibility(View.GONE);
                List<SearchHistoryItem> items = new ArrayList();
                for (int i = 0; i < dbSearchKeys.size(); i++) {
                    Timber.tag("db").d(dbSearchKeys.get(i).getName());
                    SearchHistoryItem searchHistoryItem = new SearchHistoryItem(dbSearchKeys.get(i));
                    items.add(searchHistoryItem);
                }
                recyclerAdapter.replaceAll(items);
            } else {
                binding.layoutEmptyView.NoData.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 初始化热门搜索词汇
     */
    private void initHotSearchData() {
        model.getHotKeyTop().observe(this, source -> {
            if (binding.swipeRefreshSearch.isRefreshing())
                binding.swipeRefreshSearch.setRefreshing(false);
            binding.mShowBtnLayout.removeAllViews();
            if (source instanceof Source.Success) {
                List<String> hotKeys = source.requireData();
                for (int i = 0; i < hotKeys.size(); i++) {
                    TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.user_tag_tv, binding.mShowBtnLayout, false);
                    textView.setText(hotKeys.get(i).toString());
                    textView.setTag(i);
                    textView.setOnClickListener(view -> {
                        String keyword = hotKeys.get((Integer) view.getTag()).toString();
//                        Msg.INSTANCE.toast("当前的下标索引值：" + view.getTag().toString());
                        searchTitleBarPresenter.selectKeyword(keyword);
                    });
                    binding.mShowBtnLayout.addView(textView);
                }
            } else {
                Msg.INSTANCE.handleSourceException(source.requireError());
            }
        });
    }

    /**
     * 处理搜索结果
     */
    public void handleSearchResult(String keyword) {
        //后期把data传过去,刷新搜索结果Fragment
        binding.swipeRefreshSearch.setEnabled(false);
        bundle.putString("keyword", keyword);
        fragmentSearchVideo.setArguments(bundle);
        fragmentSearchUser.setArguments(bundle);

//        Bus.INSTANCE.offer(RunnerX.BUS_SEARCH_RESULT);
        fragmentSearchVideo.onRefresh();
        fragmentSearchUser.onRefresh();

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
            if (recyclerAdapter.getAll().size() < 1) {
                binding.layoutEmptyView.NoData.setVisibility(View.VISIBLE);
            } else {
                binding.layoutEmptyView.NoData.setVisibility(View.GONE);
            }
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
                binding.layoutEmptyView.NoData.setVisibility(View.VISIBLE);
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
        binding.swipeRefreshSearch.setEnabled(true);
    }

    @Override
    public void onRefresh() {
        initHotSearchData();
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

        if (i == EditorInfo.IME_ACTION_SEARCH) {
            hideInputKeyboard(getCurrentFocus());
            handleSearchResult(textView.getText().toString());
            return true;
        }
        return false;
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
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hideInputKeyboard(getCurrentFocus());
        return super.onTouchEvent(event);
    }

    protected void hideInputKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
