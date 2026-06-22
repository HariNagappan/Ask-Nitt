package com.example.asknitt.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.asknitt.data.MULTIPARTBODY_FILE_KEY
import com.example.asknitt.data.functions.GetLocalInUTC
import com.example.asknitt.data.model.*
import com.example.asknitt.data.remote.api
import com.example.asknitt.data.repository.DoubtRepository
import com.example.asknitt.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.time.LocalDate

class DoubtsViewModel(
    private val doubtRepository: DoubtRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    var searchQuestionText by mutableStateOf("")

    private val _usernameFlow = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val userDoubts: StateFlow<List<Doubts>> = _usernameFlow
        .flatMapLatest { uname ->
            doubtRepository.getUserDoubts(uname)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recentDoubts: StateFlow<List<Doubts>> = doubtRepository.recentDoubts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val searchResults: StateFlow<List<Doubts>> = doubtRepository.searchResults.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    var filteredDoubts: MutableList<Doubts> = mutableStateListOf()
    val tags: MutableList<String> = mutableStateListOf()
    var curQuestionTags: MutableList<String> = mutableStateListOf()
    var searchQuestionTags: MutableList<String> = mutableStateListOf()
    
    var fromDate by mutableStateOf(LocalDate.now())
    var toDate by mutableStateOf(LocalDate.now())
    var shouldDateFilter by mutableStateOf(false)
    var statusDoubtFilter by mutableStateOf(QuestionStatus.ANY)
    
    val doubtFiles: MutableList<UploadFileItem> = mutableStateListOf()

    init {
        viewModelScope.launch {
            searchResults.collect { results ->
                filteredDoubts.clear()
                filteredDoubts.addAll(results)
            }
        }
    }

    fun setUsername(username: String) {
        _usernameFlow.value = username
    }

    fun getDoubtsByUsername(target: String, currentUsername: String, onFinish: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            var uname = if (target.isEmpty()) currentUsername else target
            if (uname.isEmpty()) {
                onFinish(false, "User identity not found")
                return@launch
            }
            _usernameFlow.value = uname
            val result = doubtRepository.refreshUserDoubts(uname)
            onFinish(result.isSuccess, result.exceptionOrNull()?.message ?: "")
        }
    }

    fun searchDoubts(onFinish: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = doubtRepository.performSearch(
                searchText = searchQuestionText,
                tags = searchQuestionTags,
                fromDate = GetLocalInUTC(if (shouldDateFilter) fromDate.toString() else "0001-01-01", true),
                toDate = GetLocalInUTC(toDate.toString(), false),
                status = statusDoubtFilter.name
            )
            onFinish(result.isSuccess, "")
        }
    }

    fun getRecentDoubts(onFinish: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = doubtRepository.refreshRecentDoubts()
            onFinish(result.isSuccess, "")
        }
    }

    fun postUserDoubt(username: String, title: String, question: String, onFinish: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = api.PostDoubt(PostDoubtItem(username, title, question, curQuestionTags))
                if (response.isSuccessful && response.body()?.error_msg.isNullOrEmpty()) {
                    if (doubtFiles.isNotEmpty()) {
                        uploadFilesForDoubtInternal()
                    }
                    doubtRepository.refreshUserDoubts(username)
                    onFinish(true, "")
                } else onFinish(false, response.body()?.error_msg ?: "Error")
            } catch (e: Exception) { onFinish(false, e.message ?: "Error") }
        }
    }

    private suspend fun uploadFilesForDoubtInternal(): Boolean {
        val fileParts = doubtFiles.map { MultipartBody.Part.createFormData(MULTIPARTBODY_FILE_KEY, it.filename, it.multipartBody.body) }
        return try {
            val response = api.UploadFilesForDoubt(fileParts)
            response.isSuccessful
        } catch (e: Exception) { false }
    }

    fun markQuestionAsSolved(questionId: Int, username: String, onFinish: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = doubtRepository.markQuestionSolved(questionId)
            if (result.isSuccess) {
                doubtRepository.refreshRecentDoubts()
                doubtRepository.refreshUserDoubts(username)
                onFinish(true, "")
            } else onFinish(false, result.exceptionOrNull()?.message ?: "Error")
        }
    }

    fun getTags(onFinish: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = api.GetAllTags()
                if (response.isSuccessful) {
                    tags.clear()
                    response.body()?.tags?.let { tags.addAll(it) }
                    onFinish(true, "")
                } else onFinish(false, response.message())
            } catch (e: Exception) { onFinish(false, e.message ?: "Error") }
        }
    }

    fun clearDoubtFiles() { doubtFiles.clear() }
    fun clearCurrentQuestionTags() { curQuestionTags.clear() }
}
