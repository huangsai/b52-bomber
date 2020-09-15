package com.mobile.app.bomber.tik.home;

import android.os.Bundle;

import com.mobile.app.bomber.common.base.MyBaseFragment;
import com.mobile.app.bomber.tik.MainActivity;

import org.jetbrains.annotations.Nullable;

public abstract class TopMainFragment extends MyBaseFragment {

    protected MainActivity pActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pActivity = (MainActivity) requireActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        pActivity.currentFragment = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pActivity.currentFragment == this) {
            pActivity.currentFragment = null;
        }
    }
}
