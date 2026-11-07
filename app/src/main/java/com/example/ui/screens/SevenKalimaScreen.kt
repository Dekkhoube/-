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
import com.example.data.model.KalimaItem
import com.example.data.repository.IslamicAmalRepository
import com.example.ui.theme.Emerald700
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight

/**
 * Seven Kalimas Screen (সাতটি কালেমা)
 * Visually inspired by Screenshot_20261107_155623_787d90ce.jpg
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SevenKalimaScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val kalimas = remember { IslamicAmalRepository.sevenKalimas }
    var expandedKalimaId by remember { mutableStateOf<Int?>(1) } // First one open by default

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "সাতটি কালেমা",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "ঈমান ও তাওহীদের পূর্ণাঙ্গ ও বিশুদ্ধ বাক্যসমূহ",
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
        modifier = modifier.fillMaxSize().testTag("seven_kalima_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Ornament Card matching reference screenshot
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E0E38)), // Deep purple matching screenshot
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "কালেমা",
                                color = GoldLight,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "সাতটি মৌলিক ঈমানী কালেমার বিশুদ্ধ আরবি, উচ্চারণ ও অর্থ",
                                color = Color.White.copy(alpha = 0.85f),
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Kalima Items matching reference list
            items(kalimas, key = { it.id }) { kalima ->
                val isExpanded = expandedKalimaId == kalima.id

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expandedKalimaId = if (isExpanded) null else kalima.id
                        }
                        .testTag("kalima_card_${kalima.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isExpanded) MaterialTheme.colorScheme.surface else Color(0xFF160A26) // Dark container matching screenshot
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 3.dp else 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Title Row matching screenshot: "১। কালেমা তাইয়্যেবাহ" with ornament
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${kalima.numberBengali}। ${kalima.titleBengali}",
                                    color = if (isExpanded) MaterialTheme.colorScheme.primary else Color.White,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = kalima.titleArabic,
                                    color = GoldAccent,
                                    fontSize = 14.sp
                                )
                            }

                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isExpanded) "লুকান" else "বিস্তারিত দেখুন",
                                tint = if (isExpanded) MaterialTheme.colorScheme.primary else GoldAccent
                            )
                        }

                        // Expanded Details
                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(10.dp))

                                // Arabic Text
                                Text(
                                    text = kalima.textArabic,
                                    fontSize = 22.sp,
                                    lineHeight = 36.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Right,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Pronunciation (Light mint green box matching reference screenshot)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFE8F5E9),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "উচ্চারণ:",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Emerald700
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = kalima.transliteration,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF1B5E20),
                                            lineHeight = 20.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Translation (Soft amber box matching reference screenshot)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFFF8E1),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "অনুবাদ:",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB45309)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = kalima.translationBengali,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF451A03),
                                            lineHeight = 20.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Explanation & Virtues
                                if (kalima.explanation.isNotBlank() || kalima.virtues.isNotBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFF3E8FF), // Soft purple box
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            if (kalima.explanation.isNotBlank()) {
                                                Text(
                                                    text = "তাৎপর্য: ${kalima.explanation}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color(0xFF4C1D95),
                                                    lineHeight = 18.sp
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                            }
                                            if (kalima.virtues.isNotBlank()) {
                                                Text(
                                                    text = "✨ ফজিলত: ${kalima.virtues}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color(0xFF5B21B6),
                                                    fontWeight = FontWeight.Medium,
                                                    lineHeight = 18.sp
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                // Reference & Actions
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "দলিল: ${kalima.reference}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Row {
                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = ClipData.newPlainText("Kalima", "${kalima.titleBengali}\n${kalima.textArabic}\n${kalima.transliteration}\n${kalima.translationBengali}")
                                                clipboard.setPrimaryClip(clip)
                                                Toast.makeText(context, "কালেমা কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "কপি", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        IconButton(
                                            onClick = {
                                                val shareText = "${kalima.titleBengali}:\n\n${kalima.textArabic}\n\nউচ্চারণ: ${kalima.transliteration}\n\nঅনুবাদ: ${kalima.translationBengali}\n\n— নূর ইসলামিক অ্যাপ"
                                                val sendIntent = Intent().apply {
                                                    action = Intent.ACTION_SEND
                                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                                    type = "text/plain"
                                                }
                                                context.startActivity(Intent.createChooser(sendIntent, "কালেমা শেয়ার করুন"))
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = "শেয়ার", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
