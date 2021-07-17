package com.mobile.ta.student.view.course.chapter.content

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
import com.google.firebase.Timestamp
import com.mobile.ta.R
import com.mobile.ta.databinding.FragmentCourseContentBinding
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.chapter.ChapterType
import com.mobile.ta.model.testing.TimeSpent
import com.mobile.ta.student.view.main.MainActivity
import com.mobile.ta.student.viewmodel.course.chapter.content.CourseContentViewModel
import com.mobile.ta.student.viewmodel.testingtimer.TestingTimerViewModel
import com.mobile.ta.ui.view.base.BaseFragment
import com.mobile.ta.utils.HandlerUtil
import com.mobile.ta.utils.wrapper.status.StatusType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class CourseContentFragment :
    BaseFragment<FragmentCourseContentBinding>(FragmentCourseContentBinding::inflate),
    View.OnClickListener {
    private lateinit var mMainActivity: MainActivity
    private lateinit var courseId: String
    private lateinit var chapterId: String
    private val args: CourseContentFragmentArgs by navArgs()
    private val viewModel: CourseContentViewModel by viewModels()
    private val testingViewModel: TestingTimerViewModel by viewModels()
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
            viewModel.studentProgress.observe(viewLifecycleOwner, { result ->
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
                                viewModel.course.value?.data?.chapterSummaryList ?: listOf(),
                                this@CourseContentFragment::navigateToNextChapter,
                                this@CourseContentFragment::navigateToPreviousChapter,
                                this@CourseContentFragment::navigateThreeD,
                                mActivity
                            ),
                            "Android"
                        )
                    }
                    viewModel.updateFinishedChapter(courseId, chapterId)
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
        testingViewModel.saveStartTime(Timestamp.now())
        testingViewModel.saveType(TimeSpent.CONTENT)
        setupDrawer()
    }

    override fun onDestroyView() {
        testingViewModel.saveEndTime(Timestamp.now())
        testingViewModel.startSaveTimeSpentWork()
        super.onDestroyView()
    }

    private fun navigateToNextChapter(chapter: ChapterSummary?) {
        if (chapter != null) {
            findNavController().navigate(
                getChapterDestination(
                    chapter.id,
                    chapter.type
                )
            )
        } else {
            findNavController().navigateUp()
        }
    }

    private fun navigateToPreviousChapter(chapter: ChapterSummary) {
        findNavController().navigate(
            getChapterDestination(
                chapter.id,
                chapter.type
            )
        )
    }

    private fun navigateThreeD(chapter: Chapter) {
        chapter.sketchfab?.let {
            val destination =
                CourseContentFragmentDirections.actionCourseContentFragmentTo3DViewFragment(
                    it.id as String,
                    courseId,
                    chapterId
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
                CourseContentFragmentDirections.actionCourseContentFragmentToDiscussionForumFragment(
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
                val finishedChapterIds = viewModel.studentProgress.value?.data?.finishedChapterIds
                val lastFinishedChapterIndex = if (finishedChapterIds.isNullOrEmpty()) -1
                else {
                    finishedChapterIds.maxOf { current ->
                        chapters.indexOfFirst { it.id == current }
                    }
                }
                chapters.forEachIndexed { index, it ->
                    add(it.title).isChecked = (chapterId == it.id)
                    menuItems.add(getItem(menuItems.count()).apply {
                        isEnabled = (lastFinishedChapterIndex + 1 >= index)
                    })
                }
            }
            courseContentDrawerNavigation.setNavigationItemSelectedListener {
                chapters[menuItems.indexOf(it)].let { chapter ->
                    val destination =
                        getChapterDestination(chapter.id, chapter.type)
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
        private val chapterSummaryList: List<ChapterSummary>,
        private val navigateToNextChapter: (ChapterSummary?) -> Unit,
        private val navigateToPreviousChapter: (ChapterSummary) -> Unit,
        private val navigateToThreeD: (Chapter) -> Unit,
        private val activity: Activity
    ) {
        private val index = chapterSummaryList.indexOfFirst { it.id == chapter.id }

        @JavascriptInterface
        fun hasPrevious(): Boolean {
            return index > 0
        }

        @JavascriptInterface
        fun navigateNext() {
            if (index < chapterSummaryList.size) {
                HandlerUtil.runOnUiThread(activity) {
                    val nextChapter =
                        if (index < chapterSummaryList.size - 1) chapterSummaryList[index + 1]
                        else null
                    navigateToNextChapter.invoke(nextChapter)
                }
            }
        }

        @JavascriptInterface
        fun navigatePrevious() {
            if (hasPrevious()) {
                HandlerUtil.runOnUiThread(activity) {
                    navigateToPreviousChapter.invoke(chapterSummaryList[index - 1])
                }
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
            return if (index < chapterSummaryList.size - 1) chapterSummaryList[index + 1].title
            else activity.getString(R.string.finish_course_text)
        }

        @JavascriptInterface
        fun getPreviousChapter(): String {
            return if (hasPrevious()) chapterSummaryList[index - 1].title
            else ""
        }
    }
}