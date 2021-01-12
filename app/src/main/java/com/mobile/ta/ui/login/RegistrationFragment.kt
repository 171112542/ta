package com.mobile.ta.ui.login

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.mobile.ta.R
import com.mobile.ta.config.Constants
import com.mobile.ta.databinding.FragmentRegistrationBinding
import com.mobile.ta.utils.FileUtil
import com.mobile.ta.utils.toDateString


class RegistrationFragment : Fragment() {

    companion object {
        private const val PROFILE_PICTURE_REQUEST_CODE = 1
    }

    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var birthDatePicker: MaterialDatePicker<Long>

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
                // TODO: Call viewmodel to submit data
                goToHome()
            }
        }
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
            // TODO: Call viewmodel to set bitmap
            binding.imageViewEditProfilePicture.setImageBitmap(it)
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
}