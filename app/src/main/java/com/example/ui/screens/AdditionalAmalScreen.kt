package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.IslamicAmalRepository
import com.example.ui.theme.Emerald700
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight

/**
 * Additional Islamic Amals & Guides Screen (অতিরিক্ত - ২৬টি)
 * Visually matching user screenshots Screenshot_20261107_155349_e3c2d10f.jpg & 155506_74b1d7b4.jpg
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdditionalAmalScreen(
    initialTopicId: Int? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val allAmals = remember { IslamicAmalRepository.additionalAmals26 }
    val filteredAmals = remember(searchQuery) {
        if (searchQuery.isBlank()) allAmals
        else allAmals.filter {
            it.titleBengali.contains(searchQuery, ignoreCase = true) ||
            it.translationBengali.contains(searchQuery, ignoreCase = true) ||
            it.transliteration.contains(searchQuery, ignoreCase = true) ||
            it.explanation.contains(searchQuery, ignoreCase = true) ||
            it.numberBengali.contains(searchQuery)
        }
    }

    var expandedAmalId by remember { mutableStateOf<Int?>(initialTopicId ?: 101) } // Default open first (Ayatul Kursi)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "অতিরিক্ত আমল ও বিধান",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "২৬টি প্রয়োজনীয় আমল, নিয়ত, নিয়ম ও দোয়া",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier.fillMaxSize().testTag("additional_amal_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Card matching Screenshots 155349 & 155506
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E0A0A)), // Deep maroon-black
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "অতিরিক্ত",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "দৈনন্দিন নামাজ, জানাজা, রোজা, নিয়ত ও বিশেষ ইবাদত",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("আমল বা বিধান খুঁজুন (উদাঃ জানাজা, কসর, তারাবী)") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Emerald700) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // List of Additional Amals
            items(filteredAmals, key = { it.id }) { item ->
                IslamicTopicCard(
                    item = item,
                    headerBgColor = Color(0xFF1E0A0A),
                    isExpanded = expandedAmalId == item.id,
                    onToggleExpand = {
                        expandedAmalId = if (expandedAmalId == item.id) null else item.id
                    },
                    onCopy = {
                        val clip = ClipData.newPlainText(
                            item.titleBengali,
                            "${item.titleBengali}\n\n${item.textArabic}\n\nউচ্চারণ: ${item.transliteration}\n\nঅনুবাদ: ${item.translationBengali}\n\nব্যাখ্যা: ${item.explanation}\n\nফজিলত: ${item.virtues}\n\nদলিল: ${item.reference}"
                        )
                        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                        Toast.makeText(context, "${item.titleBengali} কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                    },
                    onShare = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "✨ ${item.titleBengali} ✨\n\n${item.textArabic}\n\nউচ্চারণ:\n${item.transliteration}\n\nঅর্থ:\n${item.translationBengali}\n\nব্যাখ্যা/নিয়ম:\n${item.explanation}\n\nফজিলত:\n${item.virtues}\n\nদলিল: ${item.reference}\n\n— নূর ইসলামিক অ্যাপ"
                            )
                        }
                        context.startActivity(Intent.createChooser(intent, "শেয়ার করুন"))
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
