package com.mobile.ta.utils.view

import android.content.Context
import android.content.Intent
import com.mobile.ta.student.view.main.MainActivity
import com.mobile.ta.teacher.view.main.TeacherMainActivity
import com.mobile.ta.ui.view.login.LoginActivity
import com.mobile.ta.ui.view.login.RegistrationActivity

object RouterUtil {

    const val PARAM_IS_TEACHER = "PARAM_IS_TEACHER"

    fun goToLogin(context: Context) {
        val intent = Intent(context, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun goToRegistration(context: Context, isTeacher: Boolean) {
        val intent = Intent(context, RegistrationActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(PARAM_IS_TEACHER, isTeacher)
        }
        context.startActivity(intent)
    }

    fun goToMain(context: Context, isTeacher: Boolean) {
        val intent = Intent(context, if (isTeacher) {
            TeacherMainActivity::class.java
        } else {
            MainActivity::class.java
        }).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}