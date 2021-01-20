package com.mobile.ta.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mobile.ta.MainActivity
import com.mobile.ta.R
import com.mobile.ta.config.Constants
import com.mobile.ta.data.UserData.dobDateFormat
import com.mobile.ta.databinding.FragmentEditProfileBinding
import com.mobile.ta.utils.notBlankValidate
import com.mobile.ta.viewmodel.profile.ProfileViewModel
import java.util.*

class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var mContext: Context
    private val viewModel: ProfileViewModel by activityViewModels()
    private val EDIT_PROFILE_PICTURE_REQUEST_CODE = 3
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mContext = requireContext()
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        binding.apply {
            editProfilePictureButton.setOnClickListener {
                openGallery()
            }
            editProfileBirthDateInput.setOnClickListener { view ->
                viewModel.user.value?.dob.let {
                    if (it !== null) {
                        openDatePickerDialog(it)
                    } else {
                        val calendar = Calendar.getInstance()
                        calendar.add(Calendar.YEAR, -12)
                        openDatePickerDialog(calendar.time)
                    }
                }
            }
            viewModel.user.observe(viewLifecycleOwner, Observer {
                editProfileFullNameInput.setText(it.name)
                it.photo?.let {
                    editProfileImageView.setImageBitmap(it)
                }
                editProfileBirthDateInput.setText(dobDateFormat.format(it.dob))
                editProfileEmailInput.setText(it.email)
                editProfilePhoneNumberInput.setText(it.phone)
                editProfileBioInput.setText(it.bio)
            })
        }
        setHasOptionsMenu(true)
        return binding.root
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
                    showSnackbar()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openGallery() {
        val openGalleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.INTERNAL_CONTENT_URI
        )
        startActivityForResult(openGalleryIntent, EDIT_PROFILE_PICTURE_REQUEST_CODE)
    }

    private fun showSnackbar() {
        Snackbar.make((requireActivity() as MainActivity).getBindingRoot(), R.string.success_update_profile_label, Snackbar.LENGTH_SHORT)
            .setAction(R.string.close_action) {
            }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == EDIT_PROFILE_PICTURE_REQUEST_CODE) {
            data?.data?.let { uri ->
                val imageBitmap = MediaStore.Images.Media.getBitmap(mContext.contentResolver, uri)
                setProfilePicture(imageBitmap)
            }
        }
    }

    private fun setProfilePicture(picture: Bitmap?) {
            viewModel.setUserPhoto(picture!!)
    }

    fun openDatePickerDialog(date: Date) {
        Log.d("var", "test")
        val calendar = Calendar.getInstance()
        calendar.setTime(date)
        DatePickerDialog(
            mContext,
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(year, month, day)
                viewModel.setUserDob(pickedDateTime)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(
                Calendar.DAY_OF_MONTH
            )
        ).show()
    }

    private fun validateName() = binding.editProfileFullNameInput.notBlankValidate(Constants.NAME)
}