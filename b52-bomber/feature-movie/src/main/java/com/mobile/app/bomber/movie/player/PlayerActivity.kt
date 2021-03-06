package com.mobile.app.bomber.movie.player

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.util.Util
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.common.base.MyBaseActivity
import com.mobile.app.bomber.common.base.tool.chrome
import com.mobile.app.bomber.data.http.entities.ApiMovieDetail
import com.mobile.app.bomber.movie.MovieX
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieActivityPlayerBinding
import com.mobile.app.bomber.runner.base.PrefsManager
import com.mobile.ext.exo.ExoPlayerX
import com.mobile.guava.android.context.isLandscape
import com.mobile.guava.android.context.requestNormalScreenWithPortrait
import com.mobile.guava.android.mvvm.newStartActivity
import com.mobile.guava.data.Values
import com.mobile.guava.jvm.domain.Source
import com.mobile.guava.jvm.extension.exhaustive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 长视频播放页面
 */
class PlayerActivity : MyBaseActivity(), View.OnClickListener {
    private var _binding: MovieActivityPlayerBinding? = null
    private val binding get() = _binding!!

    var data: ApiMovieDetail? = null
        private set

    var movieId: Long = 0L

    private lateinit var commentPresenter: CommentPresenter
    private lateinit var sourcePresenter: SourcePresenter
    private lateinit var playerPresenter: PlayerPresenter

    private val model: PlayerViewModel by viewModels { MovieX.component.viewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ExoPlayerX.initialize()
        ExoPlayerX.createPlayer()

        _binding = MovieActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.includeMovieInfo.layoutMovieInfo.collapse(false)
        commentPresenter = CommentPresenter(binding, this, model)
        sourcePresenter = SourcePresenter(binding, this, model)
        playerPresenter = PlayerPresenter(binding, this, model)
        binding.includeGameAd.txtGo.setOnClickListener(this)
        movieId = Values.take("PlayerActivity_movieId")!!
        load()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        playerPresenter.onConfigurationChanged(newConfig)
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            playerPresenter.onResume()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24) {
            playerPresenter.onResume()
        }
        if (binding.danmaku.isPrepared && binding.danmaku.isPaused) {
            binding.danmaku.resume()
        }
    }


    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            playerPresenter.onPause()
        }
        if (binding.danmaku.isPrepared) {
            binding.danmaku.pause()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            playerPresenter.onPause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        commentPresenter.onDestroy()
        sourcePresenter.onDestroy()
        playerPresenter.onDestroy()
        binding.danmaku.release()
    }

    override fun onBackPressed() {
        if (isLandscape()) {
            requestNormalScreenWithPortrait()
        } else {
            super.onBackPressed()
            binding.danmaku.release()
        }
    }

    private fun load() {
        lifecycleScope.launch(Dispatchers.IO) {
            val source = if (PrefsManager.isLogin()) {
                model.getMovieDetail(movieId, PrefsManager.getUserId(), PrefsManager.getToken())
            } else {
                model.getMovieDetail(movieId, 0, "default")
            }
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                        data = source.requireData()
                        commentPresenter.onCreate()
                        sourcePresenter.onCreateSouce(movieId)
                        playerPresenter.onCreate()
                    }
                    else -> Msg.handleSourceException(source.requireError())
                }.exhaustive
            }
        }
    }

    companion object {

        fun start(activity: Activity, movieId: Long) {
            Values.put("PlayerActivity_movieId", movieId)
            activity.newStartActivity(PlayerActivity::class.java)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.txt_go -> {
                this.chrome(data?.ad?.url)
            }
        }
    }
}