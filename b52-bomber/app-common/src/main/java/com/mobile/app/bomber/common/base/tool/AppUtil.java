package com.mobile.app.bomber.common.base.tool;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;

import com.mobile.app.bomber.common.base.Msg;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AppUtil {

    public static Point getRealSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE
        );
        final Display display = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        if (Build.VERSION.SDK_INT >= 19) {
            // include navigation bar
            display.getRealSize(outPoint);
        } else {
            // exclude navigation bar
            display.getSize(outPoint);
        }
        if (outPoint.y > outPoint.x) {
            return new Point(outPoint.y, outPoint.x);
        } else {
            return new Point(outPoint.x, outPoint.y);
        }
    }

    /**
     * 复制内容
     *
     * @param context 上下文
     * @param content 复制内容
     */
    public static void copyContent(Context context, String content) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", content);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        Msg.INSTANCE.toast("复制成功");
    }

    /**
     * 处理日期问题:年月日
     *
     * @param birthday 年月日
     */
    public static String handleAgeStr(String birthday) {
        String modifyString = "";
        if (birthday.isEmpty() || birthday.length() < 4) return modifyString;
        String newString = birthday.substring(0, 4);
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");// HH:mm:ss
        String currentData = simpleDateFormat.format(date);
        Integer news = Integer.parseInt(newString);
        Integer currents = Integer.parseInt(currentData);
        if (Integer.parseInt(newString) == Integer.parseInt(currentData)) {
            modifyString = "0";
        } else if (news < currents) {
            Integer results = currents - news;
            modifyString = String.valueOf(results);
        } else {
            modifyString = "";
        }
        return modifyString;
    }

    /**
     * 判断字符串是否符合手机号码格式
     * 移动号段: 134,135,136,137,138,139,147,150,151,152,157,158,159,170,178,182,183,184,187,188
     * 联通号段: 130,131,132,145,155,156,170,171,175,176,185,186
     * 电信号段: 133,149,153,170,173,177,180,181,189,199
     * 防止号段更新频繁，不在做任何限制：第一位： 1第二位： 3～9 第三到第十一位只要是数字就可以通过
     *
     * @param mobileNums 待检测的字符串
     */
    public static boolean isMobileNO(String mobileNums) {
//        String telRegex = "^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))|(199)\\d{8}$";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
//        "^1(3([1-35-9]\\d|4[1-8])|4[14-9]\\d|5([0-25689]\\d|7[1-79])|66\\d|7[2-35-8]\\d|8[2-9]\\d|9[89]\\d)\\d{7}$");
        String telRegex = "^1[3-9]\\d{9}$";//
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex) && mobileNums.length() == 11;
    }

    /**
     * 获取验证码倒计时
     *
     * @param doneView 获取验证码按钮
     */
    public static Disposable verifyStartTime(Button doneView) {
        int count_time = 60; //总时间
        return Observable.interval(0, 1, TimeUnit.SECONDS) //0延迟  每隔1秒触发
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
                .take(count_time + 1) //设置循环次数
                .map(aLong -> count_time - aLong) //从60-1
                .doOnSubscribe(disposable -> doneView.setClickable(false)) //执行过程中按键为不可点击状态
                .subscribe(aLong -> doneView.setText(aLong + "秒"),
                        throwable -> {
                            throwable.printStackTrace();
                            doneView.setClickable(true);
                            doneView.setText("重新获取");
                        },
                        () -> {
                            doneView.setClickable(true);
                            doneView.setText("重新获取");
                        }
                );
    }

    /**
     * 判断GPS是否打开
     *
     * @param context 上下文
     */
    public static boolean isGpsAble(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 打开设置页面让用户自己打开GPS
     *
     * @param context 上下文
     */
    public static void openGPS(Context context) {
        Msg.INSTANCE.toast("请打开GPS~");
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * 获取导航栏高度
     */
    public static int getNavigationHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 获取版本号
     *
     * @param context 上下文
     * @return 版本号
     */
    public static int getVersionCode(Context context) {

        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本号
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取App的名称
     *
     * @param context 上下文
     * @return 名称
     */
    public static String getAppName(Context context) {
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //获取应用 信息
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            //获取albelRes
            int labelRes = applicationInfo.labelRes;
            //返回App的名称
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取当前app version name
     */
    public static String getAppVersionName(Context context) {
        String appVersionName = "";
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            appVersionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
//            Logger.e("CommonTools", e.getMessage());
        }
        return appVersionName;
    }

}
