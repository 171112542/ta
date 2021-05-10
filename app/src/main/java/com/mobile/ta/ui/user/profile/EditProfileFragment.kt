package com.mobile.ta.ui.user.profile

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.Timestamp
import com.mobile.ta.R
import com.mobile.ta.config.Constants
import com.mobile.ta.databinding.FragmentEditProfileBinding
import com.mobile.ta.model.user.User
import com.mobile.ta.ui.base.BaseFragment
import com.mobile.ta.utils.*
import com.mobile.ta.viewmodel.user.profile.EditProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class EditProfileFragment :
    BaseFragment<FragmentEditProfileBinding>(FragmentEditProfileBinding::inflate),
    View.OnClickListener {

    private val args: EditProfileFragmentArgs by navArgs()

    private val viewModel: EditProfileViewModel by viewModels()

    private val calendar = Calendar.getInstance()

    private val datePickerDialog by lazy {
        DatePickerDialog(
            mContext,
            { _, year, month, day ->
                val pickedDateTime = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                viewModel.setBirthDate(pickedDateTime.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(
                Calendar.DAY_OF_MONTH
            )
        )
    }

    override fun runOnCreateView() {
        binding.apply {
            editProfilePictureButton.setOnClickListener(this@EditProfileFragment)
            editProfileBirthDateInput.setOnClickListener(this@EditProfileFragment)
        }
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_profile_save_item -> updateProfile()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View?) {
        with(binding) {
            when (view) {
                editProfilePictureButton -> openGallery()
                editProfileBirthDateInput -> openDatePickerDialog()
            }
        }
    }

    private fun setupObserver() {
        viewModel.initUserData(args.user)
        viewModel.user.observe(viewLifecycleOwner, { user ->
            setProfileData(user)
        })
        viewModel.profilePicture.observe(viewLifecycleOwner, {
            it?.let { image ->
                loadImage(image, binding.editProfileImageView)
            }
        })
        viewModel.isUpdated.observe(viewLifecycleOwner, {
            if (it) {
                showToastWithCloseAction(R.string.success_update_profile_message)
                findNavController().navigateUp()
            } else {
                showToast(R.string.fail_to_update_profile)
            }
        })
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
            FileUtil.getFileAbsolutePath(mContext.contentResolver, uri)?.let {
                viewModel.setProfilePicture(it)
            }
        }
    }

    private fun openGallery() {
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun openDatePickerDialog() {
        calendar.time = viewModel.user.value?.birthDate?.let {
            Date(it)
        } ?: Timestamp.now().toDate()
        datePickerDialog.show()
    }

    private fun setProfileData(user: User) {
        binding.apply {
            editProfileFullNameInput.setText(user.name)
            user.photo?.let {
                loadImage(it, editProfileImageView)
            }
            editProfileBirthDateInput.setText(user.birthDate?.toDateString(Constants.YYYY_MM_DD))
            editProfileEmailInput.setText(user.email)
            editProfilePhoneNumberInput.setText(user.phoneNumber)
            editProfileBioInput.setText(user.bio)
        }
    }

    private fun updateProfile() {
        with(binding) {
            if (validateName()) {
                viewModel.uploadImage()
                viewModel.updateUser(
                    editProfileFullNameInput.text(),
                    editProfilePhoneNumberInput.text(),
                    editProfileBioInput.text()
                )
            }
        }
    }

    private fun validateName() = binding.editProfileFullNameInput.notBlankValidate(Constants.NAME)
}