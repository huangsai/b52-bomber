package com.mobile.app.bomber.tik.mine;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.mobile.guava.android.mvvm.RouterKt;
import com.mobile.guava.https.Values;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.AppRouterUtils;
import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.MyBaseActivity;
import com.mobile.app.bomber.common.base.tool.CacheDataUtil;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.tik.databinding.ActivitySettingEditinfoBinding;
import com.mobile.app.bomber.tik.login.LoginActivity;
import com.mobile.app.bomber.tik.login.LoginViewModel;


public class SettingAcivity extends MyBaseActivity implements View.OnClickListener {

    private LoginViewModel model;
    private ActivitySettingEditinfoBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingEditinfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        model = AppRouterUtils.viewModels(this, LoginViewModel.class);
        initView();

    }

    public void initView() {
        String wechat = Values.INSTANCE.take("FragmentMe_wechat");
        if (!TextUtils.isEmpty(wechat)) {
            binding.settingItem.settingWechatIdTv.setText(wechat);
        }
        try {
            binding.settingItem.cacheSize.setText(CacheDataUtil.getTotalCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.imgBtnBack.setOnClickListener(this);
        binding.settingItem.clearcache.setOnClickListener(this);
        binding.settingItem.loginOut.setOnClickListener(this);
        binding.settingItem.checkUpate.setOnClickListener(this);
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imgBtn_back) {
            finish();
        } else if (id == R.id.clearcache) {
            CacheDataUtil.clearAllCache(getApplicationContext());
            Msg.INSTANCE.toast("已清理缓存");
            binding.settingItem.cacheSize.setText("0.0M");
        } else if (id == R.id.checkUpate) {
            Msg.INSTANCE.toast("当前版本已是最新版本");
        } else if (id == R.id.loginOut) {
            model.logout();
            RouterKt.newStartActivity(this, LoginActivity.class);
            finish();
        }
    }
}

