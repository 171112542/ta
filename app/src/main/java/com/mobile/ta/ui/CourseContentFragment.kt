package com.mobile.ta.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mobile.ta.MainActivity

class CourseContentFragment : Fragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as MainActivity).showToolbar("", isMain = true)
    }
}