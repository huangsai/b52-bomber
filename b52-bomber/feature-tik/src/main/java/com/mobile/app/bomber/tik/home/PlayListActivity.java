package com.mobile.app.bomber.tik.home;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.gyf.immersionbar.ImmersionBar;
import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.MyBaseActivity;
import com.mobile.app.bomber.data.http.entities.ApiVideo;
import com.mobile.app.bomber.tik.base.AppRouterUtils;
import com.mobile.app.bomber.tik.databinding.ActivityPlayListBinding;
import com.mobile.app.bomber.tik.message.MsgViewModel;
import com.mobile.guava.android.mvvm.AndroidX;
import com.mobile.guava.android.mvvm.RouterKt;
import com.mobile.guava.data.Values;
import com.mobile.guava.jvm.coroutines.Bus;
import com.mobile.guava.jvm.domain.Source;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class PlayListActivity extends MyBaseActivity implements View.OnClickListener {

    private MyAdapter adapter;
    private ActivityPlayListBinding binding;
    private List<ApiVideo.Video> videos;
    private int playPosition;
    protected MsgViewModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ImmersionBar.with(this).init();
        super.onCreate(savedInstanceState);
        binding = ActivityPlayListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        final long videoId = Values.INSTANCE.take("PlayListActivity_videoId");

        if (videoId != -1L) {
            binding.progress.setVisibility(View.VISIBLE);
            playPosition = 0;
            model = AppRouterUtils.viewModels(this, MsgViewModel.class);
            getVideoById(videoId);
        } else {
            final String videoJson = "";
            if (videoJson != null && !videoJson.isEmpty()) {
                playPosition = 0;
                model = AppRouterUtils.viewModels(this, MsgViewModel.class);
                parseVideoByJson(videoJson);
            } else {
                playPosition = Values.INSTANCE.take("PlayListActivity_playPosition");
                videos = Values.INSTANCE.take("PlayListActivity");
                bindData();
            }
        }


        Bus.INSTANCE.offer(AndroidX.BUS_DIALOG_CLOSE);
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    private void parseVideoByJson(String videoJson) {
        Timber.e(videoJson);
        videos = Collections.singletonList(model.parseVideo(videoJson));
        bindData();
    }

    private void getVideoById(long videoId) {
        model.videoById(videoId).observe(this, source -> {
            if (source instanceof Source.Success) {
                videos = Collections.singletonList(source.requireData());
                bindData();
            } else {
                Msg.INSTANCE.handleSourceException(source.requireError());
            }
            binding.progress.setVisibility(View.INVISIBLE);
        });
    }

    private void bindData() {
        adapter = new MyAdapter();
        binding.viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setCurrentItem(playPosition, false);
        binding.imgBack.setOnClickListener(this);
    }

    private class MyAdapter extends FragmentStateAdapter {

        public MyAdapter() {
            super(PlayListActivity.this);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return PlayFragment.newInstance(position, videos.get(position));
        }

        @Override
        public int getItemCount() {
            return videos.size();
        }
    }

    public static void start(Activity activity, List<ApiVideo.Video> videos, int playPosition) {
        Values.INSTANCE.put("PlayListActivity_videoId", -1L);
        Values.INSTANCE.put("PlayListActivity", new ArrayList(videos));
        Values.INSTANCE.put("PlayListActivity_playPosition", playPosition);
        RouterKt.newStartActivity(activity, PlayListActivity.class);
    }

    public static void start(Activity activity, long videoId) {
        Values.INSTANCE.put("PlayListActivity_videoId", videoId);
        RouterKt.newStartActivity(activity, PlayListActivity.class);
    }

    public static void start(Activity activity, String videoJson) {
        Values.INSTANCE.put("PlayListActivity_videoId", -1L);
        Values.INSTANCE.put("PlayListActivity_videoJson", videoJson);
        RouterKt.newStartActivity(activity, PlayListActivity.class);
    }
}