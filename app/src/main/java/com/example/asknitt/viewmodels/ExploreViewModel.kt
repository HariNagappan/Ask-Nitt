package com.example.asknitt.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.asknitt.data.model.GeneralUser
import com.example.asknitt.data.model.OtherUserInfo
import com.example.asknitt.data.repository.SocialRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val socialRepository: SocialRepository
) : ViewModel() {

    val exploreUsers: StateFlow<List<GeneralUser>> = socialRepository.exploreUsers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val usersFriends: StateFlow<List<GeneralUser>> = socialRepository.friends.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val userFriendRequestsReceived: StateFlow<List<GeneralUser>> = socialRepository.receivedRequests.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val userFriendRequestsSent: StateFlow<List<GeneralUser>> = socialRepository.sentRequests.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allUsers: MutableList<GeneralUser> = mutableStateListOf()
    var otherUserInfo by mutableStateOf<OtherUserInfo?>(null)

    fun getUsersByName(usernameSearchText: String, onFinish: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = socialRepository.refreshExploreUsers(usernameSearchText)
            if (result.isSuccess && usernameSearchText.isNotEmpty()) {
                val searchResult = socialRepository.getUsersByName(usernameSearchText)
                if (searchResult.isSuccess) {
                    allUsers.clear()
                    allUsers.addAll(searchResult.getOrNull() ?: emptyList())
                }
            } else if (result.isSuccess && usernameSearchText.isEmpty()) {
                allUsers.clear()
            }
            onFinish(result.isSuccess, result.exceptionOrNull()?.message ?: "")
        }
    }

    fun getUserFriends(onFinish: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = socialRepository.refreshFriends()
            onFinish(result.isSuccess, result.exceptionOrNull()?.message ?: "")
        }
    }

    fun getUserReceivedFriendRequests(onFinish: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = socialRepository.refreshReceivedRequests()
            onFinish(result.isSuccess, result.exceptionOrNull()?.message ?: "")
        }
    }

    fun getUserSentFriendRequests(onFinish: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = socialRepository.refreshSentRequests()
            onFinish(result.isSuccess, result.exceptionOrNull()?.message ?: "")
        }
    }

    fun getOtherUserInfo(otherUsername: String, onFinish: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = socialRepository.getOtherUserInfo(otherUsername)
            if (result.isSuccess) {
                otherUserInfo = result.getOrNull()
                onFinish(true, "")
            } else {
                onFinish(false, result.exceptionOrNull()?.message ?: "Error")
            }
        }
    }

    fun sendFriendRequest(otherUsername: String, onFinish: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = socialRepository.sendFriendRequest(otherUsername)
            onFinish(result.isSuccess, result.exceptionOrNull()?.message ?: "")
        }
    }

    fun acceptFriendRequest(otherUsername: String, onFinish: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = socialRepository.acceptFriendRequest(otherUsername)
            onFinish(result.isSuccess, result.exceptionOrNull()?.message ?: "")
        }
    }

    fun declineFriendRequest(otherUsername: String, onFinish: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = socialRepository.declineFriendRequest(otherUsername)
            onFinish(result.isSuccess, result.exceptionOrNull()?.message ?: "")
        }
    }
}
