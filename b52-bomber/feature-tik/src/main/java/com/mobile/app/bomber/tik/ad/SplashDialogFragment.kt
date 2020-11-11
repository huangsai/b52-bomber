package com.mobile.app.bomber.tik.ad

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.gyf.immersionbar.ImmersionBar
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.data.http.entities.ApiAd
import com.mobile.app.bomber.tik.MainActivity
import com.mobile.app.bomber.tik.R
import com.mobile.app.bomber.tik.base.chrome
import com.mobile.app.bomber.tik.base.decodeImgUrl
import com.mobile.app.bomber.tik.databinding.FragmentSplashDialogBinding
import com.mobile.ext.glide.GlideApp
import com.mobile.guava.android.mvvm.BaseAppCompatDialogFragment
import com.mobile.guava.android.ui.screen.screen
import com.mobile.guava.data.Values
import com.mobile.guava.jvm.Guava
import java.util.concurrent.TimeUnit

class SplashDialogFragment : BaseAppCompatDialogFragment(), View.OnClickListener,
        RequestListener<Drawable> {
    private var _binding: FragmentSplashDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var ad: ApiAd
    private var timer = MyTimber()
    private var isAdFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CameraDialog)
        ad = Values.take("SplashDialogFragment")!!
        isCancelable = false
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashDialogBinding.inflate(inflater, container, false)
        binding.layoutRoot.setOnClickListener(this)
        binding.txtTimer.setOnClickListener(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ImmersionBar.with(this).init()
        val a = screen
        val result = a.x / a.y
        var imgeUrl: String = ""
        if (ad.image.eighteen.isNotEmpty()) { // 9:18分辨率
            imgeUrl = ad.image.eighteen
        } else if (ad.image.twentyone.isNotEmpty()) { //9:21分辨率
            imgeUrl = ad.image.twentyone;
        } else if (ad.image.sixteen.isNotEmpty()) {
            imgeUrl = ad.image.sixteen;
        }
        GlideApp.with(this)
                .load(decodeImgUrl(imgeUrl))
                .listener(this)
                .transition(DrawableTransitionOptions.withCrossFade())
                .thumbnail(0.25f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgSplash)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        ImmersionBar.destroy(this)
        super.onDestroy()
        timer.cancel()
        (requireActivity() as MainActivity).requestPopupAd()
    }

    override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
    ): Boolean {
        dismissAllowingStateLoss()
        return false
    }

    override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
    ): Boolean {
        binding.progress.visibility = View.GONE
        binding.txtTimer.visibility = View.VISIBLE
        timer.start()
        return false
    }

    @SingleClick
    override fun onClick(v: View?) {
        if (Guava.isDebug) {
            dismissAllowingStateLoss()
            return
        }
        if (isAdFinished && v == binding.txtTimer) {
            dismissAllowingStateLoss()
            return
        }
        if (v != binding.txtTimer) {
            chrome(ad.url)
        }
        dismissAllowingStateLoss()
    }

    companion object {

        @JvmStatic
        fun newInstance(ad: ApiAd): SplashDialogFragment = SplashDialogFragment().apply {
            Values.put("SplashDialogFragment", ad)
        }
    }

    private inner class MyTimber : CountDownTimer(5000, 1000) {

        override fun onFinish() {
            isAdFinished = true
            binding.txtTimer.setText(R.string.ad_close)
            dismissAllowingStateLoss()
        }

        override fun onTick(millisUntilFinished: Long) {
            val second = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
            binding.txtTimer.text = ("跳过 " + second)
        }
    }
}