package com.example.asknitt.ui.presentation.settings

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asknitt.R
import com.example.asknitt.data.ProfileVisibility
import com.example.asknitt.data.routes.AuthScreenRoutes
import com.example.asknitt.data.routes.MainScreenRoutes
import com.example.asknitt.viewmodels.MainViewModel

@Composable
fun SettingsScreen(mainViewModel: MainViewModel, navController: NavController) {
    val context = LocalContext.current
    var showLogoutLoading by remember { mutableStateOf(false) }
    var showVisibilityDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = dimensionResource(R.dimen.from_top_padding),
                    bottom = dimensionResource(R.dimen.large_padding),
                    start = dimensionResource(R.dimen.large_padding),
                    end = dimensionResource(R.dimen.large_padding)
                )
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "SETTINGS",
                fontSize = 32.sp,
                fontFamily = FontFamily(Font(R.font.headings)),
                color = colorResource(R.color.electric_gold),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            SettingsSection(title = "Profile") {
                SettingsItem(
                    icon = Icons.Default.Visibility,
                    title = "Profile Visibility",
                    subtitle = mainViewModel.profileVisibility.name.replace("_", " "),
                    onClick = { showVisibilityDialog = true }
                )
            }

            SettingsSection(title = "Account") {
                SettingsItem(
                    icon = Icons.Default.Logout,
                    title = "Logout",
                    titleColor = colorResource(R.color.electric_red),
                    onClick = { showLogoutLoading = true }
                )
            }
        }

        if (showVisibilityDialog) {
            VisibilityDialog(
                currentVisibility = mainViewModel.profileVisibility,
                onDismiss = { showVisibilityDialog = false },
                onSelect = { newVisibility ->
                    mainViewModel.updateProfileVisibility(newVisibility) { success, msg ->
                        if (success) {
                            Toast.makeText(context, "Visibility updated", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error: $msg", Toast.LENGTH_SHORT).show()
                        }
                    }
                    showVisibilityDialog = false
                }
            )
        }

        if (showLogoutLoading) {
            LogoutLoadingScreen(
                mainViewModel = mainViewModel,
                navController = navController
            )
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title.uppercase(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colorResource(R.color.dark_gray)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    titleColor: Color = Color.White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (titleColor == Color.White) colorResource(R.color.electric_blue) else titleColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = titleColor, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            if (subtitle != null) {
                Text(text = subtitle, color = Color.Gray, fontSize = 12.sp)
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun VisibilityDialog(
    currentVisibility: ProfileVisibility,
    onDismiss: () -> Unit,
    onSelect: (ProfileVisibility) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Profile Visibility", color = Color.White) },
        containerColor = colorResource(R.color.dark_gray),
        text = {
            Column {
                ProfileVisibility.values().forEach { visibility ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(visibility) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = visibility == currentVisibility,
                            onClick = { onSelect(visibility) },
                            colors = RadioButtonDefaults.colors(selectedColor = colorResource(R.color.electric_green))
                        )
                        Text(
                            text = visibility.name.replace("_", " "),
                            color = Color.White,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = colorResource(R.color.electric_pink))
            }
        }
    )
}

@Composable
fun LogoutLoadingScreen(mainViewModel: MainViewModel, navController: NavController) {
    var success by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        mainViewModel.logout(context = context) { _success, _msg ->
            success = _success
            errorMsg = _msg
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        if (!success && errorMsg == "") {
            CircularProgressIndicator(color = colorResource(R.color.electric_green))
        } else if (success) {
            LaunchedEffect(Unit) {
                navController.navigate(AuthScreenRoutes.AUTH.name) {
                    popUpTo(MainScreenRoutes.MAIN.name) { inclusive = true }
                }
                Toast.makeText(context, "Logged Out Successfully", Toast.LENGTH_SHORT).show()
            }
        } else {
            LaunchedEffect(Unit) {
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
