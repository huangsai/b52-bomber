package com.mobile.app.bomber.tik.mine

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat

class ChoosePicDialogFragment(private var callback: CallBack?) : BaseBottomControlDialogFragment() {

    companion object {
        @JvmStatic
        fun newInstance(callback: CallBack): ChoosePicDialogFragment = ChoosePicDialogFragment(callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.moreDialogFirstText.text = "拍一张"
        binding.moreDialogSecondText.text = "相册选择"
//        binding.moreDialogThirdText.text = "查看大图"
    }

    override fun onFirstClick() {
        super.onFirstClick()
        callback?.onTakeCamera()
    }

    override fun onSecondClick() {
        super.onSecondClick()
        callback?.onChoosePhoto()
    }

    override fun onThirdClick() {
        super.onThirdClick()
//        callback?.onBrowseBigPic()
    }

    interface CallBack {
        fun onTakeCamera()

        fun onChoosePhoto()

//        fun onBrowseBigPic()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callback = null
    }
}