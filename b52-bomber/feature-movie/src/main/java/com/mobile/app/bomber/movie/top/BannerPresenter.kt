package com.mobile.app.bomber.movie.top

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.data.http.entities.ApiMovieBanner
import com.mobile.app.bomber.movie.MovieViewModel
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieItemBannerBinding
import com.mobile.app.bomber.movie.player.PlayerActivity
import com.mobile.guava.jvm.domain.Source
import com.mobile.guava.jvm.extension.exhaustive
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import com.youth.banner.listener.OnBannerListener
import com.youth.banner.listener.OnPageChangeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BannerPresenter(
        private val fragment: Fragment,
        private val model: MovieViewModel
) : SimpleRecyclerItem(), OnBannerListener<ApiMovieBanner.Banner>, OnPageChangeListener {

    private var mData: List<ApiMovieBanner.Banner>? = null
    private var adapter: BannerImageAdapter<ApiMovieBanner.Banner>? = null

    fun onRefresh() {
        load(true)
    }

    private fun load(isRefresh: Boolean) {
        fragment.lifecycleScope.launch(Dispatchers.IO) {
            val source = model.getBanner()
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                        mData = source.requireData()
                        if (isRefresh) {
                            adapter?.setDatas(source.requireData())
                            adapter?.notifyDataSetChanged()
                        } else {
                            bindData(source.requireData())
                        }
                    }
                    is Source.Error -> {
                        Msg.handleSourceException(source.requireError())
                    }
                }.exhaustive
            }
        }
    }

    private fun bindData(data: List<ApiMovieBanner.Banner>) {
        binding.bannerImg.addOnPageChangeListener(this)
        adapter = object : BannerImageAdapter<ApiMovieBanner.Banner>(data) {
            override fun onBindView(
                    holder: BannerImageHolder,
                    data: ApiMovieBanner.Banner,
                    position: Int,
                    size: Int
            ) {
                Glide.with(holder.itemView)
                        .load(data.imgUrl)
                        .placeholder(R.drawable.movie_default_cover)
                        .apply(RequestOptions.bitmapTransform(RoundedCorners(30)))
                        .into(holder.imageView)
            }
        }
        binding.bannerImg.adapter = adapter
        binding.bannerImg
                .setBannerGalleryEffect(20, 10, 1.0f)
                .setOnBannerListener(this@BannerPresenter)
                .addBannerLifecycleObserver(fragment)
                .indicator = CircleIndicator(fragment.requireContext())
    }

    override fun OnBannerClick(data: ApiMovieBanner.Banner?, position: Int) {
        PlayerActivity.start(fragment.requireActivity(), data?.movieId!!)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        binding.bannerTitle.text = mData?.get(position)?.title
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    private lateinit var binding: MovieItemBannerBinding

    override fun bind(holder: AdapterViewHolder) {
        binding = holder.binding(MovieItemBannerBinding::bind)
        load(false)
    }

    override fun getLayout(): Int {
        return R.layout.movie_item_banner
    }
}