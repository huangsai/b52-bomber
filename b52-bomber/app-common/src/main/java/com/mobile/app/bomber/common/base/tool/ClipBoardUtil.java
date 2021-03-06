package com.mobile.app.bomber.common.base.tool;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

import com.mobile.guava.android.mvvm.AndroidX;

import com.mobile.app.bomber.common.base.Msg;

import timber.log.Timber;

public class ClipBoardUtil {

    public static void copy(String text) {
        Timber.tag("Clipboard").d(text);
        ClipboardManager manager = (ClipboardManager) AndroidX.INSTANCE.myApp()
                .getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager != null) {
            try {
                manager.setPrimaryClip(manager.getPrimaryClip());
                manager.setPrimaryClip(ClipData.newPlainText("", text));
                if (!TextUtils.isEmpty(text)) {
                    Msg.INSTANCE.toast("复制成功");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void clear() {
        copy("");
    }
}