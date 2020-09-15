package com.mobile.app.bomber.tik.mine

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mobile.guava.android.mvvm.BaseBottomSheetDialogFragment
import com.ycuwq.datepicker.date.DatePicker
import com.mobile.app.bomber.tik.R
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.tik.databinding.FragmentDatePickerDialogBinding

class DatePickerDialogFragment : BaseBottomSheetDialogFragment(), View.OnClickListener, DatePicker.OnDateSelectedListener {

    private var _binding: FragmentDatePickerDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var behavior: BottomSheetBehavior<FrameLayout>

    var callback: Callback? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ShareRoundBottomSheetDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        behavior = dialog.behavior
        behavior.isHideable = false
        return dialog
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDatePickerDialogBinding.inflate(inflater, container, false)
        binding.txtDone.setOnClickListener(this)
        binding.switchShow.setOnClickListener(this)
        binding.viewDatePicker.setOnDateSelectedListener(this)
        return binding.root
    }

    @SingleClick
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.txt_done -> {
                callback?.onConfirm(
                        binding.viewDatePicker.year,
                        binding.viewDatePicker.month,
                        binding.viewDatePicker.day,
                        binding.switchShow.isChecked
                )
                dismissAllowingStateLoss()
            }
            R.id.switch_show -> {
                callback?.onShowChanged(binding.switchShow.isChecked)
            }
        }
    }

    override fun onDateSelected(year: Int, month: Int, day: Int) {
        callback?.onDateChanged(year, month, day)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        callback = null
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        callback?.onCancel()
    }

    companion object {

        @JvmStatic
        fun newInstance(): DatePickerDialogFragment = DatePickerDialogFragment()
    }

    interface Callback {

        fun onDateChanged(year: Int, month: Int, day: Int)

        fun onShowChanged(isChecked: Boolean)

        fun onConfirm(year: Int, month: Int, day: Int, isChecked: Boolean)

        fun onCancel()
    }
}