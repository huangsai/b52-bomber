package com.mobile.app.bomber.tik.mine

import android.os.Bundle
import android.view.View
import com.mobile.app.bomber.common.base.Msg

class UserDetailMoreDialogFragment : BaseBottomControlDialogFragment() {

    companion object {

        @JvmStatic
        fun newInstance(): UserDetailMoreDialogFragment = UserDetailMoreDialogFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.moreDialogFirstText.text = "举报"
        binding.moreDialogSecondText.text = "拉黑"
        binding.moreDialogThirdText.visibility = View.GONE
    }

    override fun onFirstClick() {
        super.onFirstClick()
        Msg.toast("举报")
    }

    override fun onSecondClick() {
        super.onSecondClick()
        Msg.toast("拉黑")
    }

}