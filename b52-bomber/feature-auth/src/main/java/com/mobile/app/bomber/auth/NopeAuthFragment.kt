package com.mobile.app.bomber.auth

import com.mobile.app.bomber.common.base.MyBaseFragment
import com.mobile.guava.jvm.extension.cast

abstract class NopeAuthFragment : MyBaseFragment() {

    val pActivity: AuthActivity get() = requireActivity().cast()

    val viewModel: AuthViewModel get() = pActivity.viewModel

    override fun onResume() {
        super.onResume()
        pActivity.fragment = this
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this == pActivity.fragment) {
            pActivity.fragment = null
        }
    }
}