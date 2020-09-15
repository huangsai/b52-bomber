package com.mobile.app.bomber.common.base;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.util.Preconditions;
import com.mobile.guava.android.mvvm.BaseFragment;

import org.jetbrains.annotations.Nullable;

public abstract class MyBaseFragment extends BaseFragment {

    private int _onResumeCount = 0;
    protected int position = -1;

    public MyBaseFragment() {
        super();
    }

    public MyBaseFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            position = args.getInt("position", -1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        _onResumeCount++;
    }

    public int getOnResumeCount() {
        return _onResumeCount;
    }

    public int getPosition() {
        Preconditions.checkArgument(position >= 0, "position < 0");
        return position;
    }

    public void addFragment(int containerViewId, Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                .disallowAddToBackStack()
                .add(containerViewId, fragment, fragment.getClass().getSimpleName())
                .commit();
    }
}
