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

    var role: UserRoleEnum? = null,

    var bio: String? = null,

    var deactivated: Boolean? = false,

    var totalCourseCreated: Int = 0
) : Parcelable