package com.mobile.app.bomber.movie.player

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import com.mobile.app.bomber.common.base.tool.isLandscape
import com.mobile.app.bomber.common.base.tool.requestNormalScreenWithPortrait
import com.mobile.app.bomber.data.http.entities.ApiMovie
import com.mobile.guava.https.Values
import com.mobile.app.bomber.movie.MovieLib
import com.mobile.app.bomber.movie.databinding.MovieActivityPlayerBinding
import com.mobile.guava.android.mvvm.BaseActivity
import com.mobile.guava.android.mvvm.newStartActivity

class PlayerActivity : BaseActivity() {

    private lateinit var binding: MovieActivityPlayerBinding
    private lateinit var data: ApiMovie.Movie
    private lateinit var commentPresenter: CommentPresenter
    private lateinit var sourcePresenter: SourcePresenter
    private lateinit var playerPresenter: PlayerPresenter

    private val model: PlayerViewModel by viewModels { MovieLib.component.viewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.BLACK

        data = Values.take("PlayerActivity")
        binding = MovieActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.includeMovieInfo.layoutMovieInfo.collapse(false)

        commentPresenter = CommentPresenter(binding, this, model)
        sourcePresenter = SourcePresenter(binding, this, model)
        playerPresenter = PlayerPresenter(binding, this, model)

        commentPresenter.onCreate()
        sourcePresenter.onCreate()
        playerPresenter.onCreate()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        playerPresenter.onConfigurationChanged(newConfig)
    }

    override fun onResume() {
        super.onResume()
        playerPresenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        playerPresenter.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        commentPresenter.onDestroy()
        sourcePresenter.onDestroy()
        playerPresenter.onDestroy()
    }

    override fun onBackPressed() {
        if (isLandscape()) {
            requestNormalScreenWithPortrait()
        } else {
            super.onBackPressed()
        }
    }

    companion object {

        @JvmStatic
        fun start(activity: Activity, data: ApiMovie.Movie) {
            Values.put("PlayerActivity", data)
            activity.newStartActivity(PlayerActivity::class.java)
        }
    }
}