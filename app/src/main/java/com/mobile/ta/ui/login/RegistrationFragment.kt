package com.mobile.ta.ui.login

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.mobile.ta.R
import com.mobile.ta.config.Constants
import com.mobile.ta.databinding.FragmentRegistrationBinding
import com.mobile.ta.model.user.User
import com.mobile.ta.ui.base.BaseFragment
import com.mobile.ta.utils.FileUtil
import com.mobile.ta.utils.notBlankValidate
import com.mobile.ta.utils.text
import com.mobile.ta.utils.toDateString
import com.mobile.ta.utils.view.DialogHelper
import com.mobile.ta.viewmodel.login.RegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class RegistrationFragment :
    BaseFragment<FragmentRegistrationBinding>(FragmentRegistrationBinding::inflate),
    View.OnClickListener {

    companion object {
        private const val INPUT_BIRTH_DATE_TAG = "INPUT BIRTH DATE"
    }

    private val birthDatePicker: MaterialDatePicker<Long> by lazy {
        MaterialDatePicker.Builder.datePicker().setTitleText(
            R.string.birth_date_picker_title_label
        ).build()
    }

    private val args: RegistrationFragmentArgs by navArgs()
    private val viewModel: RegistrationViewModel by viewModels()

    override fun onIntentResult(data: Intent?) {
        data?.data?.let { uri ->
            FileUtil.getFileAbsolutePath(mContext.contentResolver, uri)?.let {
                viewModel.setProfilePicture(it)
            }
        }
    }

    override fun runOnCreateView() {
        setupDatePicker()
        binding.apply {
            buttonSkipEditInfo.setOnClickListener(this@RegistrationFragment)
            buttonEditProfilePicture.setOnClickListener(this@RegistrationFragment)
            buttonSubmitRegistrationForm.setOnClickListener(this@RegistrationFragment)
            editTextFullName.doOnTextChanged { _, _, _, _ ->
                validateName()
            }
        }
        setupBirthDateEditText()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getAuthorizedUserData(args.isTeacher)
        viewModel.profilePicture.observe(viewLifecycleOwner, {
            loadImage<File>(it, binding.imageViewEditProfilePicture)
        })
        viewModel.user.observe(viewLifecycleOwner, {
            it?.let {
                updateUserData(it)
            }
        })
        viewModel.isUpdated.observe(viewLifecycleOwner, {
            checkIsUpdated(it)
            DialogHelper.dismissDialog(loadingDialog)
        })

        viewModel.isUploaded.observe(viewLifecycleOwner, {
            if (it) {
                viewModel.updateUser(binding.editTextFullName.text())
            } else {
                DialogHelper.dismissDialog(loadingDialog)
            }
        })
    }

    private fun checkIsUpdated(isUpdated: Boolean) {
        if (isUpdated) {
            goToHome()
        } else {
            showToast(R.string.fail_to_update_profile)
        }
    }

    private fun updateUserData(user: User) {
        binding.apply {
            editTextFullName.setText(user.name)
            user.photo?.let {
                loadImage(it, binding.imageViewEditProfilePicture)
            }
        }
    }

    override fun onClick(view: View?) {
        binding.apply {
            when (view) {
                buttonEditProfilePicture -> openGallery()
                buttonSubmitRegistrationForm -> validate()
                buttonSkipEditInfo -> goToHome()
            }
        }
    }

    private fun goToHome() {
        findNavController().navigate(RegistrationFragmentDirections.actionGlobalHomeFragment())
    }

    override fun onPermissionGranted() {
        val openGalleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).apply {
            type = Constants.TYPE_IMAGE_ALL
        }
        intentLauncher.launch(openGalleryIntent)
    }

    private fun openGallery() {
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun setupBirthDateEditText() {
        with(binding.editTextDateOfBirth) {
            setOnTouchListener { view, _ ->
                view.performClick()
                if (birthDatePicker.isAdded.not()) {
                    birthDatePicker.show(mActivity.supportFragmentManager, INPUT_BIRTH_DATE_TAG)
                }
                true
            }
            doOnTextChanged { _, _, _, _ ->
                validateDateOfBirth()
            }
        }
    }

    private fun setupDatePicker() {
        birthDatePicker.addOnPositiveButtonClickListener {
            binding.editTextDateOfBirth.setText(
                it.toDateString(Constants.DD_MMMM_YYYY)
            )
            viewModel.setBirthDate(it)
        }
    }

    private fun validate() {
        if (validateName() && validateDateOfBirth()) {
            viewModel.uploadImage()
            DialogHelper.showDialog(loadingDialog)
        }
    }

    private fun validateDateOfBirth() =
        binding.editTextDateOfBirth.notBlankValidate(Constants.DATE_OF_BIRTH)

    private fun validateName() = binding.editTextFullName.notBlankValidate(Constants.NAME)
}