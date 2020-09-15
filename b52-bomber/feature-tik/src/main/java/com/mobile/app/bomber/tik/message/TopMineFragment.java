package com.mobile.app.bomber.tik.message;

import android.os.Bundle;

import com.mobile.app.bomber.common.base.RecyclerViewFragment;

import org.jetbrains.annotations.Nullable;

public abstract class TopMineFragment extends RecyclerViewFragment {

    protected MineActivity pActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pActivity = (MineActivity) requireActivity();
    }
}
