package com.liangfeng.crashhandler

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mobile.app.bomber.common.R

//import android.support.v7.app.AppCompatActivity
//import kotlinx.android.synthetic.main.dialog_crash.*

/**
 * Created by mzf on 2018/7/7.
 * Email:liangfeng093@gmail.com
 */
class CrashDialog : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_crash)
        var exception = intent?.getStringExtra("Exception")
        var deviceInfo = intent?.getStringExtra("DeviceInfo")
        var email = intent?.getStringExtra("Email")
//        btn_exit?.setOnClickListener {
//            onBackPressed()
//        }
//        btn_feedback?.setOnClickListener {
//            //收件人
//            var uri = Uri.parse("mailto:" + email)
//            /**
//             * Intent.ACTION_SENDTO 无附件的发送
//             * Intent.ACTION_SEND 带附件的发送
//             * Intent.ACTION_SEND_MULTIPLE 带有多附件的发送
//             */
//            var intent = Intent(android.content.Intent.ACTION_SENDTO, uri)
//            // 标题
//            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name) + "-异常反馈")
//            // 正文
//            intent.putExtra(android.content.Intent.EXTRA_TEXT, deviceInfo + "\n" + exception)
//            startActivity(intent)
//        }
//        iv_crash?.setOnClickListener {
//            tv_exception?.setText(exception)
//        }
    }
}