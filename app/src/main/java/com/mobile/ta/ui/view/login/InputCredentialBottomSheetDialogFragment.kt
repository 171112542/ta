package com.mobile.ta.ui.view.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.LiveData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mobile.ta.R
import com.mobile.ta.config.Constants
import com.mobile.ta.databinding.FragmentInputCredentialBottomSheetBinding
import com.mobile.ta.utils.notBlankValidate
import com.mobile.ta.utils.text

class InputCredentialBottomSheetDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

    companion object {
        fun newInstance(onSubmitListener: (String) -> Unit, isValidCredentials: LiveData<Boolean>) =
            InputCredentialBottomSheetDialogFragment().apply {
                this.onSubmitListener = onSubmitListener
                this.isValidCredentials = isValidCredentials
            }
    }

    private var _binding: FragmentInputCredentialBottomSheetBinding? = null
    private val binding: FragmentInputCredentialBottomSheetBinding
        get() = _binding as FragmentInputCredentialBottomSheetBinding

    private lateinit var onSubmitListener: (String) -> Unit

    private lateinit var isValidCredentials: LiveData<Boolean>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputCredentialBottomSheetBinding.inflate(inflater, container, false)
        binding.apply {
            buttonCloseDialog.setOnClickListener(this@InputCredentialBottomSheetDialogFragment)
            buttonSubmitCredentials.setOnClickListener(this@InputCredentialBottomSheetDialogFragment)

            editTextInputCredential.doOnTextChanged { _, _, _, _ ->
                enableSubmitButton(
                    editTextInputCredential.notBlankValidate(
                        Constants.CREDENTIALS
                    )
                )
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isValidCredentials.observe(viewLifecycleOwner, { isValid ->
            if (isValid) {
                dismiss()
            } else {
                binding.editTextInputCredential.error =
                    getString(R.string.wrong_credentials_message)
            }
            enableSubmitButton(true)
        })
    }

    override fun onClick(view: View?) {
        with(binding) {
            when (view) {
                buttonCloseDialog -> dismiss()
                buttonSubmitCredentials -> {
                    enableSubmitButton(false)
                    onSubmitListener.invoke(editTextInputCredential.text().trim())
                }
            }
        }
    }

    private fun enableSubmitButton(isEnabled: Boolean) {
        binding.buttonSubmitCredentials.isEnabled = isEnabled
    }
}