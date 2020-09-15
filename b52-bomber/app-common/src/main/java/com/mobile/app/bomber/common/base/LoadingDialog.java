package com.mobile.app.bomber.common.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.mobile.app.bomber.common.R;

import java.util.Objects;

/**
 * 等待弹框
 */
public class LoadingDialog extends DialogFragment {
    private String msg = "正在加载";
    private boolean onTouchOutside = true;
    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(onTouchOutside);
        getDialog().setCancelable(onTouchOutside);
        View loadingView = inflater.inflate(R.layout.dialog_loading, container);
        textView = loadingView.findViewById(R.id.textView);
        textView.setText(msg);
        return loadingView;
    }

    public LoadingDialog setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public LoadingDialog setOnTouchOutside(boolean onTouchOutside) {
        this.onTouchOutside = onTouchOutside;
        return this;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getDialog() != null) {
            getDialog().setOnCancelListener(null);
            getDialog().setOnDismissListener(null);
        }
    }
}