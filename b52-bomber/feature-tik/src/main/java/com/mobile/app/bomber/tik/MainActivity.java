package com.mobile.app.bomber.tik;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gyf.immersionbar.ImmersionBar;
import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.MyBaseActivity;
import com.mobile.app.bomber.common.base.tool.AppUtil;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.common.base.tool.UpdateManger;
import com.mobile.app.bomber.data.http.entities.ApiAd;
import com.mobile.app.bomber.data.http.entities.ApiAdMsg;
import com.mobile.app.bomber.data.http.entities.ApiToken;
import com.mobile.app.bomber.data.http.entities.ApiVersion;
import com.mobile.app.bomber.data.repository.SourceExtKt;
import com.mobile.app.bomber.runner.base.PrefsManager;
import com.mobile.app.bomber.runner.features.FeatureRouter;
import com.mobile.app.bomber.tik.ad.PopupAdDialogFragment;
import com.mobile.app.bomber.tik.ad.SplashDialogFragment;
import com.mobile.app.bomber.tik.base.AppRouterKt;
import com.mobile.app.bomber.tik.base.AppRouterUtils;
import com.mobile.app.bomber.tik.databinding.ActivityMainBinding;
import com.mobile.app.bomber.tik.home.HomeFragment;
import com.mobile.app.bomber.tik.home.TopMainFragment;
import com.mobile.app.bomber.tik.login.LoginActivity;
import com.mobile.app.bomber.tik.login.LoginViewModel;
import com.mobile.app.bomber.tik.message.MsgFragment;
import com.mobile.app.bomber.tik.mine.FragmentMe;
import com.mobile.app.bomber.tik.video.VideoRecordActivity;
import com.mobile.guava.android.mvvm.AndroidX;
import com.mobile.guava.android.mvvm.RouterKt;
import com.mobile.guava.android.ui.screen.ScreenUtilsKt;
import com.mobile.guava.jvm.Guava;
import com.mobile.guava.jvm.domain.Source;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import kotlin.Pair;

public class MainActivity extends MyBaseActivity implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener {

    private long firstTime = 0;
    private int mLastCheckedViewID = R.id.main_home_rb;
    private ActivityMainBinding binding;
    private LoginViewModel model;

    public TopMainFragment currentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ImmersionBar.with(this).init();
        super.onCreate(savedInstanceState);
        ScreenUtilsKt.getScreen();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        model = AppRouterUtils.viewModels(this, LoginViewModel.class);
        requestSplashAd();
    }

    private void onCreate() {
        binding.mainRg.setOnCheckedChangeListener(this);
        binding.viewPager.setOffscreenPageLimit(2);
        binding.viewPager.setUserInputEnabled(false);
        binding.viewPager.setAdapter(new MyAdapter(this));
        binding.viewPager.setCurrentItem(0);
        handleIntent(getIntent());
        binding.mainRg.setVisibility(View.VISIBLE);
    }


    private void requestSplashAd() {
        model.ad(3).observe(this, source -> {
            if (source instanceof Source.Success) {
                ApiAd ad = source.requireData();
                if (TextUtils.isEmpty(ad.getUrl())) {
                    requestPopupAd();
                } else {
                    RouterKt.showDialogFragment(this, SplashDialogFragment.newInstance(ad));
                }
            } else {
                requestPopupAd();
            }
        });
    }

    private void login() {
        LiveData<Source<ApiToken>> result;
        if (PrefsManager.INSTANCE.isLogin()) {
            result = model.fastLogin();
        } else {
            result = model.login("", "");
        }
        result.observe(this, source -> {
            onCreate();
            if (source instanceof Source.Error) {
                if (SourceExtKt.is403(source.requireError())) {
                    RouterKt.newStartActivity(this, LoginActivity.class);
                }
            } else {
            }
        });
    }

    public void requestPopupAd() {
        model.adMsg(1).observe(this, source -> {
            login();
            JSONObject jsonObject = new JSONObject();
            if (source instanceof Source.Success) {
                ApiAdMsg data = source.requireData();
                if (!TextUtils.isEmpty(data.getContent())) {
                    RouterKt.showDialogFragment(this, PopupAdDialogFragment.Companion.newInstance(data));
                }
            }
        });

        if (!Guava.INSTANCE.isDebug()) {
            requestCheckVersion();
        }
    }

    private void requestCheckVersion() {
        String curVersion = AppUtil.getAppVersionName(getApplicationContext());
        model.ckVersino().observe(this, source -> {
            if (source instanceof Source.Success) {
                ApiVersion version = source.requireData();
                ApiVersion.Version vCode = version.getVersions();
                if (!(vCode.getVersionName().equals(curVersion))) {
                    UpdateManger updateManger = new UpdateManger(getApplicationContext(),
                            vCode.getDownloadUrl(), true,
                            vCode.getVersionName(), curVersion, this);
                    updateManger.showNoticeDialog(vCode.getVersionName(), curVersion);
                }
            } else {
                Msg.INSTANCE.handleSourceException(source.requireError());
            }
        });
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.img_add) {
            AppRouterKt.requireLogin(this, result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            RouterKt.newStartActivity(MainActivity.this, VideoRecordActivity.class);
                        }
                    }
            );
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long secondTime = System.currentTimeMillis();
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (secondTime - firstTime < 2000) {
                System.exit(0);
            } else {
                Msg.INSTANCE.toast("再按一次返回键退出");
                firstTime = System.currentTimeMillis();
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton radioButton = group.findViewById(checkedId);
        int vpSelectPosition = 0;
        if (checkedId == R.id.main_home_rb) {
            vpSelectPosition = 0;
        } else if (checkedId == R.id.main_movie_rb) {
            vpSelectPosition = 1;
        } else if (checkedId == R.id.main_msg_rb) {
            vpSelectPosition = 2;
        } else if (checkedId == R.id.main_mine_rb) {
            vpSelectPosition = 3;
        }

        if ((vpSelectPosition == 2 || vpSelectPosition == 3)
                && radioButton.isChecked() && !PrefsManager.INSTANCE.isLogin()) {
            binding.mainRg.check(mLastCheckedViewID);
            RouterKt.newStartActivity(this, LoginActivity.class);
            return;
        }
        binding.viewPager.setCurrentItem(vpSelectPosition, false);
        mLastCheckedViewID = checkedId;
        YoYo.with(Techniques.ZoomIn).duration(300).playOn(radioButton);
    }

    @Override
    public void onBusEvent(@NotNull Pair<Integer, ?> event) {
        super.onBusEvent(event);
        if (event.getFirst() == AndroidX.BUS_LOGOUT) {
            binding.mainRg.check(R.id.main_home_rb);
        }
    }

    private static class MyAdapter extends FragmentStateAdapter {

        private final MainActivity activity;

        public MyAdapter(@NonNull MainActivity activity) {
            super(activity);
            this.activity = activity;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 1:
                    return FeatureRouter.INSTANCE.newMovieFragment(position);
                case 2:
                    return MsgFragment.newInstance(position);
                case 3:
                    return FragmentMe.newInstance(position);
                default:
                    return HomeFragment.newInstance(position);
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
}