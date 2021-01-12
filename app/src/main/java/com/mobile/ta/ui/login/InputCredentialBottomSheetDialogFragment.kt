package com.mobile.ta.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mobile.ta.databinding.FragmentInputCredentialBottomSheetBinding
import com.mobile.ta.utils.text

class InputCredentialBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(onSubmitListener: (String) -> Unit) = InputCredentialBottomSheetDialogFragment().apply {
            this.onSubmitListener = onSubmitListener
        }
    }

    private lateinit var binding: FragmentInputCredentialBottomSheetBinding
    private lateinit var onSubmitListener: (String) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentInputCredentialBottomSheetBinding.inflate(inflater, container, false)
        with(binding) {
            buttonCloseDialog.setOnClickListener {
                dismiss()
            }
            buttonSubmitCredentials.setOnClickListener {
                onSubmitListener.invoke(editTextInputCredential.text())
                dismiss()
            }
            editTextInputCredential.doOnTextChanged { text, _, _, _ ->
                buttonSubmitCredentials.isEnabled = when {
                    text.isNullOrBlank() -> false
                    else -> true
                }
            }
        }
        return binding.root
    }
}