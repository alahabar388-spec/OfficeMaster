package com.neurohub.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class ChatMessage(val text: String, val user: Boolean)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { NeuroHubScreen() }
    }
}

@Composable
private fun NeuroHubScreen() {
    var input by remember { mutableStateOf("") }
    var messages by remember {
        mutableStateOf(listOf(ChatMessage(
            "Привет! Я NeuroHub — единый центр искусственного интеллекта. Напиши задачу, и мы подготовим автоматический выбор модели.",
            false
        )))
    }
    MaterialTheme(colorScheme = darkColorScheme(
        primary = Color(0xFF9BAAFF),
        secondary = Color(0xFF66D7C5),
        background = Color(0xFF0B1020),
        surface = Color(0xFF17223A)
    )) {
        Scaffold(
            containerColor = Color(0xFF0B1020),
            bottomBar = {
                Row(
                    Modifier.fillMaxWidth().imePadding().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = input, onValueChange = { input = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Напиши задачу…") },
                        maxLines = 4, shape = RoundedCornerShape(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        val text = input.trim()
                        if (text.isNotEmpty()) {
                            messages = messages + ChatMessage(text, true) +
                                ChatMessage("Запрос принят. Подключение серверного AI Router — следующий этап разработки.", false)
                            input = ""
                        }
                    }) { Text("➤") }
                }
            }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                Text("NEUROHUB", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text("Единый центр искусственного интеллекта", color = Color(0xFF9AA8C7), fontSize = 13.sp)
                Spacer(Modifier.height(16.dp))
                Surface(color = Color(0xFF1D2D4C), shape = RoundedCornerShape(50)) {
                    Text("● AUTO ROUTER     ● MULTI-MODEL",
                        Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        color = Color(0xFFB7D6FF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(16.dp))
                LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(messages) { msg ->
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = if (msg.user) Arrangement.End else Arrangement.Start) {
                            Surface(
                                color = if (msg.user) Color(0xFF294574) else Color(0xFF18243C),
                                shape = RoundedCornerShape(18.dp),
                                modifier = Modifier.widthIn(max = 310.dp)
                            ) {
                                Text(msg.text, Modifier.padding(14.dp), color = Color.White, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
