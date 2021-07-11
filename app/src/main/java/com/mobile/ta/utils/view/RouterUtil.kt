package com.mobile.ta.utils.view

import android.content.Context
import android.content.Intent
import com.mobile.ta.ui.login.LoginActivity
import com.mobile.ta.student.ui.main.MainActivity

object RouterUtil {

    const val PARAM_IS_TEACHER = "PARAM_IS_TEACHER"

    fun goToLogin(context: Context) {
        val intent = Intent(context, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    // TODO: Change MainActivity to registration activity
    fun goToRegistration(context: Context, isTeacher: Boolean) {
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(PARAM_IS_TEACHER, isTeacher)
        }
        context.startActivity(intent)
    }

    fun goToMain(context: Context, isTeacher: Boolean) {
        val intent = Intent(context, if (isTeacher) {
            MainActivity::class.java
        } else {
            // TODO: Change MainActivity to teacher main activity
            MainActivity::class.java
        }).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}