package com.mobile.ta.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.mobile.ta.R
import com.mobile.ta.databinding.LayoutProfileInfoTwoSectionBinding

class ProfileInfoTwoSection(context: Context, attrs: AttributeSet) :
    ConstraintLayout(context, attrs) {

    private lateinit var binding: LayoutProfileInfoTwoSectionBinding

    private val styledAttributes =
        context.obtainStyledAttributes(attrs, R.styleable.ProfileInfoTwoSection)

    init {
        initializeBinding()
        styledAttributes.apply {
            setLabelSection1(getString(R.styleable.ProfileInfoTwoSection_labelSection1))
            setLabelSection2(getString(R.styleable.ProfileInfoTwoSection_labelSection2))
            setValueSection1(getString(R.styleable.ProfileInfoTwoSection_valueSection1))
            setValueSection2(getString(R.styleable.ProfileInfoTwoSection_valueSection2))
            recycle()
        }
    }

    fun setValueSection1(value: String?) {
        binding.textViewProfileSection1.text = if (value.isNullOrBlank()) "0" else value
    }

    fun setValueSection2(value: String?) {
        binding.textViewProfileSection2.text = if (value.isNullOrBlank()) "0" else value
    }

    fun setLabelSection1(label: String?) {
        binding.textViewProfileSection1Label.text = label.orEmpty()
    }

    fun setLabelSection2(label: String?) {
        binding.textViewProfileSection2Label.text = label.orEmpty()
    }

    private fun initializeBinding() {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = LayoutProfileInfoTwoSectionBinding.inflate(layoutInflater, this, true)
    }
}