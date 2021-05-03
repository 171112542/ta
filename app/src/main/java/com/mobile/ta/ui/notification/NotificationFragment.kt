package com.mobile.ta.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mobile.ta.databinding.FragNotificationBinding
import com.mobile.ta.viewmodel.notification.NotificationViewModel

class NotificationFragment : Fragment() {
    private var _binding: FragNotificationBinding? = null
    private val binding get() = _binding as FragNotificationBinding
    private val viewmodel by viewModels<NotificationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}