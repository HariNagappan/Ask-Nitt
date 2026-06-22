package com.example.asknitt.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.asknitt.data.functions.GetUtcInLocalTime
import com.example.asknitt.data.model.*
import com.example.asknitt.data.*
import com.example.asknitt.data.remote.api
import com.example.asknitt.data.remote.UpdateVisibilityRequest
import com.example.asknitt.data.repository.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@SuppressLint("NewApi")
class MainViewModel(
    private val doubtRepository: DoubtRepository,
    private val userRepository: UserRepository,
    private val socialRepository: SocialRepository,
    private val answerRepository: AnswerRepository
) : ViewModel() {
    var username by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    val currentUserInfo: StateFlow<CurrentUserInfo?> = userRepository.cachedUserInfo.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    var userQuestionsAsked by mutableIntStateOf(0)
    var userQuestionsHelped by mutableIntStateOf(0)
    var joinedOn by mutableStateOf("")
    var profileVisibility by mutableStateOf(ProfileVisibility.PUBLIC)

    init {
        viewModelScope.launch {
            currentUserInfo.collectLatest { info ->
                if (info != null) {
                    username = info.username
                    userQuestionsAsked = info.questions_asked
                    userQuestionsHelped = info.people_helped
                    joinedOn = GetUtcInLocalTime(info.joined_on)
                    profileVisibility = info.profile_visibility
                }
            }
        }
    }

    fun updateUsername(n: String) { username = n }
    fun updatePassword(p: String) { password = p }
    
    fun getHomeScreenStuff(o1: (Boolean, String) -> Unit, o2: (Boolean, String) -> Unit) {
        getCurrentUserInfo { s, m -> o1(s, m) }
    }
    
    fun getCurrentUserInfo(onFinish: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = userRepository.refreshCurrentUserInfo()
            onFinish(result.isSuccess || currentUserInfo.value != null, "")
        }
    }

    fun loginUser(context: Context, onFinish: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = api.Login(User(username = username, password = password))
                if (response.isSuccessful && response.body() != null) {
                    JWT_TOKEN = response.body()!!.token
                    saveJwtToken(context = context)
                    userRepository.refreshCurrentUserInfo()
                    onFinish(true, "")
                } else onFinish(false, response.body()?.msg ?: response.message())
            } catch (e: Exception) { onFinish(false, e.message.toString()) }
        }
    }

    fun signUpUser(context: Context, onFinish: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = api.SignUp(User(username = username, password = password))
                if (response.isSuccessful && response.body() != null) {
                    JWT_TOKEN = response.body()!!.token
                    saveJwtToken(context = context)
                    userRepository.refreshCurrentUserInfo()
                    onFinish(true, "")
                } else onFinish(false, response.body()?.msg ?: response.message())
            } catch (e: Exception) { onFinish(false, e.message.toString()) }
        }
    }

    fun logout(context: Context, onFinish: (Boolean, String) -> Unit) {
        deleteJwtToken(context = context); onFinish(true, "")
    }

    private fun getMasterKey(context: Context): MasterKey {
        return MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    fun saveJwtToken(context: Context) {
        val masterKey = getMasterKey(context)
        val prefs = EncryptedSharedPreferences.create(
            context,
            SHARED_PREFS_FILENAME_ENCRYPTED,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        prefs.edit().putString("JWTToken", JWT_TOKEN).apply()
    }

    fun deleteJwtToken(context: Context) {
        val masterKey = getMasterKey(context)
        val prefs = EncryptedSharedPreferences.create(
            context,
            SHARED_PREFS_FILENAME_ENCRYPTED,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        prefs.edit().putString("JWTToken", "").apply()
    }

    fun updateProfileVisibility(visibility: ProfileVisibility, onFinish: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = api.UpdateProfileVisibility(UpdateVisibilityRequest(visibility))
                if (response.isSuccessful && response.body()?.success == true) {
                    profileVisibility = response.body()!!.profile_visibility
                    userRepository.refreshCurrentUserInfo()
                    onFinish(true, "")
                } else {
                    onFinish(false, response.body()?.error_msg ?: "Update failed")
                }
            } catch (e: Exception) {
                onFinish(false, e.message ?: "Error")
            }
        }
    }
}
