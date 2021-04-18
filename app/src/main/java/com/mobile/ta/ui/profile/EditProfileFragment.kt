package com.mobile.ta.ui.profile

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mobile.ta.MainActivity
import com.mobile.ta.R
import com.mobile.ta.config.Constants
import com.mobile.ta.data.UserData.dobDateFormat
import com.mobile.ta.databinding.FragmentEditProfileBinding
import com.mobile.ta.utils.notBlankValidate
import com.mobile.ta.viewmodel.profile.ProfileViewModel
import java.util.Date

class EditProfileFragment : Fragment() {

    companion object {
        private const val EDIT_PROFILE_PICTURE_REQUEST_CODE = 3
    }

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var mContext: Context
    private val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mContext = requireContext()
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        binding.apply {
            editProfilePictureButton.setOnClickListener {
                openGallery()
            }
            editProfileBirthDateInput.setOnClickListener {
                viewModel.user.value?.dob?.let { dob ->
                    openDatePickerDialog(dob)
                } ?: run {
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.YEAR, -12)
                    openDatePickerDialog(calendar.time)
                }
            }
            viewModel.user.observe(viewLifecycleOwner, { user ->
                editProfileFullNameInput.setText(user.name)
                user.photo?.let {
                    editProfileImageView.setImageBitmap(it)
                }
                user.dob?.let {
                    editProfileBirthDateInput.setText(dobDateFormat.format(it))
                }
                editProfileEmailInput.setText(user.email)
                editProfilePhoneNumberInput.setText(user.phone)
                editProfileBioInput.setText(user.bio)
            })
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as MainActivity).showToolbar()
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
//        Snackbar.make(
//            (requireActivity() as MainActivity).getBindingRoot(),
//            R.string.success_update_profile_label,
//            Snackbar.LENGTH_SHORT
//        )
//            .setAction(R.string.close_action) {
//                it.visibility = View.GONE
//            }
//            .show()
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

    private fun openDatePickerDialog(date: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = date
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
        ).show()
    }

    private fun validateName() = binding.editProfileFullNameInput.notBlankValidate(Constants.NAME)
}