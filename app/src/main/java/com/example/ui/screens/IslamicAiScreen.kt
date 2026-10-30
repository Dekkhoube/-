package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldAccent
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

private const val TAG = "IslamicAiScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IslamicAiScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Lazy initialization of Gemini 3.8 Flash model
    val generativeModel = remember {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    "You are 'Noor Islamic AI' (নূর ইসলামিক এআই), an authentic, respectful, and scholarly Islamic knowledge assistant inside the Noor Islamic app. " +
                            "Answer questions accurately based upon the Holy Quran and authentic Sunnah/Hadith. " +
                            "Respond in Bengali by default unless the user asks in another language. " +
                            "Provide references from Quran (Surah:Ayah) and Hadith collections (Bukhari, Muslim, etc.) whenever applicable. " +
                            "Keep your tone polite, spiritual, and humble. Conclude rulings or complex matters with 'আল্লাহই সর্বজ্ঞ' (And Allah knows best)."
                )
            }
        )
    }

    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var currentStreamingAnswer by remember { mutableStateOf("") }
    var activeJob by remember { mutableStateOf<Job?>(null) }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                isUser = false,
                text = "আসসালামু আলাইকুম ওয়া রাহমাতুল্লাহ! আমি নূর ইসলামিক এআই (Gemini Powered)। কুরআন, হাদিস, নামাজ, রোজা, দোয়া কিংবা ইসলামী জীবনবিধান সম্পর্কিত যেকোনো প্রশ্ন করতে পারেন।"
            )
        )
    }

    val sampleQuestions = listOf(
        "রমজানের ফজিলত ও রোজার মূল বিধান কী?",
        "সাইয়্যিদুল ইস্তিগফারের তাৎপর্য ব্যাখ্যা করুন",
        "তাহাজ্জুদ নামাজের নিয়ম ও রাকাত সংখ্যা",
        "সূরা আল-ফাতিহাকে উম্মুল কুরআন বলা হয় কেন?",
        "যাকাতের নেসাব ও বণ্টন সংক্রান্ত বিধান",
        "পিতা-মাতার খেদমত সম্পর্কে কুরআন-হাদিসের নির্দেশ"
    )

    fun sendPrompt(promptText: String) {
        val trimmed = promptText.trim()
        if (trimmed.isBlank() || isLoading) return

        messages.add(ChatMessage(isUser = true, text = trimmed))
        inputText = ""
        isLoading = true
        currentStreamingAnswer = ""

        activeJob?.cancel()
        activeJob = coroutineScope.launch {
            val accumulated = StringBuilder()
            try {
                // Scroll to bottom
                coroutineScope.launch {
                    listState.animateScrollToItem(messages.size)
                }

                withTimeout(60_000L) {
                    generativeModel.generateContentStream(trimmed).collect { chunk ->
                        val piece = chunk.text.orEmpty()
                        accumulated.append(piece)
                        currentStreamingAnswer = accumulated.toString()
                    }
                }

                if (accumulated.isNotBlank()) {
                    messages.add(ChatMessage(isUser = false, text = accumulated.toString()))
                } else {
                    messages.add(ChatMessage(isUser = false, text = "মডেল থেকে কোনো উত্তর পাওয়া যায়নি। দয়া করে আবার চেষ্টা করুন।"))
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Streaming timed out", e)
                messages.add(ChatMessage(isUser = false, text = "উত্তরের সময়সীমা শেষ হয়েছে (৬০ সেকেন্ড)। দয়া করে পুনরায় প্রশ্ন করুন।"))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Gemini generation error: ${e.message}", e)
                messages.add(ChatMessage(isUser = false, text = "ত্রুটি: ${e.localizedMessage ?: "সংযোগজনিত সমস্যা। ইন্টারনেট চেক করুন।"}\n\nApp Check অথবা Gemini API কনফিগারেশন যাচাই করুন।"))
            } finally {
                isLoading = false
                currentStreamingAnswer = ""
                coroutineScope.launch {
                    listState.animateScrollToItem((messages.size - 1).coerceAtLeast(0))
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Emerald700, GoldAccent))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "ইসলামিক এআই সহকারী",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Emerald900
                            )
                            Text(
                                text = "Powered by Google Gemini 3.8",
                                style = MaterialTheme.typography.bodySmall,
                                color = GoldAccent,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("islamic_ai_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            messages.clear()
                            messages.add(
                                ChatMessage(
                                    isUser = false,
                                    text = "আসসালামু আলাইকুম! চ্যাট পরিষ্কার করা হয়েছে। আপনার নতুন প্রশ্ন করুন।"
                                )
                            )
                        },
                        modifier = Modifier.testTag("islamic_ai_clear_btn")
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "চ্যাট মুছুন", tint = Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize().testTag("islamic_ai_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Suggestion Chips Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sampleQuestions) { q ->
                    SuggestionChip(
                        onClick = { sendPrompt(q) },
                        label = { Text(q, fontSize = 12.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        border = SuggestionChipDefaults.suggestionChipBorder(
                            enabled = true,
                            borderColor = Emerald700.copy(alpha = 0.3f)
                        )
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // Chat Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatBubble(
                        message = msg,
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Islamic AI Answer", msg.text))
                            Toast.makeText(context, "টেক্সট কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                // Streaming in-progress response bubble
                if (isLoading && currentStreamingAnswer.isNotBlank()) {
                    item {
                        ChatBubble(
                            message = ChatMessage(isUser = false, text = currentStreamingAnswer),
                            isStreaming = true,
                            onCopy = {}
                        )
                    }
                } else if (isLoading) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = Emerald700
                            )
                            Text(
                                text = "উত্তর তৈরি করা হচ্ছে...",
                                style = MaterialTheme.typography.bodySmall,
                                color = Emerald800
                            )
                        }
                    }
                }
            }

            // Bottom Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("কুরআন ও হাদিস সম্পর্কিত প্রশ্ন লিখুন...", fontSize = 14.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_input_field"),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 4,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { sendPrompt(inputText) }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Emerald700,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    IconButton(
                        onClick = { sendPrompt(inputText) },
                        enabled = inputText.isNotBlank() && !isLoading,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (inputText.isNotBlank() && !isLoading) Emerald700 else Color.LightGray)
                            .testTag("ai_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "পাঠান",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessage,
    isStreaming: Boolean = false,
    onCopy: () -> Unit
) {
    val isUser = message.isUser
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bgColor = if (isUser) Emerald700 else Color(0xFFF8FAF9)
    val textColor = if (isUser) Color.White else Color(0xFF1E293B)
    val shape = if (isUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .clip(shape)
                .background(bgColor)
                .border(
                    width = if (isUser) 0.dp else 1.dp,
                    color = if (isUser) Color.Transparent else Emerald700.copy(alpha = 0.2f),
                    shape = shape
                )
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (!isUser) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "নূর ইসলামিক এআই",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Emerald800
                            )
                        }

                        if (!isStreaming) {
                            IconButton(onClick = onCopy, modifier = Modifier.size(24.dp)) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "কপি করুন",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = message.text,
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )
            }
        }
    }
}
