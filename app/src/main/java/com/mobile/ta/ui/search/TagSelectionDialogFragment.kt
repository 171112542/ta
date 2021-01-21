package com.mobile.ta.ui.search

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.mobile.ta.R
import com.mobile.ta.databinding.DialogTagSelectionBinding
import com.mobile.ta.model.LevelTag
import com.mobile.ta.model.TypeTag
import com.mobile.ta.viewmodel.SearchViewModel

class TagSelectionDialogFragment(private val viewmodel: SearchViewModel) :
        DialogFragment(),
        View.OnClickListener {
    private var _binding: DialogTagSelectionBinding? = null
    private val binding get() = _binding as DialogTagSelectionBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogTagSelectionBinding.inflate(inflater, container, false)
        binding.dialogTagSelectionSaveButton.setOnClickListener(this)
        binding.dialogTagSelectionCancelButton.setOnClickListener(this)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialogWidth = requireActivity().window.decorView.width * 90 / 100
        val dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(dialogWidth, dialogHeight);
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.dialog_tag_selection_save_button -> saveSelectedTags()
            R.id.dialog_tag_selection_cancel_button -> this.dismiss()
        }
    }

    private fun saveSelectedTags() {
        val selectedTags = arrayListOf<Any>()
        binding.dialogTagSelectionTypeGroup.checkedChipIds.forEach {
            val typeTag: TypeTag? = when (it) {
                R.id.dialog_tag_selection_math_chip -> TypeTag.MATH
                R.id.dialog_tag_selection_biology_chip -> TypeTag.BIOLOGY
                R.id.dialog_tag_selection_chemistry_chip -> TypeTag.CHEMISTRY
                R.id.dialog_tag_selection_physics_chip -> TypeTag.PHYSICS
                else -> null
            }
            typeTag?.let { tag ->
                selectedTags.add(tag)
            }
        }
        binding.dialogTagSelectionLevelGroup.checkedChipIds.forEach {
            val levelTag: LevelTag? = when (it) {
                R.id.dialog_tag_selection_junior_one_chip -> LevelTag.JUNIOR_ONE
                R.id.dialog_tag_selection_junior_two_chip -> LevelTag.JUNIOR_TWO
                R.id.dialog_tag_selection_junior_three_chip -> LevelTag.JUNIOR_THREE
                R.id.dialog_tag_selection_senior_one_chip -> LevelTag.SENIOR_ONE
                R.id.dialog_tag_selection_senior_two_chip -> LevelTag.SENIOR_TWO
                R.id.dialog_tag_selection_senior_three_chip -> LevelTag.SENIOR_THREE
                else -> null
            }
            levelTag?.let { tag ->
                selectedTags.add(tag)
            }
        }
        viewmodel.saveSelectedTags(selectedTags)
        this.dismiss()
    }
}