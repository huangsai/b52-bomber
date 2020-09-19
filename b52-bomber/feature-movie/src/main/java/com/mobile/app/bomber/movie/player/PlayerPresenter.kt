package com.mobile.app.bomber.movie.player

import android.content.res.Configuration
import android.graphics.Color
import android.view.View
import android.widget.*
import androidx.core.net.toUri
import com.bumptech.glide.request.target.Target
import com.github.rubensousa.previewseekbar.PreviewBar
import com.github.rubensousa.previewseekbar.PreviewLoader
import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.mobile.app.bomber.common.base.GlideApp
import com.mobile.app.bomber.common.base.tool.isLandscape
import com.mobile.app.bomber.common.base.tool.requestFullScreenWithLandscape
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieActivityPlayerBinding
import com.mobile.app.bomber.movie.player.exo.ExoPlayerMediaSourceBuilder
import com.mobile.app.bomber.movie.player.exo.GlideThumbnailTransformation
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.https.safeToFloat
import com.mobile.guava.jvm.io.ensureFileSeparator
import com.pacific.adapter.AdapterViewHolder
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import timber.log.Timber

class PlayerPresenter(
        binding: MovieActivityPlayerBinding,
        playerActivity: PlayerActivity,
        model: PlayerViewModel
) : BasePlayerPresenter(binding, playerActivity, model), Player.EventListener, PreviewLoader,
        PreviewBar.OnScrubListener, CompoundButton.OnCheckedChangeListener, PlaybackPreparer {

    private val controlDispatcher = DefaultControlDispatcher()
    private val mediaSourceBuilder = ExoPlayerMediaSourceBuilder(AndroidX.myApp)
    private val trackSelector = DefaultTrackSelector(AndroidX.myApp)
    private val trackSelectorParameters = DefaultTrackSelector.ParametersBuilder(AndroidX.myApp)
            .setTunnelingAudioSessionId(C.generateAudioSessionIdV21(AndroidX.myApp))
            .build()

    private val btnFullScreen: ImageButton = binding.viewPlayer.findViewById(R.id.btn_fullscreen)
    private val btnSpeed: TextView = binding.viewPlayer.findViewById(R.id.btn_speed)
    private val btnRate: TextView = binding.viewPlayer.findViewById(R.id.btn_rate)
    private val imgPreView: ImageView = binding.viewPlayer.findViewById(R.id.img_preview)
    private val previewTimeBar: PreviewTimeBar = binding.viewPlayer.findViewById(R.id.exo_progress)

    private var thumbnailsUrl = ""

    private var isOnPlaying = false
    private lateinit var player: SimpleExoPlayer
    private lateinit var balloon: Balloon

    private val speeds = arrayOf("0.75x", "1.0x", "1.25x", "1.5x", "1.75x", "2.0x")
    private val bitRates = arrayOf("自动", "240p", "360p", "480p", "720p", "1080p")
    private val radioButtons = ArrayList<RadioButton>()
    private var currentSpeed = 1
    private var currentBitRate = 0
    private var optionFlag = 0

    init {
        trackSelector.parameters = trackSelectorParameters
        binding.progress.setOnClickListener(this)
        binding.imgBack.setOnClickListener(this)
        btnFullScreen.setOnClickListener(this)
        btnSpeed.setOnClickListener(this)
        btnRate.setOnClickListener(this)
        previewTimeBar.addOnScrubListener(this)
        previewTimeBar.setPreviewLoader(this)
        previewTimeBar.isPreviewEnabled = thumbnailsUrl.isNotEmpty()
        btnSpeed.text = speeds[currentSpeed]
        btnRate.text = bitRates[currentBitRate]
        AndroidX.appDialogCount.observe(playerActivity, { dialogCount ->
            if (::player.isInitialized) {
                if (dialogCount > 0) {
                    onPause()
                } else {
                    onResume()
                }
            }
        })
    }

    override fun onCreate() {
        player = SimpleExoPlayer.Builder(playerActivity.application)
                .setUseLazyPreparation(false)
                .setTrackSelector(trackSelector)
                .build()

        binding.viewPlayer.player = player
        binding.viewPlayer.setPlaybackPreparer(this)

        player.addListener(this)
        player.playWhenReady = true
        player.repeatMode = Player.REPEAT_MODE_ALL
        player.prepare(createMediaSource(), false, false)
    }

    override fun onResume() {
        controlDispatcher.dispatchSetPlayWhenReady(player, true)
    }

    override fun onPause() {
        controlDispatcher.dispatchSetPlayWhenReady(player, false)
    }

    override fun onDestroy() {
        player.removeListener(this)
        player.release()
        previewTimeBar.removeOnScrubListener(this)
        previewTimeBar.setPreviewLoader(null)
    }

    private fun createMediaSource(): MediaSource {
        AndroidX.myApp.getExternalFilesDir(null)!!.absolutePath.also {
            mediaSourceBuilder.uri = (ensureFileSeparator(it) + "trailer.mp4").toUri()
        }
        return mediaSourceBuilder.getMediaSource(true)
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
            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        isOnPlaying = isPlaying
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        Timber.tag("ExoPlayer").d(error)
    }

    override fun preparePlayback() {
        player.retry()
    }

    override fun loadPreview(currentPosition: Long, max: Long) {
        if (player.isPlaying) {
            player.playWhenReady = false
        }
        GlideApp.with(playerActivity)
                .load(thumbnailsUrl)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .transform(GlideThumbnailTransformation(currentPosition))
                .into(imgPreView)
    }

    override fun onScrubStart(previewBar: PreviewBar) {
        player.playWhenReady = false
    }

    override fun onScrubMove(previewBar: PreviewBar, progress: Int, fromUser: Boolean) {
    }

    override fun onScrubStop(previewBar: PreviewBar) {
        player.playWhenReady = true
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            btnFullScreen.visibility = View.GONE
            btnSpeed.visibility = View.VISIBLE
            btnRate.visibility = View.VISIBLE
        } else {
            btnFullScreen.visibility = View.VISIBLE
            btnSpeed.visibility = View.GONE
            btnRate.visibility = View.GONE
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.progress -> binding.layoutGameAd.toggle()
            R.id.img_back -> playerActivity.onBackPressed()
            R.id.btn_fullscreen -> {
                if (playerActivity.isLandscape()) {
                    return
                }
                playerActivity.requestFullScreenWithLandscape()
            }
            R.id.btn_speed -> showVideoOptions(v, 1)
            R.id.btn_rate -> showVideoOptions(v, 2)
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
                player.setPlaybackParameters(PlaybackParameters(
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
        TODO("Not yet implemented")
    }
}