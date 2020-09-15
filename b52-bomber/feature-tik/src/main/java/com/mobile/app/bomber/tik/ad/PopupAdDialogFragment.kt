package com.mobile.app.bomber.tik.ad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.mobile.guava.android.mvvm.BaseAppCompatDialogFragment
import com.mobile.app.bomber.data.http.entities.ApiAdMsg
import com.mobile.guava.https.Values
import com.mobile.app.bomber.tik.R
import com.mobile.app.bomber.tik.base.chrome
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.tik.databinding.FragmentPopupAdDialogBinding

class PopupAdDialogFragment : BaseAppCompatDialogFragment(), View.OnClickListener {

    private lateinit var data: ApiAdMsg
    private var _binding: FragmentPopupAdDialogBinding? = null
    private val binding: FragmentPopupAdDialogBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyDialogCount = true
        data = Values.take("PopupAdDialogFragment")
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        _binding = FragmentPopupAdDialogBinding.inflate(inflater, container, false)
        binding.txtTitle.text = data.title
        binding.txtContent.text = data.content
        binding.imgClose.setOnClickListener(this)
        binding.btnLink.setOnClickListener(this)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SingleClick
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.img_close -> dismiss()
            R.id.btn_link -> {
                chrome(data.url)
                dismiss()
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(apiAdMsg: ApiAdMsg): PopupAdDialogFragment = PopupAdDialogFragment().apply {
            Values.put("PopupAdDialogFragment", apiAdMsg)
        }
    }
}