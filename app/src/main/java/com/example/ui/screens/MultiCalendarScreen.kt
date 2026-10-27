package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalculationMethod
import com.example.data.model.JuristicMethod
import com.example.data.model.LocationPreset
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.util.BengaliCalendarHelper
import com.example.util.HijriCalendarHelper
import com.example.util.PrayerCalculator
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiCalendarScreen(
    location: LocationPreset,
    calculationMethod: CalculationMethod,
    juristicMethod: JuristicMethod,
    hijriAdjustment: Int,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var calendarMonthOffset by remember { mutableIntStateOf(0) }
    var selectedDayInMonth by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) }

    val displayCal = remember(calendarMonthOffset) {
        Calendar.getInstance().apply {
            add(Calendar.MONTH, calendarMonthOffset)
        }
    }

    val todayCal = remember { Calendar.getInstance() }
    val isCurrentMonth = calendarMonthOffset == 0

    val currentYear = displayCal.get(Calendar.YEAR)
    val currentMonth = displayCal.get(Calendar.MONTH)
    val daysInMonth = displayCal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val firstDayOfWeek = remember(currentYear, currentMonth) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, currentMonth)
            set(Calendar.DAY_OF_MONTH, 1)
        }.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sunday
    }

    // Selected Date Details
    val selectedCal = remember(currentYear, currentMonth, selectedDayInMonth) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, currentMonth)
            set(Calendar.DAY_OF_MONTH, selectedDayInMonth.coerceIn(1, daysInMonth))
        }
    }

    val selectedHijri = remember(selectedCal, hijriAdjustment) {
        HijriCalendarHelper.getHijriDate(selectedCal, hijriAdjustment)
    }

    val selectedBengali = remember(selectedCal) {
        BengaliCalendarHelper.getBengaliDate(selectedCal)
    }

    val selectedPrayerTimes = remember(selectedCal, location, calculationMethod, juristicMethod) {
        PrayerCalculator.calculateTimes(
            calendar = selectedCal,
            latitude = location.latitude,
            longitude = location.longitude,
            timezoneOffset = location.timezoneOffsetHours,
            method = calculationMethod,
            juristic = juristicMethod
        )
    }

    val englishMonthNamesBengali = listOf(
        "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
        "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "বাংলা, ইংরেজি ও হিজরি ক্যালেন্ডার",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("calendar_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("multi_calendar_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Triple Calendar Synchronized Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Emerald800),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Emerald900, Emerald700)
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "নির্বাচিত তারিখ ও বিবরণ",
                                    color = GoldLight,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = selectedBengali.dayOfWeekBengali,
                                    color = Color.White.copy(alpha = 0.9f),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 1. English Gregorian
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = GoldLight, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ইংরেজি: ${selectedDayInMonth} ${englishMonthNamesBengali[currentMonth]} $currentYear",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // 2. Arabic Hijri
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Brightness2, contentDescription = null, tint = GoldLight, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "হিজরি: ${selectedHijri.day} ${selectedHijri.monthNameBengali} ${selectedHijri.year} (${selectedHijri.monthNameArabic})",
                                    color = Color.White.copy(alpha = 0.95f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // 3. Bengali Bongabdo
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Spa, contentDescription = null, tint = GoldLight, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "বাংলা: ${selectedBengali.formatted}",
                                    color = Color.White.copy(alpha = 0.95f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            selectedHijri.upcomingEventBengali?.let { event ->
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    color = GoldAccent.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = GoldLight, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = event, color = GoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Month Navigation Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { calendarMonthOffset -= 1 }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${englishMonthNamesBengali[currentMonth]} $currentYear",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "হিজরি ${selectedHijri.monthNameBengali} • বাংলা ${selectedBengali.monthNameBengali}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(onClick = { calendarMonthOffset += 1 }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
                        }
                    }
                }
            }

            // Calendar Day Grid Header & Days
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Weekday Headers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            val daysOfWeek = listOf("রবি", "সোম", "মঙ্গল", "বুধ", "বৃহঃ", "শুক্র", "শনি")
                            daysOfWeek.forEachIndexed { idx, day ->
                                Text(
                                    text = day,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (idx == 5) Emerald700 else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Grid of days
                        val totalCells = firstDayOfWeek + daysInMonth
                        val rows = (totalCells + 6) / 7

                        for (r in 0 until rows) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                for (c in 0..6) {
                                    val cellIndex = r * 7 + c
                                    val dayNum = cellIndex - firstDayOfWeek + 1

                                    if (dayNum in 1..daysInMonth) {
                                        val isToday = isCurrentMonth && dayNum == todayCal.get(Calendar.DAY_OF_MONTH)
                                        val isSelected = dayNum == selectedDayInMonth

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .padding(2.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    when {
                                                        isSelected -> MaterialTheme.colorScheme.primary
                                                        isToday -> MaterialTheme.colorScheme.primaryContainer
                                                        else -> Color.Transparent
                                                    }
                                                )
                                                .clickable { selectedDayInMonth = dayNum },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = "$dayNum",
                                                    fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    fontSize = 14.sp,
                                                    color = when {
                                                        isSelected -> Color.White
                                                        isToday -> MaterialTheme.colorScheme.primary
                                                        c == 5 -> Emerald700
                                                        else -> MaterialTheme.colorScheme.onSurface
                                                    }
                                                )
                                                Text(
                                                    text = BengaliCalendarHelper.convertToBengaliDigits(dayNum),
                                                    fontSize = 9.sp,
                                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Selected Day Prayer Times
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "এই দিনের নামাজের সময়সূচি (${location.nameBengali})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val prayerItems = listOf(
                            Triple("ফজর", selectedPrayerTimes.fajr, Icons.Default.WbTwilight),
                            Triple("সূর্যোদয়", selectedPrayerTimes.sunrise, Icons.Default.WbSunny),
                            Triple("যোহর", selectedPrayerTimes.dhuhr, Icons.Default.LightMode),
                            Triple("আসর", selectedPrayerTimes.asr, Icons.Default.WbCloudy),
                            Triple("মাগরিব", selectedPrayerTimes.maghrib, Icons.Default.NightsStay),
                            Triple("ইশা", selectedPrayerTimes.isha, Icons.Default.Bedtime)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            prayerItems.forEach { (name, timeStr, icon) ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = name, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    Text(text = timeStr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            // Annual Islamic & Bengali Holidays
            item {
                Text(
                    text = "প্রধান ইসলামিক ও জাতীয় দিবসসমূহ",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            items(HijriCalendarHelper.MAJOR_ISLAMIC_EVENTS.size) { idx ->
                val event = HijriCalendarHelper.MAJOR_ISLAMIC_EVENTS[idx]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Emerald700.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Event, contentDescription = null, tint = Emerald700, modifier = Modifier.size(20.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = event.nameBengali, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text(text = event.descriptionBengali, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${event.hijriDay} ${HijriCalendarHelper.HIJRI_MONTHS_BENGALI.getOrElse(event.hijriMonth - 1) { "" }}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
