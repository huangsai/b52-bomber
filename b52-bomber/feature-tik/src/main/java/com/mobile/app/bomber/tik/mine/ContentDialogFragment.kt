package com.mobile.app.bomber.tik.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.tik.databinding.FragmentCommonDialogBinding
import com.mobile.guava.android.mvvm.BaseAppCompatDialogFragment

class ContentDialogFragment : BaseAppCompatDialogFragment(), View.OnClickListener {
    private var _binding: FragmentCommonDialogBinding? = null
    private val binding get() = _binding!!
    private var content: String? = null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        _binding = FragmentCommonDialogBinding.inflate(inflater, container, false)
        binding.confirm.setOnClickListener(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        content = arguments?.getString("content")
        binding.contentTv.text = content
    }

    @SingleClick
    override fun onClick(v: View) {
        dismissAllowingStateLoss()
    }

    companion object {
        @JvmStatic
        fun newInstance(content: String?): ContentDialogFragment {
            val fragment = ContentDialogFragment()
            val bundle = Bundle()
            bundle.putString("content", content)
            fragment.arguments = bundle
            return fragment
        }
    }
}