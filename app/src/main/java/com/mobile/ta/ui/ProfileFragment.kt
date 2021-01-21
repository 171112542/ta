package com.mobile.ta.ui

import android.os.Bundle
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
import com.google.android.material.tabs.TabLayoutMediator
import com.mobile.ta.MainActivity
import com.mobile.ta.R
import com.mobile.ta.adapter.ProfilePagerAdapter
import com.mobile.ta.databinding.FragProfileBinding
import com.mobile.ta.viewmodel.profile.ProfileViewModel

class ProfileFragment : Fragment() {
    private var _binding: FragProfileBinding? = null
    private val binding get() = _binding as FragProfileBinding
    private val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragProfileBinding.inflate(inflater, container, false)
        val profilePagerAdapter = ProfilePagerAdapter(this)
        binding.apply {
            profileEditButton.setOnClickListener {
                it.findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
            }
            profileViewPager.adapter = profilePagerAdapter
            (profileViewPager.getChildAt(0) as ViewGroup).clipChildren = false
            TabLayoutMediator(profileTabLayout, profileViewPager) {tab, position ->
                when(position) {
                    0 -> tab.text = getString(R.string.about_tab)
                    1 -> tab.text = getString(R.string.feedback_tab)
                }
            }.attach()
            viewModel.user.observe(viewLifecycleOwner, Observer {
                it.photo?.let {
                    profilePhotoImageView.setImageBitmap(it)
                }
                profileName.text = it.name
                profileBio.text = it.bio
            })
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as MainActivity).showToolbar(isMain = true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profile_setting_item -> binding.root.findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }
        return super.onOptionsItemSelected(item)
    }
}