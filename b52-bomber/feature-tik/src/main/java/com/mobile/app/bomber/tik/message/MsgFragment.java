package com.mobile.app.bomber.tik.message;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.data.http.entities.ApiUsermsg;
import com.mobile.app.bomber.data.http.entities.Pager;
import com.mobile.app.bomber.runner.base.PrefsManager;
import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.AppRouterUtils;
import com.mobile.app.bomber.tik.base.GlideExtKt;
import com.mobile.app.bomber.tik.databinding.FragmentMsgBinding;
import com.mobile.app.bomber.tik.home.PlayListActivity;
import com.mobile.app.bomber.tik.home.TopMainFragment;
import com.mobile.app.bomber.tik.message.items.MsgItem;
import com.mobile.app.bomber.tik.mine.UserDetailActivity;
import com.mobile.app.bomber.tik.search.SearchActivity;
import com.mobile.guava.android.mvvm.RouterKt;
import com.mobile.guava.android.ui.view.recyclerview.EndlessRecyclerViewScrollListener;
import com.mobile.guava.jvm.domain.Source;
import com.pacific.adapter.AdapterImageLoader;
import com.pacific.adapter.AdapterUtils;
import com.pacific.adapter.AdapterViewHolder;
import com.pacific.adapter.OnDataSetChanged;
import com.pacific.adapter.RecyclerAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import timber.log.Timber;

public class MsgFragment extends TopMainFragment implements View.OnClickListener,
        OnDataSetChanged, AdapterImageLoader, SwipeRefreshLayout.OnRefreshListener {

    private final RecyclerAdapter adapter = new RecyclerAdapter();
    private final Pager pager = new Pager();
    private FragmentMsgBinding binding;
    private EndlessRecyclerViewScrollListener endless;
    protected MsgViewModel model;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = AppRouterUtils.viewModels(this, MsgViewModel.class);
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
                //load();
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
        GlideExtKt.loadProfile(this, item.data.getFromuserinfo().get(0).getPic(), imageView);
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
            AdapterViewHolder holder = AdapterUtils.INSTANCE.getHolder(v);
            MsgItem item = holder.item();
            if (item.data.getMsgtype() == 1) {
                UserDetailActivity.start(getActivity(), item.data.getVideoid());
            } else {
                PlayListActivity.start(getActivity(), item.data.getVideoid());
            }
            return;
        }
    }

    @Override
    public void onRefresh() {
        pager.reset();
        load();
    }

    private void load() {
        //消息提醒不需要分页加载
        // 调用接口 -》获取数据 -》存入数据库-》
        int time = (int) (System.currentTimeMillis() / 1000);
        model.postUserMsg(0, PrefsManager.INSTANCE.getMsgTime("")).observe(getViewLifecycleOwner(), source -> {
            List<ApiUsermsg.Item> items = new ArrayList<>();
            if (source instanceof Source.Success) {
                PrefsManager.INSTANCE.setMsgTime(time, "");
                List<ApiUsermsg.Item> list = source.requireData();
                items.addAll(list);
            } else {
                Msg.INSTANCE.handleSourceException(source.requireError());
            }

            model.getKey().observe(getActivity(), dbKeys -> {
                if (dbKeys != null) {

                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<ApiUsermsg.Item>>() {
                    }.getType();
                    items.addAll(gson.fromJson(dbKeys.getObj(), listType));
                }
                List<MsgItem> msgItems = items.stream()
                        .map(o -> new MsgItem(o))
                        .collect(Collectors.toList());
                adapter.replaceAll(msgItems);
                Gson gson = new Gson();
                String data = gson.toJson(items);
                model.addKey(data).observe(
                        this,
                        obj -> Timber.tag("db").d("增加成功%s", obj)
                );
                ;
            });


            binding.layoutRefresh.setRefreshing(false);
        });
    }

    public static MsgFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        MsgFragment fragment = new MsgFragment();
        fragment.setArguments(args);
        return fragment;
    }
}


