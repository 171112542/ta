package com.mobile.ta.ui.view.login

import android.Manifest
import android.content.Intent
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.google.android.material.datepicker.MaterialDatePicker
import com.mobile.ta.R
import com.mobile.ta.config.Constants
import com.mobile.ta.databinding.ActivityRegistrationBinding
import com.mobile.ta.model.user.User
import com.mobile.ta.ui.view.base.BaseActivity
import com.mobile.ta.ui.viewmodel.login.RegistrationViewModel
import com.mobile.ta.utils.FileUtil
import com.mobile.ta.utils.notBlankValidate
import com.mobile.ta.utils.text
import com.mobile.ta.utils.toDateString
import com.mobile.ta.utils.view.DialogHelper
import com.mobile.ta.utils.view.ImageUtil
import com.mobile.ta.utils.view.RouterUtil
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class RegistrationActivity : BaseActivity<ActivityRegistrationBinding>(), View.OnClickListener {

    companion object {
        private const val INPUT_BIRTH_DATE_TAG = "INPUT BIRTH DATE"
    }

    private val birthDatePicker: MaterialDatePicker<Long> by lazy {
        MaterialDatePicker.Builder.datePicker().setTitleText(
            R.string.birth_date_picker_title_label
        ).build()
    }

    private val viewModel: RegistrationViewModel by viewModels()

    override val viewBindingInflater: (LayoutInflater) -> ActivityRegistrationBinding
        get() = ActivityRegistrationBinding::inflate

    override fun setupViews() {
        supportActionBar?.hide()
        setupDatePicker()
        binding.apply {
            buttonSkipEditInfo.setOnClickListener(this@RegistrationActivity)
            buttonEditProfilePicture.setOnClickListener(this@RegistrationActivity)
            buttonSubmitRegistrationForm.setOnClickListener(this@RegistrationActivity)
            editTextFullName.doOnTextChanged { _, _, _, _ ->
                validateName()
            }
        }
        setupBirthDateEditText()
    }

    override fun setupObserver() {
        super.setupObserver()

        viewModel.getAuthorizedUserData(intent.getBooleanExtra(RouterUtil.PARAM_IS_TEACHER, false))
        viewModel.profilePicture.observe(this, { profilePicture ->
            ImageUtil.loadImage<File>(this, profilePicture, binding.imageViewEditProfilePicture)
        })
        viewModel.user.observe(this, Observer(::updateUserData))
        viewModel.isUpdated.observe(this, {
            checkIsUpdated(it)
            DialogHelper.dismissDialog(loadingDialog)
        })
        viewModel.isUploaded.observe(this, {
            if (it) {
                viewModel.updateUser(binding.editTextFullName.text())
            } else {
                DialogHelper.dismissDialog(loadingDialog)
            }
        })
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

    override fun onPermissionGranted() {
        val openGalleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).apply {
            type = Constants.TYPE_IMAGE_ALL
        }
        intentLauncher.launch(openGalleryIntent)
    }

    override fun onIntentResult(data: Intent?) {
        data?.data?.let { uri ->
            FileUtil.getFileAbsolutePath(contentResolver, uri)?.let {
                viewModel.setProfilePicture(it)
            }
        }
    }

    private fun checkIsUpdated(isUpdated: Boolean) {
        if (isUpdated) {
            goToHome()
        } else {
            showErrorToast(R.string.fail_to_update_profile)
        }
    }

    private fun goToHome() {
        RouterUtil.goToMain(this, viewModel.getIsTeacher())
    }

    private fun openGallery() {
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun setupBirthDateEditText() {
        with(binding.editTextDateOfBirth) {
            setOnTouchListener { view, _ ->
                view.performClick()
                if (birthDatePicker.isAdded.not()) {
                    birthDatePicker.show(supportFragmentManager, INPUT_BIRTH_DATE_TAG)
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

    private fun updateUserData(user: User?) {
        user?.let {
            binding.apply {
                editTextFullName.setText(user.name)
                it.photo?.let {
                    ImageUtil.loadImage(
                        this@RegistrationActivity,
                        it,
                        binding.imageViewEditProfilePicture
                    )
                }
            }
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