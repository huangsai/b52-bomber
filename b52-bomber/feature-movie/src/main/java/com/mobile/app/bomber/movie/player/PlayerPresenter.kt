package com.mobile.app.bomber.movie.player

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Point
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.core.net.toUri
import androidx.core.view.doOnLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.request.target.Target
import com.github.rubensousa.previewseekbar.PreviewBar
import com.github.rubensousa.previewseekbar.PreviewLoader
import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieActivityPlayerBinding
import com.mobile.app.bomber.movie.player.exo.GlideThumbnailTransformation
import com.mobile.app.bomber.runner.base.PrefsManager
import com.mobile.ext.exo.ExoPlayerX
import com.mobile.ext.exo.TAG_EXO_PLAYER
import com.mobile.ext.glide.GlideApp
import com.mobile.guava.android.context.isLandscape
import com.mobile.guava.android.context.requestFullScreenWithLandscape
import com.mobile.guava.android.context.requestNormalScreenWithPortrait
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.android.ui.screen.screen
import com.mobile.guava.data.safeToFloat
import com.pacific.adapter.AdapterViewHolder
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class PlayerPresenter(
        binding: MovieActivityPlayerBinding,
        playerActivity: PlayerActivity,
        model: PlayerViewModel
) : BasePlayerPresenter(binding, playerActivity, model), Player.EventListener, PreviewLoader,
        PreviewBar.OnScrubListener, CompoundButton.OnCheckedChangeListener {

    private val btnFullScreen: ImageButton = binding.viewPlayer.findViewById(R.id.btn_fullscreen)
    private val btnSpeed: TextView = binding.viewPlayer.findViewById(R.id.btn_speed)
    private val btnRate: TextView = binding.viewPlayer.findViewById(R.id.btn_rate)
    private val imgPreView: ImageView = binding.viewPlayer.findViewById(R.id.img_preview)
    private val previewTimeBar: PreviewTimeBar = binding.viewPlayer.findViewById(R.id.exo_progress)
    private val imgBack: ImageView = binding.viewPlayer.findViewById(R.id.img_back)
    private var handler: Handler? = Handler()
    private val run = Runnable {
        binding.viewPlayer.hideController()
    }

    private var thumbnailsUrl = ""

    private lateinit var balloon: Balloon

    private val speeds = arrayOf("0.75x", "1.0x", "1.25x", "1.5x", "1.75x", "2.0x")
    private val bitRates = arrayOf("自动", "240p", "360p", "480p", "720p", "1080p")
    private val radioButtons = ArrayList<RadioButton>()
    private var currentSpeed = 1
    private var currentBitRate = 0
    private var optionFlag = 0
    private var player: SimpleExoPlayer? = null
    private var markedPlayDuration = false
    private var originPlayerViewSize: Point? = null

    init {
        binding.progress.setOnClickListener(this)
        imgBack.setOnClickListener(this)
        btnFullScreen.setOnClickListener(this)
        btnSpeed.setOnClickListener(this)
        btnRate.setOnClickListener(this)
        previewTimeBar.addOnScrubListener(this)
        previewTimeBar.setPreviewLoader(this)
        previewTimeBar.isPreviewEnabled = thumbnailsUrl.isNotEmpty()
        btnSpeed.text = speeds[currentSpeed]
        btnRate.text = bitRates[currentBitRate]
        AndroidX.appDialogCount.observe(playerActivity, Observer { dialogCount ->
            if (dialogCount > 0) {
                onPause()
            } else {
                onResume()
            }
        })

        binding.viewPlayer.doOnLayout {
            originPlayerViewSize = Point(it.measuredWidth, it.measuredHeight)
        }
    }

    override fun onCreate() {
        ExoPlayerX.addEventListener(this)
        player = ExoPlayerX.player
        player?.repeatMode = Player.REPEAT_MODE_OFF
        player?.addListener(this)
        binding.viewPlayer.player = player
        binding.viewPlayer.setControllerVisibilityListener {
            if (it == View.VISIBLE) {
                handler?.postDelayed(run, 10000)
            } else if (it == View.GONE) {
                handler?.removeCallbacks(run)
            }
        }
        // val sdCard = ensureFileSeparator(AndroidX.myApp.getExternalFilesDir(null)!!.absolutePath!!)
        // ExoPlayerX.play((sdCard + "trailer.mp4").toUri())
        playerActivity.data?.apply {
            ExoPlayerX.play(movie.movieUrl.toUri())
        }

        playerActivity.lifecycleScope.launch(Dispatchers.IO) {
            if (PrefsManager.isLogin()) {
                model.postMoviePlayNum(playerActivity.movieId.toInt(), PrefsManager.getUserId())
            } else {
                model.postMoviePlayNum(playerActivity.movieId.toInt(), 0L)
            }
        }

    }

    override fun onResume() {
        ExoPlayerX.resume()
    }

    override fun onPause() {
        ExoPlayerX.pause()
        playDuration()
    }

    override fun onDestroy() {
        handler?.removeCallbacks(run)
        handler = null
        player = null
        ExoPlayerX.removeEventListener(this)
        ExoPlayerX.stop()
        previewTimeBar.removeOnScrubListener(this)
        previewTimeBar.setPreviewLoader(null)
        binding.viewPlayer.player = null
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        if (playWhenReady) {
            binding.progress.visibility = View.INVISIBLE
        } else {
            if (reason == Player.PLAY_WHEN_READY_CHANGE_REASON_REMOTE) {
                binding.progress.visibility = View.VISIBLE
            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        Timber.tag(TAG_EXO_PLAYER).d(error)
    }

    override fun loadPreview(currentPosition: Long, max: Long) {
        if (ExoPlayerX.isPlaying) {
            ExoPlayerX.pause()
        }
        GlideApp.with(playerActivity)
                .load(thumbnailsUrl)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .transform(GlideThumbnailTransformation(currentPosition))
                .into(imgPreView)
    }

    override fun onScrubStart(previewBar: PreviewBar) {
        ExoPlayerX.pause()
    }

    override fun onScrubMove(previewBar: PreviewBar, progress: Int, fromUser: Boolean) {
    }

    override fun onScrubStop(previewBar: PreviewBar) {
        ExoPlayerX.resume()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            btnSpeed.visibility = View.VISIBLE
            setPlayerViewSize(screen.y, screen.x)
        } else {
            btnSpeed.visibility = View.GONE
            originPlayerViewSize?.let {
                setPlayerViewSize(it.x, it.y)
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.progress -> binding.layoutGameAd.toggle()
            R.id.img_back -> playerActivity.onBackPressed()
            R.id.btn_fullscreen -> {
                if (playerActivity.isLandscape()) {
                    playerActivity.requestNormalScreenWithPortrait()
                } else {
                    playerActivity.requestFullScreenWithLandscape()
                }
            }
            R.id.btn_speed -> showVideoOptions(v, 1)
            R.id.btn_rate -> showVideoOptions(v, 2)
        }
    }

    private fun setPlayerViewSize(w: Int, h: Int) {
        binding.viewPlayer.layoutParams?.let {
            it.width = w
            it.height = h
            binding.viewPlayer.layoutParams = it
        }

    }

    private fun showVideoOptions(anchor: View, flag: Int) {
        optionFlag = flag
        balloon = Balloon.Builder(playerActivity.application)
                .setLayout(R.layout.movie_player_video_options)
                .setArrowVisible(false)
                .setBackgroundColor(Color.TRANSPARENT)
                .setBalloonAnimation(BalloonAnimation.FADE)
                .setLifecycleOwner(playerActivity)
                .setMarginRight(40)
                .setMarginBottom(24)
                .setCornerRadius(0f)
                .setElevation(0)
                .setAutoDismissDuration(3000)
                .build()

        balloon.setOnBalloonDismissListener {
            radioButtons.clear()
            optionFlag = 0
        }

        with(balloon.getContentView()) {
            radioButtons.add(findViewById(R.id.rb_one))
            radioButtons.add(findViewById(R.id.rb_two))
            radioButtons.add(findViewById(R.id.rb_three))
            radioButtons.add(findViewById(R.id.rb_four))
            radioButtons.add(findViewById(R.id.rb_five))
            radioButtons.add(findViewById(R.id.rb_six))
        }

        if (flag == 1) {
            radioButtons.indices.forEach { index ->
                radioButtons[currentSpeed].isChecked = true
                radioButtons[index].setOnCheckedChangeListener(this)
                radioButtons[index].text = speeds[index]
            }
        } else {
            radioButtons.indices.forEach { index ->
                radioButtons[currentBitRate].isChecked = true
                radioButtons[index].setOnCheckedChangeListener(this)
                radioButtons[index].text = bitRates[index]
            }
        }

        balloon.show(anchor)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (radioButtons.isNotEmpty()) {
            val index = radioButtons.indexOf(buttonView)
            if (optionFlag == 1) {
                currentSpeed = index
                btnSpeed.text = speeds[index]
                ExoPlayerX.player.setPlaybackParameters(PlaybackParameters(
                        speeds[index].replace("x", "").safeToFloat(),
                        1.0f
                ))
            } else if (optionFlag == 2) {
                currentBitRate = index
                btnRate.text = bitRates[index]
            }
        }
        balloon.dismiss()
    }

    override fun load(view: ImageView, holder: AdapterViewHolder) {
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            Player.STATE_IDLE -> {
            }
            Player.STATE_BUFFERING -> {
                binding.progress.visibility = View.VISIBLE
            }
            Player.STATE_READY -> {
                binding.progress.visibility = View.INVISIBLE
            }
            Player.STATE_ENDED -> {
                playDuration()
            }
        }
    }

    private fun playDuration() {
        if (!markedPlayDuration) {
            markedPlayDuration = true
            player?.let {
                playerActivity.lifecycleScope.launch(Dispatchers.IO) {
                    model.postMoviePlayDurationRecord(
                            playerActivity.movieId.toInt(),
                            it.duration,
                            if (PrefsManager.isLogin()) PrefsManager.getUserId() else 0L
                    )
                }
            }
        }
    }
}