package com.mobile.app.bomber.movie.search

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.mobile.app.bomber.data.db.entities.DbMovieSearchKey
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieItemSearchHistoryBinding
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchHistoryPresenter(private var activity: SearchActivity) : SimpleRecyclerItem() {
    private var _binding: MovieItemSearchHistoryBinding? = null
    private val binding get() = _binding!!

    override fun bind(holder: AdapterViewHolder) {
        _binding = holder.binding(MovieItemSearchHistoryBinding::bind)
        binding.historyClear.setOnClickListener { clearData() }
        requestData()
    }

    private fun clearData() {
        activity.lifecycleScope.launch(Dispatchers.IO) {
            activity.model.clearKeys()
            withContext(Dispatchers.Main) {
                binding.historyClear.visibility = View.GONE
                binding.historyShow.removeAllViews()
            }
        }
    }

    private fun requestData() {
        activity.lifecycleScope.launch(Dispatchers.IO) {
            val keys = activity.model.getKeys()
            withContext(Dispatchers.Main) {
                binding.historyClear.visibility = if (keys.isEmpty()) View.GONE else View.VISIBLE
                keys.forEach {
                    addLabelView(it)
                }
            }
        }
    }

    private fun addLabelView(it: DbMovieSearchKey) {
        val textView = LayoutInflater.from(activity).inflate(R.layout.movie_item_search_content,
                binding.historyShow, false) as TextView
        textView.text = it.name
        textView.tag = it.name
        textView.setOnClickListener { view: View ->
            val keyword = view.tag as String
            activity.setInputContent(keyword)
        }
        binding.historyShow.addView(textView)
    }

    override fun unbind(holder: AdapterViewHolder) {
        super.unbind(holder)
        _binding = null
    }

    override fun getLayout(): Int {
        return R.layout.movie_item_search_history
    }
}