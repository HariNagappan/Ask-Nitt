package com.example.asknitt

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.asknitt.data.local.AppDatabase
import com.example.asknitt.data.model.Doubts
import com.example.asknitt.data.model.GeneralUser
import com.example.asknitt.data.JWT_TOKEN
import com.example.asknitt.data.LoginType
import com.example.asknitt.data.SHARED_PREFS_FILENAME_ENCRYPTED
import com.example.asknitt.data.remote.api
import com.example.asknitt.data.repository.*
import com.example.asknitt.data.routes.AuthScreenRoutes
import com.example.asknitt.data.routes.MainScreenRoutes
import com.example.asknitt.ui.components.CustomBottomNavigationBar
import com.example.asknitt.ui.components.LoadingScreenWithRetry
import com.example.asknitt.ui.presentation.ai.AiChatScreen
import com.example.asknitt.ui.presentation.auth.LoginScreen
import com.example.asknitt.ui.presentation.doubts.*
import com.example.asknitt.ui.presentation.home.HomeScreenIntermediate
import com.example.asknitt.ui.presentation.search.SearchScreen
import com.example.asknitt.ui.presentation.settings.SettingsScreen
import com.example.asknitt.ui.presentation.social.*
import com.example.asknitt.ui.theme.AskNITTTheme
import com.example.asknitt.viewmodels.*

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = false
        
        loadJwtToken(this)

        setContent {
            AskNITTTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color.Black) { padding ->
                    NavigationScreen(modifier = Modifier.padding(padding))
                }
            }
        }
    }

    private fun loadJwtToken(context: Context) {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            val prefs = EncryptedSharedPreferences.create(
                context,
                SHARED_PREFS_FILENAME_ENCRYPTED,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            JWT_TOKEN = prefs.getString("JWTToken", "") ?: ""
        } catch (e: Exception) {
            Log.e("MainActivity", "Failed to load token", e)
            JWT_TOKEN = ""
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationScreen(navController: NavHostController = rememberNavController(), modifier: Modifier = Modifier) {
    val context = LocalContext.current
    
    val database = remember { AppDatabase.getDatabase(context) }
    val doubtRepository = remember { DoubtRepository(api, database.doubtDao(), database.searchExploreDao()) }
    val userRepository = remember { UserRepository(api, database.userDao()) }
    val socialRepository = remember { SocialRepository(api, database.socialDao(), database.searchExploreDao()) }
    val answerRepository = remember { AnswerRepository(api, database.answerDao()) }
    val aiChatRepository = remember { AiChatRepository(api, database.aiChatDao()) }
    
    val factory = MainViewModelFactory(doubtRepository, userRepository, socialRepository, answerRepository, aiChatRepository)
    
    val mainViewModel: MainViewModel = viewModel(factory = factory)
    val doubtsViewModel: DoubtsViewModel = viewModel(factory = factory)
    val answerViewModel: AnswerViewModel = viewModel(factory = factory)
    val exploreViewModel: ExploreViewModel = viewModel(factory = factory)
    val aiViewModel: AiViewModel = viewModel(factory = factory)

    Scaffold(
        containerColor = Color.Black,
        bottomBar = { CustomBottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (JWT_TOKEN == "") AuthScreenRoutes.AUTH.name else MainScreenRoutes.MAIN.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .background(Color.Black)
        ) {
            navigation(startDestination = AuthScreenRoutes.LOGIN.name, route = AuthScreenRoutes.AUTH.name) {
                composable(AuthScreenRoutes.LOGIN.name) { LoginScreen(navController,LoginType.LOGIN, mainViewModel) }
                composable(AuthScreenRoutes.SIGN_UP.name) { LoginScreen(navController,LoginType.SIGN_UP, mainViewModel) }
            }

            navigation(startDestination = MainScreenRoutes.HOME.name, route = MainScreenRoutes.MAIN.name) {
                composable(MainScreenRoutes.HOME.name) { HomeScreenIntermediate(mainViewModel,doubtsViewModel, navController) }
                composable(MainScreenRoutes.SETTINGS.name) { SettingsScreen(mainViewModel, navController) }

                navigation(startDestination = MainScreenRoutes.MY_DOUBTS_LIST.name, route = MainScreenRoutes.MY_DOUBTS.name) {
                    composable(MainScreenRoutes.MY_DOUBTS_LIST.name) {
                        LoadingScreenWithRetry(
                            inside_launched_effect = { onResult ->
                                doubtsViewModel.getDoubtsByUsername("", mainViewModel.username, onResult)
                            },
                            navController = navController,
                            to_show_on_success = { DoubtsScreen(doubtsViewModel, navController) }
                        )
                    }
                    composable(MainScreenRoutes.ADD_DOUBT.name) {
                        LoadingScreenWithRetry(
                            inside_launched_effect = { onResult -> doubtsViewModel.getTags(onResult) },
                            should_verify_exp_sign = true,
                            navController = navController,
                            to_show_on_success = {
                                doubtsViewModel.clearDoubtFiles()
                                AddDoubtScreen(doubtsViewModel, navController,mainViewModel)
                            }
                        )
                    }
                }

                navigation(startDestination = MainScreenRoutes.SEARCH.name, route = MainScreenRoutes.SEARCH_STUFF.name) {
                    composable(MainScreenRoutes.SEARCH.name) {
                        LoadingScreenWithRetry(
                            inside_launched_effect = { onResult -> doubtsViewModel.getTags(onResult) },
                            navController = navController,
                            to_show_on_success = { SearchScreen(navController, doubtsViewModel) }
                        )
                    }
                }

                navigation(startDestination = MainScreenRoutes.EXPLORE_USERS_HOME.name, route = MainScreenRoutes.EXPLORE_USERS_STUFF.name) {
                    composable(MainScreenRoutes.EXPLORE_USERS_HOME.name) {
                        LoadingScreenWithRetry(
                            inside_launched_effect = { onResult -> exploreViewModel.getUsersByName("", onResult) },
                            should_verify_exp_sign = true,
                            navController = navController,
                            to_show_on_success = { ExploreUsersHome(exploreViewModel, navController) }
                        )
                    }
                    composable(MainScreenRoutes.FRIENDS.name) {
                        LoadingScreenWithRetry(
                            inside_launched_effect = { onResult -> exploreViewModel.getUserFriends(onResult) },
                            navController = navController,
                            to_show_on_success = { Friends(exploreViewModel, navController) }
                        )
                    }
                    composable(MainScreenRoutes.FRIEND_REQUESTS.name) { FriendRequests(exploreViewModel, navController) }
                }

                navigation(startDestination = MainScreenRoutes.AICHAT_HOME.name, route = MainScreenRoutes.AICHAT.name) {
                    composable(MainScreenRoutes.AICHAT_HOME.name) { AiChatScreen(navController, aiViewModel) }
                }
            }

            composable<Doubts> { backStackEntry ->
                val doubt = backStackEntry.toRoute<Doubts>()
                LoadingScreenWithRetry(
                    inside_launched_effect = { onResult -> answerViewModel.getAnswersByQuestionId(doubt.question_id, onResult) },
                    navController = navController,
                    to_show_on_success = { ViewDoubtInDetail(doubt, navController, mainViewModel, doubtsViewModel,answerViewModel ) }
                )
            }

            composable<GeneralUser> { backStackEntry ->
                val user = backStackEntry.toRoute<GeneralUser>()
                LoadingScreenWithRetry(
                    inside_launched_effect = { onResult -> exploreViewModel.getOtherUserInfo(user.username, onResult) },
                    navController = navController,
                    to_show_on_success = { ViewUserInDetail(exploreViewModel, navController) }
                )
            }
        }
    }
}
