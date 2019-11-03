package com.sac.dulanga.newsapp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.sac.dulanga.newsapp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Created by chamithdulanga on 2019-11-02.
 */

class FilterBottomSheetFragment : BottomSheetDialogFragment() {

    internal val TAG = this@FilterBottomSheetFragment.javaClass.simpleName

    companion object {

        fun newInstance(): FilteredNewsFragment {
            return FilteredNewsFragment()
        }

        @SuppressLint("StaticFieldLeak")
        var filterBottomSheetFragment: FilterBottomSheetFragment? = null
    }

    private var selectedFilter : String = ""

    @BindView(R.id.new_filter_picker)
    lateinit  var mNewsFilterPicker: NumberPicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView: View? = null
        try {
            rootView = inflater.inflate(R.layout.filter_bottom_sheet, container, false)
            ButterKnife.bind(this, rootView!!)

            filterBottomSheetFragment = this

            val colors = arrayOf(
                    getString(R.string.text_bitcoin),
                    getString(R.string.text_apple),
                    getString(R.string.text_earthquake),
                    getString(R.string.text_animal))

            mNewsFilterPicker.minValue = 0
            mNewsFilterPicker.maxValue = colors.size - 1

            mNewsFilterPicker.displayedValues = colors
            mNewsFilterPicker.setOnValueChangedListener { _, _, newVal ->
                selectedFilter = colors[newVal]
            }
            Log.d(TAG, "onCreateView")
        } catch (e: Exception) {
            Log.e(TAG, "onCreateView: $e")
        }
        return rootView
    }

    @OnClick(R.id.btn_cancel)
    fun onClickCancel() {
        dismiss()
    }

    @OnClick(R.id.btn_done)
    fun onClickDone() {
        if(FilteredNewsFragment.filteredNewsFragment != null){
            FilteredNewsFragment.filteredNewsFragment!!.setFilter(selectedFilter)
        }
        dismiss()
    }

    override fun onDestroyView() {
        filterBottomSheetFragment = null
        super.onDestroyView()
    }
}
