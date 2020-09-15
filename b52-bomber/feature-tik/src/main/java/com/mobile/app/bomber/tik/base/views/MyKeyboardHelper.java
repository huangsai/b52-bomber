package com.mobile.app.bomber.tik.base.views;

import android.app.Dialog;
import android.graphics.Rect;
import android.view.View;

import com.mobile.guava.android.context.ContextExtKt;

public class MyKeyboardHelper {

    public static boolean isKeyboardVisible(Dialog dialog) {
        Rect r = new Rect();
        View activityRoot = dialog.getWindow().getDecorView();
        int visibleThreshold = Math.round(
                ContextExtKt.dp2px(dialog.getContext(), 100f)
        );
        activityRoot.getWindowVisibleDisplayFrame(r);
        int heightDiff = activityRoot.getRootView().getHeight() - r.height();
        return heightDiff > visibleThreshold;
    }
}
