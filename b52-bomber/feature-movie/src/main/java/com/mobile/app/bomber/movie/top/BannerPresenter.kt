package com.mobile.app.bomber.movie.top

import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.common.base.tool.chrome
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

/**
 * 轮播图
 */
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
                            //adapter?.setDatas(source.requireData())
                            //adapter?.notifyDataSetChanged()
                            bindData(source.requireData())
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
                (holder.itemView as ImageView).scaleType = ImageView.ScaleType.FIT_XY
                Glide.with(holder.itemView)
                        .load(data.imgUrl)
                        .placeholder(R.drawable.movie_default_cover)
                        .into(holder.imageView)
            }
        }
        binding.bannerImg.adapter = adapter
        binding.bannerImg
                .setBannerGalleryEffect(20, 6, 1.0f)
                .setOnBannerListener(this@BannerPresenter)
                .addBannerLifecycleObserver(fragment)
                .indicator = CircleIndicator(fragment.requireContext())

        if (data.isNotEmpty()) {
            binding.bannerTitle.text = data[0].title
        }
    }

    override fun OnBannerClick(data: ApiMovieBanner.Banner?, position: Int) {
        if (mData!!.get(position).movieUrl.isNotEmpty()) {
            fragment.chrome(mData!!.get(position).movieUrl)
        } else {
            if (mData!!.get(position).movieId < 9) {
                Msg.toast("当前轮播图不能播放")
                return
            }
            PlayerActivity.start(fragment.requireActivity(), data?.movieId!!)
        }
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