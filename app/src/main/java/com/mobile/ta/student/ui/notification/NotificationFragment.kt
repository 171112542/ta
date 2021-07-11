package com.mobile.ta.student.ui.notification

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.ta.student.adapter.diff.NotificationDiffCallback
import com.mobile.ta.student.adapter.notification.NotificationAdapter
import com.mobile.ta.databinding.FragNotificationBinding
import com.mobile.ta.ui.base.BaseFragment
import com.mobile.ta.utils.view.RVSeparator
import com.mobile.ta.student.viewmodel.notification.NotificationViewModel
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
                    binding.fragNotificationLoading.visibility = View.VISIBLE
                    binding.fragNotificationList.visibility = View.GONE
                }
                false -> {
                    binding.fragNotificationLoading.visibility = View.GONE
                    val isNotificationEmpty = viewmodel.notificationList.value?.isEmpty() ?: true
                    binding.fragNotificationList.visibility =
                        if (isNotificationEmpty) View.GONE else View.VISIBLE
                    binding.fragNotificationListEmpty.visibility =
                        if (isNotificationEmpty) View.VISIBLE else View.GONE
                }
            }
        })

        viewmodel.notificationList.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
    }
}