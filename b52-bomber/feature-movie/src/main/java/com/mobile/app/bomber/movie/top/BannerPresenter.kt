package com.mobile.app.bomber.movie.top

import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieItemBannerVpBinding
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import com.youth.banner.listener.OnBannerListener


class BannerPresenter(private val fragment: Fragment) : SimpleRecyclerItem(), OnBannerListener<String> {

    private var imgs: ArrayList<String>? = null

    init {
        imgs = ArrayList()
        imgs?.add("www")
        imgs?.add("www")
        imgs?.add("www")
        imgs?.add("www")
        imgs?.add("www")
        imgs?.add("www")
    }

    override fun bind(holder: AdapterViewHolder) {
        val binding = holder.binding(MovieItemBannerVpBinding::bind)
        binding.banner.adapter = object : BannerImageAdapter<String>(imgs) {
            override fun onBindView(holder: BannerImageHolder, data: String, position: Int, size: Int) {
                //图片加载自己实现
                Glide.with(holder.itemView)
                        .load(data)
                        .placeholder(R.drawable.movie_default_cover)
                        .apply(RequestOptions.bitmapTransform(RoundedCorners(30)))
                        .into(holder.imageView)
            }
        }
        binding.banner
                .setBannerGalleryEffect(20, 10, 1.0f)
                .setOnBannerListener(this)
                .addBannerLifecycleObserver(fragment)
                .indicator = CircleIndicator(fragment.requireContext())
    }

    override fun getLayout(): Int {
        return R.layout.movie_item_banner_vp
    }

    override fun OnBannerClick(data: String?, position: Int) {
        Msg.toast("点击了轮播图")
    }
}