package com.mobile.ta.utils.mapper

import com.google.firebase.firestore.DocumentSnapshot

object DataMapper {

    fun <T> mapToLists(
        snapshots: MutableList<DocumentSnapshot>,
        dataMapper: (DocumentSnapshot) -> T
    ): MutableList<T> {
        return snapshots.mapNotNull {
            dataMapper.invoke(it)
        }.toMutableList()
    }
}