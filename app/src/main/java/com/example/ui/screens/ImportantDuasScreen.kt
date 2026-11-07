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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.IslamicTopicItem
import com.example.data.repository.IslamicAmalRepository
import com.example.ui.theme.Emerald700
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight

/**
 * 23 Important Quranic Duas Screen (দো'আ - ২৩টি)
 * Visually matching Screenshot_20261107_155526_c7650af7.jpg
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportantDuasScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val allDuas = remember { IslamicAmalRepository.importantDuas23 }
    val filteredDuas = remember(searchQuery) {
        if (searchQuery.isBlank()) allDuas
        else allDuas.filter {
            it.titleBengali.contains(searchQuery, ignoreCase = true) ||
            it.translationBengali.contains(searchQuery, ignoreCase = true) ||
            it.transliteration.contains(searchQuery, ignoreCase = true) ||
            it.textArabic.contains(searchQuery, ignoreCase = true) ||
            it.reference.contains(searchQuery, ignoreCase = true)
        }
    }

    var expandedDuaId by remember { mutableStateOf<Int?>(1) } // First one open by default

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "দো'আ (২৩টি)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "পবিত্র কুরআনের শ্রেষ্ঠ রব্বানা ও বরকতময় দোয়াসমূহ",
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
        modifier = modifier.fillMaxSize().testTag("important_duas_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Title Card matching Screenshot 155526
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1035)), // Deep purple-maroon
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "দো'আ (২৩টি)",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "وَقَالَ رَبُّكُمُ ادْعُونِي أَسْتَجِبْ لَكُمْ",
                            fontSize = 15.sp,
                            color = GoldLight.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "তোমাদের প্রতিপালক বলেন: তোমরা আমাকে ডাকো, আমি তোমাদের ডাকে সাড়া দেব।",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.75f),
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
                    placeholder = { Text("দোয়া বা বিষয় খুঁজুন (উদাঃ পিতা-মাতা, রিযিক, ক্ষমা)") },
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

            // List of Duas
            items(filteredDuas, key = { it.id }) { dua ->
                IslamicTopicCard(
                    item = dua,
                    headerBgColor = Color(0xFF1E1035),
                    isExpanded = expandedDuaId == dua.id,
                    onToggleExpand = {
                        expandedDuaId = if (expandedDuaId == dua.id) null else dua.id
                    },
                    onCopy = {
                        val clip = ClipData.newPlainText(
                            dua.titleBengali,
                            "${dua.titleBengali}\n\n${dua.textArabic}\n\nউচ্চারণ: ${dua.transliteration}\n\nঅর্থ: ${dua.translationBengali}\n\nফজিলত: ${dua.virtues}\n\nদলিল: ${dua.reference}"
                        )
                        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                        Toast.makeText(context, "${dua.titleBengali} কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                    },
                    onShare = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "✨ ${dua.titleBengali} ✨\n\n${dua.textArabic}\n\nউচ্চারণ:\n${dua.transliteration}\n\nঅর্থ:\n${dua.translationBengali}\n\nফজিলত:\n${dua.virtues}\n\nদলিল: ${dua.reference}\n\n— নূর ইসলামিক অ্যাপ"
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

/**
 * Reusable Card Component for Dua & Amal topics
 */
@Composable
fun IslamicTopicCard(
    item: IslamicTopicItem,
    headerBgColor: Color = Color(0xFF04261C),
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    var arabicFontSize by remember { mutableStateOf(20) }
    var bengaliFontSize by remember { mutableStateOf(14) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleExpand),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpanded) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 3.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(headerBgColor)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "❖",
                        color = GoldAccent,
                        fontSize = 14.sp
                    )
                    Text(
                        text = if (item.numberBengali.isNotBlank()) "${item.numberBengali} ।  ${item.titleBengali}" else item.titleBengali,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = GoldAccent
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Actions row: Font resizing and copy/share
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "ফন্ট:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            FilledTonalIconButton(
                                onClick = {
                                    if (arabicFontSize > 16) arabicFontSize -= 2
                                    if (bengaliFontSize > 12) bengaliFontSize -= 1
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Text("-", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                            FilledTonalIconButton(
                                onClick = {
                                    if (arabicFontSize < 34) arabicFontSize += 2
                                    if (bengaliFontSize < 20) bengaliFontSize += 1
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Text("+", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "কপি", modifier = Modifier.size(17.dp), tint = Emerald700)
                            }
                            IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Share, contentDescription = "শেয়ার", modifier = Modifier.size(17.dp), tint = Emerald700)
                            }
                        }
                    }

                    // Arabic Text (if present)
                    if (item.textArabic.isNotBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Text(
                                text = item.textArabic,
                                fontSize = arabicFontSize.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Right,
                                lineHeight = (arabicFontSize * 1.8).sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            )
                        }
                    }

                    // Pronunciation Box (Soft Mint)
                    if (item.transliteration.isNotBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "উচ্চারণ",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B5E20)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.transliteration,
                                    fontSize = bengaliFontSize.sp,
                                    color = Color(0xFF1B5E20),
                                    lineHeight = (bengaliFontSize * 1.5).sp
                                )
                            }
                        }
                    }

                    // Meaning Box (Soft Amber)
                    if (item.translationBengali.isNotBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "অর্থ / অনুবাদ",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.translationBengali,
                                    fontSize = bengaliFontSize.sp,
                                    color = Color(0xFF78350F),
                                    lineHeight = (bengaliFontSize * 1.5).sp
                                )
                            }
                        }
                    }

                    // Explanation / Rules Box (if present)
                    if (item.explanation.isNotBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "ব্যাখ্যা ও মাসআলা",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.explanation,
                                    fontSize = (bengaliFontSize - 1).sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = ((bengaliFontSize - 1) * 1.5).sp
                                )
                            }
                        }
                    }

                    // Procedure steps (if present)
                    if (item.procedureSteps.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E8FF))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "পদ্ধতি ও ধাপসমূহ",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6B21A8)
                                )
                                item.procedureSteps.forEachIndexed { idx, step ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(text = "${idx + 1}.", fontWeight = FontWeight.Bold, color = Color(0xFF7E22CE), fontSize = 12.sp)
                                        Text(text = step, color = Color(0xFF581C87), fontSize = 13.sp, lineHeight = 19.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Virtues & Benefits (Soft Sky Blue)
                    if (item.virtues.isNotBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "ফজিলত ও আমল",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D4ED8)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.virtues,
                                    fontSize = (bengaliFontSize - 1).sp,
                                    color = Color(0xFF1E3A8A),
                                    lineHeight = ((bengaliFontSize - 1) * 1.5).sp
                                )
                            }
                        }
                    }

                    // Reference Footer
                    if (item.reference.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "দলিল: ${item.reference}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
