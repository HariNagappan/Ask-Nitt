package com.example.asknitt.ui.presentation.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.asknitt.R
import com.example.asknitt.viewmodels.AiViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(navController: NavHostController, aiViewModel: AiViewModel) {
    val chatMessages by aiViewModel.chatHistory.collectAsState()
    var textFieldInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, null, tint = colorResource(R.color.electric_gold))
                        Spacer(Modifier.width(12.dp))
                        Text("AI Assistant", fontWeight = FontWeight.ExtraBold)
                    }
                },
                actions = {
                    IconButton(onClick = { aiViewModel.clearHistory { _, _ -> } }) {
                        Icon(Icons.Default.DeleteSweep, "Clear", tint = colorResource(R.color.electric_red))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(R.color.black), titleContentColor = Color.White)
            )
        },
        containerColor = colorResource(R.color.black)
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(chatMessages) { message ->
                    if (message.isUser) UserQueryBubble(message.text) else AiResponseBubble(message.text)
                }
                if (aiViewModel.isTyping) item { TypingIndicator() }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = colorResource(R.color.dark_gray),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding().imePadding(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = textFieldInput,
                        onValueChange = { textFieldInput = it },
                        placeholder = { Text("Ask something...", color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(28.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = colorResource(R.color.electric_blue),
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Black.copy(alpha = 0.3f),
                            unfocusedContainerColor = Color.Black.copy(alpha = 0.3f),
                        )
                    )
                    IconButton(
                        onClick = {
                            if (textFieldInput.isNotBlank()) {
                                aiViewModel.askAI(textFieldInput) { _, _ -> }
                                textFieldInput = ""
                            }
                        },
                        enabled = textFieldInput.isNotBlank() && !aiViewModel.isTyping,
                        modifier = Modifier.size(48.dp).background(colorResource(R.color.electric_green), RoundedCornerShape(24.dp))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, "Send", tint = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun AiResponseBubble(text: String) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Box(Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(Brush.linearGradient(listOf(colorResource(R.color.electric_pink), colorResource(R.color.electric_blue))))) {
            Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.align(Alignment.Center).size(18.dp))
        }
        Spacer(Modifier.width(8.dp))
        Card(shape = RoundedCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp), colors = CardDefaults.cardColors(colorResource(R.color.dark_gray))) {
            Box(Modifier.padding(12.dp)) { MarkdownText(markdown = text, style = MaterialTheme.typography.bodyMedium.copy(Color.White)) }
        }
    }
}

@Composable
fun UserQueryBubble(text: String) {
    Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), contentAlignment = Alignment.CenterEnd) {
        Card(shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 0.dp, bottomStart = 16.dp), colors = CardDefaults.cardColors(colorResource(R.color.electric_blue))) {
            Text(text, Modifier.padding(12.dp), color = Color.White)
        }
    }
}

@Composable
fun TypingIndicator() {
    Text("AI is thinking...", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(16.dp))
}
