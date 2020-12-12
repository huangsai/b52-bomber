package com.mobile.app.bomber.tik.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.MyBaseFragment;
import com.mobile.app.bomber.common.base.RecyclerAdapterEmpty;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.data.http.entities.ApiAtUser;
import com.mobile.app.bomber.data.http.entities.ApiMovieCollectionList;
import com.mobile.app.bomber.data.http.entities.ApiVideo;
import com.mobile.app.bomber.data.http.entities.Pager;
import com.mobile.app.bomber.movie.player.PlayerActivity;
import com.mobile.app.bomber.runner.RunnerX;
import com.mobile.app.bomber.runner.base.PrefsManager;
import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.AppRouterUtils;
import com.mobile.app.bomber.tik.base.GlideExtKt;
import com.mobile.app.bomber.tik.category.items.RankItem;
import com.mobile.app.bomber.tik.databinding.FragmentMeVideoBinding;
import com.mobile.app.bomber.tik.home.PlayListActivity;
import com.mobile.app.bomber.tik.mine.items.UserFavriteItem;
import com.mobile.app.bomber.tik.mine.items.UserVideoItem;
import com.mobile.ext.glide.GlideApp;
import com.mobile.guava.android.ui.view.recyclerview.EndlessRecyclerViewScrollListener;
import com.mobile.guava.android.ui.view.recyclerview.TestedGridItemDecoration;
import com.mobile.guava.data.Values;
import com.mobile.guava.jvm.domain.Source;
import com.pacific.adapter.AdapterImageLoader;
import com.pacific.adapter.AdapterUtils;
import com.pacific.adapter.AdapterViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import kotlin.Pair;

public class UserVideoFavoriteFragment extends MyBaseFragment implements AdapterImageLoader {
    private FragmentMeVideoBinding binding;
    private RecyclerAdapterEmpty recyclerAdapter;
    private MeViewModel meViewModel;
    private long userId;
    private static long selfID;
    private int type;
    private List<ApiMovieCollectionList.Movie> mVideos;
    private EndlessRecyclerViewScrollListener endlessListener;
    private Pager pager = new Pager();
    private UserDetailFragment userDetailFragment;

    public static UserVideoFavoriteFragment newInstance(int type, long userId, long self_userId, UserDetailFragment parentFragment) {
        Values.INSTANCE.put("UserVideoFavoriteFragment_type", type);
        selfID = self_userId;
        Values.INSTANCE.put("UserVideoFavoriteFragment_userId", userId);
        Values.INSTANCE.put("UserVideoFavoriteFragment_parentFragment", parentFragment);
        return new UserVideoFavoriteFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMeVideoBinding.inflate(inflater, container, false);
        meViewModel = AppRouterUtils.viewModels(this, MeViewModel.class);
        initRecyclerView();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        type = Values.INSTANCE.take("UserVideoFavoriteFragment_type");
        userId = Values.INSTANCE.take("UserVideoFavoriteFragment_userId");
        userDetailFragment = Values.INSTANCE.take("UserVideoFavoriteFragment_parentFragment");
        mVideos = new ArrayList<>();
        loadUserFavriteData(userId);
        if (userDetailFragment != null) {
            loadLikeVideoFavorites(userId);
        }
    }

    void initRecyclerView() {
        recyclerAdapter = new RecyclerAdapterEmpty();
        recyclerAdapter.setImageLoader(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        binding.recyclerView.addItemDecoration(new TestedGridItemDecoration(requireActivity(), R.dimen.size_1dp));
        binding.recyclerView.setLayoutManager(gridLayoutManager);
        endlessListener = new EndlessRecyclerViewScrollListener(gridLayoutManager, (integer, recyclerView) -> {
            if (pager.isAvailable()) {
                loadUserFavriteData(userId);
            }
            return null;
        });
        binding.recyclerView.addOnScrollListener(endlessListener);
        recyclerAdapter.setEmptyView(binding.emptyView.NoData, binding.recyclerView);
        recyclerAdapter.setOnClickListener(new View.OnClickListener() {

            @SingleClick
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.layout_user_video_item) {
                    if (mVideos != null) {
                        AdapterViewHolder holder = AdapterUtils.INSTANCE.getHolder(v);
                        UserFavriteItem item = holder.item();
                        PlayerActivity.Companion.start(getActivity(),item.data.getMovieId());
                    }
                }
            }
        });
        binding.recyclerView.setAdapter(recyclerAdapter);
    }

    public void loadUserFavriteData(long userId) {
        meViewModel.videosOfFavrite(userId,pager).observe(getViewLifecycleOwner(), listSource -> {
            if (listSource instanceof Source.Success) {
                List<ApiMovieCollectionList.Movie> videos = listSource.requireData();
                List<UserFavriteItem> items = new ArrayList();
                for (ApiMovieCollectionList.Movie movie : videos) {
                    UserFavriteItem myLikeVideoItem = new UserFavriteItem(movie, selfID);
                    items.add(myLikeVideoItem);
                }
                if (pager.isFirstPage(2)) {
                    recyclerAdapter.replaceAll(items);
                    mVideos = videos;
                } else {
                    recyclerAdapter.addAll(items);
                    mVideos.addAll(videos);
                }
                if (pager.isReachedTheEnd()) {
                    Msg.INSTANCE.toast("已加载完数据");
                    System.out.println("111");
                }
            }
        });
    }

    public void loadLikeVideoFavorites(long userId) {
        meViewModel.videosOfCollecCount(pager, userId).observe(getViewLifecycleOwner(), listSource -> {
            if (listSource instanceof Source.Success) {
                ApiMovieCollectionList movieCollectionList = listSource.requireData();
                userDetailFragment.setUserLikeVideoFavorites(movieCollectionList.getTotalCount());
            }
        });
    }

    @Override
    public void load(@NotNull ImageView imageView, @NotNull AdapterViewHolder holder) {
        UserFavriteItem item = holder.item();
        GlideApp.with(this)
                .load(GlideExtKt.decodeImgUrl(item.data.getCover()))
                .placeholder(R.drawable.nearby_absent)
                .transition(DrawableTransitionOptions.withCrossFade())
                .thumbnail(0.25f)
                .into(imageView);
    }

    private void loadData() {
        pager.reset();
        endlessListener.reset();
        loadUserFavriteData(userId);
        if (userDetailFragment != null) {
            loadLikeVideoFavorites(userId);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (selfID == 2) {
            userId = PrefsManager.INSTANCE.getUserId();
        }
     }

    @Override
    public void onBusEvent(@NotNull Pair<Integer, ?> event) {
        super.onBusEvent(event);
        if (event.getFirst() == RunnerX.BUS_FRAGMENT_ME_REFRESH || event.getFirst() == RunnerX.BUS_Login) {
            onResume();
            loadData();
        }
    }
}
