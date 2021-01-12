package com.mobile.ta.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mobile.ta.databinding.FragmentInputCredentialBottomSheetBinding

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
                onSubmitListener.invoke(editTextInputCredential.text.toString())
                dismiss()
            }
        }
        return binding.root
    }
}