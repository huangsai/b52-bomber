package com.mobile.app.bomber.tik.mine;

import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.MyBaseActivity;
import com.mobile.app.bomber.common.base.tool.AppUtil;
import com.mobile.app.bomber.common.base.tool.CacheDataUtil;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.data.http.entities.ApiDownLoadUrl;
import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.AppRouterUtils;
import com.mobile.app.bomber.tik.databinding.ActivitySettingEditinfoBinding;
import com.mobile.app.bomber.tik.home.ShareDialogFragment;
import com.mobile.app.bomber.tik.login.LoginActivity;
import com.mobile.app.bomber.tik.login.LoginViewModel;
import com.mobile.guava.android.mvvm.RouterKt;
import com.mobile.guava.data.Values;
import com.mobile.guava.jvm.domain.Source;

import java.lang.ref.WeakReference;


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
                    if (TextUtils.isEmpty(shareUrl)) {
                        Msg.INSTANCE.toast("暂时不能分享");
                    } else {
                        ShareDialogFragment.goSystemShareSheet(this, shareUrl, "点击一下 立即拥有 ");//"在xx世界最流行的色情视频app中免费观看各种视频，国产网红、日本av、欧美色情应有尽有.");
                    }
                } else {
                    Msg.INSTANCE.toast("暂时不能分享");
                    Msg.INSTANCE.handleSourceException(source.requireError());
                }
            });
//            new MyThread(this,shareUrl).start();

//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }

    private static class MyThread extends Thread {
        WeakReference<SettingAcivity> mThreadActivityRef;
        String urlContent;

        public MyThread(SettingAcivity activity, String urlContent) {
            mThreadActivityRef = new WeakReference<SettingAcivity>(
                    activity);
            this.urlContent = urlContent;
        }


        @Override
        public void run() {
            super.run();
            if (mThreadActivityRef == null)
                return;
            if (mThreadActivityRef.get() != null) {
                if (TextUtils.isEmpty(urlContent)) {
                    Looper.prepare();
                    Msg.INSTANCE.toast("暂时不能分享");
                    Looper.loop();
                    return;
                }
            }
        }
    }
}

