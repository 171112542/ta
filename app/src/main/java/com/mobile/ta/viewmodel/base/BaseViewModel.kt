package com.mobile.ta.viewmodel.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.ta.model.status.StatusType
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    protected fun launchViewModelScope(block: suspend () -> Unit) {
        viewModelScope.launch {
            block.invoke()
        }
    }

    protected fun checkStatus(
        status: StatusType,
        onSuccessListener: () -> Unit,
        onFailureListener: () -> Unit
    ) {
        if (status == StatusType.FAILED) {
            onFailureListener.invoke()
        } else {
            onSuccessListener.invoke()
        }
    }
}