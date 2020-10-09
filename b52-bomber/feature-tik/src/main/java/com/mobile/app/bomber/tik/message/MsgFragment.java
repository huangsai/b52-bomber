package com.mobile.app.bomber.tik.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.data.db.TikSearchKeyDao;
import com.pacific.adapter.AdapterImageLoader;
import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.OnDataSetChanged;
import com.pacific.adapter.RecyclerAdapter;
import com.mobile.guava.android.mvvm.RouterKt;
import com.mobile.app.bomber.data.http.entities.Pager;
import com.mobile.guava.android.ui.view.recyclerview.EndlessRecyclerViewScrollListener;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.GlideExtKt;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.tik.databinding.FragmentMsgBinding;
import com.mobile.app.bomber.tik.home.TopMainFragment;
import com.mobile.app.bomber.tik.message.items.MsgItem;
import com.mobile.app.bomber.tik.search.SearchActivity;

import java.util.ArrayList;
import java.util.List;

public class MsgFragment extends TopMainFragment implements View.OnClickListener,
        OnDataSetChanged, AdapterImageLoader, SwipeRefreshLayout.OnRefreshListener {

    private final RecyclerAdapter adapter = new RecyclerAdapter();
    private final Pager pager = new Pager();
    private FragmentMsgBinding binding;
    private EndlessRecyclerViewScrollListener endless;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter.setOnClickListener(this);
        adapter.setOnDataSetChanged(this);
        adapter.setImageLoader(this);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentMsgBinding.inflate(inflater, container, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        endless = new EndlessRecyclerViewScrollListener(layoutManager, (count, recyclerView) -> {
            if (pager.isAvailable()) {
                load();
            }
            return null;
        });
        binding.recycler.setLayoutManager(layoutManager);
        binding.recycler.setAdapter(adapter);
        binding.txtFans.setOnClickListener(this);
        binding.txtLike.setOnClickListener(this);
        binding.txtAt.setOnClickListener(this);
        binding.txtComment.setOnClickListener(this);
        binding.imgSearch.setOnClickListener(this);
        binding.layoutRefresh.setOnRefreshListener(this);
        binding.recycler.addOnScrollListener(endless);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter.isEmpty()) {
            binding.layoutRefresh.setRefreshing(true);
            onRefresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.recycler.setAdapter(null);
        binding.recycler.removeOnScrollListener(endless);
    }

    @Override
    public void apply(int i) {
        if (i > 0) {
            binding.txtEmpty.setVisibility(View.INVISIBLE);
        } else {
            binding.txtEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void load(@NonNull ImageView imageView, @NonNull AdapterViewHolder holder) {
        MsgItem item = holder.item();
        GlideExtKt.loadProfile(this, item.data, imageView);
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.txt_fans) {
            MineActivity.start(requireActivity(), 1);
            return;
        }

        if (id == R.id.txt_like) {
            MineActivity.start(requireActivity(), 2);
            return;
        }

        if (id == R.id.txt_at) {
            MineActivity.start(requireActivity(), 3);
            return;
        }

        if (id == R.id.txt_comment) {
            MineActivity.start(requireActivity(), 4);
            return;
        }
        if (id == R.id.img_search) {
            RouterKt.newStartActivity(this, SearchActivity.class);
            return;
        }
        if (id == R.id.item_msg) {
            Msg.INSTANCE.toast("点击了");
            return;
        }
    }

    @Override
    public void onRefresh() {
        pager.reset();
        load();
    }

    private void load() {
        List<MsgItem> item = new ArrayList<>();
        MsgItem item1 = new MsgItem("1111");
        item.add(item1);
        adapter.replaceAll(item);

       //消息提醒不需要分页加载
       // 调用接口 -》获取数据 -》存入数据库-》
        binding.layoutRefresh.setRefreshing(false);
    }

    public static MsgFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        MsgFragment fragment = new MsgFragment();
        fragment.setArguments(args);
        return fragment;
    }
}


