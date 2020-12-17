package com.mobile.app.bomber.tik.video

import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import com.google.android.material.internal.ContextUtils.getActivity
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.common.base.MyBaseActivity
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.tik.R
import com.mobile.app.bomber.tik.databinding.ActivityVideoEditorBinding
import com.mobile.guava.android.mvvm.AndroidX
import com.trinity.core.TrinityCore
import com.trinity.editor.*
import com.trinity.listener.OnExportListener
import org.joor.Reflect
import timber.log.Timber

//视频编辑页面 配置
class VideoEditorActivity : MyBaseActivity(), View.OnClickListener, OnExportListener {

    private var _binding: ActivityVideoEditorBinding? = null
    private val binding get() = _binding!!
    private lateinit var mSurfaceView: SurfaceView
    private lateinit var mVideoEditor: TrinityVideoEditor
    private var mVideoExport: VideoExport? = null
    private var exportVideoPath: String? = null
    private var comPressPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVideoEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initPlay()
    }
    //初始化视频配置
    private fun initPlay() {
        val mediasArrays = intent.getSerializableExtra("medias") as Array<*>
        mVideoEditor = TrinityCore.createEditor(AndroidX.myApp)
        mSurfaceView = binding.surfaceView
        val widthPixels = resources.displayMetrics.widthPixels
        val params = mSurfaceView.layoutParams
        params.width = widthPixels
        params.height = widthPixels * 16 / 9
        mSurfaceView.layoutParams = params
        mVideoEditor.setSurfaceView(mSurfaceView)
        mediasArrays.forEach {
            val media = it as MediaData
            val clip = MediaClip(media.path, TimeRange(0, media.duration.toLong()))
            mVideoEditor.insertClip(clip)
        }
        val result = mVideoEditor.play(true)
        if (result != 0) {
            Msg.toast("播放失败")
        }
    }

    private fun initView() {
        binding.editorBack.setOnClickListener(this)
        binding.editorBeauty.setOnClickListener(this)
        binding.editorFilter.setOnClickListener(this)
        binding.editorVolume.setOnClickListener(this)
        binding.editorMusic.setOnClickListener(this)
        binding.editorNext.setOnClickListener(this)
    }

    @SingleClick
    override fun onClick(v: View) {
        when (v.id) {
            R.id.editor_back -> finish()
            R.id.editor_next -> exportVideo()
            R.id.editor_beauty,
            R.id.editor_filter,
            R.id.editor_volume,
            R.id.editor_music -> {
                Msg.toast("此功能暂未开放")
            }
        }
    }

    /**
     * 合成视频
     */
    private fun exportVideo() {
        exportVideoPath = "${externalCacheDir?.absolutePath}/weiseRecord.mp4"
        val info = VideoExportInfo(exportVideoPath!!)
        info.videoBitRate = mVideoBitRate
        info.audioBitRate = mAudioBitRate
        info.channelCount = mChannels
        info.frameRate = mFrameRate
        info.height = mHeight
        info.width = mWidth
        info.mediaCodecEncode = mMediaCodecEncode
        info.mediaCodecDecode = mMediaCodecDecode
        info.sampleRate = mSampleRate
        mVideoExport = TrinityCore.createExport(AndroidX.myApp)
        mVideoExport!!.export(info, this)
        showLoading(null, false)
    }


    override fun onExportCanceled() {
        Timber.tag("VideoEditorActivity").d("合成取消")
    }
    override fun onExportComplete() {
        Timber.tag("VideoEditorActivity").d("合成完成")
        hideLoading()
        VideoUploadActivity.start(this, exportVideoPath)
        finish()
    }

    override fun onExportFailed(error: Int) {
        Timber.tag("VideoEditorActivity").d("合成失败")
    }

    override fun onExportProgress(progress: Float) {
    }

    override fun onResume() {
        super.onResume()
        mVideoEditor.resume()
    }

    override fun onPause() {
        super.onPause()
        mVideoEditor.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideLoading()
        mVideoEditor.destroy()
        mVideoExport?.cancel()
        if (mVideoExport != null) {
            Reflect.on(mVideoExport).set("mListener", null)
        }
    }

}