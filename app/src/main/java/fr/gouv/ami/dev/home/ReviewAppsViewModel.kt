package fr.gouv.ami.dev.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.gouv.ami.data.models.Review
import fr.gouv.ami.data.repository.getReviewApps
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ReviewAppsViewModel : ViewModel() {
    private val TAG = this::class.java.simpleName

    private val _reviews = MutableStateFlow<MutableList<Review>?>(null)
    val reviews = _reviews.asStateFlow()
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true

            val reviewFlow = getReviewApps()
            reviewFlow
                .catch { e ->
                    Log.e(TAG, "Error fetching review apps", e)
                }
                .collect { response ->
                    if (response.isSuccessful) {
                        _reviews.value = response.body()
                        Log.d(TAG, "Successfully loaded ${reviews.value?.size ?: 0} review apps")
                    } else {
                        Log.e(
                            TAG,
                            "Error loading review apps: ${response.code()} - ${response.message()}"
                        )
                    }
                }

            _isRefreshing.value = false
        }
    }
}