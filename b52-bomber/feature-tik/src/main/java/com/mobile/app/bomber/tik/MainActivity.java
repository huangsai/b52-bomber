package com.mobile.app.bomber.tik;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.result.ActivityResultCallback;
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
import com.mobile.app.bomber.data.http.entities.ApiToken;
import com.mobile.app.bomber.data.http.entities.ApiVersion;
import com.mobile.app.bomber.data.repository.SourceExtKt;
import com.mobile.guava.android.mvvm.AndroidX;
import com.mobile.app.bomber.runner.base.PrefsManager;
import com.mobile.app.bomber.runner.features.FeatureRouter;
import com.mobile.guava.android.mvvm.RouterKt;
import com.mobile.app.bomber.tik.ad.PopupAdDialogFragment;
import com.mobile.app.bomber.tik.ad.SplashDialogFragment;
import com.mobile.app.bomber.tik.base.AppRouterKt;
import com.mobile.app.bomber.tik.base.AppRouterUtils;
import com.mobile.app.bomber.tik.databinding.ActivityMainBinding;
import com.mobile.app.bomber.tik.home.HomeFragment;
import com.mobile.app.bomber.tik.home.LocationLiveData;
import com.mobile.app.bomber.tik.home.TestDialogFragment;
import com.mobile.app.bomber.tik.home.TopMainFragment;
import com.mobile.app.bomber.tik.login.LoginActivity;
import com.mobile.app.bomber.tik.login.LoginViewModel;
import com.mobile.app.bomber.tik.message.MsgFragment;
import com.mobile.app.bomber.tik.mine.FragmentMe;
import com.mobile.app.bomber.tik.video.VideoRecordActivity;
import com.mobile.guava.jvm.domain.Source;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import kotlin.Pair;
import timber.log.Timber;

public class MainActivity extends MyBaseActivity implements View.OnClickListener,
        View.OnLongClickListener, ActivityResultCallback<Map<String, Boolean>>,
        RadioGroup.OnCheckedChangeListener {

    private long longClickNano = 0;
    private long firstTime = 0;
    private int mLastCheckedViewID = R.id.main_home_rb;
    private ActivityMainBinding binding;
    private LoginViewModel model;

    public TopMainFragment currentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ImmersionBar.with(this).init();
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        model = AppRouterUtils.viewModels(this, LoginViewModel.class);
        requestPermission();
    }

    private void onCreate() {
        binding.mainRg.setOnCheckedChangeListener(this);
        binding.viewPager.setOffscreenPageLimit(2);
        binding.viewPager.setUserInputEnabled(false);
        binding.viewPager.setAdapter(new MyAdapter(this));
        binding.viewPager.setCurrentItem(0);
        binding.imgAdd.setOnClickListener(this);
        binding.imgAdd.setOnLongClickListener(this);
    }

    private void requestPermission() {
        String[] permissions = new String[]{
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.RECORD_AUDIO

        };
        registerForActivityResult(permissionsContract, this).launch(permissions);
    }

    @Override
    public void onActivityResult(Map<String, Boolean> map) {
        boolean hasNeededPermission = true;
        boolean hasLocationPermission = true;
        String key;
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            key = entry.getKey();
            if (!entry.getValue()) {
                if (key.equals(android.Manifest.permission.CAMERA)) {
                    hasNeededPermission = false;
                    MainActivity.this.alertPermission(R.string.alert_msg_permission_camera, true);
                } else if (key.equals(android.Manifest.permission.READ_PHONE_STATE)) {
                    hasNeededPermission = false;
                    MainActivity.this.alertPermission(R.string.alert_msg_permission_phone_state, true);
                } else if (key.equals(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    hasNeededPermission = false;
                    MainActivity.this.alertPermission(R.string.alert_msg_permission_storage, true);
                } else if (key.equals(android.Manifest.permission.RECORD_AUDIO)) {
                    hasNeededPermission = false;
                    MainActivity.this.alertPermission(R.string.alert_msg_permission_record, true);
                } else {
                    hasLocationPermission = false;
                    MainActivity.this.alertPermission(R.string.alert_msg_permission_location, false);
                }
            }
        }
        if (hasLocationPermission) {
            lookupLocation();
        }
        if (hasNeededPermission) {
            requestSplashAd();
        }
    }

    private void lookupLocation() {
        if (LocationLiveData.INSTANCE.getValue() == null && !AppUtil.isGpsAble(this)) {
            AppUtil.openGPS(this);
        }
        LocationLiveData.INSTANCE.observe(this, location -> {
            if (location != null) {
                Timber.tag("LocationLiveData").d(
                        "(%s,%s)",
                        location.getLatitude(),
                        location.getLongitude()
                );
                LocationLiveData.INSTANCE.removeObservers(this);
            }
        });
    }

    private void requestSplashAd() {
        model.ad().observe(this, source -> {
            if (source instanceof Source.Success) {
                ApiAd ad = source.requireData();
                if (TextUtils.isEmpty(ad.getUrl())) {
                    requestPopupAd();
//                    requestCheckVersion();
                } else {
                    RouterKt.showDialogFragment(this, SplashDialogFragment.newInstance(ad));
                }
            } else {
                requestPopupAd();
//                requestCheckVersion();
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
            }
        });
    }

    public void requestPopupAd() {
        model.adMsg().observe(this, source -> {
            login();
            if (source instanceof Source.Success) {
                RouterKt.showDialogFragment(
                        this,
                        PopupAdDialogFragment.newInstance(source.requireData())
                );
            }
        });
        requestCheckVersion();
    }

    public void requestCheckVersion() {
         int currentversion = AppUtil.getVersionCode(getApplicationContext());
        model.ckVersino().observe(this, source -> {
            if (source instanceof Source.Success) {
                ApiVersion version = source.requireData();
                ApiVersion.Version vCode = version.getVersions();
                 if (!(vCode.equals(String.valueOf(currentversion)))) {
//                     UpdateManger updateManger = new UpdateManger(getApplicationContext(),
//                             vCode.getDownloadUrl(), true,
//                             vCode.getVersionName(), String.valueOf(currentversion),this);
//                    updateManger.showNoticeDialog(vCode.getVersionName(), String.valueOf(currentversion));
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
    public boolean onLongClick(View v) {
        long nano = System.currentTimeMillis();
        if (nano - longClickNano < 2000) {
            RouterKt.showDialogFragment(this, TestDialogFragment.newInstance());
        } else {
            longClickNano = nano;
        }
        return true;
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
        } else if (checkedId == R.id.main_categoty_rb) {
            vpSelectPosition = 1;
        } else if (checkedId == R.id.main_msg_rb) {
            vpSelectPosition = 2;
        } else if (checkedId == R.id.main_mine_rb) {
            vpSelectPosition = 3;
        }
        // 选中我的页面
        if (vpSelectPosition == 3 && radioButton.isChecked() && !PrefsManager.INSTANCE.isLogin()) {
            binding.mainRg.check(mLastCheckedViewID);
            RouterKt.newStartActivity(MainActivity.this, LoginActivity.class);
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
                    // return CategoryFragment.newInstance(position);
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