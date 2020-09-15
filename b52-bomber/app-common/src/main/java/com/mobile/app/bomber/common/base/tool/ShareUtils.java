package com.mobile.app.bomber.common.base.tool;

import android.content.Context;
import android.content.Intent;

import com.mobile.app.bomber.common.base.Msg;

/**
 * Intent分享工具类
 */
public class ShareUtils {

    //分享QQ，打开QQ页面
    public static void shareToQQ(Context context) {

        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.tencent.mobileqq");
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Msg.INSTANCE.toast("请检查是否安装QQ");
        }

    }

    //分享微信，打开微信页面
    public static void shareToWechat(Context context) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Msg.INSTANCE.toast("请检查是否安装微信");
        }

    }
}
