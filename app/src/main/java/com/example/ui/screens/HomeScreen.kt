package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ayah
import com.example.data.model.Hadith
import com.example.data.model.PrayerSchedule
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.viewmodel.AppUiState
import java.util.Locale

@Composable
fun HomeScreen(
    uiState: AppUiState,
    adminNotices: List<com.example.data.model.AdminContentEntity> = emptyList(),
    lastReadSurahName: String = "আল-ফাতিহা",
    lastReadAyahNumber: Int = 1,
    lastReadPageNumber: Int = 1,
    onResumeLastRead: () -> Unit = {},
    onNavigateToNamazShikkha: () -> Unit = {},
    onNavigateToHafeziQuran: (Int) -> Unit = {},
    onNavigateToQuran: () -> Unit,
    onNavigateToPrayer: () -> Unit,
    onNavigateToQibla: () -> Unit,
    onNavigateToTasbih: () -> Unit,
    onNavigateToRamadan: () -> Unit,
    onNavigateToZakat: () -> Unit,
    onNavigateToWorship: () -> Unit,
    onNavigateToMosque: () -> Unit,
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToAi: () -> Unit = {},
    onPlayAyah: (Ayah, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val schedule = uiState.prayerSchedule
    val hijri = uiState.currentHijriDate
    val bengali = uiState.currentBengaliDate

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Hero Islamic Banner Card ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Emerald800),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Emerald900, Emerald700)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            color = GoldLight,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Hijri & Bengali date pill (Clickable to Calendar)
                        Surface(
                            onClick = onNavigateToCalendar,
                            shape = RoundedCornerShape(50.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (hijri != null && bengali != null) {
                                        "${hijri.day} ${hijri.monthNameBengali} হিজরি • ${bengali.monthNameBengali} বঙ্গাব্দ"
                                    } else if (hijri != null) {
                                        "${hijri.day} ${hijri.monthNameBengali} ${hijri.year} হিজরি"
                                    } else {
                                        "পবিত্র হিজরি ও বাংলা ক্যালেন্ডার"
                                    },
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = GoldLight,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Countdown to next prayer
                        if (schedule != null) {
                            Text(
                                text = "পরবর্তী নামাজ: ${schedule.nextPrayerNameBengali}",
                                color = Color.White.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatMillisToCountdown(schedule.remainingMillis),
                                color = GoldLight,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "ওয়াক্ত শুরু: ${schedule.nextPrayerTimeFormatted}",
                                color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${uiState.selectedLocation.nameBengali} (${uiState.juristicMethod.displayNameBengali.split(" ")[0]})",
                                color = Color.White.copy(alpha = 0.85f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        // --- 2. Last Read Quran Card ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onResumeLastRead() }
                    .testTag("home_last_read_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Emerald700.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Emerald700.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = Emerald800,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "সর্বশেষ পঠিত আয়াত",
                                style = MaterialTheme.typography.labelSmall,
                                color = Emerald800,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "সূরা $lastReadSurahName • আয়াত $lastReadAyahNumber (পৃষ্ঠা $lastReadPageNumber)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    FilledTonalButton(
                        onClick = onResumeLastRead,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("চালিয়ে যান", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- Admin Announcements / Notices Banner ---
        if (adminNotices.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Campaign,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "প্রশাসনিক বিজ্ঞপ্তি ও নোটিশ",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        adminNotices.take(2).forEach { notice ->
                            Text(
                                text = "• ${notice.titleBengali}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (notice.contentBengali.isNotBlank()) {
                                Text(
                                    text = notice.contentBengali,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }

        // --- 2. Today's 5 Prayer Times Summary ---
        if (schedule != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToPrayer() }
                        .testTag("prayer_schedule_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "আজকের নামাজের সময়সূচি",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "বিস্তারিত >",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PrayerTimeItem("ফজর", schedule.fajr, schedule.nextPrayerNameBengali == "ফজর")
                            PrayerTimeItem("যোহর", schedule.dhuhr, schedule.nextPrayerNameBengali == "যোহর")
                            PrayerTimeItem("আসর", schedule.asr, schedule.nextPrayerNameBengali == "আসর")
                            PrayerTimeItem("মাগরিব", schedule.maghrib, schedule.nextPrayerNameBengali == "মাগরিব")
                            PrayerTimeItem("ইশা", schedule.isha, schedule.nextPrayerNameBengali == "ইশা")
                        }
                    }
                }
            }
        }

        // --- 3. Quick Action Feature Grid ---
        item {
            Text(
                text = "জরুরি ইসলামিক সেবা",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Gemini Islamic AI Banner Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToAi() }
                        .testTag("quick_gemini_ai_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Emerald800.copy(alpha = 0.08f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Emerald700.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Emerald800),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Gemini AI",
                                tint = GoldLight,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "ইসলামিক এআই সহকারী",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = GoldAccent.copy(alpha = 0.25f)
                                ) {
                                    Text(
                                        text = "Gemini",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald900,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "কুরআন, হাদিস, মাসআলা ও দ্বীনি প্রশ্নোত্তর জানুন কৃত্রিম বুদ্ধিমত্তার সাহায্যে",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Emerald800
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "আল-কুরআন",
                        subtitle = "১১৪ সূরা ও ৩০ পারা",
                        icon = Icons.Default.MenuBook,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_quran",
                        onClick = onNavigateToQuran
                    )
                    QuickActionCard(
                        title = "হাফেজী কুরআন",
                        subtitle = "মুসহাফ ৬০৪ পৃষ্ঠা ভিউ",
                        icon = Icons.Default.AutoStories,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_hafezi_mushaf",
                        onClick = { onNavigateToHafeziQuran(lastReadPageNumber) }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "নামাজ ও অযু শিক্ষা",
                        subtitle = "নিয়ম, রাকাত ও দোয়া",
                        icon = Icons.Default.SelfImprovement,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_namaz_shikkha",
                        onClick = onNavigateToNamazShikkha
                    )
                    QuickActionCard(
                        title = "কিবলা কম্পাস",
                        subtitle = "কা'বার সঠিক দিক",
                        icon = Icons.Default.Explore,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_qibla",
                        onClick = onNavigateToQibla
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "ডিজিটাল তাসবীহ",
                        subtitle = "দৈনিক জিকির ও হিসাব",
                        icon = Icons.Default.TouchApp,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_tasbih",
                        onClick = onNavigateToTasbih
                    )
                    QuickActionCard(
                        title = "মাহে রমজান",
                        subtitle = "সেহরি ও ইফতার সময়",
                        icon = Icons.Default.WbTwilight,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_ramadan",
                        onClick = onNavigateToRamadan
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "যাকাত হিসাব",
                        subtitle = "স্বর্ণ ও রূপার নেসাব",
                        icon = Icons.Default.Calculate,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_zakat",
                        onClick = onNavigateToZakat
                    )
                    QuickActionCard(
                        title = "আমল ট্র্যাকার",
                        subtitle = "৫ ওয়াক্ত নামাজ ও আমল",
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_worship",
                        onClick = onNavigateToWorship
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "কাছের মসজিদ",
                        subtitle = "ম্যাপ ও দিকনির্দেশনা",
                        icon = Icons.Default.Mosque,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_mosque",
                        onClick = onNavigateToMosque
                    )
                    QuickActionCard(
                        title = "নামাজের সময়",
                        subtitle = "নিষেধ সময় ও আজান",
                        icon = Icons.Default.AccessTime,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_prayer",
                        onClick = onNavigateToPrayer
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "বহুভাষী ক্যালেন্ডার",
                        subtitle = "বাংলা, ইংরেজি ও হিজরি ক্যালেন্ডার",
                        icon = Icons.Default.CalendarMonth,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_calendar",
                        onClick = onNavigateToCalendar
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // --- 4. Daily Quran Verse of the Day ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_verse_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "আজকের আয়াত (আয়াতুল কুরসী)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        IconButton(
                            onClick = {
                                val ayah = Ayah(
                                    262, 2, 255,
                                    "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ...",
                                    "আল্লাহ, তিনি ছাড়া কোনো সত্য উপাস্য নেই; তিনি চিরঞ্জীব...",
                                    "আল্লাহু লা ইলাহা ইল্লা হুয়াল হাইয়্যুল ক্বাইয়্যুম",
                                    "https://everyayah.com/data/Alafasy_128kbps/002255.mp3",
                                    3
                                )
                                onPlayAyah(ayah, "আল-বাকারা")
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Play Ayah Audio",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ...",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "আল্লাহ, তিনি ছাড়া কোনো সত্য উপাস্য নেই; তিনি চিরঞ্জীব, সর্বসত্তার ধারক। তন্দ্রা বা নিদ্রা তাঁকে স্পর্শ করে না। আসমান ও যমীনে যা কিছু আছে সব তাঁরই।",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "সূরা আল-বাকারা: ২৫৫",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun PrayerTimeItem(
    name: String,
    time: String,
    isNext: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isNext) Emerald700 else Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isNext) FontWeight.Bold else FontWeight.Medium,
            color = if (isNext) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = time.replace("AM", "").replace("PM", "").trim(),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = if (isNext) GoldLight else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
    }
}

private fun formatMillisToCountdown(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
}
