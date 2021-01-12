package com.mobile.ta.ui.login

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.mobile.ta.R
import com.mobile.ta.config.Constants
import com.mobile.ta.databinding.FragmentRegistrationBinding
import com.mobile.ta.utils.FileUtil
import com.mobile.ta.utils.text
import com.mobile.ta.utils.toDateString
import com.mobile.ta.viewmodel.login.RegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    companion object {
        private const val PROFILE_PICTURE_REQUEST_CODE = 1
        private const val INPUT_BIRTH_DATE_TAG = "INPUT BIRTH DATE"
    }

    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var birthDatePicker: MaterialDatePicker<Long>

    private val viewModel: RegistrationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        setupDatePicker()
        with(binding) {
            buttonEditProfilePicture.setOnClickListener {
                openGallery()
            }
            buttonSubmitRegistrationForm.setOnClickListener {
                validate()
            }
            editTextFullName.doOnTextChanged { _, _, _, _ ->
                validateName()
            }
        }
        setupBirthDateEditText()
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PROFILE_PICTURE_REQUEST_CODE) {
            data?.data?.let { uri ->
                context?.let { context ->
                    val filePath = FileUtil.getFilePath(uri, context.contentResolver)
                    filePath?.let {
                        val fileBitmap = FileUtil.convertFilePathToBitmap(it)
                        setProfilePicture(fileBitmap)
                    }
                }
            }
        }
    }

    private fun goToHome() {
        findNavController().navigate(RegistrationFragmentDirections.actionGlobalHomeFragment())
    }

    private fun openGallery() {
        val openGalleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.INTERNAL_CONTENT_URI
        )
        startActivityForResult(openGalleryIntent, PROFILE_PICTURE_REQUEST_CODE)
    }

    private fun setProfilePicture(picture: Bitmap?) {
        picture?.let {
            viewModel.setProfilePicture(it)
            binding.imageViewEditProfilePicture.setImageBitmap(it)
        }
    }

    private fun setupBirthDateEditText() {
        with(binding.editTextDateOfBirth) {
            setOnTouchListener { view, _ ->
                view.performClick()
                birthDatePicker.show(parentFragmentManager, INPUT_BIRTH_DATE_TAG)
                true
            }
            doOnTextChanged { _, _, _, _ ->
                validateDateOfBirth()
            }
        }
    }

    private fun setupDatePicker() {
        birthDatePicker = MaterialDatePicker.Builder.datePicker().setTitleText(
            R.string.birth_date_picker_title_label
        ).build()
        birthDatePicker.addOnPositiveButtonClickListener {
            binding.editTextDateOfBirth.setText(
                it.toDateString(Constants.DD_MMMM_YYYY)
            )
        }
    }

    private fun validate() {
        with(binding) {
            val name = editTextFullName.text()
            val dateOfBirth = editTextDateOfBirth.text()

            if (validateName() && validateDateOfBirth()) {
                // TODO: Call view model to set data
                goToHome()
            }
        }
    }

    private fun validateDateOfBirth() =
        validateEditText(binding.editTextDateOfBirth, Constants.DATE_OF_BIRTH)

    private fun validateName() = validateEditText(binding.editTextFullName, Constants.NAME)

    private fun validateEditText(editText: TextInputEditText, errorObject: String): Boolean {
        var isError = true
        val text = editText.text()
        editText.error = when {
            text.isBlank() -> Constants.getEmptyErrorMessage(errorObject)
            else -> {
                isError = false
                null
            }
        }
        return isError.not()
    }
}