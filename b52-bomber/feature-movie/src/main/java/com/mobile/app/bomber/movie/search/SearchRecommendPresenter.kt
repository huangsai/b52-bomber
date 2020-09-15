package com.mobile.app.bomber.movie.search

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieItemSearchRecommendBinding
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

class SearchRecommendPresenter(private var activity: SearchActivity) : SimpleRecyclerItem() {
    private var _binding: MovieItemSearchRecommendBinding? = null
    private val binding get() = _binding!!

    override fun bind(holder: AdapterViewHolder) {
        _binding = holder.binding(MovieItemSearchRecommendBinding::bind)
        requestData()
    }

    private fun requestData() {
        val labels = listOf(
                "王菲演唱会",
                "钟南山获奖",
                "一男子失足掉进河里",
                "公交车事故",
                "飞机事故",
                "吴亦凡和Giao哥同台演唱画画的Baby"
        )
        labels.forEach {
            val textView = LayoutInflater.from(activity).inflate(R.layout.movie_item_search_content,
                    binding.recommendShow, false) as TextView
            textView.text = it
            textView.tag = it
            textView.setOnClickListener { view: View ->
                val keyword = view.tag as String
                activity.setInputContent(keyword)
            }
            binding.recommendShow.addView(textView)
        }
    }

    override fun unbind(holder: AdapterViewHolder) {
        super.unbind(holder)
        _binding = null
    }

    override fun getLayout(): Int {
        return R.layout.movie_item_search_recommend
    }
}