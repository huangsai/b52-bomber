package com.mobile.app.bomber.tik.login;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;

import com.mobile.app.bomber.runner.base.PrefsManager;
import com.mobile.app.bomber.data.repository.SourceExtKt;
import com.mobile.guava.jvm.domain.Source;

import com.mobile.app.bomber.tik.BuildConfig;
import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.AppRouterUtils;
import com.mobile.app.bomber.tik.base.GlideExtKt;
import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.MyBaseActivity;
import com.mobile.app.bomber.common.base.tool.AppUtil;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.tik.databinding.ActivityLoginBinding;

import io.reactivex.rxjava3.disposables.Disposable;

import static com.mobile.app.bomber.common.base.tool.ActivityUtilsKt.isSoftInputShowing;
import static com.mobile.guava.android.context.ActivityExtKt.hideSoftInput;

public class LoginActivity extends MyBaseActivity implements View.OnClickListener {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private Disposable disposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loginViewModel = AppRouterUtils.viewModels(this, LoginViewModel.class);
        String token = PrefsManager.INSTANCE.getToken();
        if (TextUtils.isEmpty(token)) {
            initLoginPhoneView();
        } else {
            initLoginFastView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }


    private void initLoginFastView() {
        binding.loginFastBack.setOnClickListener(this);
        binding.loginFastBtn.setOnClickListener(this);
        binding.loginFastLoginOtherLl.setOnClickListener(this);
        String headPicUrl = PrefsManager.INSTANCE.getHeadPicUrl();
        String loginName = PrefsManager.INSTANCE.getLoginName();
        if (!TextUtils.isEmpty(headPicUrl)) {
            GlideExtKt.loadProfile(this, headPicUrl, binding.loginFastHeadImg);
        }
        binding.loginFastNickTv.setText(loginName);
    }

    private void initLoginPhoneView() {
        binding.loginTypeFastRl.setVisibility(View.GONE);
        binding.loginTypePhoneRl.setVisibility(View.VISIBLE);
        if (BuildConfig.DEBUG) {
            binding.tilLogin.setText("19965412404");//16776826168
        }
        binding.loginIvBack.setOnClickListener(this);
        binding.getPasss.setOnClickListener(this);
        binding.btnLogin.setOnClickListener(this);
    }

    private void handlerLoginFast() {
        showLoading("正在登录", false);
        loginViewModel.fastLogin().observe(this, source -> {
            hideLoading();
            if (source instanceof Source.Error) {
                Msg.INSTANCE.handleSourceException(source.requireError());
                if (SourceExtKt.is403(source.requireError()))
                    initLoginPhoneView();
                return;
            }
            Msg.INSTANCE.toast("登录成功");
            finish();
            setResult(Activity.RESULT_OK);
            finish();
        });
    }


    public void handleVerifyCode() {
        String phoneNumber = binding.tilLogin.getText().toString();
        if (phoneNumber.length() < 1) {
            Msg.INSTANCE.toast("手机号不能为空");
            return;
        }
        showLoading("验证码发送中", false);
        loginViewModel.getVerifyCode(phoneNumber).observe(this, nopeSource -> {
            hideLoading();
            if (nopeSource instanceof Source.Success) {
                if (disposable != null && !disposable.isDisposed()) {
                    disposable.dispose();
                }
                disposable = AppUtil.verifyStartTime(binding.getPasss);
                Msg.INSTANCE.toast("发送成功");
            } else {
                Msg.INSTANCE.handleSourceException(nopeSource.requireError());
            }
        });
    }

    public void handlerLoginPhone() {
        showLoading("正在登录", false);
        String phoneNumber = binding.tilLogin.getText().toString();
        String verifyCode = binding.etPassword.getText().toString();
        loginViewModel.login(phoneNumber, verifyCode).observe(this, source -> {
            hideLoading();
            if (source instanceof Source.Error) {
                Msg.INSTANCE.handleSourceException(source.requireError());
                return;
            }
            Msg.INSTANCE.toast("登录成功");
            setResult(Activity.RESULT_OK);
            finish();
        });
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.login_fast_back || id == R.id.login_iv_back) {
            if (isSoftInputShowing(this)) hideSoftInput(this);
            finish();
        } else if (id == R.id.login_fast_btn) {
            handlerLoginFast();
        } else if (id == R.id.login_fast_login_other_ll) {
            initLoginPhoneView();
        } else if (id == R.id.getPasss) {
            if (AppUtil.isMobileNO(binding.tilLogin.getText().toString().trim())) {
                handleVerifyCode();
            } else {
                Msg.INSTANCE.toast("手机号格式不对,请重新输入");
            }
        } else if (id == R.id.btn_login) {
            String verifyCode = binding.etPassword.getText().toString();
            if (verifyCode.length() < 1) {
                Msg.INSTANCE.toast("验证码不能为空");
                return;
            }
            handlerLoginPhone();
        }
    }

}
