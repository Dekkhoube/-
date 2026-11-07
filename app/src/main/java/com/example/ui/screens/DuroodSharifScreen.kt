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
import com.example.data.model.DuroodItem
import com.example.data.repository.IslamicAmalRepository
import com.example.ui.theme.Emerald700
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight

/**
 * Durood Sharif Screen (দরুদ শরিফ)
 * Visually inspired by Screenshot_20261107_155602_a2434b20.jpg & 155550_65621b8a.jpg
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuroodSharifScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val duroods = remember { IslamicAmalRepository.duroodList }
    var expandedDuroodId by remember { mutableStateOf<Int?>(1) } // Default open first (Ibrahim)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "দরুদ শরিফ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "৭টি বিখ্যাত ও বরকতময় দরুদ শরিফ",
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
        modifier = modifier.fillMaxSize().testTag("durood_sharif_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Card matching Screenshot 155602
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF04261C)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "দুরূদ",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "إِنَّ اللَّهَ وَمَلَائِكَتَهُ يُصَلُّونَ عَلَى النَّبِيِّ ۚ يَا أَيُّهَا الَّذِينَ آمَنُوا صَلُّوا عَلَيْهِ وَسَلِّمُوا تَسْلِيمًا",
                            fontSize = 15.sp,
                            color = GoldLight.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "পবিত্র কুরআন ও সুন্নাহ সম্মত বিশুদ্ধ দরুদসমূহ",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // List of Durood Items
            items(duroods, key = { it.id }) { durood ->
                DuroodCard(
                    item = durood,
                    isExpanded = expandedDuroodId == durood.id,
                    onToggleExpand = {
                        expandedDuroodId = if (expandedDuroodId == durood.id) null else durood.id
                    },
                    onCopy = {
                        val clip = ClipData.newPlainText(
                            durood.titleBengali,
                            "${durood.titleBengali}\n\n${durood.textArabic}\n\nউচ্চারণ: ${durood.transliteration}\n\nঅনুবাদ: ${durood.translationBengali}\n\nফজিলত: ${durood.virtues}\nদলিল: ${durood.reference}"
                        )
                        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                        Toast.makeText(context, "${durood.titleBengali} কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                    },
                    onShare = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "✨ ${durood.titleBengali} ✨\n\n${durood.textArabic}\n\nউচ্চারণ:\n${durood.transliteration}\n\nঅনুবাদ:\n${durood.translationBengali}\n\nফজিলত:\n${durood.virtues}\n\nদলিল: ${durood.reference}\n\n— নূর ইসলামিক অ্যাপ"
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

@Composable
fun DuroodCard(
    item: DuroodItem,
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
        elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header row with dark pill style matching screenshot 155602
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF04261C))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "❖",
                        color = GoldAccent,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${item.numberBengali} ।  ${item.titleBengali}",
                        fontSize = 16.sp,
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
                        .padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Font Controls & Actions Row
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
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            FilledTonalIconButton(
                                onClick = {
                                    if (arabicFontSize < 34) arabicFontSize += 2
                                    if (bengaliFontSize < 20) bengaliFontSize += 1
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(onClick = onCopy, modifier = Modifier.size(34.dp)) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "কপি", modifier = Modifier.size(18.dp), tint = Emerald700)
                            }
                            IconButton(onClick = onShare, modifier = Modifier.size(34.dp)) {
                                Icon(Icons.Default.Share, contentDescription = "শেয়ার", modifier = Modifier.size(18.dp), tint = Emerald700)
                            }
                        }
                    }

                    // Arabic Text Box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                .padding(16.dp)
                        )
                    }

                    // Pronunciation Box (Soft Mint) - matching reference screenshot 155550
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "উচ্চারণ",
                                fontSize = 12.sp,
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

                    // Meaning Box (Soft Amber) - matching reference screenshot 155550
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "অর্থ / অনুবাদ",
                                fontSize = 12.sp,
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

                    // Virtues & Explanation Box (Soft Sky Blue)
                    if (item.virtues.isNotBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "ফজিলত ও আমল",
                                    fontSize = 12.sp,
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
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "দলিল: ${item.reference}",
                                fontSize = 12.sp,
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
