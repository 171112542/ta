package com.mobile.ta.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.ta.adapter.diff.NotificationDiffCallback
import com.mobile.ta.adapter.notification.NotificationAdapter
import com.mobile.ta.databinding.FragNotificationBinding
import com.mobile.ta.ui.base.BaseFragment
import com.mobile.ta.utils.RVSeparator
import com.mobile.ta.viewmodel.notification.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment :
    BaseFragment<FragNotificationBinding>(FragNotificationBinding::inflate) {
    private val viewmodel by viewModels<NotificationViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val diffCallback = NotificationDiffCallback()
        val adapter = NotificationAdapter(diffCallback)
        with (binding.fragNotificationRv) {
            this.adapter = adapter
            addItemDecoration(
                RVSeparator.getSpaceSeparator(
                    context,
                    LinearLayoutManager.VERTICAL,
                    resources
                )
            )
        }

        viewmodel.isLoading.observe(viewLifecycleOwner, {
            when (it) {
                true -> {
                    binding.fragNotificationList.visibility = View.GONE
                    binding.fragNotificationLoading.visibility = View.VISIBLE
                }
                false -> {
                    binding.fragNotificationList.visibility = View.VISIBLE
                    binding.fragNotificationLoading.visibility = View.GONE
                }
            }
        })

        viewmodel.notificationList.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
    }
}