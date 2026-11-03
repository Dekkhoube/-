package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LugatWord
import com.example.data.model.RisaleBook
import com.example.data.model.RisaleChapter
import com.example.data.model.RisaleSection
import com.example.data.repository.RisaleRepository
import com.example.ui.theme.GoldAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RisaleReaderScreen(
    book: RisaleBook,
    initialChapterId: String? = null,
    onBack: () -> Unit,
    onOpenLugatDictionary: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val clipboard = LocalClipboardManager.current
    val chapters = remember(book.id) { RisaleRepository.getChapters(book.id) }
    var currentChapterIndex by remember {
        mutableIntStateOf(
            if (initialChapterId != null) {
                val idx = chapters.indexOfFirst { it.id == initialChapterId }
                if (idx != -1) idx else 0
            } else 0
        )
    }

    val currentChapter = chapters.getOrNull(currentChapterIndex) ?: chapters.firstOrNull()

    var selectedLanguage by remember { mutableStateOf("বাংলা") } // "বাংলা", "English", "Türkçe"
    var fontSizeMultiplier by remember { mutableFloatStateOf(1.0f) }
    var showLugatSheet by remember { mutableStateOf(false) }
    var selectedLugatWord by remember { mutableStateOf<LugatWord?>(null) }
    var lugatSearchQuery by remember { mutableStateOf("") }
    var isBookmarked by remember { mutableStateOf(false) }

    // Update reading progress on chapter open
    LaunchedEffect(currentChapter?.id) {
        if (currentChapter != null) {
            RisaleRepository.updateReadingProgress(book.id, currentChapter.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = currentChapter?.titleBengali ?: book.titleBengali,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1
                        )
                        Text(
                            text = "${book.titleBengali} • অধ্যায় ${currentChapter?.chapterNumber ?: 1}/${chapters.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_reader_back")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "পিছনে যান")
                    }
                },
                actions = {
                    // Font size adjustments
                    IconButton(
                        onClick = {
                            if (fontSizeMultiplier > 0.8f) fontSizeMultiplier -= 0.1f
                        },
                        modifier = Modifier.testTag("btn_font_smaller")
                    ) {
                        Text("A-", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    IconButton(
                        onClick = {
                            if (fontSizeMultiplier < 1.6f) fontSizeMultiplier += 0.1f
                        },
                        modifier = Modifier.testTag("btn_font_larger")
                    ) {
                        Text("A+", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    // Lugat Quick Lookup Button
                    IconButton(
                        onClick = { showLugatSheet = true },
                        modifier = Modifier.testTag("btn_reader_lugat")
                    ) {
                        Icon(imageVector = Icons.Default.Spellcheck, contentDescription = "লূগাত অভিধান")
                    }

                    // Bookmark
                    IconButton(
                        onClick = { isBookmarked = !isBookmarked },
                        modifier = Modifier.testTag("btn_reader_bookmark")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "বুকমার্ক",
                            tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // Chapter Navigation Bar
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = {
                            if (currentChapterIndex > 0) currentChapterIndex--
                        },
                        enabled = currentChapterIndex > 0,
                        modifier = Modifier.testTag("btn_prev_chapter")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("পূর্ববর্তী", fontSize = 12.sp)
                    }

                    // Language Selector Segmented Buttons
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
                            .padding(2.dp)
                    ) {
                        listOf("বাংলা", "English", "Türkçe").forEach { lang ->
                            val isSelected = selectedLanguage == lang
                            Surface(
                                onClick = { selectedLanguage = lang },
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Text(
                                    text = lang,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    FilledTonalButton(
                        onClick = {
                            if (currentChapterIndex < chapters.size - 1) currentChapterIndex++
                        },
                        enabled = currentChapterIndex < chapters.size - 1,
                        modifier = Modifier.testTag("btn_next_chapter")
                    ) {
                        Text("পরবর্তী", fontSize = 12.sp)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (currentChapter != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item {
                    Spacer(Modifier.height(8.dp))
                    // Chapter Heading Header Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "অধ্যায় ${currentChapter.chapterNumber}: ${currentChapter.titleOriginal}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = currentChapter.titleBengali,
                                fontWeight = FontWeight.Bold,
                                fontSize = (18 * fontSizeMultiplier).sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (currentChapter.introductionBengali.isNotBlank()) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = currentChapter.introductionBengali,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }

                // Render sections
                items(currentChapter.sections, key = { it.id }) { section ->
                    ReaderSectionView(
                        section = section,
                        selectedLanguage = selectedLanguage,
                        fontSizeMultiplier = fontSizeMultiplier,
                        onWordClick = { word ->
                            val match = RisaleRepository.lugatWords.find {
                                it.term.equals(word, ignoreCase = true) ||
                                it.pronunciationBengali.contains(word)
                            }
                            if (match != null) {
                                selectedLugatWord = match
                            }
                        }
                    )
                }

                item {
                    Spacer(Modifier.height(32.dp))
                }
            }
        }

        // Quick Lügat Dictionary Bottom Sheet
        if (showLugatSheet) {
            ModalBottomSheet(
                onDismissRequest = { showLugatSheet = false },
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "লূগাত (ইসলামী ও উসমানীয় শব্দকোষ)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "রিসালে-ই নূর ও ইসলামী ধর্মতত্ত্বের গুরুত্বপূর্ণ পরিভাষাসমূহ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = lugatSearchQuery,
                        onValueChange = { lugatSearchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("শব্দ অনুসন্ধান (যেমন: তাওহিদ, হাশর, ইখলাস)...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    val searchResults = remember(lugatSearchQuery) {
                        RisaleRepository.searchLugat(lugatSearchQuery)
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 350.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(searchResults, key = { it.id }) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${item.term} (${item.pronunciationBengali})",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Surface(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = item.originLanguage,
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = item.meaningBengali,
                                        style = MaterialTheme.typography.bodyMedium,
                                        lineHeight = 20.sp
                                    )
                                    if (item.theologicalContextBengali.isNotBlank()) {
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = "তাৎপর্য: ${item.theologicalContextBengali}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun ReaderSectionView(
    section: RisaleSection,
    selectedLanguage: String,
    fontSizeMultiplier: Float,
    onWordClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (section.subHeadingBengali.isNotBlank()) {
            Text(
                text = when (selectedLanguage) {
                    "English" -> section.subHeadingEnglish.ifEmpty { section.subHeadingBengali }
                    else -> section.subHeadingBengali
                },
                fontWeight = FontWeight.Bold,
                fontSize = (16 * fontSizeMultiplier).sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Quranic Verse Callout Box (if available)
        if (!section.quranicReference.isNullOrBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = GoldAccent.copy(alpha = 0.12f)
                ),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "কুরআনিক দলিল",
                        tint = GoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = section.quranicReference,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = (14 * fontSizeMultiplier).sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = (20 * fontSizeMultiplier).sp
                    )
                }
            }
        }

        // Hadith Callout Box (if available)
        if (!section.hadithReference.isNullOrBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoStories,
                        contentDescription = "হাদিস দলিল",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "হাদিস: ${section.hadithReference}",
                        fontSize = (13 * fontSizeMultiplier).sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = (19 * fontSizeMultiplier).sp
                    )
                }
            }
        }

        // Main Text Content
        val bodyText = when (selectedLanguage) {
            "English" -> section.textEnglish.ifEmpty { section.textBengali }
            "Türkçe" -> section.textOriginal.ifEmpty { section.textBengali }
            else -> section.textBengali
        }

        Text(
            text = bodyText,
            fontSize = (15 * fontSizeMultiplier).sp,
            lineHeight = (26 * fontSizeMultiplier).sp,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    }
}
