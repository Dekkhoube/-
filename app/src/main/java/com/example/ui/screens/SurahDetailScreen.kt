package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.data.model.Ayah
import com.example.data.model.ReciterInfo
import com.example.data.model.Surah
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahDetailScreen(
    surah: Surah,
    ayahs: List<Ayah>,
    arabicFontSize: Float,
    bengaliFontSize: Float,
    currentPlayingAyahNumber: Int = 0,
    currentPlayingSurahNumber: Int = 0,
    isAudioPlaying: Boolean = false,
    selectedReciter: ReciterInfo,
    recitersList: List<ReciterInfo>,
    isContinuousPlayback: Boolean = true,
    onSelectReciter: (ReciterInfo) -> Unit,
    onToggleContinuous: () -> Unit,
    onBack: () -> Unit,
    onPlayAyah: (Ayah) -> Unit,
    onPlayFullSurah: () -> Unit = {},
    onBookmarkAyah: (Ayah) -> Unit,
    onReportAyah: (Ayah) -> Unit,
    onSwitchToMushafPage: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val listState = rememberLazyListState()
    var showReciterDialog by remember { mutableStateOf(false) }

    // Auto-scroll to currently playing ayah during recitation
    LaunchedEffect(currentPlayingAyahNumber) {
        if (currentPlayingSurahNumber == surah.number && currentPlayingAyahNumber > 0) {
            val targetIndex = ayahs.indexOfFirst { it.numberInSurah == currentPlayingAyahNumber }
            if (targetIndex >= 0) {
                // +2 offset for top Bismillah and Recitation action bar items
                val scrollIndex = if (surah.number == 9) targetIndex + 1 else targetIndex + 2
                listState.animateScrollToItem(scrollIndex)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = surah.nameBengali,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${surah.revelationType} • ${surah.totalVerses} আয়াত • পারা ${surah.startingJuz}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("surah_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Switch to 15-line Hafezi Mushaf Page Mode
                    IconButton(
                        onClick = {
                            val targetPage = ayahs.firstOrNull()?.page ?: 1
                            onSwitchToMushafPage(targetPage)
                        },
                        modifier = Modifier.testTag("switch_to_mushaf_page_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "হাফেজী পৃষ্ঠা ভিউ",
                            tint = GoldAccent
                        )
                    }

                    // Reciter Selector Button
                    IconButton(
                        onClick = { showReciterDialog = true },
                        modifier = Modifier.testTag("select_reciter_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = "Select Reciter",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = surah.nameArabic,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("surah_detail_content"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Surah Header Calligraphy Banner (except Surah 9 At-Tawbah)
            if (surah.number != 9) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Emerald800)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Emerald900, Emerald800)
                                    )
                                )
                                .padding(18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                    color = GoldLight,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "পরম করুণাময় অতি দয়ালু আল্লাহর নামে শুরু করছি",
                                    color = Color.White.copy(alpha = 0.85f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            // Recitation Controls & Voice Selector Bar
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Voice Selector Chip
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                                modifier = Modifier.clickable { showReciterDialog = true }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RecordVoiceOver,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = selectedReciter.nameBengali,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Full Surah Recitation Button
                            Button(
                                onClick = onPlayFullSurah,
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                val isThisSurahPlaying = isAudioPlaying && currentPlayingSurahNumber == surah.number
                                Icon(
                                    imageVector = if (isThisSurahPlaying) Icons.Default.GraphicEq else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isThisSurahPlaying) "চলমান" else "সম্পূর্ণ সূরা",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Continuous Playback Toggle Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (isContinuousPlayback) Icons.Default.RepeatOne else Icons.Default.PlayDisabled,
                                    contentDescription = null,
                                    tint = if (isContinuousPlayback) Emerald700 else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "ধারাবাহিক আয়াত তিলাওয়াত (Auto-advance)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Switch(
                                checked = isContinuousPlayback,
                                onCheckedChange = { onToggleContinuous() },
                                modifier = Modifier.height(24.dp)
                            )
                        }
                    }
                }
            }

            // Ayahs List with Synchronised Real-Time Highlighting
            itemsIndexed(ayahs, key = { _, item -> item.numberInQuran }) { _, ayah ->
                val isCurrentAyah = (currentPlayingSurahNumber == ayah.surahNumber && currentPlayingAyahNumber == ayah.numberInSurah)

                AyahCard(
                    ayah = ayah,
                    surahNameBengali = surah.nameBengali,
                    arabicFontSize = arabicFontSize,
                    bengaliFontSize = bengaliFontSize,
                    isCurrentPlaying = isCurrentAyah,
                    isPlaying = isAudioPlaying && isCurrentAyah,
                    reciterName = selectedReciter.nameBengali,
                    onPlay = { onPlayAyah(ayah) },
                    onBookmark = { onBookmarkAyah(ayah) },
                    onReport = { onReportAyah(ayah) },
                    onShare = {
                        val shareText = "${ayah.textArabic}\n\n${ayah.textBengali}\n\n— [সূরা ${surah.nameBengali}: আয়াত ${ayah.numberInSurah} - নূর ইসলামিক অ্যাপ]"
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "আয়াত শেয়ার করুন"))
                    }
                )
            }
        }
    }

    // Reciter Voice Selection Dialog
    if (showReciterDialog) {
        AlertDialog(
            onDismissRequest = { showReciterDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RecordVoiceOver,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "ক্বারী নির্বাচন করুন (ভয়েস)",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(recitersList.size) { index ->
                        val reciter = recitersList[index]
                        val isSelected = reciter.id == selectedReciter.id

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectReciter(reciter)
                                    showReciterDialog = false
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            ),
                            border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = reciter.nameBengali,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = reciter.nameEnglish,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showReciterDialog = false }) {
                    Text("ঠিক আছে", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun AyahCard(
    ayah: Ayah,
    surahNameBengali: String,
    arabicFontSize: Float,
    bengaliFontSize: Float,
    isCurrentPlaying: Boolean = false,
    isPlaying: Boolean = false,
    reciterName: String = "",
    onPlay: () -> Unit,
    onBookmark: () -> Unit,
    onReport: () -> Unit,
    onShare: () -> Unit
) {
    val cardBorder = if (isCurrentPlaying) {
        BorderStroke(2.dp, if (isPlaying) GoldAccent else Emerald700)
    } else {
        BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    }

    val cardBackground = if (isCurrentPlaying) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ayah_card_${ayah.numberInSurah}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        border = cardBorder,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrentPlaying) 4.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Active recitation indicator ribbon when currently highlighted
            if (isCurrentPlaying) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPlaying) Emerald800 else MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.GraphicEq else Icons.Default.PauseCircle,
                                contentDescription = null,
                                tint = GoldLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isPlaying) "এখন তিলাওয়াত হচ্ছে: আয়াত ${ayah.numberInSurah}" else "বিরতি দেওয়া হয়েছে",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Text(
                            text = reciterName,
                            color = GoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Header Row: Ayah badge, Page info and Action icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCurrentPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${ayah.numberInSurah}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrentPlaying) Color.White else MaterialTheme.colorScheme.primary
                        )
                    }

                    Column {
                        Text(
                            text = "আয়াত ${ayah.numberInSurah} • পারা ${ayah.juzNumber}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "পৃষ্ঠা ${ayah.page} (হাফেজী কুরআন)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Play/Pause button for this Ayah
                    IconButton(onClick = onPlay, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                            contentDescription = if (isPlaying) "Pause Audio" else "Play Audio",
                            tint = if (isCurrentPlaying) GoldDark else MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onBookmark, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.BookmarkAdd,
                            contentDescription = "Bookmark",
                            tint = GoldAccent
                        )
                    }
                    IconButton(onClick = onShare, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onReport, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = "Report Error",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Arabic Text (with highlight color if active)
            Text(
                text = ayah.textArabic,
                fontSize = arabicFontSize.sp,
                lineHeight = (arabicFontSize * 1.6).sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isCurrentPlaying) Emerald900 else MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Transliteration (Pronunciation)
            if (ayah.transliteration.isNotBlank()) {
                Text(
                    text = "উচ্চারণ: ${ayah.transliteration}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Bengali Translation
            Text(
                text = ayah.textBengali,
                fontSize = bengaliFontSize.sp,
                lineHeight = (bengaliFontSize * 1.5).sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isCurrentPlaying) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}
