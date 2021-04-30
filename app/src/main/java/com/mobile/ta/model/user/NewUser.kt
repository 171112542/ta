package com.mobile.ta.model.user

import com.google.firebase.firestore.DocumentId

data class NewUser(

    @DocumentId
    var id: String,

    var name: String,

    var email: String,

    var photo: String? = null,

    var birthDate: Long? = null,

    var role: UserRoleEnum
)