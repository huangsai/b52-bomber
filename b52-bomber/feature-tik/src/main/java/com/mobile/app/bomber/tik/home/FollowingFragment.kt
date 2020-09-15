package com.mobile.app.bomber.tik.home

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.mobile.app.bomber.runner.base.PrefsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FollowingFragment : BaseRecommendFragment() {

    override fun onResume() {
        super.onResume()
        if (PrefsManager.isLogin()) {
            loadIfEmpty()
        } else {
            if (onResumeCount > 1) {
                myAdapter.clear()
                lifecycleScope.launch {
                    delay(500)
                    try {
                        pFragment.selectFragment(1)
                    } catch (ignored: Exception) {
                    }
                }
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(position: Int): FollowingFragment = FollowingFragment().apply {
            arguments = Bundle().apply {
                putInt("position", position)
            }
        }
    }
}