package com.mobile.app.bomber.tik.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.tabs.TabLayout
import com.mobile.guava.android.mvvm.BaseAppCompatDialogFragment
import com.mobile.app.bomber.tik.R
import com.mobile.app.bomber.tik.databinding.FragmentTestDialogBinding

class TestDialogFragment : BaseAppCompatDialogFragment(), TabLayout.OnTabSelectedListener {
    private var _binding: FragmentTestDialogBinding? = null
    private val binding: FragmentTestDialogBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTestDialogBinding.inflate(inflater, container, false)
        arrayOf("Alpha-内网", "Alpha-外网", "Beta", "Release").forEach {
            binding.layoutTab.addTab(binding.layoutTab.newTab().setText(it))
        }
        binding.layoutTab.addOnTabSelectedListener(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
    }

    private fun load() {
    }

    private fun save() {
    }

    companion object {

        @JvmStatic
        fun newInstance(): TestDialogFragment {
            val args = Bundle()
            val fragment = TestDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}