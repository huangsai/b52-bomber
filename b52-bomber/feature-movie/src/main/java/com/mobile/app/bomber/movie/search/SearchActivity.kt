package com.mobile.app.bomber.movie.search

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.common.base.MyBaseActivity
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.movie.MovieX
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieActivitySearchBinding
import com.mobile.app.bomber.movie.search.items.SearchInputItem
import com.mobile.app.bomber.movie.search.result.SearchResultListPresenter
import com.mobile.guava.android.context.hideSoftInput
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.RecyclerAdapter
import com.pacific.adapter.SimpleRecyclerItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class SearchActivity : MyBaseActivity(), TextWatcher, View.OnClickListener {
    private lateinit var binding: MovieActivitySearchBinding
    val model: SearchViewModel by viewModels { MovieX.component.viewModelFactory() }

    private val adapter = RecyclerAdapter()
    private lateinit var mainItems: ArrayList<SimpleRecyclerItem>
    private var isMainView: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MovieActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.done.setOnClickListener(this)
        binding.back.setOnClickListener(this)
        binding.clearText.setOnClickListener(this)
        binding.etSearch.addTextChangedListener(this)

        adapter.onClickListener = this
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter

        mainItems = ArrayList()
        mainItems.add(SearchHistoryPresenter(this))
//        mainItems.add(SearchRecommendPresenter(this))
        mainItems.add(SearchTodayPresenter(this, model))
        adapter.addAll(mainItems)
    }

    /**
     * 搜索历史、搜索热词
     */
    private fun replaceMainView() {
        binding.recycler.adapter = null
        binding.recycler.adapter = adapter
        adapter.replaceAll(mainItems)
        isMainView = true
    }

    /**
     * 搜索输入联想
     */
    private fun replaceInputView() {
        val data = listOf(
                "唐人街探案",
                "唐人街探案1",
                "唐人街探案2",
                "唐人街探案3",
                "唐人街探案4",
                "唐人街探案5"
        )
        val items = data.map {
            SearchInputItem(it)
        }

        binding.recycler.adapter = null
        binding.recycler.adapter = adapter
        adapter.replaceAll(items)
    }

    /**
     * 搜索结果
     */
    private fun replaceResultView(keyword: String) {
        val items = listOf<SimpleRecyclerItem>(
                SearchResultListPresenter(this, model, keyword),
//                SearchResultRecommendPresenter(this)
        )
        binding.recycler.adapter = null
        binding.recycler.adapter = adapter
        adapter.replaceAll(items)
        isMainView = false
        this.hideSoftInput();

    }
    fun setInputContent(inputContent: String) {
        binding.etSearch.setText(inputContent)
        binding.etSearch.setSelection(inputContent.length)
    }

    override fun afterTextChanged(s: Editable?) {
        val keyword = binding.etSearch.text.toString().trim()
        if (keyword.isEmpty() && !isMainView) {
            replaceMainView()
        } else {
//            replaceInputView()
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    @SingleClick
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.back -> finish()
            R.id.done -> searchDone()
            R.id.item_search_input -> {
                val pos = AdapterUtils.getHolder(v).bindingAdapterPosition
                val content = adapter.get<SearchInputItem>(pos).data
                setInputContent(content)
            }
            R.id.clear_text -> {
                if (binding.etSearch.text.isNotEmpty())
                    binding.etSearch.text = null
            }
        }
    }

    private fun searchDone() {
        val keyword = binding.etSearch.text.toString().trim()
        if (keyword.trim().isEmpty()) {
            Msg.toast(getString(R.string.movie_pls_input_search_content))
            return
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val dbMovieSearchKey = model.addKey(keyword)
            withContext(Dispatchers.Main) {
                Timber.tag("db").d("增加成功%s", dbMovieSearchKey.name)
            }
        }
        replaceResultView(keyword)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.recycler.layoutManager = null
        binding.recycler.adapter = null
    }
}