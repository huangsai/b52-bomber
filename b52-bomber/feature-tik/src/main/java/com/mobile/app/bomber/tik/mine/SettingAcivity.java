package com.mobile.app.bomber.tik.mine;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.MyBaseActivity;
import com.mobile.app.bomber.common.base.tool.AppUtil;
import com.mobile.app.bomber.common.base.tool.CacheDataUtil;
import com.mobile.app.bomber.common.base.tool.FileUtil;
import com.mobile.app.bomber.common.base.tool.QRCodeUtil;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.data.http.entities.ApiShareUrl;
import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.AppRouterUtils;
import com.mobile.app.bomber.tik.databinding.ActivitySettingEditinfoBinding;
import com.mobile.app.bomber.tik.home.ShareDialogFragment;
import com.mobile.app.bomber.tik.home.UserShareDialogFragment;
import com.mobile.app.bomber.tik.login.LoginActivity;
import com.mobile.app.bomber.tik.login.LoginViewModel;
import com.mobile.ext.glide.GlideApp;
import com.mobile.guava.android.mvvm.AndroidX;
import com.mobile.guava.android.mvvm.RouterKt;
import com.mobile.guava.data.Values;
import com.mobile.guava.jvm.domain.Source;

import java.io.File;
import java.lang.ref.WeakReference;


public class SettingAcivity extends MyBaseActivity implements View.OnClickListener, UserShareDialogFragment.CallBack {

    private LoginViewModel model;
    private ActivitySettingEditinfoBinding binding;
    private String shareUrl;
    private String content;
    private String bgUrl;
    private Bitmap urlMap;

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
            RouterKt.showDialogFragment(this, UserShareDialogFragment.newInstance(this));
        }
    }

    private void DialogFragmetText() {
        model.shareAppUrl().observe(this, source -> {
            if (source instanceof Source.Success) {
                ApiShareUrl url = source.requireData();
                shareUrl = url.getShareUrl();
                content = url.getDesc();
                if (TextUtils.isEmpty(shareUrl)) {
                    Msg.INSTANCE.toast("暂时不能分享");
                } else {
                    dialogFragmet(1, shareUrl, null);
                }
            } else {
                Msg.INSTANCE.toast("暂时不能分享");
                Msg.INSTANCE.handleSourceException(source.requireError());
            }
        });
    }


    private void DialogFragmetImage() {
        model.shareAppUrl().observe(this, source -> {
            if (source instanceof Source.Success) {
                ApiShareUrl url = source.requireData();
                shareUrl = url.getShareUrl();
                content = url.getDesc();
                bgUrl = url.getBgUrl();
                if (TextUtils.isEmpty(shareUrl)) {
                    Msg.INSTANCE.toast("暂时不能分享");
                } else {
                    dialogFragmet(2, shareUrl, bgUrl);
                }
            } else {
                Msg.INSTANCE.toast("暂时不能分享");
                Msg.INSTANCE.handleSourceException(source.requireError());
            }
        });
    }


    private void dialogFragmet(Integer flg, String url, String BgUrl) {
        if (flg == 1) {
            ShareDialogFragment.goSystemShareSheet(this, url, "点击一下 立即拥有 ", null);//
        } else {
            GlideApp.with(AndroidX.INSTANCE.myApp()).asBitmap().load(bgUrl).into(new CustomTarget<Bitmap>() {

                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    if (resource == null) {
                        Msg.INSTANCE.toast("地址无效,无法分享");
                        return;
                    }
                    Bitmap logoQR = QRCodeUtil.createQRCode(url, 560 + 50, 580 + 70);
                    Bitmap bitmap = QRCodeUtil.addTwoLogo(resource, logoQR);
                    String coverFilePath = FileUtil.saveBitmapToFile(bitmap, "bg_image");
                    File coverFile = new File(coverFilePath);
                    dialogFragmetContent(2, coverFile);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });
        }

    }

    private void dialogFragmetContent(Integer flg, File coverFile) {
        ShareDialogFragment.goSystemShareSheet(this, shareUrl, "点击一下 立即拥有 ", coverFile);

    }

    @Override
    public void onShareText() {
        DialogFragmetText();
    }

    @Override
    public void onShareImage() {
        DialogFragmetImage();
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

