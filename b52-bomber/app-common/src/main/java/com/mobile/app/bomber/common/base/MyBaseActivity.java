package com.mobile.app.bomber.common.base;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.mobile.app.bomber.common.R;
import com.mobile.guava.android.mvvm.AndroidX;
import com.mobile.guava.android.mvvm.AndroidX;
import com.mobile.guava.android.mvvm.BaseActivity;
import com.mobile.guava.jvm.coroutines.Bus;

public abstract class MyBaseActivity extends BaseActivity {

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

    public void addFragment(int containerViewId, Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .disallowAddToBackStack()
                .add(containerViewId, fragment, fragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoading();
    }
}
