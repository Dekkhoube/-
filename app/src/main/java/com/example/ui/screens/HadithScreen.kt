package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Hadith
import com.example.data.repository.HadithAndDuaRepository
import com.example.ui.theme.Emerald700
import com.example.ui.theme.GoldAccent

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

    val displayedHadiths = remember(selectedCollectionId, searchQuery) {
        val base = HadithAndDuaRepository.searchHadith(searchQuery)
        if (selectedCollectionId == "all") base else base.filter { it.collectionId == selectedCollectionId }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("hadith_screen")
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("হাদিস, বিষয় বা বর্ণনাকারী অনুসন্ধান...") },
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("hadith_search_bar"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Collection Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(HadithAndDuaRepository.collections) { collection ->
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

        Spacer(modifier = Modifier.height(8.dp))

        // Hadith Items List
        if (displayedHadiths.isEmpty()) {
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
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(displayedHadiths, key = { it.id }) { hadith ->
                    HadithCard(
                        hadith = hadith,
                        onBookmark = { onBookmarkHadith(hadith) },
                        onReport = { onReportHadith(hadith) },
                        onShare = {
                            val shareText = "${hadith.chapterBengali}\n\n${hadith.textArabic}\n\n${hadith.textBengali}\n\nবর্ণনাকারী: ${hadith.narratorBengali}\nমান: ${hadith.grade}\nরেফারেন্স: ${hadith.reference} - নূর ইসলামিক অ্যাপ"
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "হাদিস শেয়ার করুন"))
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
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hadith_card_${hadith.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Reference & Grade Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = hadith.reference,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

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

            Spacer(modifier = Modifier.height(10.dp))

            // Arabic Text
            Text(
                text = hadith.textArabic,
                fontSize = 20.sp,
                lineHeight = 32.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Right,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Bengali Translation
            Text(
                text = hadith.textBengali,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBookmark, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.BookmarkAdd, contentDescription = "Bookmark", tint = GoldAccent)
                }
                IconButton(onClick = onShare, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onReport, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Flag, contentDescription = "Report Error", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
