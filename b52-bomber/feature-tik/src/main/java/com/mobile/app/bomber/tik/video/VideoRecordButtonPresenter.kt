package com.mobile.app.bomber.tik.video

import androidx.core.animation.AnimatorSet
import androidx.core.animation.ObjectAnimator
import androidx.core.animation.ValueAnimator
import com.mobile.app.bomber.tik.databinding.VideoRecordButtonLayoutBinding

open class VideoRecordButtonPresenter(binding: VideoRecordButtonLayoutBinding, callback: Callback) {

    companion object {
        const val STATUS_INIT = 0
        const val STATUS_START = 1
        const val STATUS_STOP = 2
    }

    private var _binding: VideoRecordButtonLayoutBinding? = null
    private val binding get() = _binding!!
    private var mCallback: Callback? = null
    private var mStatus: Int = STATUS_INIT
    val status get() = mStatus
    private var animationSetScale = AnimatorSet()

    init {
        this._binding = binding
        this.mCallback = callback
        binding.tvStatus.text = "录制"
        binding.layoutDone.setOnClickListener {
            when (mStatus) {
                STATUS_INIT -> {
                    mCallback?.onStartRecord()
                }
                STATUS_START -> {
                    mCallback?.onStopRecord()
                }
                STATUS_STOP -> {
                    mCallback?.onStartRecord()
                }
            }
        }
    }

    fun setStatus(status: Int) {
        mStatus = status
        when (mStatus) {
            STATUS_INIT -> {
                binding.tvStatus.text = "录制"
                stopAnimation()
            }
            STATUS_START -> {
                binding.tvStatus.text = "暂停"
                startAnimation()
            }
            STATUS_STOP -> {
                binding.tvStatus.text = "继续"
                stopAnimation()
            }
        }
    }

    private fun startAnimation() {
        val scaleX = ObjectAnimator.ofFloat(binding.ivRing, "scaleX", 1.0f, 1.1f, 1.0f);//后几个参数是放大的倍数
        val scaleY = ObjectAnimator.ofFloat(binding.ivRing, "scaleY", 1.0f, 1.1f, 1.0f);
        scaleX.repeatCount = ValueAnimator.INFINITE;//永久循环
        scaleY.repeatCount = ValueAnimator.INFINITE;
        animationSetScale.duration = 800;//时间
        animationSetScale.play(scaleX).with(scaleY);//两个动画同时开始
        animationSetScale.start();//开始
    }

    private fun stopAnimation() {
        animationSetScale.end()
        animationSetScale.cancel()
    }

    interface Callback {
        fun onStartRecord()

        fun onStopRecord()
    }
}