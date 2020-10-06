package com.mobile.app.bomber.tik.mine;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.mobile.app.bomber.runner.RunnerX;
import com.mobile.guava.android.mvvm.RouterKt;
import com.mobile.guava.data.Values;
import com.mobile.guava.jvm.coroutines.Bus;
import com.mobile.guava.jvm.domain.Source;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.AppRouterUtils;
import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.MyBaseActivity;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.tik.databinding.ActivityUserDetailBinding;

/**
 * 用户详情页
 */
public class UserDetailActivity extends MyBaseActivity implements View.OnClickListener {

    private ActivityUserDetailBinding binding;
    private MeViewModel meViewModel;
    private long userId;

    public static void start(FragmentActivity activity, long userId) {
        Values.INSTANCE.put("UserDetailActivity_userId", userId);
        RouterKt.newStartActivity(activity, UserDetailActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailBinding.inflate(getLayoutInflater());
        meViewModel = AppRouterUtils.viewModels(this, MeViewModel.class);
        setContentView(binding.getRoot());
        userId = Values.INSTANCE.take("UserDetailActivity_userId");
        followStatus(userId);
        UserDetailFragment userDetailFragment = UserDetailFragment.newInstance(userId);
        addFragment(R.id.layout_container, userDetailFragment);
        binding.ibBack.setOnClickListener(this);
        binding.ibMore.setOnClickListener(this);
        binding.userBtnFollow.setOnClickListener(this);
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ib_back) {
            finish();
        } else if (id == R.id.ib_more) {
            RouterKt.showDialogFragment(this, UserDetailMoreDialogFragment.newInstance());
        } else if (id == R.id.user_btn_follow) {
            followDone(userId);
        }
    }

    public void handleFollowStatusView(boolean isUnFollow) {
        binding.userBtnFollow.setText(isUnFollow ? "+关注" : "已关注");
        binding.userBtnFollow.setSelected(!isUnFollow);
    }


    private void followStatus(long userId) {
        meViewModel.isFollowing(userId).observe(this, apiIsFollowingSource -> {
            if (apiIsFollowingSource instanceof Source.Success) {
                boolean followed = apiIsFollowingSource.requireData().isFollowed();
                handleFollowStatusView(!followed);
            } else {
                Msg.INSTANCE.handleSourceException(apiIsFollowingSource.requireError());
            }
        });
    }

    private void followDone(long userId) {
        boolean isUnFollow = !binding.userBtnFollow.getText().toString().equals("+关注");
        meViewModel.follow(userId, isUnFollow).observe(this, apiFollowSource -> {
            if (apiFollowSource instanceof Source.Success) {
                handleFollowStatusView(isUnFollow);
            } else {
                Msg.INSTANCE.handleSourceException(apiFollowSource.requireError());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bus.INSTANCE.offer(RunnerX.BUS_VIDEO_UPDATE);
    }
}



