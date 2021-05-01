package com.mobile.ta.ui.profile

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.mobile.ta.R
import com.mobile.ta.config.Constants
import com.mobile.ta.databinding.FragmentEditProfileBinding
import com.mobile.ta.ui.BaseFragment
import com.mobile.ta.utils.FileUtil
import com.mobile.ta.utils.notBlankValidate
import com.mobile.ta.viewmodel.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class EditProfileFragment :
    BaseFragment<FragmentEditProfileBinding>(FragmentEditProfileBinding::inflate),
    View.OnClickListener {

    private val viewModel: ProfileViewModel by activityViewModels()

    private val calendar = Calendar.getInstance()

    private val datePickerDialog by lazy {
        DatePickerDialog(
            mContext,
            { _, year, month, day ->
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(year, month, day)
                viewModel.setUserDob(pickedDateTime)
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
            editProfileBirthDateInput.setOnClickListener {
                viewModel.user.value?.dob?.let { dob ->
                    openDatePickerDialog(dob)
                } ?: run {
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.YEAR, -12)
                    openDatePickerDialog(calendar.time)
                }
            }
        }
        setHasOptionsMenu(true)
        setupObserver()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_profile_save_item -> {
                if (validateName()) {
                    binding.apply {
                        viewModel.setUser(
                            editProfileFullNameInput.text.toString(),
                            editProfilePhoneNumberInput.text.toString(),
                            editProfileBioInput.text.toString()
                        )
                        root.findNavController().navigateUp()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View?) {
        with(binding) {
            when (view) {
                editProfilePictureButton -> openGallery()
            }
        }
    }

    private fun setupObserver() {
        viewModel.user.observe(viewLifecycleOwner, { user ->
//            editProfileFullNameInput.setText(user.name)
//            user.photo?.let {
//                editProfileImageView.setImageBitmap(it)
//            }
//            user.dob?.let {
//                editProfileBirthDateInput.setText(dobDateFormat.format(it))
//            }
//            editProfileEmailInput.setText(user.email)
//            editProfilePhoneNumberInput.setText(user.phone)
//            editProfileBioInput.setText(user.bio)
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
//                viewModel.setProfilePicture(it)
            }
        }
    }

    private fun openGallery() {
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun setProfilePicture(picture: Bitmap?) {
        viewModel.setUserPhoto(picture!!)
    }

    private fun openDatePickerDialog(date: Date) {
        calendar.time = date
        datePickerDialog.show()
    }

    private fun validateName() = binding.editProfileFullNameInput.notBlankValidate(Constants.NAME)
}