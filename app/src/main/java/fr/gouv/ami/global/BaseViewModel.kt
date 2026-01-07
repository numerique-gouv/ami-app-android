package fr.gouv.ami.global

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

open class BaseViewModel : ViewModel() {

    private val _refreshView = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val refreshView = _refreshView.asSharedFlow()

    fun requestRefresh() {
        _refreshView.tryEmit(Unit)
    }
}