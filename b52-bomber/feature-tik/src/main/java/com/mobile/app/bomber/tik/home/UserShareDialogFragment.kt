package com.mobile.app.bomber.tik.home

import android.os.Bundle
import android.view.View
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.tik.mine.BaseBottomControlDialogFragment

class UserShareDialogFragment(private var callback: UserShareDialogFragment.CallBack?) : BaseBottomControlDialogFragment() {

    companion object {
        @JvmStatic
        fun newInstance(callback: UserShareDialogFragment.CallBack): UserShareDialogFragment = UserShareDialogFragment(callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.moreDialogFirstText.text = "分享链接"
        binding.moreDialogSecondText.text = "图片分享"
        binding.moreDialogThirdText.visibility = View.GONE
    }

    override fun onFirstClick() {
        super.onFirstClick()
        callback?.onShareText()
        Msg.toast("分享")
    }

    override fun onSecondClick() {
        super.onSecondClick()
        callback?.onShareImage()
        Msg.toast("图片")
    }

    interface CallBack {
        fun onShareText()

        fun onShareImage()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        callback = null
    }

}