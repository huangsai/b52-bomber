package com.mobile.app.bomber.tik.mine

import android.os.Bundle
import android.view.View

class ChooseGenderDialogFragment(private var callback: CallBack?) : BaseBottomControlDialogFragment() {

    companion object {
        @JvmStatic
        fun newInstance(callback: CallBack): ChooseGenderDialogFragment = ChooseGenderDialogFragment(callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.moreDialogFirstText.text = "男"
        binding.moreDialogSecondText.text = "女"
        binding.moreDialogThirdText.text = "不显示"
    }

    override fun onFirstClick() {
        super.onFirstClick()
        callback?.onSelectMan()
    }

    override fun onSecondClick() {
        super.onSecondClick()
        callback?.onSelectWoman()
    }

    override fun onThirdClick() {
        super.onThirdClick()
        callback?.onSelectNoDisplay()
    }

    interface CallBack {
        fun onSelectMan()

        fun onSelectWoman()

        fun onSelectNoDisplay()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callback = null
    }
}