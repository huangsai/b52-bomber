package com.mobile.app.bomber.tik.mine;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.mobile.app.bomber.common.base.tool.AppUtil;
import com.mobile.app.bomber.data.http.entities.ApiDownLoadUrl;
import com.mobile.app.bomber.tik.home.ShareDialogFragment;
import com.mobile.guava.android.mvvm.RouterKt;
import com.mobile.guava.data.Values;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.AppRouterUtils;
import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.MyBaseActivity;
import com.mobile.app.bomber.common.base.tool.CacheDataUtil;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.tik.databinding.ActivitySettingEditinfoBinding;
import com.mobile.app.bomber.tik.login.LoginActivity;
import com.mobile.app.bomber.tik.login.LoginViewModel;
import com.mobile.guava.jvm.domain.Source;


public class SettingAcivity extends MyBaseActivity implements View.OnClickListener {

    private LoginViewModel model;
    private ActivitySettingEditinfoBinding binding;
    private String shareUrl;
    private String content;

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
        binding.settingItem.share.setOnClickListener(this);
        binding.settingItem.currntBuild.setText(AppUtil.getAppVersionName(getApplicationContext()));

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
        } else if (id == R.id.share) {
            model.shareAppUrl().observe(this, source -> {
                if (source instanceof Source.Success) {
                    ApiDownLoadUrl url = source.requireData();
                    shareUrl = url.getDownloadUrl();
                    content = url.getDesc();
                } else {
                    Msg.INSTANCE.handleSourceException(source.requireError());
                }
            });

            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                        if (TextUtils.isEmpty(shareUrl)) {
                            Msg.INSTANCE.toast("暂时不能分享");
                            return;
                        }
                    } catch (InterruptedException e) {
                    }
                }
            }.start();
            ShareDialogFragment.goSystemShareSheet(this, shareUrl, "在xx世界最流行的色情视频app中免费观看各种视频，国产网红、日本av、欧美色情应有尽有 ");
        }
    }
}

