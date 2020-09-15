package com.mobile.app.bomber.tik.home

import android.os.Bundle
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
                R.id.fragment_container_view,
                NearbyFragment.newInstance(0)
        )
     }
}