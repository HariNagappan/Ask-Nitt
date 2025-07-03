package com.example.asknitt

import android.R.attr.entries
import android.R.attr.name
import android.content.Context
import android.graphics.Paint
import android.net.http.SslCertificate.restoreState
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ContextualFlowColumn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.asknitt.ui.theme.AskNITTTheme
import java.util.Map.entry
import kotlin.math.log

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window,false)
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = false
        setContent {
            AskNITTTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    NavigationScreen(modifier=Modifier.padding(padding))
                    Get_JWT_Token_From_Preferences(context = LocalContext.current)
                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationScreen(mainViewModel: MainViewModel= viewModel(), navController: NavHostController= rememberNavController(), modifier: Modifier) {
    Scaffold(
        containerColor = Color.Black,
        bottomBar = {
                CustomBottomNavigationBar(
                    mainViewModel = mainViewModel,
                    navController = navController
                )
            }
    )
    {
        NavHost(
            navController = navController,
            startDestination = if(JWT_TOKEN=="") AuthScreenRoutes.AUTH.name else MainScreenRoutes.MAIN.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = it.calculateBottomPadding())
                .background(colorResource(R.color.black))
        ) {//.then(modifier)
            navigation(
                startDestination = AuthScreenRoutes.LOGIN.name,
                route = AuthScreenRoutes.AUTH.name
            ) {
                composable(route = AuthScreenRoutes.LOGIN.name) {
                    LoginScreen(
                        loginType = LoginType.LOGIN,
                        navController = navController,
                        mainViewModel = mainViewModel
                    )
                }
                composable(route = AuthScreenRoutes.SIGN_UP.name) {
                    LoginScreen(
                        loginType = LoginType.SIGN_UP,
                        navController = navController,
                        mainViewModel = mainViewModel
                    )
                }
            }
            navigation(
                startDestination = MainScreenRoutes.HOME.name,
                route = MainScreenRoutes.MAIN.name
            ) {
                composable(MainScreenRoutes.HOME.name) {
                    HomeScreenIntermediate(mainViewModel = mainViewModel,navController=navController)
                }
                composable(MainScreenRoutes.SETTINGS.name) {
                    SettingsScreen(navController=navController, mainViewModel = mainViewModel)
                }
                navigation(
                    startDestination = MainScreenRoutes.MY_DOUBTS_LIST.name,
                    route = MainScreenRoutes.MY_DOUBTS.name
                ) {
                    composable(MainScreenRoutes.MY_DOUBTS_LIST.name) {
                        LoadingScreenWithRetry(
                            inside_launched_effect = {onResult->
                                mainViewModel.GetDoubtsByUsername(
                                    username = mainViewModel.username,
                                    onFinish ={success,msg->
                                        onResult(success,msg)
                                    }
                                )
                            },
                            navController=navController,
                            should_verify_exp_sign = false,
                            to_show_on_success = {
                                DoubtsScreen(mainViewModel=mainViewModel,navController=navController)
                            }
                        )
                    }
                    composable(MainScreenRoutes.ADD_DOUBT.name) {
                        LoadingScreenWithRetry(
                            inside_launched_effect = {onResult->
                                mainViewModel.GetTags(
                                    onFinish = {success,new_msg->
                                        onResult(success,new_msg)
                                    })
                            },
                            should_verify_exp_sign = true,
                            navController = navController,
                            to_show_on_success = {
                                AddDoubtScreen(mainViewModel=mainViewModel,navController=navController)
                            }
                        )
                    }
//                    composable<Doubt> {
//                        val doubt=it.toRoute<Doubt>()
//                        ViewDoubtInDetailIntermediate(doubt=doubt,navController=navController,mainViewModel=mainViewModel)
//                    }
                }
                navigation(
                    startDestination = MainScreenRoutes.SEARCH.name,
                    route = MainScreenRoutes.SEARCH_STUFF.name
                ) {
                    composable(MainScreenRoutes.SEARCH.name) {
                        LoadingScreenWithRetry(
                            inside_launched_effect = {onResult->
                                mainViewModel.GetTags(
                                    onFinish ={success,msg->
                                        onResult(success,msg)
                                    }
                                )
                            },
                            should_verify_exp_sign = false,
                            navController = navController,
                            to_show_on_success = {
                                SearchScreen(mainViewModel=mainViewModel,navController=navController)
                            }
                        )
                    }
                }
                navigation(
                    startDestination = MainScreenRoutes.EXPLORE_USERS_HOME.name,
                    route= MainScreenRoutes.EXPLORE_USERS_STUFF.name) {
                    composable(MainScreenRoutes.EXPLORE_USERS_HOME.name) {
                        LoadingScreenWithRetry(
                            inside_launched_effect = {onResult->
                                mainViewModel.GetUsersByName(
                                    username_search_text = "",
                                    onFinish = {success,error_msg->
                                        onResult(success,error_msg)
                                    })
                            },
                            should_verify_exp_sign = true,
                            navController = navController,
                            to_show_on_success = {
                                ExploreUsersHome(mainViewModel=mainViewModel,navController=navController)
                            }
                        )
                    }
                    composable(MainScreenRoutes.FRIENDS.name) {
                        LoadingScreenWithRetry(
                            inside_launched_effect = {onResult->
                                mainViewModel.GetUserFriends(
                                    onFinish = {success,msg->
                                        onResult(success,msg)
                                    }
                                )
                            },
                            navController=navController,
                            should_verify_exp_sign = true,
                            to_show_on_success = {
                                Friends(mainViewModel=mainViewModel,navController=navController)
                            }
                        )
                    }
                    composable(MainScreenRoutes.FRIEND_REQUESTS.name) {
                        FriendRequests(mainViewModel=mainViewModel,navController=navController)
                    }
                }
            }

            composable<Doubt> {
                val doubt=it.toRoute<Doubt>()
                LoadingScreenWithRetry(
                    inside_launched_effect = {onResult->
                        mainViewModel.GetAnswersByQuestionId(
                            question_id = doubt.question_id,
                            onFinish = {success,new_msg->
                                onResult(success,new_msg)
                            })
                    },
                    should_verify_exp_sign = false,
                    navController = navController,
                    to_show_on_success = {
                        ViewDoubtInDetail(doubt=doubt,navController=navController,mainViewModel=mainViewModel)
                    }
                )
            }
            composable<GeneralUser>{
                val generalUser=it.toRoute<GeneralUser>()
                LoadingScreenWithRetry(
                    inside_launched_effect = {onResult->
                        mainViewModel.GetOtherUserInfo(
                            other_username = generalUser.username,
                            onFinish = {success,msg->
                                onResult(success,msg)
                            }
                        )
                    },
                    should_verify_exp_sign = true,
                    navController = navController,
                    to_show_on_success = {
                        Log.d("general","mainviewmodel.other_user:${mainViewModel.other_user_info}")
                        ViewUserInDetail(
                            mainViewModel=mainViewModel,
                            navController=navController)
                    }
                )

            }
        }
    }
}
@Composable
fun CustomBottomNavigationBar(mainViewModel: MainViewModel,navController: NavHostController){
    val entries =GetBottomBarEntries()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.hierarchy

    if(currentRoute?.any{it.route== MainScreenRoutes.MAIN.name }==true) {
        NavigationBar(
            containerColor = colorResource(R.color.black),
            tonalElevation = 10.dp,
            modifier = Modifier.height(100.dp)
        ) {
            entries.forEachIndexed { idx, entry ->
                val isselected=currentRoute.any { it.route == entry.route } == true
                NavigationBarItem(
                    selected = isselected,
                    onClick = {//TODO bug input some text in post doubt screen and go to settings and come back-> all ok no problem, it is restored, but if i go to settings and click settings again, and come back to my-doubt, the text is not there,
                            navController.navigate(entry.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                            }
                    },
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = entry.icon,
                                contentDescription = null,
                                tint = colorResource(if (isselected) R.color.electric_green else R.color.white),
                            )
                            AnimatedVisibility(visible = isselected) {
                                Text(
                                    text = entry.label.uppercase(),
                                    color = colorResource(R.color.electric_green),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                )
            }
        }
    }
}

fun GetBottomBarEntries():List<AllScreensNamesItem>{
    return listOf(
        AllScreensNamesItem(
            route = MainScreenRoutes.HOME.name,
            label = "Home",
            icon = Icons.Default.Home
        ),
        AllScreensNamesItem(
            route = MainScreenRoutes.MY_DOUBTS.name,
            label = "My Doubts",
            icon = Icons.AutoMirrored.Filled.Assignment
        ),
        AllScreensNamesItem(
            route = MainScreenRoutes.SEARCH_STUFF.name,
            label = "Search",
            icon = Icons.Default.Search
        ),
        AllScreensNamesItem(
            route=MainScreenRoutes.EXPLORE_USERS_STUFF.name,
            label="Explore",
            icon=Icons.Default.People
        ),
        AllScreensNamesItem(
            route = MainScreenRoutes.SETTINGS.name,
            label = "Settings",
            icon = Icons.Default.Settings
        )

    )
}

fun Get_JWT_Token_From_Preferences(context: Context){
    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    val prefs = EncryptedSharedPreferences.create(
        SHARED_PREFS_FILENAME,
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    JWT_TOKEN=prefs.getString("JWTToken","")?:""
}
