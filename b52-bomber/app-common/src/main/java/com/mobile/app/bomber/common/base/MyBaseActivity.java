package com.mobile.app.bomber.common.base;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.mobile.app.bomber.common.R;
import com.mobile.guava.android.mvvm.AndroidX;
import com.mobile.guava.android.mvvm.BaseActivity;
import com.mobile.guava.jvm.coroutines.Bus;

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

    public void alertPermission(@StringRes int msgRes, boolean exitSystem) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.base_alert_title)
                .setMessage(msgRes)
                .setPositiveButton(R.string.base_alert_known, (dialog, which) -> {
                    dialog.dismiss();
                    if (exitSystem) Bus.INSTANCE.offer(AndroidX.BUS_EXIT);
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
     * @param containerViewId
     * @param fragment
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
}
