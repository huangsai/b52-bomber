package com.mobile.app.bomber.tik.home

import com.mobile.app.bomber.common.base.RecyclerViewFragment

abstract class TopHomeFragment : RecyclerViewFragment() {

    protected val pFragment by lazy { parentFragment as HomeFragment }
    protected val model: HomeViewModel
        get() = pFragment.model

    override fun onResume() {
        super.onResume()
        pFragment.currentFragment = this
    }

    override fun onDestroy() {
        super.onDestroy()
        if (pFragment.currentFragment == this) {
            pFragment.currentFragment = null
        }
    }
}