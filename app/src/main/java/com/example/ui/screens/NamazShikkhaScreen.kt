package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NamazRakatSummary
import com.example.data.model.NamazStepItem
import com.example.data.model.WuduStepItem
import com.example.data.repository.HadithAndDuaRepository
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldAccent

enum class NamazShikkhaTab(val titleBengali: String) {
    NAMAZ_STEPS("নামাজ শিক্ষা"),
    RAKAT_INFO("রাকাতের হিসাব"),
    WUDU_STEPS("অযু শিক্ষা"),
    IMPORTANT_DUAS("নামাজের দোয়া")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamazShikkhaScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(NamazShikkhaTab.NAMAZ_STEPS) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "নামাজ ও অযু শিক্ষা",
                        fontWeight = FontWeight.Bold,
                        color = Emerald800
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("namaz_shikkha_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ফিরে যান"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("namaz_shikkha_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Emerald700
            ) {
                NamazShikkhaTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = {
                            Text(
                                text = tab.titleBengali,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                NamazShikkhaTab.NAMAZ_STEPS -> NamazStepsList(steps = HadithAndDuaRepository.namazSteps)
                NamazShikkhaTab.RAKAT_INFO -> NamazRakatList(rakats = HadithAndDuaRepository.namazRakatList)
                NamazShikkhaTab.WUDU_STEPS -> WuduStepsList(steps = HadithAndDuaRepository.wuduSteps)
                NamazShikkhaTab.IMPORTANT_DUAS -> NamazImportantDuasList()
            }
        }
    }
}

@Composable
private fun NamazStepsList(steps: List<NamazStepItem>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Emerald800)
                    Text(
                        text = "নামাজ ইসলামের দ্বিতীয় স্তম্ভ। তাকবীরে তাহরিমা থেকে সালাম ফিরানো পর্যন্ত প্রতিটি রুকন ধাপে ধাপে বিশুদ্ধভাবে আদায় করুন।",
                        style = MaterialTheme.typography.bodySmall,
                        color = Emerald900,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        items(steps) { step ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Emerald700),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${step.stepNumber}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            text = step.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (step.arabicText.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFBF8F0),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = step.arabicText,
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Right,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B),
                                lineHeight = 34.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            )
                        }
                    }

                    if (step.transliteration.isNotBlank()) {
                        Column {
                            Text(
                                text = "বাংলা উচ্চারণ:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Emerald800
                            )
                            Text(
                                text = step.transliteration,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1E293B),
                                lineHeight = 22.sp
                            )
                        }
                    }

                    if (step.meaning.isNotBlank()) {
                        Column {
                            Text(
                                text = "বাংলা অর্থ:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Emerald800
                            )
                            Text(
                                text = step.meaning,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 22.sp
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Text(
                        text = "নিয়ম: ${step.instruction}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun NamazRakatList(rakats: List<NamazRakatSummary>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "৫ ওয়াক্ত ফরজ ও সুন্নাতে মুয়াক্কাদা নামাজ প্রতিদিন আদায় করা অত্যন্ত জরুরি। নিচে ওয়াক্ত অনুযায়ী রাকাতের হিসাব দেওয়া হলো:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Emerald900,
                    modifier = Modifier.padding(14.dp),
                    lineHeight = 20.sp
                )
            }
        }

        items(rakats) { item ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.prayerName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Emerald800
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Emerald700
                        ) {
                            Text(
                                text = "মোট ${item.total} রাকাত",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        if (item.sunnahBefore > 0) {
                            RakatBadge(label = "সুন্নত (পূর্বে)", count = item.sunnahBefore)
                        }
                        RakatBadge(label = "ফরজ", count = item.fard, isFard = true)
                        if (item.sunnahAfter > 0) {
                            RakatBadge(label = "সুন্নত (পরে)", count = item.sunnahAfter)
                        }
                        if (item.witr > 0) {
                            RakatBadge(label = "বিতর", count = item.witr, isWitr = true)
                        }
                        if (item.nafl > 0) {
                            RakatBadge(label = "নফল", count = item.nafl)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RakatBadge(label: String, count: Int, isFard: Boolean = false, isWitr: Boolean = false) {
    val bgColor = when {
        isFard -> Emerald700.copy(alpha = 0.15f)
        isWitr -> GoldAccent.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = when {
        isFard -> Emerald800
        isWitr -> Color(0xFF92400E)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = "$count",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = textColor
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = textColor
        )
    }
}

@Composable
private fun WuduStepsList(steps: List<WuduStepItem>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "অযুর ৪টি ফরজ (আবশ্যকীয় কাজ):",
                        fontWeight = FontWeight.Bold,
                        color = Emerald900,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "১. সমস্ত মুখমণ্ডল একবার ধোয়া\n২. উভয় হাত কনুইসহ একবার ধোয়া\n৩. মাথার চারভাগের একভাগ মাসেহ করা\n৪. উভয় পা টাখনুসহ একবার ধোয়া",
                        style = MaterialTheme.typography.bodySmall,
                        color = Emerald800,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        items(steps) { step ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Emerald700),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${step.stepNumber}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            text = step.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (step.arabicDua.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFBF8F0),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = step.arabicDua,
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Right,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B),
                                lineHeight = 34.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            )
                        }
                    }

                    if (step.transliteration.isNotBlank()) {
                        Text(
                            text = "উচ্চারণ: ${step.transliteration}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E293B),
                            lineHeight = 22.sp
                        )
                    }

                    if (step.meaning.isNotBlank()) {
                        Text(
                            text = "অর্থ: ${step.meaning}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                    }

                    Text(
                        text = "পদ্ধতি: ${step.instruction}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "অযু ভঙ্গের কারণসমূহ (৭টি প্রধান কারণ):",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "১. পায়খানা বা প্রস্রাবের রাস্তা দিয়ে কোনো কিছু বের হওয়া।\n২. মুখ ভরে বমি হওয়া।\n৩. শরীর থেকে রক্ত, পুঁজ বের হয়ে গড়িয়ে পড়া।\n৪. কোনো কিছুর সাথে হেলান দিয়ে ঘুমিয়ে পড়া।\n৫. পাগল, মাতাল বা বেহুঁশ হওয়া।\n৬. নামাজের মধ্যে উচ্চৈঃস্বরে হেসে ওঠা।\n৭. সহবাস করা।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun NamazImportantDuasList() {
    val prayerDuas = remember {
        HadithAndDuaRepository.verifiedDuas.filter { it.category == com.example.data.model.DuaCategory.PRAYER }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "নামাজের অপরিহার্য মাসনূন দোয়াসমূহ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Emerald800
            )
        }

        items(prayerDuas) { dua ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = dua.titleBengali,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Emerald800
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFBF8F0),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = dua.textArabic,
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Right,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B),
                            lineHeight = 34.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "বাংলা উচ্চারণ:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Emerald800
                        )
                        Text(
                            text = dua.transliteration,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E293B),
                            lineHeight = 22.sp
                        )
                    }

                    Column {
                        Text(
                            text = "বাংলা অর্থ:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Emerald800
                        )
                        Text(
                            text = dua.meaningBengali,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                    }

                    Text(
                        text = "রেফারেন্স: ${dua.reference}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Emerald700,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
