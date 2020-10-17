package com.mobile.app.bomber.tik.video;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mobile.guava.android.mvvm.RouterKt;
import com.mobile.guava.jvm.domain.Source;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.AppRouterUtils;
import com.mobile.ext.glide.GlideApp;
import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.MyBaseActivity;
import com.mobile.app.bomber.common.base.tool.FileUtil;
import com.mobile.app.bomber.common.base.tool.ShareUtils;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.tik.databinding.ActivityVideoUploadBinding;
import com.mobile.app.bomber.tik.home.LocationLiveData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用它作为视频上传
 */


public class VideoUploadActivity extends MyBaseActivity implements View.OnClickListener {

    private VideoViewModel videoViewModel;
    private ActivityVideoUploadBinding binding;

    private int maxInputDesCount = 22;
    private List<String> labelList = new ArrayList<>();
    private boolean shareQQBool;
    private boolean shareWechatBool;

    private float latitude = 0.00f; //维度
    private float longitude = 0.00f;//经度
    private File sourceVideoFile;
    private File coverFile;
    private String videoPath;

    public static void start(AppCompatActivity activity, String videoPath) {
        Bundle bundle = new Bundle();
        bundle.putString("videoPath", videoPath);
        RouterKt.newStartActivity(activity, VideoUploadActivity.class, bundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoUploadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        videoViewModel = AppRouterUtils.viewModels(this, VideoViewModel.class);
        videoPath = getIntent().getStringExtra("videoPath");
        initView();
    }

    private void initView() {
        if (videoPath != null) {
            GlideApp.with(this)
                    .load(videoPath)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .thumbnail(0.25f)
                    .into(binding.recordCoverImg);
            sourceVideoFile = new File(videoPath);
        }
        if (sourceVideoFile != null) {
            Bitmap coverBitmap = FileUtil.getLocalVideoBitmap(sourceVideoFile);
            if (coverBitmap != null) {
                String coverFilePath = FileUtil.saveBitmapToFile(coverBitmap, "cover");
                coverFile = new File(coverFilePath);
            }
        }
        initLabelsData();
        binding.editDes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.textCount.setText(s.length() + "/" + maxInputDesCount);
            }
        });
        binding.backImage.setOnClickListener(this);
        binding.developVideo.setOnClickListener(this);

        shareWechatBool = true; //默认微信分享
        binding.shareWechatText.setSelected(true);
        binding.weixin.setSelected(true);
        binding.llShareQqLayout.setOnClickListener(this);
        binding.llShareWechatLayout.setOnClickListener(this);
        binding.tvLocation.setOnClickListener(this);
    }

    private void clickLocation() {
        binding.tvLocation.setSelected(!binding.tvLocation.isSelected());
        if (binding.tvLocation.isSelected()) {
            Location location = LocationLiveData.INSTANCE.getValue();
            String address = LocationLiveData.INSTANCE.getAddressByLocation();
            binding.tvLocation.setText(address);
            if (location != null) {
                latitude = (float) location.getLatitude();
                longitude = (float) location.getLongitude();
            }
        } else {
            binding.tvLocation.setText("未启动定位");
        }
    }

    private void clickWechatShareLayout() {
        binding.shareQqText.setSelected(false);
        binding.qq.setSelected(false);
        shareQQBool = false;
        if (shareWechatBool) { //已经选中，取消选中
            shareWechatBool = false;
            binding.shareWechatText.setSelected(false);
            binding.weixin.setSelected(false);
            return;
        }
        binding.shareWechatText.setSelected(true);
        binding.weixin.setSelected(true);
        shareWechatBool = true;
    }

    private void clickQQShareLayout() {
        binding.shareWechatText.setSelected(false);
        binding.weixin.setSelected(false);
        shareWechatBool = false;
        if (shareQQBool) { //已经选中，取消选中
            shareQQBool = false;
            binding.shareQqText.setSelected(false);
            binding.qq.setSelected(false);
            return;
        }
        binding.shareQqText.setSelected(true);
        binding.qq.setSelected(true);
        shareQQBool = true;
    }

    private void publishVideo() {
        if (sourceVideoFile == null || coverFile == null) {
            Msg.INSTANCE.toast("视频文件为空");
            return;
        }
        String desc = binding.editDes.getText().toString().trim();
        StringBuilder sbLabels = new StringBuilder();
        for (int i = 0; i < labelList.size(); i++) {
            if (i == 0) {
                sbLabels.append(labelList.get(i));
            } else {
                sbLabels.append("#" + labelList.get(i));
            }
        }
        showLoading("视频上传中", false);
        videoViewModel.uploadVideo(
                sourceVideoFile, coverFile, desc, sbLabels.toString(), latitude, longitude
        ).observe(VideoUploadActivity.this, source -> {
            hideLoading();
            if (source instanceof Source.Success) {
                Msg.INSTANCE.toast("上传成功");
                if (shareWechatBool)
                    ShareUtils.shareToWechat(VideoUploadActivity.this);
                if (shareQQBool)
                    ShareUtils.shareToQQ(VideoUploadActivity.this);
            } else {
                Msg.INSTANCE.handleSourceException(source.requireError());
            }
        });
        if (binding.checkboxRelease.isChecked()) FileUtil.saveFileToLocal(sourceVideoFile);
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back_image) {
            finish();
        } else if (id == R.id.developVideo) {
            publishVideo();
        } else if (id == R.id.tv_location) {
            clickLocation();
        } else if (id == R.id.ll_share_wechat_layout) {
            clickWechatShareLayout();
        } else if (id == R.id.ll_share_qq_layout) {
            clickQQShareLayout();
        }
    }

    private void initLabelsData() {
//        String[] hotWords = getResources().getStringArray(R.array.upload_labels);
        videoViewModel.getVideoTags().observe(this, source -> {
            if (source instanceof Source.Success) {
                List<String> keywords = source.requireData();
                for (String hotWord : keywords) {
               TextView view = (TextView) LayoutInflater.from(this).inflate(R.layout.item_tag_tv, binding.mShowBtnLayout, false);
                    view.setText("#" + hotWord);
                    view.setTag(hotWord);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String keyword = (String) view.getTag();
                            if (view.isSelected()) {
                                view.setSelected(false);
                                labelList.remove(keyword);
                                return;
                            }
                            if (labelList.size() >= 3) {
                                Msg.INSTANCE.toast("最多只能选择3个");
                                return;
                            }
                            view.setSelected(true);
                            labelList.add(keyword);
                        }
                    });
                    binding.mShowBtnLayout.addView(view);
                }

            } else {
                Msg.INSTANCE.handleSourceException(source.requireError());
            }
        });

//        String[] hotWords = getResources().getStringArray(R.array.upload_labels);
//        for (String hotWord : hotWords) {
//                TextView view = (TextView) LayoutInflater.from(this).inflate(R.layout.item_tag_tv, binding.mShowBtnLayout, false);
//                view.setText("#" + hotWord);
//                view.setTag(hotWord);
//                view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        String keyword = (String) view.getTag();
//                        if (view.isSelected()) {
//                            view.setSelected(false);
//                            labelList.remove(keyword);
//                            return;
//                        }
//                        if (labelList.size() >= 3) {
//                            Msg.INSTANCE.toast("最多只能选择3个");
//                            return;
//                        }
//                        view.setSelected(true);
//                        labelList.add(keyword);
//                    }
//                });
//            binding.mShowBtnLayout.addView(view);
//        }
    }


}
