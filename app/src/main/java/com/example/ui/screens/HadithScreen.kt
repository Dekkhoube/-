package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.data.model.Hadith
import com.example.data.repository.HadithAndDuaRepository
import com.example.data.repository.HadithDatabase
import com.example.ui.theme.Emerald700
import com.example.ui.theme.GoldAccent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithScreen(
    selectedCollectionId: String,
    onSelectCollection: (String) -> Unit,
    onBookmarkHadith: (Hadith) -> Unit,
    onReportHadith: (Hadith) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Dynamic online fetched hadiths
    val onlineFetchedHadiths = remember { mutableStateListOf<Hadith>() }
    var isFetchingOnline by remember { mutableStateOf(false) }
    var onlineFetchMessage by remember { mutableStateOf<String?>(null) }

    // Popular Hadith numbers for quick 1-tap lookup
    val quickHadithNumbers = remember {
        listOf(
            "১" to 1,
            "২" to 2,
            "৩" to 3,
            "৮" to 8,
            "১৩" to 13,
            "২২৩" to 223,
            "৫২৮" to 528,
            "১৯৮৭" to 1987,
            "২৫১৬" to 2516,
            "৫০৫৮" to 5058,
            "৫৯৭১" to 5971,
            "৬০৯৪" to 6094,
            "৬৪১৬" to 6416,
            "৬৪৯১" to 6491
        )
    }

    // Number extracted from current query if any
    val queriedNumber = remember(searchQuery) {
        HadithDatabase.extractHadithNumber(searchQuery)
    }

    // Combined Hadith list (Database matches + online fetched)
    val displayedHadiths = remember(selectedCollectionId, searchQuery, onlineFetchedHadiths.size) {
        val baseMatches = HadithDatabase.searchHadith(searchQuery, selectedCollectionId)
        val relevantOnline = onlineFetchedHadiths.filter {
            (selectedCollectionId == "all" || it.collectionId == selectedCollectionId) &&
            (searchQuery.isBlank() || it.hadithNumber == queriedNumber || it.textBengali.contains(searchQuery, ignoreCase = true))
        }
        (relevantOnline + baseMatches).distinctBy { it.reference }
    }

    // Currently selected collection object
    val currentCollection = remember(selectedCollectionId) {
        HadithDatabase.collections.find { it.id == selectedCollectionId }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("hadith_screen")
    ) {
        // Search Bar with Hadith Number support
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                onlineFetchMessage = null
            },
            placeholder = { Text("হাদিস নম্বর (যেমন: ১, ১৩, ৫০৫৮) বা বিষয়...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = {
                        searchQuery = ""
                        onlineFetchMessage = null
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("hadith_search_bar"),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        // Quick Hadith Number Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "হাদিস নং:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 6.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(end = 8.dp)
            ) {
                items(quickHadithNumbers) { (label, number) ->
                    val isCurrent = queriedNumber == number
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.clickable {
                            searchQuery = number.toString()
                        }
                    ) {
                        Text(
                            text = "নং $label",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isCurrent) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Collection Filter Chips (Kutub al-Sittah)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // All collections chip
            item {
                val isAllSelected = selectedCollectionId == "all"
                FilterChip(
                    selected = isAllSelected,
                    onClick = { onSelectCollection("all") },
                    label = { Text("সকল হাদিস", fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }

            items(HadithDatabase.collections) { collection ->
                val isSelected = collection.id == selectedCollectionId
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectCollection(collection.id) },
                    label = { Text(collection.nameBengali, fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Search status banner when querying by number
        if (queriedNumber != null) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Emerald700.copy(alpha = 0.1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "🔍 হাদিস নম্বর ${HadithDatabase.formatToBengaliDigits(queriedNumber)} (ইংরেজি: $queriedNumber) অনুসন্ধান ফলাফল",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Emerald700
                    )
                    Text(
                        text = "${displayedHadiths.size}টি প্রাপ্ত",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Online lookup card if number was entered and not in bundled cache
        if (queriedNumber != null && displayedHadiths.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "হাদিস নং ${HadithDatabase.formatToBengaliDigits(queriedNumber)} লোকাল ক্যাশে নেই",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "সম্পূর্ণ সিহাহ সিত্তাহ লাইব্রেরি থেকে মূল আরবি ও অনুবাদসহ বের করতে নিচের বোতামটি চাপুন।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isFetchingOnline) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp), strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "লাইব্রেরি থেকে হাদিসটি সংগৃহীত হচ্ছে...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isFetchingOnline = true
                                    onlineFetchMessage = null
                                    val collectionName = currentCollection?.nameBengali ?: "সহীহ বুখারী"
                                    val fetched = HadithDatabase.fetchHadithFromOnlineLibrary(collectionName, queriedNumber)
                                    if (fetched != null) {
                                        onlineFetchedHadiths.add(0, fetched)
                                        onlineFetchMessage = "হাদিস নং ${HadithDatabase.formatToBengaliDigits(queriedNumber)} সফলভাবে যুক্ত হয়েছে!"
                                    } else {
                                        onlineFetchMessage = "দুঃখিত, এই মুহূর্তে অনলাইন লাইব্রেরি থেকে হাদিসটি লোড করা সম্ভব হয়নি।"
                                    }
                                    isFetchingOnline = false
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("হাদিস নং ${HadithDatabase.formatToBengaliDigits(queriedNumber)} অনলাইনে খুঁজুন")
                        }
                    }

                    if (onlineFetchMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = onlineFetchMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (onlineFetchMessage!!.contains("সফলভাবে")) Emerald700 else MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Hadith Items List
        if (displayedHadiths.isEmpty() && queriedNumber == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "কোনো হাদিস পাওয়া যায়নি",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "হাদিস নম্বর (যেমন: ১, ১৩, ৫০৫৮) বা বিষয় লিখে অনুসন্ধান করুন",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(displayedHadiths, key = { it.reference + "_" + it.id }) { hadith ->
                    HadithCard(
                        hadith = hadith,
                        onBookmark = { onBookmarkHadith(hadith) },
                        onReport = { onReportHadith(hadith) },
                        onShare = {
                            val shareText = "📖 ${hadith.reference}\n[${hadith.chapterBengali}]\n\n${hadith.textArabic}\n\nউচ্চারণ: ${hadith.transliteration}\n\nঅনুবাদ: ${hadith.textBengali}\n\nতাৎপর্য ও শিক্ষা:\n${hadith.explanationBengali}\n\n— নূর ইসলামিক (Noor Islamic) অ্যাপ"
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "হাদিস শেয়ার করুন"))
                        },
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("হাদিস", "${hadith.reference}\n${hadith.textArabic}\n${hadith.textBengali}")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "হাদিস ক্লিপবোর্ডে কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HadithCard(
    hadith: Hadith,
    onBookmark: () -> Unit,
    onReport: () -> Unit,
    onShare: () -> Unit,
    onCopy: () -> Unit
) {
    var isExplanationExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hadith_card_${hadith.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Prominent Hadith Number Pill & Grade Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Number & Reference Pill
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Emerald700
                    ) {
                        Text(
                            text = "হাদিস নং ${HadithDatabase.formatToBengaliDigits(hadith.hadithNumber)}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = hadith.reference,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Grade badge
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = Emerald700.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = hadith.grade,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Emerald700,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Chapter & Narrator
            Text(
                text = hadith.chapterBengali,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "বর্ণনাকারী: ${hadith.narratorBengali}",
                style = MaterialTheme.typography.bodySmall,
                color = GoldAccent,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Authentic Arabic Text with Harakat/Tashkeel
            Text(
                text = hadith.textArabic,
                fontSize = 21.sp,
                lineHeight = 34.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Right,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Bengali Transliteration / Pronunciation
            if (hadith.transliteration.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF1F5F9),
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
                            text = hadith.transliteration,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF334155),
                            lineHeight = 20.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Bengali Translation
            Text(
                text = hadith.textBengali,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 23.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Explanation & Key Lessons (হাদিসের শিক্ষা ও তাৎপর্য)
            if (hadith.explanationBengali.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFFFBEB), // Soft warm amber background
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { isExplanationExpanded = !isExplanationExpanded }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "হাদিসের তাৎপর্য ও শিক্ষা",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                            }
                            Icon(
                                imageVector = if (isExplanationExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isExplanationExpanded) "সংক্ষিপ্ত করুন" else "বিস্তারিত দেখুন",
                                tint = Color(0xFF92400E),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        AnimatedVisibility(
                            visible = isExplanationExpanded,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(modifier = Modifier.padding(top = 8.dp)) {
                                Divider(color = Color(0xFFFDE68A), thickness = 1.dp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = hadith.explanationBengali,
                                    style = MaterialTheme.typography.bodySmall,
                                    lineHeight = 20.sp,
                                    color = Color(0xFF78350F)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row: Copy, Share, Bookmark, Report
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick expand toggle hint
                if (hadith.explanationBengali.isNotBlank()) {
                    TextButton(
                        onClick = { isExplanationExpanded = !isExplanationExpanded },
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = if (isExplanationExpanded) "তাৎপর্য লুকান" else "তাৎপর্য পড়ুন...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onCopy, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "কপি করুন", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = onBookmark, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.BookmarkAdd, contentDescription = "বুকমার্ক", tint = GoldAccent)
                    }
                    IconButton(onClick = onShare, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Share, contentDescription = "শেয়ার করুন", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = onReport, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Flag, contentDescription = "রিপোর্ট", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
