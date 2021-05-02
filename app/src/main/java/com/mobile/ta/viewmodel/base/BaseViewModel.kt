package com.mobile.ta.viewmodel.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.ta.utils.wrapper.status.StatusType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    protected fun launchViewModelScope(block: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            block.invoke()
        }
    }

    protected fun checkStatus(
        status: StatusType,
        onSuccessListener: () -> Unit,
        onFailureListener: (() -> Unit)? = null
    ) {
        if (status == StatusType.FAILED) {
            onFailureListener?.invoke()
        } else {
            onSuccessListener.invoke()
        }
    }
}