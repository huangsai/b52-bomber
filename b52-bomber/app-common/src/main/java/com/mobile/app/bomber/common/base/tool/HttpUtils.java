package com.mobile.app.bomber.common.base.tool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtils {
    /**
     * 获取网络图片
     *
     * @param urlString 如：http://f.hiphotos.baidu.com/image/w%3D2048/sign=3
     *                  b06d28fc91349547e1eef6462769358
     *                  /d000baa1cd11728b22c9e62ccafcc3cec2fd2cd3.jpg
     * @return
     * @date 2014.05.10
     */
    public static Bitmap getNetWorkBitmap(String urlString) {
        URL imgUrl = null;
        Bitmap bitmap = null;
        try {
//           boolean ischeck = checkUrl(urlString,1000);
//           if (ischeck ==false){
//              return null;
//           }
            imgUrl = new URL(urlString);
            // 使用HttpURLConnection打开连接
            HttpURLConnection urlConn = (HttpURLConnection) imgUrl
                    .openConnection();
            urlConn.setDoInput(true);
            urlConn.connect();
            // 将得到的数据转化成InputStream
            InputStream is = urlConn.getInputStream();
            // 将InputStream转换成Bitmap
            bitmap = BitmapFactory.decodeStream(is);
//            String coverFilePath = FileUtil.saveBitmapToFile(bitmap, "co_image");
//            File file = new File(coverFilePath)

            is.close();
            int code = urlConn.getResponseCode();
            if ((code!=200)){
                return null;
            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            System.out.println("[getNetWorkBitmap->]MalformedURLException");
            e.printStackTrace();
        } catch (IOException e) {

            System.out.println("[getNetWorkBitmap->]IOException");
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Boolean checkUrl(String address, int waitMilliSecond) {
        try {
            URL url = new URL(address);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(waitMilliSecond);
            conn.setReadTimeout(waitMilliSecond);

            try {
                conn.connect();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            int code = conn.getResponseCode();
            if ((code >= 100) && (code < 400)) {
                return true;
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
