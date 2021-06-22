package com.mobile.ta.model.user

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(

    @DocumentId
    var id: String,

    var name: String = "",

    var email: String = "",

    var photo: String? = null,

    var birthDate: Long? = null,

    var role: UserRoleEnum = UserRoleEnum.ROLE_STUDENT,

    var bio: String? = null,

    var totalCourseCreated: Int = 0
) : Parcelable