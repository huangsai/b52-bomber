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
import com.mobile.ext.glide.GlideApp;
import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.MyBaseFragment;
import com.mobile.app.bomber.common.base.RecyclerAdapterEmpty;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.data.http.entities.ApiVideo;
import com.mobile.app.bomber.data.http.entities.Pager;
import com.mobile.guava.data.Values;
import com.mobile.app.bomber.runner.RunnerX;
import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.AppRouterUtils;
import com.mobile.app.bomber.tik.base.GlideExtKt;
import com.mobile.app.bomber.tik.databinding.FragmentMeVideoBinding;
import com.mobile.app.bomber.tik.home.PlayListActivity;
import com.mobile.app.bomber.tik.mine.items.UserVideoItem;
import com.mobile.guava.android.ui.view.recyclerview.EndlessRecyclerViewScrollListener;
import com.mobile.guava.android.ui.view.recyclerview.TestedGridItemDecoration;
import com.mobile.guava.jvm.domain.Source;
import com.pacific.adapter.AdapterImageLoader;
import com.pacific.adapter.AdapterUtils;
import com.pacific.adapter.AdapterViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import kotlin.Pair;

public class UserVideoFragment extends MyBaseFragment implements AdapterImageLoader {
    public static final int TYPE_VIDEO = 0; //作品
    public static final int TYPE_LIKE = 1; //喜欢

    private FragmentMeVideoBinding binding;
    private RecyclerAdapterEmpty recyclerAdapter;
    private MeViewModel meViewModel;
    private long userId;
    private int type;

    private List<ApiVideo.Video> mVideos;
    private EndlessRecyclerViewScrollListener endlessListener;
    private Pager pager = new Pager();
    private UserDetailFragment userDetailFragment;

    public static UserVideoFragment newInstance(int type, long userId, UserDetailFragment parentFragment) {
        Values.INSTANCE.put("UserVideoFragment_type", type);
        Values.INSTANCE.put("UserVideoFragment_userId", userId);
        Values.INSTANCE.put("UserVideoFragment_parentFragment", parentFragment);
        return new UserVideoFragment();
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
        type = Values.INSTANCE.take("UserVideoFragment_type");
        userId = Values.INSTANCE.take("UserVideoFragment_userId");
        userDetailFragment = Values.INSTANCE.take("UserVideoFragment_parentFragment");
        mVideos = new ArrayList<>();
        if (type == TYPE_VIDEO) {
            loadUserVideoData(userId);
            if (userDetailFragment != null) {
                loadUserVideoDataCount(userId);
            }
        } else if (type == TYPE_LIKE) {
            loadLikeVideoData(userId);
            if (userDetailFragment != null) {
                loadLikeVideoDataCount(userId);
            }
        }
    }

    void initRecyclerView() {
        recyclerAdapter = new RecyclerAdapterEmpty();
        recyclerAdapter.setImageLoader(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 3);
        binding.recyclerView.addItemDecoration(new TestedGridItemDecoration(requireActivity(), R.dimen.size_1dp));
        binding.recyclerView.setLayoutManager(gridLayoutManager);
        endlessListener = new EndlessRecyclerViewScrollListener(gridLayoutManager, (integer, recyclerView) -> {
            if (pager.isAvailable()) {
                if (type == TYPE_VIDEO) {
                    loadUserVideoData(userId);
                } else if (type == TYPE_LIKE) {
                    loadLikeVideoData(userId);
                }
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
                        PlayListActivity.start(requireActivity(), mVideos, AdapterUtils.INSTANCE.getHolder(v).getBindingAdapterPosition());
                    }
                }
            }
        });
        binding.recyclerView.setAdapter(recyclerAdapter);
    }

    public void loadLikeVideoData(long userId) {
        meViewModel.videosOfLike(pager, userId).observe(getViewLifecycleOwner(), listSource -> {
            if (listSource instanceof Source.Success) {
                List<ApiVideo.Video> videos = listSource.requireData();
                mVideos.addAll(videos);

                List<UserVideoItem> items = new ArrayList();
                for (ApiVideo.Video video : videos) {
                    UserVideoItem myLikeVideoItem = new UserVideoItem(video);
                    items.add(myLikeVideoItem);
                }
                if (pager.isReachedTheEnd()) {
                    Msg.INSTANCE.toast("已经加载完数据");
                }
                if (pager.isFirstPage(2)) {
                    recyclerAdapter.replaceAll(items);
                } else {
                    recyclerAdapter.addAll(items);
                }
            }
        });
    }

    public void loadUserVideoData(long userId) {
        meViewModel.videosOfUser(pager, userId).observe(getViewLifecycleOwner(), listSource -> {
            if (listSource instanceof Source.Success) {
                List<ApiVideo.Video> videos = listSource.requireData();
                mVideos.addAll(videos);
                List<UserVideoItem> items = new ArrayList();
                for (ApiVideo.Video video : videos) {
                    UserVideoItem myLikeVideoItem = new UserVideoItem(video);
                    items.add(myLikeVideoItem);
                }

                if (pager.isFirstPage(2)) {
                    recyclerAdapter.replaceAll(items);
                } else {
                    recyclerAdapter.addAll(items);
                }
                if (pager.isReachedTheEnd()) {
                    Msg.INSTANCE.toast("已加载完数据");
                }
            }
        });
    }


    public void loadUserVideoDataCount(long userId) {
        meViewModel.videosOfUserCount(pager, userId).observe(getViewLifecycleOwner(), listSource -> {
            if (listSource instanceof Source.Success) {
                ApiVideo video = listSource.requireData();
                userDetailFragment.setUserVideoCount(video.getTotalCount());
            }
        });
    }

    public void loadLikeVideoDataCount(long userId) {
        meViewModel.videosOfLikeCount(pager, userId).observe(getViewLifecycleOwner(), listSource -> {
            if (listSource instanceof Source.Success) {
                ApiVideo video = listSource.requireData();
                userDetailFragment.setUserLikeVideoCount(video.getTotalCount());
            }
        });
    }

    @Override
    public void load(@NotNull ImageView imageView, @NotNull AdapterViewHolder holder) {
        UserVideoItem item = holder.item();
        GlideApp.with(this)
                .load(GlideExtKt.decodeImgUrl(item.data.getCoverImageUrl()))
                .placeholder(R.drawable.nearby_absent)
                .transition(DrawableTransitionOptions.withCrossFade())
                .thumbnail(0.25f)
                .into(imageView);
    }

    @Override
    public void onBusEvent(@NotNull Pair<Integer, ?> event) {
        super.onBusEvent(event);
        if (event.getFirst() == RunnerX.BUS_FRAGMENT_ME_REFRESH) {
            pager.reset();
            endlessListener.reset();
            if (type == TYPE_VIDEO) {
                loadUserVideoData(userId);
                if (userDetailFragment != null) {
                    loadUserVideoDataCount(userId);
                }
            } else if (type == TYPE_LIKE) {
                loadLikeVideoData(userId);
                if (userDetailFragment != null) {
                    loadLikeVideoDataCount(userId);
                }
            }
        }
    }
}
