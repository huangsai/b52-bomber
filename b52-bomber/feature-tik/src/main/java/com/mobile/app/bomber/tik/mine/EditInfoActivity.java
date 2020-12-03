package com.mobile.app.bomber.tik.mine;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;

import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.MyBaseActivity;
import com.mobile.app.bomber.common.base.tool.FileUtil;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.data.http.entities.ApiUser;
import com.mobile.app.bomber.data.http.entities.Nope;
import com.mobile.app.bomber.data.repository.SourceExtKt;
import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.AppRouterKt;
import com.mobile.app.bomber.tik.base.AppRouterUtils;
import com.mobile.app.bomber.tik.base.GlideExtKt;
import com.mobile.app.bomber.tik.databinding.ActivityEditinfoBinding;
import com.mobile.app.bomber.tik.login.LoginActivity;
import com.mobile.ext.file.FileSelector;
import com.mobile.ext.file.FileUtils;
import com.mobile.ext.permission.PermissionCallback;
import com.mobile.ext.permission.PermissionRequest;
import com.mobile.guava.android.mvvm.RouterKt;
import com.mobile.guava.data.Values;
import com.mobile.guava.jvm.domain.Source;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class EditInfoActivity extends MyBaseActivity
        implements View.OnClickListener, DatePickerDialogFragment.Callback,
        ChoosePicDialogFragment.CallBack, ChooseGenderDialogFragment.CallBack {

    private MeViewModel meViewModel;
    private ActivityEditinfoBinding binding;
    private String bitmap;
    private ApiUser mApiUser;

    private final ActivityResultLauncher<Intent> launcher = FileSelector.INSTANCE.registerPicResultCallback(
            this, new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Uri uri;
                        File file;
                        if (FileSelector.INSTANCE.getCameraPicFile() == null) {
                            //选择图片
                            uri = result.getData().getData();
                            file = FileUtils.INSTANCE.getFile(EditInfoActivity.this, uri);
                        } else {
                            //拍照
                            uri = FileSelector.INSTANCE.getCameraPicUri(EditInfoActivity.this);
                            file = FileSelector.INSTANCE.getCameraPicFile();
                        }
                        if (uri != null) binding.hHead.setImageURI(uri);
                        bitmap = uri.getPath();
                        if (file != null) updateHeadPicUrl(file);
                    }
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditinfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        meViewModel = AppRouterUtils.viewModels(this, MeViewModel.class);

        mApiUser = Values.INSTANCE.take("FragmentMe_user");
        if (mApiUser != null) {
            GlideExtKt.loadProfile(this, mApiUser.getPic(), binding.hHead);
            binding.editInfoName.setText(mApiUser.getUsername());
            binding.wechatNum.setText(mApiUser.getWechat());
            binding.desptionNum.setText(mApiUser.getSign());
            binding.genders.setText(mApiUser.getGender() == 0 ? "女" : "男");
            binding.birNum.setText(mApiUser.getBirthday());
        }
        binding.imgBtnBack.setOnClickListener(this);
        binding.hHead.setOnClickListener(this);
        binding.editInfoName.setOnClickListener(this);
        binding.wechatNum.setOnClickListener(this);
        binding.desptionNum.setOnClickListener(this);
        binding.genders.setOnClickListener(this);
        binding.birNum.setOnClickListener(this);
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imgBtn_back) {
            finish();
        } else if (id == R.id.h_head) {
            RouterKt.showDialogFragment(this, ChoosePicDialogFragment.newInstance(this));
        } else if (id == R.id.birNum) {
            DatePickerDialogFragment dialog = DatePickerDialogFragment.newInstance();
            dialog.setCallback(this);
            RouterKt.showDialogFragment(this, dialog);
        } else if (id == R.id.genders) {
            RouterKt.showDialogFragment(this, ChooseGenderDialogFragment.newInstance(this));
        } else if (id == R.id.edit_info__name) {
            Bundle bundle1 = EditInfoInputActivity.setBundle(EditInfoInputActivity.TYPE_NICK, binding.editInfoName.getText().toString().trim());
            AppRouterKt.newStartActivityForResult(EditInfoActivity.this, EditInfoInputActivity.class, 1000, bundle1);
        } else if (id == R.id.wechatNum) {
            Bundle bundle2 = EditInfoInputActivity.setBundle(EditInfoInputActivity.TYPE_WECHAT, binding.wechatNum.getText().toString().trim());
            AppRouterKt.newStartActivityForResult(EditInfoActivity.this, EditInfoInputActivity.class, 1000, bundle2);
        } else if (id == R.id.desptionNum) {
            Bundle bundle = EditInfoInputActivity.setBundle(EditInfoInputActivity.TYPE_SIGN, binding.desptionNum.getText().toString().trim());
            AppRouterKt.newStartActivityForResult(EditInfoActivity.this, EditInfoInputActivity.class, 1000, bundle);
        }
    }

    public void modifyUserBirthday(String birthday) {
        LiveData<Source<Nope>> sourceLiveData = meViewModel.updateBirthday(birthday);
        handlerUpdateResultView(sourceLiveData);
    }

    public void modifyUserGender(Integer gender) {
        LiveData<Source<Nope>> sourceLiveData = meViewModel.updateGender(gender);
        handlerUpdateResultView(sourceLiveData);
    }

    private void handlerUpdateResultView(LiveData<Source<Nope>> sourceLiveData) {
        sourceLiveData.observe(this, nopeSource -> {
            if (nopeSource instanceof Source.Success) {
                Msg.INSTANCE.toast("修改成功");
            } else {
                Msg.INSTANCE.toast("修改失败");
            }
        });
    }


    @Override
    public void onDateChanged(int year, int month, int day) {

    }

    @Override
    public void onShowChanged(boolean isChecked) {
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onConfirm(int year, int month, int day, boolean isChecked) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        String currentData = simpleDateFormat.format(date);
        String selectData = year + "-" + month + "-" + day;
        Date dt1 = null;
        Date dt2 = null;
        try {
            dt1 = simpleDateFormat.parse(currentData);
            dt2 = simpleDateFormat.parse(selectData);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dt2.getTime() > dt1.getTime()) {
            Msg.INSTANCE.toast("不能大于当前日期");
        } else {
            String birthdayStr = year + "-" + month + "-" + day;
            binding.birNum.setText(birthdayStr);
            modifyUserBirthday(birthdayStr);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1001) {
            String result_value = data.getStringExtra("result");
            binding.editInfoName.setText(result_value);
        }
        if (resultCode == 1002) {
            String result_value = data.getStringExtra("result");
            binding.desptionNum.setText(result_value);
        }
        if (resultCode == 1003) {
            String result_value = data.getStringExtra("result");
            binding.wechatNum.setText(result_value);
        }
    }

    public void updateHeadPicUrl(File file) {
        showLoading("修改头像中", false);
        meViewModel.updateHeadPic(file).observe(this, source -> {
            hideLoading();
            if (source instanceof Source.Error) {
                Msg.INSTANCE.handleSourceException(source.requireError());
                if (SourceExtKt.is403(source.requireError())) {
                    RouterKt.newStartActivity(this, LoginActivity.class);
                }
                return;
            }
             Msg.INSTANCE.toast("修改成功");
        });
    }


    @Override
    public void onTakeCamera() {
        PermissionRequest.INSTANCE.request(this, 104, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                this::takeCamera);
    }

    @Override
    public void onChoosePhoto() {
        PermissionRequest.INSTANCE.request(this, 106, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                this::choosePhoto);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 104:
                Boolean hasCameraPermission = true;
                Boolean hasWritePermission = true;
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        //判断是否勾选禁止后不再询问
                        String key = permissions[i];
                        if (key.equals(Manifest.permission.CAMERA)) {
                            hasCameraPermission = false;
                        } else if (key.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            hasWritePermission = false;
                        }
                    }
                }
                if (hasCameraPermission && hasWritePermission) {
                    takeCamera();
                } else {
                    PermissionRequest.INSTANCE.alertPermission(R.string.alert_msg_permission_camera_storage,this);
                }
                break;
            case 106:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    //判断是否勾选禁止后不再询问
                    PermissionRequest.INSTANCE.alertPermission(R.string.alert_msg_permission_storage,this);
                } else {
                    choosePhoto();
                }
                break;
        }
    }

    private void takeCamera() {
        FileSelector.INSTANCE.takeCamera(this, launcher);
    }

    private void choosePhoto() {
        FileSelector.INSTANCE.selectPic(launcher);
    }

//    @Override
//    public void onBrowseBigPic() {
//        show_click(binding.hHead);
//    }

    public void show_click(View v){
        Intent intent = new Intent(getApplicationContext(),ImageShower.class);
        intent.putExtra("head",mApiUser.getPic());
        startActivity(intent);
    }

    @Override
    public void onSelectMan() {
        binding.genders.setText("男");
        modifyUserGender(1);
    }

    @Override
    public void onSelectWoman() {
        binding.genders.setText("女");
        modifyUserGender(0);
    }

    @Override
    public void onSelectNoDisplay() {
        binding.genders.setText("不显示");
    }

}

