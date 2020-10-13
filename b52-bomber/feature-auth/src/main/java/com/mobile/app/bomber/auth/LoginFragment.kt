package com.mobile.app.bomber.auth

import android.os.Bundle
import android.os.CountDownTimer
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import com.mobile.app.bomber.auth.databinding.AuthFragmentLoginBinding
import com.mobile.app.bomber.runner.base.PrefsManager
import com.mobile.ext.glide.GlideApp
import com.mobile.guava.android.context.toColor
import com.mobile.guava.android.ui.view.text.MySpannable
import com.mobile.guava.jvm.Guava
import java.util.concurrent.TimeUnit

class LoginFragment : NopeAuthFragment() {

    private var _binding: AuthFragmentLoginBinding? = null
    private val binding: AuthFragmentLoginBinding get() = _binding!!
    private var isCountDown = false
    private var myTimer: MyTimer? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = AuthFragmentLoginBinding.inflate(inflater, container, false)
        binding.txtAgreement1.text = buildAgreementSpannable()
        binding.txtAgreement2.text = buildAgreementSpannable()
        binding.txtLoginWithAnother.text = buildAnotherSpannable()
        setLoginState()

        binding.imgClose.setOnClickListener {
            pActivity.onBackPressed()
        }

        binding.txtVerifyCode.setOnClickListener {
            if (!isCountDown) {
                getVerifyCode()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        myTimer?.cancel()
    }

    private fun getVerifyCode() {
        viewModel.getVerifyCode()
    }

    private fun buildAgreementSpannable(): MySpannable {
        val highlightColor = pActivity.toColor(R.color.auth_login_agreement_highlight)
        return MySpannable(getString(R.string.auth_agreement))
                .findAndSpan("用户协议") { ForegroundColorSpan(highlightColor) }
                .findAndSpan("隐私政策") { ForegroundColorSpan(highlightColor) }
    }

    private fun buildAnotherSpannable(): MySpannable {
        val highlightColor = pActivity.toColor(R.color.auth_login_with_another_highlight)
        return MySpannable(getString(R.string.auth_login_with_another))
                .findAndSpan("登录") { ForegroundColorSpan(highlightColor) }
    }

    private fun setLoginState() {
        if (Guava.isDebug) {
            binding.editPhone.setText("19965412404")
        }

        if (PrefsManager.getToken().isNullOrEmpty()) {
            binding.layoutPhone.isInvisible = false
            binding.layoutVerifyCode.isInvisible = false
            binding.txtLoginTitle.isInvisible = false
            binding.txtAgreement1.isInvisible = false
            binding.btnLogin.setText(R.string.auth_login)
            binding.imgClose.setImageResource(R.drawable.auth_close)
        } else {
            binding.imgProfile.isInvisible = false
            binding.txtAgreement2.isInvisible = false
            binding.txtLoginWithAnother.isInvisible = false
            binding.imgClose.setImageResource(R.drawable.auth_back)
            binding.btnLogin.setText(R.string.auth_login_with_key)
            binding.txtUsername.text = PrefsManager.getLoginName()
            GlideApp.with(this)
                    .load(PrefsManager.getHeadPicUrl())
                    .placeholder(R.drawable.auth_default_profile)
                    .into(binding.imgProfile)
        }
    }

    private fun startCountDown() {
        myTimer?.cancel()
        myTimer = MyTimer().also {
            isCountDown = true
            it.start()
        }
    }

    private inner class MyTimer : CountDownTimer(60 * 1000L, 1000L) {

        override fun onTick(millisUntilFinished: Long) {
            _binding?.txtVerifyCode?.let {
                it.text = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished).toString() + "秒"
            }
        }

        override fun onFinish() {
            isCountDown = false
            _binding?.txtVerifyCode?.let {
                it.text = "重新获取"
            }
        }
    }
}