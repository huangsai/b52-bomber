package com.mobile.app.bomber.tik.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.mobile.app.bomber.runner.base.PrefsManager
import com.mobile.app.bomber.tik.R
import com.mobile.app.bomber.tik.databinding.FragmentHttpInputDialogBinding
import com.mobile.guava.android.mvvm.BaseAppCompatDialogFragment

/**
 * 动态切换网络环境
 */
class HttpInputDialogFragment : BaseAppCompatDialogFragment(), View.OnClickListener {
    private var _binding: FragmentHttpInputDialogBinding? = null
    private val binding: FragmentHttpInputDialogBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHttpInputDialogBinding.inflate(inflater, container, false)
        binding.btnChange.setOnClickListener(this)
        binding.btnClear.setOnClickListener(this)
        binding.etAddress.setText(PrefsManager.getHttpAddress())
        binding.etAddressUpload.setText(PrefsManager.getHttpAddressUpload())
        return binding.root
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_change -> {
                val address = binding.etAddress.text.toString().trim()
                val addressUpload = binding.etAddressUpload.text.toString().trim()
                PrefsManager.setHttpAddress(address)
                PrefsManager.setHttpAddressUpload(addressUpload)
            }
            R.id.btn_clear -> {
                PrefsManager.setHttpAddress("")
                PrefsManager.setHttpAddressUpload("")
            }
        }
        dismissAllowingStateLoss()


    }

    companion object {

        @JvmStatic
        fun newInstance(): HttpInputDialogFragment {
            val args = Bundle()
            val fragment = HttpInputDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

}