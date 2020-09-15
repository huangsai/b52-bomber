package com.mobile.app.bomber.tik.message;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.util.Preconditions;
import com.mobile.guava.android.mvvm.RouterKt;
import com.mobile.guava.https.Values;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.AppRouterKt;
import com.mobile.app.bomber.common.base.MyBaseActivity;
import com.mobile.app.bomber.tik.databinding.ActivityMineBinding;

import org.jetbrains.annotations.Nullable;

public class MineActivity extends MyBaseActivity implements View.OnClickListener {
    private ActivityMineBinding binding;
    private int flag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.layoutToolbar.toolbar.setTitle("");
        setSupportActionBar(binding.layoutToolbar.toolbar);
        binding.layoutToolbar.toolbar.setNavigationIcon(R.drawable.gg_fanhui);
        binding.layoutToolbar.toolbar.setNavigationOnClickListener(this);

        flag = Values.INSTANCE.take("MineActivity");
        Preconditions.checkArgument(flag >= 1 && flag <= 4, "Unknown flag");
        Fragment fragment;
        switch (flag) {
            case 1:
                binding.layoutToolbar.txtToolbarTitle.setText("粉丝");
                fragment = FansFragment.newInstance();
                break;
            case 2:
                binding.layoutToolbar.txtToolbarTitle.setText("点赞");
                fragment = LikingFragment.newInstance();
                break;
            case 3:
                binding.layoutToolbar.txtToolbarTitle.setText("@我的");
                fragment = AtFragment.newInstance();
                break;
            case 4:
                binding.layoutToolbar.txtToolbarTitle.setText("评论");
                fragment = CommentFragment.newInstance();
                break;
            default:
                throw new IllegalStateException("IllegalStateException");
        }

        getSupportFragmentManager().beginTransaction()
                .disallowAddToBackStack()
                .add(R.id.layout_container, fragment, fragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    public static void start(FragmentActivity activity, int flag) {
        Preconditions.checkArgument(flag >= 1 && flag <= 4, "Unknown flag");
        AppRouterKt.requireLogin(activity, result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Values.INSTANCE.put("MineActivity", flag);
                RouterKt.newStartActivity(activity, MineActivity.class);
            }
        });
    }
}
