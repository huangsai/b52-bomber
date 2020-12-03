package com.mobile.app.bomber.tik.mine;

import android.content.Intent;
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

import com.google.android.material.tabs.TabLayoutMediator;
import com.mobile.app.bomber.common.base.MyBaseFragment;
import com.mobile.app.bomber.common.base.tool.AppUtil;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.data.http.entities.ApiUser;
import com.mobile.app.bomber.data.http.entities.ApiUserCount;
import com.mobile.app.bomber.runner.RunnerX;
import com.mobile.app.bomber.runner.base.PrefsManager;
import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.AppRouterUtils;
import com.mobile.app.bomber.tik.base.GlideExtKt;
import com.mobile.app.bomber.tik.databinding.FragmentUserDetailBinding;
import com.mobile.guava.android.ui.view.text.TextViewUtilsKt;
import com.mobile.guava.data.Values;
import com.mobile.guava.jvm.coroutines.Bus;
import com.mobile.guava.jvm.domain.Source;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import kotlin.Pair;

public class UserDetailFragment extends MyBaseFragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private MeViewModel meViewModel;
    private FragmentUserDetailBinding binding;
    private MyAdapter adapter;
    private TabLayoutMediator tabLayoutMediator;
    private ApiUserCount mApiUserCount;
    protected ApiUser mApiUser;
    private List<String> indexTitle = new ArrayList<>();
    private long userId;
    private static long selfId;
    private String imgPath;


    public static UserDetailFragment newInstance(long userId, long selfID) {
        Values.INSTANCE.put("UserDetailFragment_userId", userId);
        selfId = selfID;
        return new UserDetailFragment();
    }

    @Override
    public void onBusEvent(@NotNull Pair<Integer, ?> event) {
        super.onBusEvent(event);
        if (event.getFirst() == RunnerX.BUS_fragmentME) {
            binding.userCopyWechat.setVisibility(View.GONE);
        } else if (event.getFirst() == RunnerX.BUS_Fragment_DTAIL) {
            binding.userCopyWechat.setVisibility(View.VISIBLE);
        }
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
    public void onViewCreated(@NotNull View view, @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.userCopyWechat.setOnClickListener(this);
        binding.userLike.setOnClickListener(this);
        binding.userFans.setOnClickListener(this);
        binding.userFollow.setOnClickListener(this);
        binding.userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), SpaceImageDetailActivity.class);
                int[] location = new int[2];
                binding.userProfile.getLocationOnScreen(location);
                intent.putExtra("locationX", location[0]);//必须
                intent.putExtra("locationY", location[1]);//必须
                intent.putExtra("width", binding.userProfile.getWidth());//必须
                intent.putExtra("height", binding.userProfile.getHeight());//必须
//                intent.putExtra("image", imgPath);//必须
//                startActivity(intent);
//                requireActivity().overridePendingTransition(0, 0);        //去掉activity的切换动画
            }
        });
//        indexTitle.add("作品0");
//        indexTitle.add("喜欢0");
//
//        adapter = new MyAdapter(requireActivity(), userId, this);
//        binding.viewPager.setAdapter(adapter);
//        binding.viewPager.setOffscreenPageLimit(2);
//        binding.swipeRefresh.setOnRefreshListener(this);
//        binding.swipeRefresh.setRefreshing(true);
//        tabLayoutMediator = new TabLayoutMediator(binding.layoutTab, binding.viewPager, (tab, position) -> tab.setText(indexTitle.get(position)));
//        tabLayoutMediator.attach();
        loadViewPageData();
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

    private void loadViewPageData() {
        indexTitle.add("作品0");
        indexTitle.add("喜欢0");
        adapter = new MyAdapter(requireActivity(), userId, selfId, this);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOffscreenPageLimit(2);
        binding.swipeRefresh.setRefreshing(true);
        binding.swipeRefresh.setOnRefreshListener(this);
        tabLayoutMediator = new TabLayoutMediator(binding.layoutTab, binding.viewPager, (tab, position) -> tab.setText(indexTitle.get(position)));
        tabLayoutMediator.attach();
    }

    private void loadData() {
        if (selfId == 2) {
            userId = PrefsManager.INSTANCE.getUserId();
        }
        getUserInfo();
        getUserCount();
    }

    private void getUserInfo() {
        if (PrefsManager.INSTANCE.isLogin()) {
            meViewModel.getUserInfo(userId, selfId).observe(getViewLifecycleOwner(), apiUserSource -> {
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
                    binding.userId.setText("ID：" + apiUser.getUid());
                    if (TextUtils.isEmpty(apiUser.getSign())) {
                        binding.userSign.setText("这个人很懒,什么也没写");
                    } else {
                        binding.userSign.setText(apiUser.getSign());
                    }
                    String birString = AppUtil.handleAgeStr(apiUser.getBirthday());
                    if (TextUtils.isEmpty(birString)) {
                        binding.userAge.setText("19");
                    } else {
                        binding.userAge.setText(birString);
                    }
                    if (TextUtils.isEmpty(apiUser.getWechat())) {
                        binding.userWechat.setText("微信号：weise00");
                    } else {
                        binding.userWechat.setText("微信号：" + apiUser.getWechat());
                    }
                    GlideExtKt.loadProfile(this, apiUser.getPic(), binding.userProfile);
//                    this.imgPath = FileUtil.saveBitmapToFile(binding.userProfile.getDrawingCache(), "header");
                } else {
                    binding.userId.setText("ID：" + 0);
                    binding.userWechat.setText("微信号：weise00");
                    binding.userGender.setText("女");
                    binding.userName.setText("test0");
                    binding.userSign.setText("这个人很懒,什么也没写");
                    binding.userAge.setText("19");
                }
            });
        }
    }

    private void getUserCount() {
        if (PrefsManager.INSTANCE.isLogin()) {
            meViewModel.getUserCount(userId).observe(getViewLifecycleOwner(), apiUserCountSource -> {
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
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onRefresh() {
        Bus.INSTANCE.offer(RunnerX.BUS_FRAGMENT_ME_REFRESH);
        loadData();
    }

    private static class MyAdapter extends FragmentStateAdapter {

        private long mUserId;
        private long self_id;
        private UserDetailFragment mUserDetailFragment;

        public MyAdapter(FragmentActivity activity, long mUserId, long selfID, UserDetailFragment userDetailFragment) {
            super(activity);
            this.self_id = selfID;
            this.mUserId = mUserId;
            this.mUserDetailFragment = userDetailFragment;
        }

        @NotNull
        @Override
        public Fragment createFragment(int position) {
            Fragment frament = null;
            switch (position) {
                case 0:
                    frament = UserVideoFragment.newInstance(UserVideoFragment.TYPE_VIDEO, this.mUserId, this.self_id, mUserDetailFragment);
                    break;
                case 1:
                    frament = UserVideoFragment.newInstance(UserVideoFragment.TYPE_LIKE, this.mUserId, this.self_id, mUserDetailFragment);
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