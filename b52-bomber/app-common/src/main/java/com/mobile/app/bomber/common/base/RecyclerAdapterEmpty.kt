package com.mobile.app.bomber.common.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.BaseRecyclerAdapter
import com.pacific.adapter.OnDataSetChanged
import com.pacific.adapter.RecyclerItem

class RecyclerAdapterEmpty(
        data: ArrayList<RecyclerItem> = ArrayList()
) : BaseRecyclerAdapter<RecyclerItem>(data) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {

        if (inflater == null) {
            inflater = LayoutInflater.from(parent.context)
        }
        return AdapterViewHolder(inflater!!.inflate(viewType, parent, false), this)
    }

    /**
     * 初始化的时候设置
     */
    fun setEmptyView(emptyView: View, recyclerView: RecyclerView) {
        onDataSetChanged = object : OnDataSetChanged {
            override fun apply(count: Int) {
                if (itemCount <= 0) {
                    emptyView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    emptyView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }
        }
    }

}