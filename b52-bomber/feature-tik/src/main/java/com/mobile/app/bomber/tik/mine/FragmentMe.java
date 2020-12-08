package com.mobile.app.bomber.tik.mine;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mobile.app.bomber.runner.RunnerX;
import com.mobile.guava.android.mvvm.RouterKt;
import com.mobile.app.bomber.runner.base.PrefsManager;
import com.mobile.guava.data.Values;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.AppRouterKt;
import com.mobile.app.bomber.common.base.MyBaseFragment;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.tik.databinding.FragmentMeBinding;
import com.mobile.guava.jvm.coroutines.Bus;

import org.jetbrains.annotations.NotNull;

import kotlin.Pair;

public class FragmentMe extends MyBaseFragment implements View.OnClickListener {
    private FragmentMeBinding binding;
    private UserDetailFragment userDetailFragment;
    private static long selfId = 2;
    public static FragmentMe newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        FragmentMe fragment = new FragmentMe();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMeBinding.inflate(inflater, container, false);
        userDetailFragment = UserDetailFragment.newInstance(PrefsManager.INSTANCE.getUserId(),selfId);
        addFragment(R.id.layout_container, userDetailFragment);
        return binding.getRoot();
    }
    /**
     * 设置tag值来决定是否隐藏复制按钮 （100代表个人中心，101代表从首页进入作者主页）
     */
    @Override
    public void onResume() {
        super.onResume();
        PrefsManager.INSTANCE.setRefresh("100");
     }


    @Override
    public void onViewCreated(@NotNull View view, @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.edit.setOnClickListener(this);
        binding.settings.setOnClickListener(this);
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.edit) {
            AppRouterKt.requireLogin(requireActivity(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (userDetailFragment.mApiUser != null)
                        Values.INSTANCE.put("FragmentMe_user", userDetailFragment.mApiUser);
                    RouterKt.newStartActivity(this, EditInfoActivity.class);
                }
            });
        } else if (id == R.id.settings) {
            AppRouterKt.requireLogin(this.requireActivity(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (userDetailFragment.mApiUser != null)
                        Values.INSTANCE.put("FragmentMe_wechat", userDetailFragment.mApiUser.getWechat());
                    RouterKt.newStartActivity(this, SettingAcivity.class);
                }
            });
        }
    }
}