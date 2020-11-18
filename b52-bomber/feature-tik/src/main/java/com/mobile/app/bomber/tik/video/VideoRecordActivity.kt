package com.mobile.app.bomber.tik.video

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.common.base.MyBaseActivity
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.runner.RunnerX
import com.mobile.app.bomber.tik.R
import com.mobile.app.bomber.tik.databinding.ActivityVideoRecordBinding
import com.mobile.app.bomber.tik.video.VideoRecordButtonPresenter.Companion.STATUS_INIT
import com.mobile.app.bomber.tik.video.VideoRecordButtonPresenter.Companion.STATUS_START
import com.mobile.app.bomber.tik.video.VideoRecordButtonPresenter.Companion.STATUS_STOP
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.android.mvvm.newStartActivity
import com.trinity.OnRecordingListener
import com.trinity.camera.Facing
import com.trinity.camera.Flash
import com.trinity.record.TrinityRecord
import org.joor.Reflect
import java.text.SimpleDateFormat
import java.util.*

class VideoRecordActivity : MyBaseActivity(), View.OnClickListener, OnRecordingListener, VideoRecordButtonPresenter.Callback {

    private var _binding: ActivityVideoRecordBinding? = null
    private val binding get() = _binding!!
    private lateinit var mRecord: TrinityRecord
    private val mFlashModes = arrayOf(Flash.OFF, Flash.TORCH)
    private val mFlashImage = arrayOf(R.drawable.ps_weishanguang, R.drawable.ps_shanguang)
    private var mFlashIndex = 0
    private val mMedias = mutableListOf<MediaData>()
    private val mRecordDurations = mutableListOf<Int>()
    private var mCurrentRecordDuration = 0
    private var mTotalRecordDuration = 0

    private var mRecordButtonPresenter: VideoRecordButtonPresenter? = null

    init {
        System.loadLibrary("trinity")
        System.loadLibrary("c++_shared")
        System.loadLibrary("marsxlog")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVideoRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initRecord()
    }

    override fun onBusEvent(event: Pair<Int, Any>) {
        super.onBusEvent(event)
        if (event.first == RunnerX.BUS_VIDEO_UPLOAD_SUCCESS) {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        mRecord.startPreview(mPreviewResolution)
    }

    override fun onPause() {
        super.onPause()
        mRecord.stopPreview()
        onStopRecord()
    }

    override fun onDestroy() {
        super.onDestroy()
        Reflect.on(mRecord).set("mOnRecordingListener", null)
        Reflect.on(mRecord).set("mCamera", null)
        mRecord.release()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRecord() {
        val preview = binding.recordPreview
        mRecord = TrinityRecord(AndroidX.myApp, preview)
        mRecord.setOnRecordingListener(this)
        mRecord.setCameraFacing(Facing.BACK)
        mRecord.setFrame(mFrame)
    }

    private fun initView() {
        binding.recordBack.setOnClickListener(this)
        binding.recordSwitchCamera.setOnClickListener(this)
        binding.recordSwitchLight.setOnClickListener(this)
        binding.recordDelete.setOnClickListener(this)
        binding.recordUpload.setOnClickListener(this)
        binding.recordNext.setOnClickListener(this)
        binding.recordMusic.setOnClickListener(this)
        binding.recordBeauty.setOnClickListener(this)
        mRecordButtonPresenter = VideoRecordButtonPresenter(binding.recordButtonDone, this)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onStartRecord() {
        if (mRecordButtonPresenter?.status == STATUS_START) return
        computeTotalRecordDuration()
        if (mTotalRecordDuration >= mRecordMaxDuration) {
            Msg.toast("已达最大时长")
            return
        }
        val date = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val path = externalCacheDir?.absolutePath + "/VID_$date.mp4"
        mRecord.startRecording(path, mWidth, mHeight, mVideoBitRate, mFrameRate,
                mMediaCodecEncode, mSampleRate, mChannels, mAudioBitRate, mRecordMaxDuration)
        val media = MediaData(path, "video", mWidth, mHeight)
        mMedias.add(media)
        mRecordButtonPresenter?.setStatus(STATUS_START)
        showHideStartStopRecordingView()
    }

    override fun onStopRecord() {
        if (mRecordButtonPresenter?.status != STATUS_START) return
        mRecord.stopRecording()
        mRecordButtonPresenter?.setStatus(STATUS_STOP)
        mRecordDurations.add(mCurrentRecordDuration)
        val item = mMedias[mMedias.size - 1]
        item.duration = mCurrentRecordDuration
        binding.lineProgressView.addProgress(mCurrentRecordDuration * 1.0F / mRecordMaxDuration)
        showHideStartStopRecordingView()
    }

    @SingleClick
    override fun onClick(v: View) {
        when (v.id) {
            R.id.record_back -> {
                finish()
            }
            R.id.record_switch_camera -> {
                mRecord.switchCamera()
            }
            R.id.record_switch_light -> {
                mFlashIndex = if (mFlashIndex == 0) 1 else 0
                mRecord.flash(mFlashModes[mFlashIndex])
                binding.recordSwitchLight.setImageResource(mFlashImage[mFlashIndex])
            }
            R.id.record_upload -> {
                newStartActivity(VideoLocalListActivity::class.java)
            }
            R.id.record_delete -> {
                deleteFile()
            }
            R.id.record_next -> {
                computeTotalRecordDuration()
                if (mTotalRecordDuration < mRecordMinDuration) {
                    Msg.toast("请上传超过${mRecordMinDuration / 1000}秒的视频")
                    return
                }
                val bundle = Bundle()
                bundle.putSerializable("medias", mMedias.toTypedArray())
                newStartActivity(VideoEditorActivity::class.java, bundle)
                finish()
            }
            R.id.record_music,
            R.id.record_beauty -> {
                Msg.toast("此功能暂未开放")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onRecording(currentTime: Int, duration: Int) {
        mCurrentRecordDuration = currentTime
        runOnUiThread {
            binding.lineProgressView.setLoadingProgress(currentTime * 1.0f / duration)
            binding.recordUpdatetime.text = "${(mTotalRecordDuration + currentTime) / 1000}/${duration / 1000}秒"
            if (currentTime >= duration) {
                Msg.toast("已达最大时长，停止录制")
                onStopRecord()
            }
        }
    }

    /**
     * 计算录制总时长
     */
    private fun computeTotalRecordDuration() {
        mTotalRecordDuration = 0
        mRecordDurations.forEach {
            mTotalRecordDuration += it
        }
    }

    /**
     * 删除上一段视频
     */
    @SuppressLint("SetTextI18n")
    private fun deleteFile() {
        if (mMedias.isNotEmpty()) {
            mMedias.removeAt(mMedias.size - 1)
        }
        if (mRecordDurations.isNotEmpty()) {
            mRecordDurations.removeAt(mRecordDurations.size - 1)
        }
        binding.lineProgressView.deleteProgress()
        computeTotalRecordDuration()
        binding.recordUpdatetime.text = "${mTotalRecordDuration / 1000}/${mRecordMaxDuration / 1000}秒"
        if (mTotalRecordDuration == 0) {
            mRecordButtonPresenter?.setStatus(STATUS_INIT)
            showHideStartStopRecordingView()
        }
    }

    /**
     * 显示和隐藏录制开始、暂停后的view
     */
    private fun showHideStartStopRecordingView() {
        when (mRecordButtonPresenter?.status) {
            STATUS_START -> {
                binding.recordUpload.visibility = View.INVISIBLE
                binding.recordBack.visibility = View.INVISIBLE
                binding.recordMusic.visibility = View.INVISIBLE
                binding.recordBeauty.visibility = View.INVISIBLE
                binding.recordDelete.visibility = View.INVISIBLE
                binding.recordNext.visibility = View.INVISIBLE
            }
            STATUS_STOP -> {
                binding.recordUpload.visibility = View.INVISIBLE
                binding.recordBack.visibility = View.VISIBLE
                binding.recordMusic.visibility = View.VISIBLE
                binding.recordBeauty.visibility = View.VISIBLE
                binding.recordDelete.visibility = View.VISIBLE
                binding.recordNext.visibility = View.VISIBLE
            }
            STATUS_INIT -> {
                binding.recordDelete.visibility = View.INVISIBLE
                binding.recordNext.visibility = View.INVISIBLE
                binding.recordUpload.visibility = View.VISIBLE
            }
        }
    }
}