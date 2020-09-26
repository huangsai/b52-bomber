package com.mobile.app.bomber.tik.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.mobile.guava.android.mvvm.BaseBottomSheetDialogFragment;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.common.base.tool.ClipBoardUtil;
import com.mobile.app.bomber.common.base.tool.ShareUtils;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.tik.databinding.FragmentShareDialogBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShareDialogFragment extends BaseBottomSheetDialogFragment
        implements View.OnClickListener {

    private FragmentShareDialogBinding binding;
    private String data;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ShareRoundBottomSheetDialog);
        data = getArguments().getString("data");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentShareDialogBinding.inflate(inflater, container, false);
        binding.imgClose.setOnClickListener(this);
        binding.txtQq.setOnClickListener(this);
        binding.txtWechat.setOnClickListener(this);
        binding.txtLink.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.txt_link) {
            ClipBoardUtil.copy(data);
        } else if (id == R.id.txt_qq) {
            ShareUtils.shareToQQ(requireContext());
        } else if (id == R.id.txt_wechat) {
            ShareUtils.shareToWechat(requireContext());
        }
        dismissAllowingStateLoss();
    }

    public static ShareDialogFragment newInstance(String data) {
        ShareDialogFragment fragment = new ShareDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("data", data);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static void goSystemShareSheet(Activity activity, String data,String Subtitle) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, Subtitle);
        sendIntent.putExtra(Intent.EXTRA_TEXT, data + "\n"+ Subtitle);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        activity.startActivity(shareIntent);
    }
}
