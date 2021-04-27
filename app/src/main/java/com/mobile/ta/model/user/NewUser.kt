package com.mobile.ta.model.user

import android.net.Uri

data class NewUser(

    var name: String,

    var email: String,

    var photo: Uri? = null,

    var birthDate: Long? = null,

    var role: UserRoleEnum
)