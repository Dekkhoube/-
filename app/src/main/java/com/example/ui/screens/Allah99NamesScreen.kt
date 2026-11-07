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
import com.example.data.model.AllahNameItem
import com.example.data.repository.IslamicAmalRepository
import com.example.ui.theme.Emerald700
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight

/**
 * 99 Names of Allah Screen (আসমাউল হুসনা / ইসমে আজম)
 * Visually matching Screenshot_20261107_155616_7a08eaff.jpg
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Allah99NamesScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var showOnlyFavorites by remember { mutableStateOf(false) }
    val favoriteNumbers = remember { mutableStateListOf<Int>() }

    val allNames = remember { IslamicAmalRepository.allah99Names }
    val filteredNames = remember(searchQuery, showOnlyFavorites, favoriteNumbers.size) {
        allNames.filter { item ->
            val matchesSearch = searchQuery.isBlank() ||
                item.pronunciationBengali.contains(searchQuery, ignoreCase = true) ||
                item.meaningBengali.contains(searchQuery, ignoreCase = true) ||
                item.nameArabic.contains(searchQuery, ignoreCase = true) ||
                item.number.toString() == searchQuery.trim()

            val matchesFav = !showOnlyFavorites || favoriteNumbers.contains(item.number)
            matchesSearch && matchesFav
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "আসমাউল হুসনা / ইসমে আজম",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "আল্লাহর ৯৯টি গুণবাচক পবিত্র নাম ও অর্থ",
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
                    IconButton(onClick = { showOnlyFavorites = !showOnlyFavorites }) {
                        Icon(
                            imageVector = if (showOnlyFavorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "পছন্দের তালিকা",
                            tint = if (showOnlyFavorites) Color(0xFFDC2626) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier.fillMaxSize().testTag("allah_99_names_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Ornament & Hadith Card exactly matching Screenshot_20261107_155616_7a08eaff.jpg
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F261C)), // Dark green matching screenshot
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "আসমাউল হুসনা / ইসমে আজম",
                            color = GoldLight,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "আবূ হুরাইরাহ (রাঃ) হতে বর্ণিত, রাসূলেপাক (সাঃ) বলেছেন, আল্লাহ তা'আলার নিরানব্বই খানা নাম আছে অর্থাৎ এক কম একশত। যে লোক এই নামসমূহ মুখস্থ করবে বা পড়বে সে জান্নাতে প্রবেশ করবে। আল্লাহ তা'আলা বেজোড় তাই তিনি বেজোড়ই পছন্দ করেন।",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "(সহীহ বুখারী হাঃ ৫৯৬৮, আত-তিরমিযী হাঃ ৩৫০৬, ৩৫০৮)",
                            color = GoldAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("নামের উচ্চারণ, অর্থ বা আরবি অনুসন্ধান...") },
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

            // Results count
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (showOnlyFavorites) "সংরক্ষিত নামসমূহ (${filteredNames.size}টি)" else "সর্বমোট নাম: ${filteredNames.size}টি",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Name Cards matching reference screenshot:
            // 1. Number pill (১, ২, ৩...) in maroon shape
            // 2. Arabic text in white card
            // 3. উচ্চারণঃ- (light green box)
            // 4. অর্থঃ- (soft amber box)
            items(filteredNames, key = { it.number }) { item ->
                val isFav = favoriteNumbers.contains(item.number)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("allah_name_${item.number}"),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Number Pill Tab matching screenshot
                    Surface(
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        color = Color(0xFF6B1D1D), // Maroon top badge
                        modifier = Modifier
                            .width(80.dp)
                            .height(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${item.number}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Card container
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column {
                            // Arabic Name Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.quranReference,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF64748B)
                                )

                                Text(
                                    text = item.nameArabic,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A),
                                    textAlign = TextAlign.Center
                                )

                                IconButton(
                                    onClick = {
                                        if (isFav) favoriteNumbers.remove(item.number)
                                        else favoriteNumbers.add(item.number)
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (isFav) Color(0xFFDC2626) else Color(0xFF94A3B8)
                                    )
                                }
                            }

                            // উচ্চারণঃ- Box (Light green box matching reference screenshot)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFDCFCE7)) // Light green
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "উচ্চারণঃ- ${item.pronunciationBengali}",
                                    color = Color(0xFF14532D),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // অর্থঃ- Box (Soft yellow/amber box matching reference screenshot)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFEF3C7)) // Soft yellow amber
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "অর্থঃ- ${item.meaningBengali}",
                                    color = Color(0xFF78350F),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Explanation & Actions footer
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.explanationBengali,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF334155),
                                    modifier = Modifier.weight(1f)
                                )

                                Row {
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("AllahName", "${item.number}. ${item.nameArabic} - ${item.pronunciationBengali}\nঅর্থ: ${item.meaningBengali}\n${item.explanationBengali}")
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "নামটি কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF64748B))
                                    }
                                    IconButton(
                                        onClick = {
                                            val shareText = "আল্লাহর পবিত্র নাম নং ${item.number}:\n\n${item.nameArabic}\nউচ্চারণ: ${item.pronunciationBengali}\nঅর্থ: ${item.meaningBengali}\nতাৎপর্য: ${item.explanationBengali}\n\n— নূর ইসলামিক অ্যাপ"
                                            val sendIntent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, shareText)
                                                type = "text/plain"
                                            }
                                            context.startActivity(Intent.createChooser(sendIntent, "শেয়ার করুন"))
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF64748B))
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
