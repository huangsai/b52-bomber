package com.mobile.app.bomber.tik.mine;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mobile.app.bomber.runner.RunnerX;
import com.mobile.app.bomber.runner.base.PrefsManager;
import com.mobile.app.bomber.data.http.entities.ApiUser;
import com.mobile.app.bomber.data.http.entities.ApiUserCount;
import com.mobile.guava.data.Values;
import com.mobile.guava.android.ui.view.text.TextViewUtilsKt;
import com.mobile.guava.jvm.coroutines.Bus;
import com.mobile.guava.jvm.domain.Source;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.AppRouterUtils;
import com.mobile.app.bomber.tik.base.GlideExtKt;
import com.mobile.app.bomber.common.base.MyBaseFragment;
import com.mobile.app.bomber.common.base.tool.AppUtil;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.tik.databinding.FragmentUserDetailBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class UserDetailFragment extends MyBaseFragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private MeViewModel meViewModel;
    private FragmentUserDetailBinding binding;
    private ApiUserCount mApiUserCount;
    protected ApiUser mApiUser;
    private List<String> indexTitle = new ArrayList<>();
    private long userId;

    public static UserDetailFragment newInstance(long userId) {
        Values.INSTANCE.put("UserDetailFragment_userId", userId);
        return new UserDetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserDetailBinding.inflate(inflater, container, false);
        meViewModel = AppRouterUtils.viewModels(this, MeViewModel.class);
        userId = Values.INSTANCE.take("UserDetailFragment_userId");
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onViewCreated(@NotNull View view, @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.userCopyWechat.setOnClickListener(this);
        binding.userLike.setOnClickListener(this);
        binding.userFans.setOnClickListener(this);
        binding.userFollow.setOnClickListener(this);

        indexTitle.add("作品0");
        indexTitle.add("喜欢0");
        MyAdapter adapter = new MyAdapter(requireActivity(), userId, this);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOffscreenPageLimit(2);
        binding.swipeRefresh.setOnRefreshListener(this);
        binding.swipeRefresh.setRefreshing(true);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(binding.layoutTab, binding.viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(indexTitle.get(position));
            }
        });
        tabLayoutMediator.attach();
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.user_copy_wechat) {
            if (mApiUser != null)
                AppUtil.copyContent(requireContext(), mApiUser.getWechat());
        } else if (id == R.id.user_like) {
            showLikeDialog();
        } else if (id == R.id.user_fans) {
            if (userId == PrefsManager.INSTANCE.getUserId())
                AttentionFansActivity.start(requireActivity(), AttentionFansActivity.TYPE_FANS, userId);
        } else if (id == R.id.user_follow) {
            if (userId == PrefsManager.INSTANCE.getUserId())
                AttentionFansActivity.start(requireActivity(), AttentionFansActivity.TYPE_FOLLOW, userId);
        }
    }

    private void loadData() {
        getUserInfo();
        getUserCount();
    }

    private void getUserInfo() {
        if (PrefsManager.INSTANCE.isLogin()) {
            meViewModel.getUserInfo(PrefsManager.INSTANCE.getUserId()).observe(getViewLifecycleOwner(), apiUserSource -> {
                if (binding.swipeRefresh.isRefreshing()) binding.swipeRefresh.setRefreshing(false);
                if (apiUserSource instanceof Source.Success) {
                    ApiUser apiUser = apiUserSource.requireData();
                    this.mApiUser = apiUser;
                    if (apiUser.getGender() == 0) {
                        binding.userGender.setText("女");
                        binding.userGender.setBackgroundResource(R.drawable.tvgender_setting);
                        binding.userAge.setBackgroundResource(R.drawable.tvgender_setting);
                    } else {
                        binding.userGender.setText("男");
                        binding.userGender.setBackgroundResource(R.drawable.tvgenderman_setting);
                        binding.userAge.setBackgroundResource(R.drawable.tvgenderman_setting);
                    }
                    binding.userName.setText(apiUser.getUsername());
                    binding.userId.setText("ID:" + apiUser.getUid());
                    if (apiUser.getWechat().equals("") ||apiUser.getWechat().isEmpty()){
                        binding.userCopyWechat.setVisibility(View.GONE);
                    }else{
                        binding.userCopyWechat.setVisibility(View.VISIBLE);
                    }
                    binding.userWechat.setText("微信号:" + apiUser.getWechat());
                    String birString = AppUtil.handleAgeStr(apiUser.getBirthday());
                    if (TextUtils.isEmpty(birString)) {
                        binding.userAge.setVisibility(View.GONE);
                    } else {
                        binding.userAge.setVisibility(View.VISIBLE);
                        binding.userAge.setText(birString);
                    }
                    GlideExtKt.loadProfile(this, apiUser.getPic(), binding.userProfile);
                    binding.userSign.setText(apiUser.getSign());
                }else{
                    binding.userId.setText("ID:" + 0);
                    binding.userGender.setText("女");
                    binding.userAge.setText("19");
                }
            });
        }
    }

    private void getUserCount() {
        if (PrefsManager.INSTANCE.isLogin()) {
            meViewModel.getUserCount(PrefsManager.INSTANCE.getUserId()).observe(getViewLifecycleOwner(), apiUserCountSource -> {
                if (binding.swipeRefresh.isRefreshing()) binding.swipeRefresh.setRefreshing(false);
                if (apiUserCountSource instanceof Source.Success) {
                    ApiUserCount apiUserCount = apiUserCountSource.requireData();
                    this.mApiUserCount = apiUserCount;
                    TextViewUtilsKt.applyColorSpan(binding.userLike, String.valueOf(apiUserCount.getLikeCount()), " 获赞", R.color.white);
                    TextViewUtilsKt.applyColorSpan(binding.userFollow, String.valueOf(apiUserCount.getFollowCount()), " 关注", R.color.white);
                    TextViewUtilsKt.applyColorSpan(binding.userFans, String.valueOf(apiUserCount.getFansCount()), " 粉丝", R.color.white);
                }
            });
        }
    }

    private void showLikeDialog() {
        int likeCount = 0;
        if (mApiUserCount != null) likeCount = mApiUserCount.getLikeCount();
        String content = PrefsManager.INSTANCE.getLoginName() + "共获得" + likeCount + "个赞";
        ContentDialogFragment.newInstance(content).show(getChildFragmentManager(), null);
    }

    public void setUserVideoCount(int count) {
        binding.layoutTab.getTabAt(0).setText("作品" + count);
    }

    public void setUserLikeVideoCount(int count) {
        binding.layoutTab.getTabAt(1).setText("喜欢" + count);
    }

    @Override
    public void onRefresh() {
        Bus.INSTANCE.offer(RunnerX.BUS_FRAGMENT_ME_REFRESH);
        loadData();
    }

    private static class MyAdapter extends FragmentStateAdapter {

        private final long mUserId;
        private final UserDetailFragment mUserDetailFragment;

        public MyAdapter(FragmentActivity activity, long userId, UserDetailFragment userDetailFragment) {
            super(activity);
            this.mUserId = userId;
            this.mUserDetailFragment = userDetailFragment;
        }

        @NotNull
        @Override
        public Fragment createFragment(int position) {
            Fragment frament = null;
            switch (position) {
                case 0:
                    frament = UserVideoFragment.newInstance(UserVideoFragment.TYPE_VIDEO, mUserId, mUserDetailFragment);
                    break;
                case 1:
                    frament = UserVideoFragment.newInstance(UserVideoFragment.TYPE_LIKE, mUserId, mUserDetailFragment);
                    break;
            }
            return frament;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

}