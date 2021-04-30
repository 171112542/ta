package com.mobile.ta.ui.coursecontent

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mobile.ta.R
import com.mobile.ta.databinding.FragmentCourseContentBinding
import com.mobile.ta.model.Status
import com.mobile.ta.model.course.MChapter
import com.mobile.ta.ui.BaseFragment
import com.mobile.ta.ui.main.MainActivity
import com.mobile.ta.viewmodel.courseContent.CourseContentViewModel

class CourseContentFragment :
    BaseFragment<FragmentCourseContentBinding>(FragmentCourseContentBinding::inflate),
    View.OnClickListener {
    private lateinit var mMainActivity: MainActivity
    private lateinit var courseId: String
    private lateinit var chapterId: String
    private val args: CourseContentFragmentArgs by navArgs()
    private val viewModel: CourseContentViewModel by viewModels()
    private var chapters = listOf<MChapter>()
    private var menuItems = mutableListOf<MenuItem>()

    @SuppressLint("SetJavaScriptEnabled")
    override fun runOnCreateView() {
        super.runOnCreateView()
        binding.apply {
            courseContentDiscussionButton.setOnClickListener(this@CourseContentFragment)
            courseContentContentWebview.settings.apply {
                allowContentAccess = true
                javaScriptEnabled = true
            }
            courseContentContentWebview.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    return true
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                }
            }
            viewModel.course.observe(viewLifecycleOwner, { result ->
                if (result.status == Status.StatusType.SUCCESS) {
                    val totalDiscussion = result.data?.discussions?.size ?: 0
                    courseContentDiscussionButton.text =
                        String.format(getString(R.string.see_discussion), totalDiscussion)
                    result.data?.chapters?.let {
                        chapters = it
                        setupMenu()
                    }
                }
            })
            viewModel.chapter.observe(viewLifecycleOwner, { result ->
                if (result.status == Status.StatusType.SUCCESS) {
                    courseContentContentWebview.loadUrl("file:///android_asset/course-content.html")
                    result.data?.let {
                        addAppInterface(object : CourseContentAppInterface() {
                            override fun navigateNext() {
                                if (hasNextChapter()) {
                                    findNavController().navigate(
                                        getChapterDestination(
                                            it.nextChapterId as String,
                                            it.nextChapterType as String
                                        )
                                    )
                                }
                            }

                            override fun navigatePrevious() {
                                if (hasNextChapter()) {
                                    findNavController().navigate(
                                        getChapterDestination(
                                            it.previousChapterId as String,
                                            it.previousChapterType as String
                                        )
                                    )
                                }
                            }

                            override fun navigateThreeD(sketchfabId: String) {
                                val destination =
                                    CourseContentFragmentDirections.actionCourseContentFragmentTo3DViewFragment(
                                        sketchfabId
                                    )
                                findNavController().navigate(destination)
                            }

                            override fun hasNextChapter(): Boolean = it.nextChapterId != null

                            override fun hasPreviousChapter(): Boolean =
                                it.previousChapterId != null

                            override fun getTitle(): String = it.name as String

                            override fun getContent(): String = it.content as String
                        })
                    }
                }
            })
        }
        setupDrawer()
    }

    private fun addAppInterface(appInterface: CourseContentAppInterface) {
        removeAppInterface()
        binding.courseContentContentWebview.addJavascriptInterface(
            appInterface, "Android"
        )
    }

    private fun removeAppInterface() {
        binding.courseContentContentWebview.removeJavascriptInterface("Android")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMainActivity = (mActivity as MainActivity)
        courseId = args.courseId
        chapterId = args.chapterId
        viewModel.getCourse(courseId)
        viewModel.getChapter(courseId, chapterId)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.course_content_discussion_button -> v.findNavController().navigate(
                CourseContentFragmentDirections.actionCourseContentFragmentToDiscussionFragment(
                    courseId
                )
            )
        }
    }

    private fun setupDrawer() {
        binding.apply {
            val toggle = ActionBarDrawerToggle(
                mMainActivity,
                drawerLayout,
                mMainActivity.toolbar,
                R.string.open_drawer,
                R.string.close_drawer
            )
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()
        }
    }

    private fun setupMenu() {
        binding.apply {
            drawerNavigation.menu.apply {
                menuItems.clear()
                clear()
                chapters.forEach {
                    add(it.name).isChecked = (chapterId == it.id)
                    menuItems.add(getItem(menuItems.count()))
                }
            }
            drawerNavigation.setNavigationItemSelectedListener {
                chapters[menuItems.indexOf(it)].let { chapter ->
                    val destination =
                        getChapterDestination(chapter.id as String, chapter.type as String)
                    findNavController().navigate(destination)
                }
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
        }
    }

    private fun getChapterDestination(chapterId: String, type: String): NavDirections {
        return if (type == "chapter") {
            CourseContentFragmentDirections.actionCourseContentFragmentSelf(chapterId)
        } else {
            CourseContentFragmentDirections.actionCourseContentFragmentToCoursePracticeFragment(
                chapterId
            )
        }
    }

    abstract class CourseContentAppInterface(
    ) {
        @JavascriptInterface
        abstract fun navigateNext()

        @JavascriptInterface
        abstract fun navigatePrevious()

        @JavascriptInterface
        abstract fun navigateThreeD(sketchfabId: String)

        @JavascriptInterface
        abstract fun hasNextChapter(): Boolean

        @JavascriptInterface
        abstract fun hasPreviousChapter(): Boolean

        @JavascriptInterface
        abstract fun getTitle(): String

        @JavascriptInterface
        abstract fun getContent(): String
    }
}