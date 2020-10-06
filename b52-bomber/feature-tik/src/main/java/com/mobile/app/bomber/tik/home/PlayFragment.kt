package com.mobile.app.bomber.tik.home

import android.app.Activity
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.text.style.StyleSpan
import android.view.*
import android.widget.SeekBar
import androidx.activity.result.ActivityResultCallback
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.mobile.ext.glide.GlideApp
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.common.base.MyBaseFragment
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.data.http.entities.ApiDownLoadUrl
import com.mobile.app.bomber.data.http.entities.ApiVideo
import com.mobile.guava.data.Values
import com.mobile.app.bomber.runner.RunnerX
import com.mobile.app.bomber.tik.R
import com.mobile.app.bomber.tik.base.*
import com.mobile.app.bomber.tik.databinding.FragmentPlayBinding
import com.mobile.app.bomber.tik.mine.UserDetailActivity
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.android.mvvm.showDialogFragment
import com.mobile.guava.android.ui.view.text.MySpannable
import com.mobile.guava.data.nullSafe
import com.mobile.guava.jvm.domain.Source
import com.mobile.guava.jvm.extension.exhaustive
import com.mobile.guava.jvm.math.MathUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class PlayFragment : MyBaseFragment(), View.OnClickListener, Player.EventListener, SeekBar.OnSeekBarChangeListener {

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!
    private var player: SimpleExoPlayer? = null

    private lateinit var video: ApiVideo.Video
    private var isAdVideo = false
    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var thumbDrawable: Drawable

    private var currentWindow = 0
    private var shareURl: String = ""
    private var content: String = ""

    private var playbackPosition: Long = 0
    private var isPlayerPlaying = false
    private var markedPlayCount = false
    private var markedPlayDuration = false

    private val model by viewModels<HomeViewModel> { AppRouterUtils.viewModelFactory() }

    private val onGestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent?): Boolean {
            binding.txtLiked.performClick()
            return super.onDoubleTap(e)
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            togglePlayer()
            return super.onSingleTapConfirmed(e)
        }

        override fun onDown(event: MotionEvent): Boolean {
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt("position")
        }
        video = Values.take("PlayFragment_$position")
        isAdVideo = true
        gestureDetector = GestureDetectorCompat(requireContext(), onGestureListener)
        Timber.d("videoUrl : " + video.decodeVideoUrl())
        GoogleExo.preload(Uri.parse(video.decodeVideoUrl()))
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)
        binding.layoutContent.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }
        binding.txtShare.setOnClickListener(this)
        binding.txtLiked.setOnClickListener(this)
        binding.txtComment.setOnClickListener(this)
        binding.imgAdd.setOnClickListener(this)
        binding.imgProfile.setOnClickListener(this)
        binding.layoutLink.setOnClickListener(this)
        binding.seekBar.setOnSeekBarChangeListener(this)
        binding.seekBar.thumb.also {
            thumbDrawable = it
            thumbDrawable.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
        }
        binding.seekBar.thumb = null
        AndroidX.appDialogCount.observe(viewLifecycleOwner, Observer { dialogCount ->
            if (dialogCount > 0) {
                forcePausePlayer()
            } else {
                forceResumePlayer()
            }
        })
        bindData()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (parentFragment is RecommendFragment) {
            (parentFragment as RecommendFragment).currentFragment = this
        }
        initializePlayer()
    }

    override fun onPause() {
        super.onPause()
        player?.let {
            if (it.currentPosition > 3000L) {
                playDuration(it.currentPosition)
            }
        }
        releasePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.layoutContent.setOnTouchListener(null)
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (parentFragment is RecommendFragment) {
            (parentFragment as RecommendFragment).currentFragment = null
        }
    }

    private fun bindData() {
        updateVideoData()
        binding.txtName.text = "@${video.username}"
        val moment = video.moment()
        if (moment.isNullOrEmpty()) {
            binding.txtDesc.visibility = View.GONE
        } else {
            binding.txtDesc.visibility = View.VISIBLE
            if (video.label.isNullOrEmpty()) {
                binding.txtDesc.text = video.moment()
            } else {
                binding.txtDesc.text = MySpannable(video.moment()).findAndSpan(video.label!!) {
                    StyleSpan(Typeface.BOLD_ITALIC)
                }
            }
        }

        if (isAdVideo) {
            binding.imgProfile.setImageResource(R.drawable.sy_guanggaotouxiang)
            binding.imgAdd.setImageResource(R.drawable.sy_guanggaolianjie)
            binding.imgAdd.visibility = View.VISIBLE
            binding.imgAdLabel.visibility = View.VISIBLE
            binding.layoutLink.visibility = View.VISIBLE
        } else {
            binding.imgAdd.setImageResource(R.drawable.sy_tianjia)
            binding.imgAdLabel.visibility = View.GONE
            binding.layoutLink.visibility = View.GONE
            this.loadProfile(video.profile, binding.imgProfile)
        }
        binding.imgOnlineGame.setImageResource(R.drawable.sy_online)
        GlideApp.with(this)
                .load(decodeImgUrl(video.coverImageUrl))
                .transition(DrawableTransitionOptions.withCrossFade())
                .thumbnail(0.25f)
                .into(binding.imgCover)
    }

    private fun updateVideoData() {
        binding.txtLiked.text = video.likeCount.toString()
        binding.txtLiked.isSelected = video.isLiking
        binding.txtShare.text = video.shareCount.toString()
        binding.txtComment.text = video.commentCount.toString()
        if (!isAdVideo) {
            if (video.isFollowing) {
                binding.imgAdd.visibility = View.INVISIBLE
            } else {
                binding.imgAdd.visibility = View.VISIBLE
            }
        }
    }

    @SingleClick
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.layout_link -> chrome(video.adUrl)
            R.id.txt_share -> {
                shareAppURl()
                if (TextUtils.isEmpty(shareURl)) {
                    Msg.toast("暂时不能分享")
                    return
                }
                ShareDialogFragment.goSystemShareSheet(requireActivity(), shareURl, "在xx世界最流行的色情视频app中免费观看各种视频，国产网红、日本av、欧美色情应有尽有。")
            }
            R.id.txt_liked -> requireActivity().requireLogin(ActivityResultCallback {
                if (it.resultCode == Activity.RESULT_OK) {
                    likeVideo()
                }
            })
            R.id.txt_comment -> {
                Values.put("CommentDialogFragment", video)
                showDialogFragment(CommentDialogFragment.newInstance())
            }
            R.id.img_onlineGame -> {
                chrome(video.adUrl)
            }
            R.id.img_add -> {
                if (isAdVideo) {
                    chrome(video.adUrl)
                } else {
                    requireActivity().requireLogin(ActivityResultCallback {
                        if (it.resultCode == Activity.RESULT_OK) {
                            follow()
                        }
                    })
                }
            }
            R.id.img_profile -> {
                if (isAdVideo) {
                    chrome(video.adUrl)
                } else {
                    requireActivity().requireLogin(ActivityResultCallback {
                        if (it.resultCode == Activity.RESULT_OK) {
                            UserDetailActivity.start(activity, video.owner)
                        }
                    })
                }
            }
        }
    }

    private fun follow() {
        val oldIsFollowing = video.isFollowing
        switchFollowState()
        lifecycleScope.launch(Dispatchers.IO) {
            val source = model.follow(video.owner, oldIsFollowing)
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                    }
                    is Source.Error -> {
                        switchFollowState()
                        Msg.toast("关注失败")
                    }
                }.exhaustive
            }
        }
    }

    private fun switchFollowState() {
        video.isFollowing = !video.isFollowing
        if (video.isFollowing) {
            binding.imgAdd.visibility = View.INVISIBLE
        } else {
            binding.imgAdd.visibility = View.VISIBLE
        }
    }

    private fun likeVideo() {
        val oldVideo = video.copy()
        switchLikingState()
        lifecycleScope.launch(Dispatchers.IO) {
            val source = model.likeVideo(oldVideo)
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                    }
                    is Source.Error -> {
                        switchLikingState()
                        Msg.toast("操作失败")
                    }
                }
            }
        }
    }

    private fun switchLikingState() {
        video.isLiking = !video.isLiking
        if (video.isLiking) {
            YoYo.with(Techniques.ZoomIn).duration(300).playOn(binding.txtLiked)
        }
        if (video.isLiking) {
            video.likeCount++
        } else {
            video.likeCount--
        }
        binding.txtLiked.text = video.likeCount.toString()
        binding.txtLiked.isSelected = video.isLiking
    }

    private fun initializePlayer() {
        binding.seekBar.progress = 0
        player = SimpleExoPlayer.Builder(AndroidX.myApp)
                .setUseLazyPreparation(false)
                .build()
                .also {
                    binding.viewPlayer.player = it
                    it.addListener(this)
                    it.playWhenReady = true
                    it.repeatMode = Player.REPEAT_MODE_ALL
                    it.seekTo(currentWindow, playbackPosition)
                    it.setMediaSource(createMediaSource(), false)
                    it.prepare()
                }
    }

    private fun createMediaSource(): MediaSource {

        return GoogleExo.buildMediaSource(
                AndroidX.myApp,
                Uri.parse(video.videoURL),
                true
        )
    }

    private fun releasePlayer() {
        isPlayerPlaying = false
        player?.let {
            playbackPosition = it.currentPosition
            currentWindow = it.currentWindowIndex
            it.removeListener(this)
            it.stop(true)
            it.release()
        }
        player = null
        binding.imgCover.visibility = View.VISIBLE
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            Player.STATE_IDLE -> {
            }
            Player.STATE_BUFFERING -> {
                binding.progress.visibility = View.VISIBLE
            }
            Player.STATE_READY -> {
                if (AndroidX.appDialogCount.value!! > 0 && playWhenReady) {
                    forcePausePlayer()
                    return
                }
                binding.progress.visibility = View.INVISIBLE
                if (playWhenReady) {
                    binding.imgCover.visibility = View.INVISIBLE
                } else {
                }
                binding.imgPlay.isVisible = !playWhenReady
            }
            Player.STATE_ENDED -> {
            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        isPlayerPlaying = isPlaying
        if (!isPlayerPlaying) {
            return
        }
        binding.seekBar.visibility = View.VISIBLE
        val duration = player!!.duration
        var currentPosition = 0L
        lifecycleScope.launch(Dispatchers.Default) {
            while (isPlayerPlaying && (currentPosition < duration)) {
                try {
                    delay(200L)
                    withContext(Dispatchers.Main) {
                        currentPosition = player!!.currentPosition
                        val percent = MathUtils.divide(
                                currentPosition.toDouble(),
                                duration.toDouble()
                        )
                        binding.seekBar.progress = (percent * 100).toInt()
                        if (duration - currentPosition < 1000L) {
                            playDuration(player!!.duration)
                        }
                    }
                } catch (ignored: Exception) {
                }
            }
        }
    }

    private fun playDuration(duration: Long) {
        if (!markedPlayDuration) {
            markedPlayDuration = true
            lifecycleScope.launch(Dispatchers.IO) {
                val source = model.playDuration(video.videoId, duration)
                Timber.d(source.toString())
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (!markedPlayCount && progress > 20) {
            markedPlayCount = true
            lifecycleScope.launch(Dispatchers.IO) {
                val source = model.playVideo(video.videoId)
                Timber.d(source.toString())
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        binding.seekBar.thumb = thumbDrawable
        forcePausePlayer()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        binding.seekBar.thumb = null
        val seekPositionMs = MathUtils.multiply(
                0.01,
                binding.seekBar.progress.toDouble(),
                player!!.duration.toDouble()
        )
        player?.let {
            GoogleExo.controlDispatcher.dispatchSeekTo(it, 0, seekPositionMs.toLong())
            GoogleExo.controlDispatcher.dispatchSetPlayWhenReady(it, true)
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        Timber.tag("ExoPlayer").d(error)
    }

    private fun forceResumePlayer() {
        player?.let {
            GoogleExo.controlDispatcher.dispatchSetPlayWhenReady(it, true)
        }
    }

    private fun forcePausePlayer() {
        player?.let {
            GoogleExo.controlDispatcher.dispatchSetPlayWhenReady(it, false)
        }
    }

    private fun togglePlayer() {
        player?.let {
            GoogleExo.controlDispatcher.dispatchSetPlayWhenReady(it, !it.playWhenReady)
        }
    }

    private fun shareAppURl() {
        lifecycleScope.launch(Dispatchers.IO) {
            val source = model.shareAppUrl()
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                        var downurl = source.requireData()
                        shareURl = downurl.downloadUrl
                        content = downurl.desc
                    }
                    is Source.Error -> {
                        Msg.handleSourceException(source.requireError())
                    }
                }.exhaustive
            }
        }
    }


    private fun shareVideo(videoId: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            val source = model.shareVideo(videoId)
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                    }
                    is Source.Error -> {
                        Msg.handleSourceException(source.requireError())
                    }
                }.exhaustive
            }
        }
    }

    override fun onBusEvent(event: Pair<Int, Any>) {
        if (event.first == RunnerX.BUS_VIDEO_UPDATE) {
            updateVideo()
            return
        }
    }

    private fun updateVideo() {
        lifecycleScope.launch(Dispatchers.IO) {
            val source = model.videoById(video.videoId)
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                        video = source.requireData()
                        updateVideoData()
                    }
                    is Source.Error -> {
                    }
                }.exhaustive
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(position: Int, video: ApiVideo.Video): PlayFragment = PlayFragment().apply {
            arguments = Bundle().apply {
                putInt("position", position)
            }
            Values.put("PlayFragment_$position", video)
        }
    }
}