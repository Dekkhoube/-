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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ShortSurah
import com.example.data.repository.IslamicAmalRepository
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight

/**
 * 26 Short Surahs List Screen (২৬টি ছোট সূরা)
 * Visually matching user reference screenshot (Screenshot_20261107_155628_9942678e.jpg & 155636_d1879cc5.jpg)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortSurahListScreen(
    onSelectSurah: (ShortSurah) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val surahs = remember { IslamicAmalRepository.shortSurahs }
    val filteredSurahs = remember(searchQuery) {
        if (searchQuery.isBlank()) surahs
        else surahs.filter {
            it.nameBengali.contains(searchQuery, ignoreCase = true) ||
            it.nameArabic.contains(searchQuery, ignoreCase = true) ||
            it.surahNumber.toString() == searchQuery.trim()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "২৬ ছোট সূরা",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "উচ্চারণ, অর্থ, অডিও ও তাফসীর",
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
        modifier = modifier.fillMaxSize().testTag("short_surah_list_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Ornament Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2A0808)), // Deep rich maroon matching screenshot
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
                                text = "২৬ ছোট সূরা",
                                color = GoldLight,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "উচ্চারণ, অর্থ, অডিও ও তাফসীর",
                                color = Color.White.copy(alpha = 0.85f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("সূরা অনুসন্ধান (যেমন: ফাতিহা, ইখলাস, ১০৮)...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Surah Cards matching reference screenshots
            items(filteredSurahs, key = { it.id }) { surah ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectSurah(surah) }
                        .testTag("surah_card_${surah.surahNumber}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2D0B0B)), // Dark maroon card matching reference screenshot
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${surah.id}। ${surah.nameBengali}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = surah.nameArabic,
                                    color = GoldLight,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "অর্থ: ${surah.nameMeaning}",
                                color = Color.White.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Surface(
                                shape = RoundedCornerShape(50.dp),
                                color = Color.White.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "(${surah.revelationType}) মোট আয়াত ${surah.totalVerses}",
                                    color = GoldAccent,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Short Surah Detail Screen with Audio Player, Harakat Arabic, Transliteration, Translation & Tafsir
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortSurahDetailScreen(
    surah: ShortSurah,
    onBack: () -> Unit,
    onPlayAudio: (url: String, title: String) -> Unit = { _, _ -> },
    isAudioPlaying: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var arabicFontSize by remember { mutableStateOf(24f) }
    var bengaliFontSize by remember { mutableStateOf(16f) }
    var isTafsirExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = surah.nameBengali,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${surah.nameArabic} • ${surah.revelationType} • ${surah.totalVerses} আয়াত",
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
                actions = {
                    // Font Size Adjusters
                    IconButton(onClick = {
                        arabicFontSize = (arabicFontSize - 2f).coerceAtLeast(18f)
                        bengaliFontSize = (bengaliFontSize - 1f).coerceAtLeast(13f)
                    }) {
                        Icon(Icons.Default.TextDecrease, contentDescription = "ছোট করুন")
                    }
                    IconButton(onClick = {
                        arabicFontSize = (arabicFontSize + 2f).coerceAtMost(36f)
                        bengaliFontSize = (bengaliFontSize + 1f).coerceAtMost(24f)
                    }) {
                        Icon(Icons.Default.TextIncrease, contentDescription = "বড় করুন")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier.fillMaxSize().testTag("short_surah_detail_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Audio Player Bar matching reference screenshot (Screenshot_20261107_155550_65621b8a.jpg)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E0A0A)) // Matching reference audio player
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "অডিও (Audio Recitation)",
                                color = GoldLight,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )

                            FilledIconButton(
                                onClick = {
                                    onPlayAudio(surah.fullSurahAudioUrl, surah.nameBengali)
                                },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = GoldAccent)
                            ) {
                                Icon(
                                    imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isAudioPlaying) "Pause" else "Play",
                                    tint = Color.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "ক্বারী মিশারি রাশিদ আল-আফাসী • সম্পূর্ণ সূরা তেলওয়াত",
                            color = Color.White.copy(alpha = 0.75f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Bismillah Header (except Surah Tawbah)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            fontSize = (arabicFontSize - 2f).sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "পরম করুণাময় অসীম দয়ালু আল্লাহর নামে শুরু করছি",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Tafsir & Virtues Card
            if (surah.virtues.isNotBlank() || surah.tafsirOverview.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isTafsirExpanded = !isTafsirExpanded },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "সূরার ফজিলত ও তাৎপর্য",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF92400E)
                                    )
                                }
                                Icon(
                                    imageVector = if (isTafsirExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = Color(0xFF92400E)
                                )
                            }

                            AnimatedVisibility(visible = isTafsirExpanded) {
                                Column(modifier = Modifier.padding(top = 8.dp)) {
                                    if (surah.virtues.isNotBlank()) {
                                        Text(
                                            text = "✨ ফজিলত: ${surah.virtues}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF78350F),
                                            lineHeight = 20.sp
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }
                                    if (surah.tafsirOverview.isNotBlank()) {
                                        Text(
                                            text = "📖 সংক্ষিপ্ত তাফসীর: ${surah.tafsirOverview}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF78350F),
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Ayah List with Structured Layout matching reference screenshot:
            // Arabic text -> Bengali pronunciation (green) -> Bengali translation (amber)
            items(surah.ayahs, key = { it.ayahNumber }) { ayah ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Ayah Badge Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50.dp),
                                color = Emerald700.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "আয়াত নং ${ayah.ayahNumber}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald700,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Row {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Ayah", "${ayah.textArabic}\n${ayah.transliteration}\n${ayah.translationBengali}")
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "আয়াত কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(
                                    onClick = {
                                        val shareText = "${surah.nameBengali} - আয়াত ${ayah.ayahNumber}:\n\n${ayah.textArabic}\n\nউচ্চারণ: ${ayah.transliteration}\n\nঅনুবাদ: ${ayah.translationBengali}\n\n— নূর ইসলামিক অ্যাপ"
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "আয়াত শেয়ার করুন"))
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 1. Arabic Text with full Tashkeel
                        Text(
                            text = ayah.textArabic,
                            fontSize = arabicFontSize.sp,
                            lineHeight = (arabicFontSize * 1.6f).sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Right,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 2. Bengali Pronunciation (Light mint green box matching reference)
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
                                    text = ayah.transliteration,
                                    fontSize = bengaliFontSize.sp,
                                    lineHeight = (bengaliFontSize * 1.4f).sp,
                                    color = Color(0xFF1B5E20),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 3. Bengali Translation (Soft amber box matching reference)
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
                                    text = ayah.translationBengali,
                                    fontSize = bengaliFontSize.sp,
                                    lineHeight = (bengaliFontSize * 1.45f).sp,
                                    color = Color(0xFF451A03)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
