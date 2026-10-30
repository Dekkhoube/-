package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ayah
import com.example.data.model.ReciterInfo
import com.example.data.repository.QuranRepository
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.GoldAccent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HafeziQuranPageView(
    initialPage: Int = 1,
    currentPlayingSurah: Int = 0,
    currentPlayingAyah: Int = 0,
    isAudioPlaying: Boolean = false,
    selectedReciter: ReciterInfo = QuranRepository.reciters.first(),
    onPlayAyah: (Ayah) -> Unit = {},
    onBookmarkPage: (Int) -> Unit = {},
    onSwitchToListView: () -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val totalPages = 604
    val initialPageIndex = (initialPage - 1).coerceIn(0, totalPages - 1)
    val pagerState = rememberPagerState(initialPage = initialPageIndex, pageCount = { totalPages })

    var isNightMode by remember { mutableStateOf(false) }
    var showJumpDialog by remember { mutableStateOf(false) }
    var jumpInputText by remember { mutableStateOf("") }
    var selectedAyahForAction by remember { mutableStateOf<Ayah?>(null) }

    // Auto-turn page when audio advances to an ayah on another page
    LaunchedEffect(currentPlayingSurah, currentPlayingAyah) {
        if (currentPlayingSurah > 0 && currentPlayingAyah > 0) {
            val pageForAyah = QuranRepository.getPageForAyah(currentPlayingSurah, currentPlayingAyah)
            if (pageForAyah in 1..totalPages && pagerState.currentPage != pageForAyah - 1) {
                pagerState.animateScrollToPage(pageForAyah - 1)
            }
        }
    }

    val pageBgColor = if (isNightMode) Color(0xFF121212) else Color(0xFFFFFDF8)
    val pageTextColor = if (isNightMode) Color(0xFFE2E8F0) else Color(0xFF1E293B)
    val frameBorderColor = if (isNightMode) Color(0xFF475569) else Color(0xFFC5A059)
    val headerFooterBg = if (isNightMode) Color(0xFF1E293B) else Color(0xFFFBF8F0)

    val currentPageNum = pagerState.currentPage + 1
    val currentAyahs = remember(currentPageNum) {
        QuranRepository.getAyahsForPage(currentPageNum)
    }

    val firstAyah = currentAyahs.firstOrNull()
    val firstSurahNum = firstAyah?.surahNumber ?: 1
    val surahInfo = remember(firstSurahNum) { QuranRepository.getSurah(firstSurahNum) }
    val juzNum = firstAyah?.juzNumber ?: ((currentPageNum - 1) / 20 + 1)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "পৃষ্ঠা #$currentPageNum, পারা #$juzNum",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isNightMode) Color.White else Emerald800
                        )
                        Text(
                            text = "সূরা ${surahInfo?.nameBengali ?: ""} (${surahInfo?.nameArabic ?: ""})",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isNightMode) Color.LightGray else Color(0xFF64748B)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("hafezi_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ফিরে যান"
                        )
                    }
                },
                actions = {
                    // Switch to List View
                    IconButton(
                        onClick = onSwitchToListView,
                        modifier = Modifier.testTag("hafezi_switch_to_list")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewList,
                            contentDescription = "তালিকা ভিউ",
                            tint = if (isNightMode) Color(0xFFE2E8F0) else Emerald700
                        )
                    }

                    // Night Mode Toggle (Moon / Sun)
                    IconButton(
                        onClick = { isNightMode = !isNightMode },
                        modifier = Modifier.testTag("hafezi_night_mode_toggle")
                    ) {
                        Icon(
                            imageVector = if (isNightMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "নাইট মোড পরিবর্তন",
                            tint = if (isNightMode) GoldAccent else Color(0xFF475569)
                        )
                    }

                    // Jump to Page / Juz / Surah
                    IconButton(
                        onClick = {
                            jumpInputText = "$currentPageNum"
                            showJumpDialog = true
                        },
                        modifier = Modifier.testTag("hafezi_jump_page_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FindInPage,
                            contentDescription = "পৃষ্ঠা নির্বাচন",
                            tint = if (isNightMode) Color(0xFFE2E8F0) else Emerald700
                        )
                    }

                    // Bookmark Page
                    IconButton(
                        onClick = { onBookmarkPage(currentPageNum) },
                        modifier = Modifier.testTag("hafezi_bookmark_page_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = "পৃষ্ঠা বুকমার্ক",
                            tint = GoldAccent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isNightMode) Color(0xFF1E293B) else Color(0xFFF8FAF9)
                )
            )
        },
        containerColor = pageBgColor,
        modifier = modifier.testTag("hafezi_quran_page_view")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Horizontal Pager for smooth side scrolling (page turning)
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                val pageNum = pageIndex + 1
                val ayahsOnPage = remember(pageNum) {
                    QuranRepository.getAyahsForPage(pageNum)
                }

                HafeziMushafPageContent(
                    pageNumber = pageNum,
                    ayahs = ayahsOnPage,
                    isNightMode = isNightMode,
                    pageBgColor = pageBgColor,
                    textColor = pageTextColor,
                    frameBorderColor = frameBorderColor,
                    headerFooterBg = headerFooterBg,
                    currentPlayingSurah = currentPlayingSurah,
                    currentPlayingAyah = currentPlayingAyah,
                    isAudioPlaying = isAudioPlaying,
                    onAyahClick = { ayah ->
                        selectedAyahForAction = ayah
                    }
                )
            }

            // Bottom Navigation Footer Bar (Quick Page Slider & Controls)
            Surface(
                color = if (isNightMode) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Previous Page button
                    FilledTonalButton(
                        onClick = {
                            if (pagerState.currentPage > 0) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        },
                        enabled = pagerState.currentPage > 0,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("পূর্ববর্তী", fontSize = 12.sp)
                    }

                    // Page progress indicator
                    Text(
                        text = "পৃষ্ঠা $currentPageNum / $totalPages",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isNightMode) Color.White else Color(0xFF334155)
                    )

                    // Next Page button
                    FilledTonalButton(
                        onClick = {
                            if (pagerState.currentPage < totalPages - 1) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        enabled = pagerState.currentPage < totalPages - 1,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("পরবর্তী", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }

    // Quick Jump Dialog
    if (showJumpDialog) {
        AlertDialog(
            onDismissRequest = { showJumpDialog = false },
            title = {
                Text("পৃষ্ঠা নির্বাচন করুন", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "১ থেকে ৬০৪ পর্যন্ত যেকোনো পৃষ্ঠায় যেতে নম্বর লিখুন:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = jumpInputText,
                        onValueChange = { jumpInputText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("পৃষ্ঠা নম্বর (১-৬০৪)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = jumpInputText.toIntOrNull()
                        if (target != null && target in 1..totalPages) {
                            coroutineScope.launch {
                                pagerState.scrollToPage(target - 1)
                            }
                        }
                        showJumpDialog = false
                    }
                ) {
                    Text("যাও")
                }
            },
            dismissButton = {
                TextButton(onClick = { showJumpDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Ayah Action Dialog (when user taps an Ayah in Page View)
    selectedAyahForAction?.let { ayah ->
        val surah = QuranRepository.getSurah(ayah.surahNumber)
        AlertDialog(
            onDismissRequest = { selectedAyahForAction = null },
            title = {
                Text(
                    text = "সূরা ${surah?.nameBengali ?: ""} - আয়াত ${ayah.numberInSurah}",
                    fontWeight = FontWeight.Bold,
                    color = Emerald800
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = ayah.textArabic,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Right,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        lineHeight = 36.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    HorizontalDivider()
                    Text(
                        text = "বাংলা অর্থ:",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall,
                        color = Emerald700
                    )
                    Text(
                        text = ayah.textBengali,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onPlayAyah(ayah)
                        selectedAyahForAction = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("তেলাওয়াত শুনুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedAyahForAction = null }) {
                    Text("বন্ধ করুন")
                }
            }
        )
    }
}

/**
 * Renders the authentic 15-Line Hafezi Mushaf page format with decorative borders.
 */
@Composable
private fun HafeziMushafPageContent(
    pageNumber: Int,
    ayahs: List<Ayah>,
    isNightMode: Boolean,
    pageBgColor: Color,
    textColor: Color,
    frameBorderColor: Color,
    headerFooterBg: Color,
    currentPlayingSurah: Int,
    currentPlayingAyah: Int,
    isAudioPlaying: Boolean,
    onAyahClick: (Ayah) -> Unit
) {
    val firstAyah = ayahs.firstOrNull()
    val firstSurahNum = firstAyah?.surahNumber ?: 1
    val surahInfo = QuranRepository.getSurah(firstSurahNum)
    val juzNum = firstAyah?.juzNumber ?: ((pageNumber - 1) / 20 + 1)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .background(pageBgColor, RoundedCornerShape(8.dp))
            .border(2.5.dp, frameBorderColor, RoundedCornerShape(8.dp))
            .padding(3.dp)
            .border(1.dp, frameBorderColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Authentic Top Mushaf Header (Juz on right, Surah on left)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerFooterBg, RoundedCornerShape(4.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الجزء $juzNum (পারা $juzNum)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = frameBorderColor
                )
                Text(
                    text = "سورة ${surahInfo?.nameArabic ?: ""}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = frameBorderColor
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Main 15-line Quranic text block
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // If this page contains Ayah 1 of a Surah, render the classical ornate Surah header banner
                val surahHeaderAyah = ayahs.find { it.numberInSurah == 1 }
                if (surahHeaderAyah != null) {
                    val s = QuranRepository.getSurah(surahHeaderAyah.surahNumber)
                    SurahOrnateHeaderBanner(surah = s, isNightMode = isNightMode)
                }

                // Render Ayahs grouped or formatted cleanly
                ayahs.forEach { ayah ->
                    val isPlayingThisAyah = isAudioPlaying &&
                            currentPlayingSurah == ayah.surahNumber &&
                            currentPlayingAyah == ayah.numberInSurah

                    val ayahBgColor by animateColorAsState(
                        targetValue = if (isPlayingThisAyah) {
                            if (isNightMode) Color(0xFF2E382A) else Color(0xFFFEF3C7)
                        } else Color.Transparent,
                        label = "ayah_bg_color"
                    )

                    val ayahBorderColor = if (isPlayingThisAyah) {
                        if (isNightMode) Color(0xFF86EFAC) else Color(0xFFD97706)
                    } else Color.Transparent

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(ayahBgColor)
                            .border(1.dp, ayahBorderColor, RoundedCornerShape(6.dp))
                            .clickable { onAyahClick(ayah) }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Highlighting playing indicator
                            if (isPlayingThisAyah) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(
                                            if (isNightMode) Color(0xFF166534) else Color(0xFFD97706),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "তেলাওয়াত",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                            }

                            // Ayah Arabic text with ornate End Ayah circle (۝)
                            Text(
                                text = buildAnnotatedString {
                                    append(ayah.textArabic)
                                    append(" ")
                                    withStyle(
                                        SpanStyle(
                                            color = if (isNightMode) Color(0xFFFDE047) else Color(0xFFB45309),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    ) {
                                        append("۝${toArabicNumerals(ayah.numberInSurah)}")
                                    }
                                },
                                color = if (isPlayingThisAyah) {
                                    if (isNightMode) Color(0xFF86EFAC) else Color(0xFF92400E)
                                } else textColor,
                                fontSize = 19.sp,
                                lineHeight = 34.sp,
                                fontWeight = if (isPlayingThisAyah) FontWeight.Bold else FontWeight.Medium,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Authentic Page Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerFooterBg, RoundedCornerShape(4.dp))
                    .padding(horizontal = 10.dp, vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "মঞ্জিল ${((juzNum - 1) / 4 + 1).coerceIn(1, 7)}",
                    fontSize = 10.sp,
                    color = frameBorderColor
                )
                Text(
                    text = "صفحة $pageNumber (পৃষ্ঠা $pageNumber)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = frameBorderColor
                )
            }
        }
    }
}

@Composable
private fun SurahOrnateHeaderBanner(
    surah: com.example.data.model.Surah?,
    isNightMode: Boolean
) {
    if (surah == null) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ornate Surah Title Banner
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isNightMode) Color(0xFF1E293B) else Color(0xFFFEF3C7),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if (isNightMode) Color(0xFF475569) else Color(0xFFD97706)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "সূরা ${surah.nameBengali}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isNightMode) Color.White else Color(0xFF92400E)
                )
                Text(
                    text = "سورة ${surah.nameArabic}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (isNightMode) Color.White else Color(0xFF92400E)
                )
                Text(
                    text = "${surah.revelationType} • ${surah.totalVerses} আয়াত",
                    fontSize = 11.sp,
                    color = if (isNightMode) Color.LightGray else Color(0xFF78350F)
                )
            }
        }

        // Bismillah (except Surah At-Tawbah)
        if (surah.number != 9) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = if (isNightMode) Color(0xFFE2E8F0) else Color(0xFF0F172A),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }
    }
}

private fun toArabicNumerals(number: Int): String {
    val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    val sb = StringBuilder()
    for (ch in number.toString()) {
        if (ch in '0'..'9') {
            sb.append(arabicDigits[ch - '0'])
        } else {
            sb.append(ch)
        }
    }
    return sb.toString()
}
