package com.mobile.app.bomber.tik.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.mobile.guava.android.mvvm.BaseAppCompatDialogFragment;

import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.tik.databinding.FragmentCommonDialogBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ContentDialogFragment extends BaseAppCompatDialogFragment implements View.OnClickListener {

    private FragmentCommonDialogBinding binding;
    private String content;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCommonDialogBinding.inflate(inflater, container, false);
        binding.confirm.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        content = getArguments().getString("content");
        binding.contentTv.setText(content);
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        dismissAllowingStateLoss();
    }

    public static ContentDialogFragment newInstance(String content) {
        ContentDialogFragment fragment = new ContentDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("content", content);
        fragment.setArguments(bundle);
        return fragment;
    }
}
