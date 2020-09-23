package com.mobile.app.bomber

import android.os.Bundle
import com.mobile.app.bomber.common.base.MyBaseActivity
import com.mobile.app.bomber.databinding.ActivityAppBinding
import com.mobile.app.bomber.runner.features.FeatureRouter
import com.mobile.guava.android.mvvm.newStartActivity

class AppActivity : MyBaseActivity() {

    private lateinit var binding: ActivityAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // testMovie()
        testTik()
    }

    private fun testTik() {
        this.newStartActivity(com.mobile.app.bomber.tik.MainActivity::class.java)
        finish()
    }

    private fun testMovie() {
        supportFragmentManager.beginTransaction()
                .add(R.id.layout_fragment, FeatureRouter.newMovieFragment(0), "MovieFragment")
                .addToBackStack("MovieFragment")
                .commit()
    }
}