package com.mobile.app.bomber.tik.mine;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.MyBaseActivity;
import com.mobile.app.bomber.common.base.tool.AppUtil;
import com.mobile.app.bomber.common.base.tool.CacheDataUtil;
import com.mobile.app.bomber.common.base.tool.FileUtil;
import com.mobile.app.bomber.common.base.tool.HttpUtils;
import com.mobile.app.bomber.common.base.tool.QRCodeUtil;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.data.http.entities.ApiDownLoadUrl;
import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.AppRouterUtils;
import com.mobile.app.bomber.tik.databinding.ActivitySettingEditinfoBinding;
import com.mobile.app.bomber.tik.home.ShareDialogFragment;
import com.mobile.app.bomber.tik.home.UserShareDialogFragment;
import com.mobile.app.bomber.tik.login.LoginActivity;
import com.mobile.app.bomber.tik.login.LoginViewModel;
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
                ApiDownLoadUrl url = source.requireData();
                shareUrl = url.getDownloadUrl();
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
        Msg.INSTANCE.toast("555");

        model.shareAppUrl().observe(this, source -> {
            if (source instanceof Source.Success) {
                ApiDownLoadUrl url = source.requireData();
                shareUrl = url.getDownloadUrl();
                content = url.getDesc();
                bgUrl = url.getBgUrl();
                if (TextUtils.isEmpty(shareUrl)) {
                    Msg.INSTANCE.toast("暂时不能分享");
                } else {
                    dialogFragmet(2, bgUrl, bgUrl);
                }
            } else {
                Msg.INSTANCE.toast("暂时不能分享");
                Msg.INSTANCE.handleSourceException(source.requireError());
            }
        });
    }


    private void dialogFragmet(Integer flg, String url, String BgUrl) {
        if (flg == 1) {
            ShareDialogFragment.goSystemShareSheet(this, url, "点击一下 立即拥有 ");//
        } else {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build());

            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyDeath()
                    .build());
            Bitmap urlAndBitmap = HttpUtils.getNetWorkBitmap(BgUrl);
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Bitmap logoQR = QRCodeUtil.createQRCode(shareUrl, 560 + 50, 580 + 70);
                    Bitmap bitmap = QRCodeUtil.addTwoLogo(urlAndBitmap, logoQR);
                    String coverFilePath = FileUtil.saveBitmapToFile(bitmap, "bg_image");
                    File coverFile = new File(coverFilePath);
                    dialogFragmetContent(2, coverFile);
                }
            };
            handler.postDelayed(runnable, 2000);
        }

    }

    private void dialogFragmetContent(Integer flg, File coverFile) {
        ShareDialogFragment.goSystemShareSheet(this, shareUrl, "点击一下 立即拥有 ");

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

