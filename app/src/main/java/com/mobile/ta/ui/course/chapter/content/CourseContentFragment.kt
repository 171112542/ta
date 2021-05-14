package com.mobile.ta.ui.course.chapter.content

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.chapter.ChapterType
import com.mobile.ta.ui.base.BaseFragment
import com.mobile.ta.ui.main.MainActivity
import com.mobile.ta.utils.wrapper.status.StatusType
import com.mobile.ta.viewmodel.course.chapter.content.CourseContentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class CourseContentFragment :
    BaseFragment<FragmentCourseContentBinding>(FragmentCourseContentBinding::inflate),
    View.OnClickListener {
    private lateinit var mMainActivity: MainActivity
    private lateinit var courseId: String
    private lateinit var chapterId: String
    private val args: CourseContentFragmentArgs by navArgs()
    private val viewModel: CourseContentViewModel by viewModels()
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
                if (result.status == StatusType.SUCCESS) {
                    result.data?.chapterSummaryList?.let {
                        setupMenu(it)
                    }
                }
            })
            viewModel.chapter.observe(viewLifecycleOwner, { result ->
                if (result.status == StatusType.SUCCESS) {
                    mContext.assets.open("course/course_content.html").bufferedReader().use {
                        courseContentContentWebview.loadDataWithBaseURL(
                            "file:///android_asset/course/", it.readText(),
                            "text/html",
                            "UTF-8",
                            null
                        )
                    }
                    result.data?.let {
                        removeAppInterface()
                        courseContentContentWebview.addJavascriptInterface(
                            CourseContentAppInterface(
                                it,
                                this@CourseContentFragment::navigateToNextChapter,
                                this@CourseContentFragment::navigateToPreviousChapter,
                                this@CourseContentFragment::navigateThreeD,
                                mActivity
                            ),
                            "Android"
                        )
                        courseContentDiscussionButton.text =
                            String.format(
                                getString(R.string.see_discussion),
                                it.totalDiscussion ?: 0
                            )
                    }
                }
            })
        }
        setupDrawer()
    }

    @JavascriptInterface
    fun navigateToNextChapter(chapter: Chapter) {
        chapter.nextChapter?.let {
            findNavController().navigate(
                getChapterDestination(
                    it.id as String,
                    it.type as ChapterType
                )
            )
        }
    }

    fun navigateToPreviousChapter(chapter: Chapter) {
        chapter.previousChapter?.let {
            findNavController().navigate(
                getChapterDestination(
                    it.id as String,
                    it.type as ChapterType
                )
            )
        }
    }

    @JavascriptInterface
    fun navigateThreeD(chapter: Chapter) {
        chapter.sketchfab?.let {
            val destination =
                CourseContentFragmentDirections.actionCourseContentFragmentTo3DViewFragment(
                    it.id as String
                )
            findNavController().navigate(destination)
        }
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
                mMainActivity.getToolbar(),
                R.string.open_drawer,
                R.string.close_drawer
            )
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()
        }
    }

    private fun setupMenu(chapters: List<ChapterSummary>) {
        binding.apply {
            drawerNavigation.menu.apply {
                menuItems.clear()
                clear()
                chapters.forEach {
                    add(it.title).isChecked = (chapterId == it.id)
                    menuItems.add(getItem(menuItems.count()))
                }
            }
            drawerNavigation.setNavigationItemSelectedListener {
                chapters[menuItems.indexOf(it)].let { chapter ->
                    val destination =
                        getChapterDestination(chapter.id as String, chapter.type as ChapterType)
                    findNavController().navigate(destination)
                }
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
        }
    }

    private fun getChapterDestination(chapterId: String, type: ChapterType): NavDirections {
        return when (type) {
            ChapterType.CONTENT -> CourseContentFragmentDirections.actionCourseContentFragmentSelf(
                chapterId
            )
            else -> CourseContentFragmentDirections.actionCourseContentFragmentToCoursePracticeFragment(
                courseId,
                chapterId
            )
        }
    }

    class CourseContentAppInterface(
        private val chapter: Chapter,
        private val navigateToNextChapter: (Chapter) -> Unit,
        private val navigateToPreviousChapter: (Chapter) -> Unit,
        private val navigateToThreeD: (Chapter) -> Unit,
        private val activity: Activity
    ) {
        private val handler = Handler(Looper.getMainLooper())

        @JavascriptInterface
        fun navigateNext() {
            handler.post {
                activity.runOnUiThread {
                    navigateToNextChapter.invoke(chapter)
                }
            }
        }

        @JavascriptInterface
        fun navigatePrevious() {
            handler.post {
                activity.runOnUiThread {
                    navigateToPreviousChapter.invoke(chapter)
                }
            }
        }

        @JavascriptInterface
        fun navigateThreeD() {
            handler.post {
                activity.runOnUiThread {
                    navigateToThreeD.invoke(chapter)
                }
            }
        }

        @JavascriptInterface
        fun getChapter(): String {
            return Json.encodeToString(chapter)
        }
    }
}