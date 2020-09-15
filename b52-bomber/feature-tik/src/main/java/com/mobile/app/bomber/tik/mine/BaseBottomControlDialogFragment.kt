package com.mobile.app.bomber.tik.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.mobile.guava.android.mvvm.BaseBottomSheetDialogFragment
import com.mobile.app.bomber.tik.R
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.tik.databinding.FragmentMoreDialogBinding

abstract class BaseBottomControlDialogFragment : BaseBottomSheetDialogFragment(), View.OnClickListener {

    private var _binding: FragmentMoreDialogBinding? = null
    protected val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.NoDimBottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentMoreDialogBinding.inflate(inflater, container, false)
        binding.moreDialogFirstText.setOnClickListener(this)
        binding.moreDialogSecondText.setOnClickListener(this)
        binding.moreDialogThirdText.setOnClickListener(this)
        binding.moreDialogCancel.setOnClickListener(this)
        return binding.root
    }

    @SingleClick
    override fun onClick(v: View) {
        when (v.id) {
            R.id.more_dialog_first_text -> onFirstClick()
            R.id.more_dialog_second_text -> onSecondClick()
            R.id.more_dialog_third_text -> onThirdClick()
        }
        dismissAllowingStateLoss()
    }

    open fun onFirstClick() {

    }

    open fun onSecondClick() {

    }

    open fun onThirdClick() {

    }

}