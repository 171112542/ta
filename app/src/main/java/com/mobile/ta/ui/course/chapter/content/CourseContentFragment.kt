package com.mobile.ta.ui.course.chapter.content

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
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
import com.mobile.ta.utils.HandlerUtil
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
                    courseContentProgressBarContainer.isVisible = false
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    courseContentProgressBarContainer.isVisible = false
                }
            }
            viewModel.course.observe(viewLifecycleOwner, { result ->
                if (result.status == StatusType.SUCCESS) {
                    result.data?.chapterSummaryList?.let {
                        setupMenu(it)
                    }
                }
            })
            viewModel.userChapters.observe(viewLifecycleOwner, { result ->
                if (result.status == StatusType.SUCCESS) {
                    viewModel.course.value?.data?.chapterSummaryList?.let {
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
                    }
                    viewModel.addUserChapter(courseId, chapterId)
                }
            })
            viewModel.discussions.observe(viewLifecycleOwner, {
                if (it.status == StatusType.SUCCESS) {
                    courseContentDiscussionButton.text =
                        String.format(
                            getString(R.string.see_discussion),
                            it.data?.size ?: 0
                        )
                }
            })
        }
        setupDrawer()
    }

    private fun navigateToNextChapter(chapter: Chapter) {
        if (chapter.nextChapter != null) {
            chapter.nextChapter.let {
                findNavController().navigate(
                    getChapterDestination(
                        it.id,
                        it.type as ChapterType
                    )
                )
            }
        } else {
            findNavController().navigate(
                CourseContentFragmentDirections.actionCourseContentFragmentToMyCourseFragment(1)
            )
        }
    }

    private fun navigateToPreviousChapter(chapter: Chapter) {
        chapter.previousChapter?.let {
            findNavController().navigate(
                getChapterDestination(
                    it.id,
                    it.type as ChapterType
                )
            )
        }
    }

    private fun navigateThreeD(chapter: Chapter) {
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

    override fun onResume() {
        super.onResume()
        viewModel.getDiscussion(courseId, chapterId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMainActivity = (mActivity as MainActivity)
        courseId = args.courseId
        chapterId = args.chapterId
        viewModel.getCourseChapter(courseId, chapterId)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.course_content_discussion_button -> v.findNavController().navigate(
                CourseContentFragmentDirections.actionCourseContentFragmentToDiscussionFragment(
                    courseId, chapterId
                )
            )
        }
    }

    private fun setupDrawer() {
        binding.apply {
            val toggle = ActionBarDrawerToggle(
                mMainActivity,
                courseContentDrawerLayout,
                mMainActivity.getToolbar(),
                R.string.open_drawer,
                R.string.close_drawer
            )
            courseContentDrawerLayout.addDrawerListener(toggle)
            toggle.syncState()
        }
    }

    private fun setupMenu(chapters: List<ChapterSummary>) {
        binding.apply {
            courseContentDrawerNavigation.menu.apply {
                menuItems.clear()
                clear()
                chapters.forEachIndexed { index, it ->
                    add(it.title).isChecked = (chapterId == it.id)
                    menuItems.add(getItem(menuItems.count()).apply {
                        isEnabled = (viewModel.userChapters.value?.data?.size ?: 0 >= index)
                    })
                }
            }
            courseContentDrawerNavigation.setNavigationItemSelectedListener {
                chapters[menuItems.indexOf(it)].let { chapter ->
                    val destination =
                        getChapterDestination(chapter.id, chapter.type as ChapterType)
                    findNavController().navigate(destination)
                }
                courseContentDrawerLayout.closeDrawer(GravityCompat.START)
                true
            }
        }
    }

    private fun getChapterDestination(chapterId: String, type: ChapterType): NavDirections {
        return when (type) {
            ChapterType.CONTENT -> CourseContentFragmentDirections.actionCourseContentFragmentSelf(
                courseId, chapterId
            )
            else -> CourseContentFragmentDirections.actionCourseContentFragmentToCoursePracticeFragment(
                courseId, chapterId
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
        @JavascriptInterface
        fun navigateNext() {
            HandlerUtil.runOnUiThread(activity) {
                navigateToNextChapter.invoke(chapter)
            }
        }

        @JavascriptInterface
        fun navigatePrevious() {
            HandlerUtil.runOnUiThread(activity) {
                navigateToPreviousChapter.invoke(chapter)
            }
        }

        @JavascriptInterface
        fun navigateThreeD() {
            HandlerUtil.runOnUiThread(activity) {
                navigateToThreeD.invoke(chapter)
            }
        }

        @JavascriptInterface
        fun getChapter(): String {
            return Json.encodeToString(chapter)
        }

        @JavascriptInterface
        fun getNextChapter(): String {
            return chapter.nextChapter?.title ?: "Finish Course"
        }
    }
}