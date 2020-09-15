package com.mobile.app.bomber.tik.home

import android.os.Bundle

class RecommendFragment : BaseRecommendFragment() {

    override fun onResume() {
        super.onResume()
        loadIfEmpty()
    }

    companion object {

        @JvmStatic
        fun newInstance(position: Int): RecommendFragment = RecommendFragment().apply {
            arguments = Bundle().apply {
                putInt("position", position)
            }
        }
    }
}