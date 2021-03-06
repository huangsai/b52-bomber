package com.mobile.app.bomber.tik.home

import android.os.Bundle
import android.view.View
import com.mobile.app.bomber.common.base.MyBaseActivity

import com.mobile.app.bomber.tik.R
import com.mobile.app.bomber.tik.databinding.ActivityNearybyBinding

class NearByActivity : MyBaseActivity() {

    private lateinit var binding: ActivityNearybyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNearybyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addFragment(
                R.id.fragment_nearby_container_view,
                NearbyFragment.newInstance(0)
        )
        binding.backRoot.setOnClickListener { v: View? ->
            finish()
        }
    }
}