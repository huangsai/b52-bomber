package com.mobile.app.bomber.common.base;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.flurry.android.FlurryAgent;
import com.mobile.app.bomber.common.R;
import com.mobile.guava.android.context.ActivityExtKt;
import com.mobile.guava.android.mvvm.BaseActivity;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class MyBaseActivity extends BaseActivity {

    private final AtomicInteger mNextLocalRequestCode = new AtomicInteger();

    public final ActivityResultContracts.RequestMultiplePermissions permissionsContract =
            new ActivityResultContracts.RequestMultiplePermissions();

    public final ActivityResultContracts.StartActivityForResult startActivityContract =
            new ActivityResultContracts.StartActivityForResult();

    public MyBaseActivity() {
        super();
    }

    public MyBaseActivity(int contentLayoutId) {
        super(contentLayoutId);
    }

    public void alertPermission(@StringRes int msgRes) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.base_alert_title)
                .setMessage(msgRes)
                .setPositiveButton(R.string.base_alert_known, (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    private LoadingDialog loadingDialog;

    public void showLoading(String msg, boolean touch) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog();
        } else {
            loadingDialog.dismiss();
        }
        loadingDialog.setMsg(msg).setOnTouchOutside(touch).show(getSupportFragmentManager(), "loading");
    }

    public void hideLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    /**
     * 不建议使用
     *
     * @param containerViewId 被fragment替换的View
     * @param fragment        添加的fragment
     */
    @Deprecated
    public void addFragment(int containerViewId, Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .disallowAddToBackStack()
                .add(containerViewId, fragment, fragment.getClass().getSimpleName())
                .commit();
    }

    @NonNull
    public final <I, O> ActivityResultLauncher<I> registerForActivityResult2(
            @NonNull final ActivityResultContract<I, O> contract,
            @NonNull final ActivityResultCallback<O> callback) {
        return getActivityResultRegistry().register(
                "activity_rq#" + mNextLocalRequestCode.getAndIncrement(),
                contract,
                callback
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoading();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityExtKt.hideSoftInput(this);
    }
}
