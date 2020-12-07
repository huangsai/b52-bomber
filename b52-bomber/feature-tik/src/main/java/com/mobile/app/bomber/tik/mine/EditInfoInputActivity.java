package com.mobile.app.bomber.tik.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;

import com.mobile.app.bomber.runner.base.PrefsManager;
import com.mobile.guava.jvm.domain.Source;

import com.mobile.app.bomber.tik.R;
import com.mobile.app.bomber.tik.base.AppRouterUtils;
import com.mobile.app.bomber.common.base.MyBaseActivity;
import com.mobile.app.bomber.common.base.Msg;
import com.mobile.app.bomber.common.base.tool.SingleClick;
import com.mobile.app.bomber.tik.databinding.ActivityEditinfoInputBinding;


public class EditInfoInputActivity extends MyBaseActivity implements View.OnClickListener {

    public static final int TYPE_NICK = 0; //昵称
    public static final int TYPE_SIGN = 1;// 签名（简介）
    public static final int TYPE_WECHAT = 2;//微信
    private int type;

    private MeViewModel meViewModel;
    private ActivityEditinfoInputBinding binding;

    public static Bundle setBundle(int type, String content) {
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putString("content", content);
        return args;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditinfoInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        meViewModel = AppRouterUtils.viewModels(this, MeViewModel.class);
        Intent intent = getIntent();
        type = intent.getIntExtra("type", 0);
        String content = intent.getStringExtra("content");

        switch (type) {
            case TYPE_NICK:
                binding.titleTv.setText("名字修改");
                binding.editTypeTitleTv.setText("我的名字");
                binding.editTextNick.setVisibility(View.VISIBLE);
                binding.editNickCount.setVisibility(View.VISIBLE);
                binding.editTextNick.setText(content);
                binding.editTextNick.requestFocus();
                binding.editNickCount.setText(binding.editTextNick.getText().length() + "/10");
                break;
            case TYPE_SIGN:
                binding.titleTv.setText("修改简介");
                binding.editTypeTitleTv.setText("我的简介");
                binding.editTextDesc.setVisibility(View.VISIBLE);
                binding.editTextDesc.setText(content);
                binding.editTextDesc.requestFocus();
                break;
            case TYPE_WECHAT:
                binding.titleTv.setText("绑定微信号");
                binding.editTypeTitleTv.setText("我的微信号");
                binding.editTextWechat.setVisibility(View.VISIBLE);
                binding.editTextWechat.setText(content);
                binding.editTextWechat.requestFocus();
                break;
        }

        binding.backIb.setOnClickListener(this);
        binding.doneTv.setOnClickListener(this);
        binding.editTextNick.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 10) {
                    Msg.INSTANCE.toast("昵称不能空并且不能超过10个字符");
                } else {
                    binding.editNickCount.setText(s.length() + "/10");
                }
            }
        });
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back_ib) {
            finish();
        } else if (id == R.id.done_tv) {
            handleSaveDone();
        }
    }

    //点击保存按钮-》调用修改接口
    private void handleSaveDone() {
        switch (type) {
            case TYPE_NICK:
                String nickName = binding.editTextNick.getText().toString().trim();
                if (nickName.length() > 10 || nickName.length() < 1) {
                    Msg.INSTANCE.toast("昵称不能空并且不能超过10个字符");
                } else {
                    modifyNickname(nickName);
                }
                break;
            case TYPE_SIGN:
                String desc = binding.editTextDesc.getText().toString().trim();
                if (desc.length() > 50 || desc.length() < 1) {
                    Msg.INSTANCE.toast("简介不能小于1个字符且不能超过50字符");
                } else {
                    modifyUserDesption(desc);
                }
                break;
            case TYPE_WECHAT:
                String WXID = binding.editTextWechat.getText().toString().trim();
                if (WXID.length() > 20 || WXID.length() < 6) {
                    Msg.INSTANCE.toast("微信号不能小于6个字符且不能超过20个字符");
                } else {
                    modifyUserWXID(WXID);
                }
                break;
        }
    }

    //昵称修改
    public void modifyNickname(String nickName) {
        meViewModel.updateNickname(nickName).observe(this, nopeSource -> {
            if (nopeSource instanceof Source.Success) {
                Msg.INSTANCE.toast("修改成功");
                PrefsManager.INSTANCE.setLoginName(nickName);
                Intent intent = new Intent();
                intent.putExtra("result", nickName);
                setResult(1001, intent);
                finish();
            } else {
                Msg.INSTANCE.toast("修改失败");
            }
        });
    }

    // 用户描述修改
    public void modifyUserDesption(String desc) {
        meViewModel.updateSign(desc).observe(this, nopeSource -> {
            if (nopeSource instanceof Source.Success) {
                Msg.INSTANCE.toast("修改成功");
                Intent intent = new Intent();
                intent.putExtra("result", desc);
                setResult(1002, intent);
                finish();
            } else {
                Msg.INSTANCE.toast("修改失败");
            }
        });
    }

    //用户的微信id 修改
    public void modifyUserWXID(String WXID) {
        meViewModel.updateWechatID(WXID).observe(this, nopeSource -> {
            if (nopeSource instanceof Source.Success) {
                Msg.INSTANCE.toast("修改成功");
                Intent intent = new Intent();
                intent.putExtra("result", WXID);
                setResult(1003, intent);
                finish();
            } else {
                Msg.INSTANCE.toast("修改失败");
            }
        });
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Msg.INSTANCE.toast("111");
//
//        return super.dispatchTouchEvent(ev);
//
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hideInputKeyboard(getCurrentFocus());
        return super.onTouchEvent(event);
    }

    protected void hideInputKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}

