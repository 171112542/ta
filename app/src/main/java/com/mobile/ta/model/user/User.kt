package com.mobile.ta.model.user

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(

    @DocumentId
    var id: String,

    var name: String,

    var email: String,

    var photo: String? = null,

    var birthDate: Long? = null,

    var role: UserRoleEnum,

    var phoneNumber: String? = null,

    var bio: String? = null
) : Parcelable