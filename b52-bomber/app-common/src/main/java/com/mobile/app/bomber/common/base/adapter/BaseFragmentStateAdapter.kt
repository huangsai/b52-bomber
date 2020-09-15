package com.mobile.app.bomber.common.base.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pacific.adapter.OnDataSetChanged

abstract class BaseFragmentStateAdapter<T> : FragmentStateAdapter {
    private val _data = ArrayList<T>()
    val data: List<T> get() = _data

    var onDataSetChanged: OnDataSetChanged? = null

    constructor(fragmentActivity: FragmentActivity) : super(fragmentActivity)

    constructor(fragment: Fragment) : super(fragment)

    constructor(
            fragmentManager: FragmentManager,
            lifecycle: Lifecycle
    ) : super(fragmentManager, lifecycle)

    override fun getItemCount(): Int = _data.size

    private fun onDataSetChanged() {
        onDataSetChanged?.apply(_data.size)
    }

    fun clear() {
        if (_data.isNotEmpty()) {
            _data.clear()
            notifyDataSetChanged()
            onDataSetChanged()
        }
    }

    fun add(element: T) {
        val size = _data.size
        if (_data.add(element)) {
            notifyItemInserted(size)
            onDataSetChanged()
        }
    }

    fun remove(element: T) {
        val index = _data.indexOf(element)
        if (_data.remove(element)) {
            notifyItemRemoved(index)
            onDataSetChanged()
        }
    }

    fun add(index: Int, element: T) {
        val size = itemCount
        require(index < size)
        _data.add(index, element)
        if (_data.size > size) {
            notifyItemInserted(index)
            onDataSetChanged()
        }
    }

    fun addAll(list: List<T>) {
        val size = _data.size
        if (_data.addAll(list)) {
            notifyItemRangeInserted(size, list.size)
            onDataSetChanged()
        }
    }

    fun addAll(index: Int, list: List<T>) {
        if (_data.addAll(index, list)) {
            notifyItemRangeInserted(index, list.size)
            onDataSetChanged()
        }
    }

    fun removeAll(list: List<T>) {
        val indexes: MutableList<Int> = ArrayList()
        var index: Int
        for (item in list) {
            index = _data.indexOf(item)
            if (index >= 0) {
                indexes.add(index)
            }
        }
        if (_data.removeAll(list)) {
            for (k in indexes.indices) {
                notifyItemRemoved(indexes[k])
            }
            onDataSetChanged()
        }
    }

    fun retainAll(list: List<T>) {
        val indexes: MutableList<Int> = ArrayList()
        var index: Int
        for (item in list) {
            index = _data.indexOf(item)
            if (index >= 0) {
                indexes.add(index)
            }
        }
        val size = _data.size
        if (_data.retainAll(list)) {
            for (i in 0 until size) {
                if (!indexes.contains(i)) {
                    notifyItemRemoved(i)
                }
            }
            onDataSetChanged()
        }
    }

    fun replaceAt(index: Int, element: T) {
        if (_data.set(index, element) != null) {
            notifyItemChanged(index)
        }
    }

    fun replace(oldElement: T, newElement: T) = replaceAt(_data.indexOf(oldElement), newElement)

    fun replaceAll(list: List<T>) {
        if (_data.isNotEmpty()) {
            _data.clear()
        }
        _data.addAll(list)
        notifyDataSetChanged()
        onDataSetChanged()
    }

    fun replaceAll(index: Int, list: List<T>) = replaceAll(index, list, false)

    fun replaceAll(index: Int, list: List<T>, notifyDataSetChanged: Boolean) {
        val size = _data.size
        if (index >= size) {
            addAll(list)
        } else {
            for (i in index until size) {
                _data.removeAt(index)
            }
            if (!notifyDataSetChanged) {
                notifyItemRangeRemoved(index, size - index)
            }
            if (_data.addAll(list) && !notifyDataSetChanged) {
                notifyItemRangeInserted(index, list.size)
            }
            if (notifyDataSetChanged) {
                notifyDataSetChanged()
            }
            onDataSetChanged()
        }
    }

    fun remove(index: Int): T {
        val obj = _data.removeAt(index)
        if (obj != null) {
            notifyItemRemoved(index)
            onDataSetChanged()
        }
        return obj
    }
}