package com.mobile.app.bomber.tik.mine;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditinfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        meViewModel = AppRouterUtils.viewModels(this, MeViewModel.class);

        ApiUser mApiUser = Values.INSTANCE.take("FragmentMe_user");
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

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {  // 拍照
                Bundle extras = data.getExtras();
                Bitmap photoBit = (Bitmap) extras.get("data");
                binding.hHead.setImageBitmap(photoBit);
                String path = FileUtil.saveBitmapToFile(photoBit, "headPic");
                File photoFile = new File(path);
                updateHeadPicUrl(photoFile);

            } else if (requestCode == 2) { // 相册
                Uri uri = data.getData();
                String[] pojo = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(uri, pojo, null, null, null);
                if (cursor != null) {
                    int colunm_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String path = cursor.getString(colunm_index);
                    final File file = new File(path);
                    binding.hHead.setImageURI(uri);
                    updateHeadPicUrl(file);
                }
            }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    + ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                takeCamera();
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        104);
            }
        } else {
            takeCamera();
        }
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
                    alertPermission(R.string.alert_msg_permission_camera_storage);
                }
                break;
        }
    }

    private void takeCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, 1);
    }

    @Override
    public void onChoosePhoto() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//调用android的图库
        startActivityForResult(i, 2);
    }

    @Override
    public void onBrowseBigPic() {
        Msg.INSTANCE.toast("查看大图");
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

