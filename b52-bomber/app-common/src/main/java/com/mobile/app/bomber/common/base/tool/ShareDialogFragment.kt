package com.mobile.app.bomber.common.base.tool

import android.os.Bundle
import android.view.View
import com.mobile.app.bomber.common.base.Msg

class ShareDialogFragment(private var callback: ShareDialogFragment.CallBack?) : BaseControlDialogFragment() {

    companion object {
        @JvmStatic
        fun newInstance(callback: ShareDialogFragment.CallBack): ShareDialogFragment = ShareDialogFragment(callback)
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
     }

    override fun onSecondClick() {
        super.onSecondClick()
        callback?.onShareImage()
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