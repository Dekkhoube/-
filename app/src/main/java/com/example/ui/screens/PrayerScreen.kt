package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalculationMethod
import com.example.data.model.JuristicMethod
import com.example.data.model.LocationPreset
import com.example.data.model.PrayerSchedule
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.WarningAmber
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerScreen(
    schedule: PrayerSchedule?,
    selectedLocation: LocationPreset,
    locationPresets: List<LocationPreset>,
    calculationMethod: CalculationMethod,
    juristicMethod: JuristicMethod,
    isAutoAzanEnabled: Boolean = false,
    isFajrAlarmEnabled: Boolean = true,
    isDhuhrAlarmEnabled: Boolean = true,
    isAsrAlarmEnabled: Boolean = true,
    isMaghribAlarmEnabled: Boolean = true,
    isIshaAlarmEnabled: Boolean = true,
    isGpsActive: Boolean = false,
    onSelectLocation: (LocationPreset) -> Unit,
    onSelectCalculationMethod: (CalculationMethod) -> Unit,
    onSelectJuristicMethod: (JuristicMethod) -> Unit,
    onToggleAutoAzan: (Boolean) -> Unit = {},
    onTogglePrayerAlarm: (String, Boolean) -> Unit = { _, _ -> },
    onPlayAzanPreview: () -> Unit = {},
    onDetectGpsLocation: () -> Unit = {},
    onNavigateToQibla: () -> Unit,
    onNavigateToCalendar: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showLocationSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            onDetectGpsLocation()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("prayer_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Location & GPS Tracking Bar ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = { showLocationSheet = true },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.testTag("location_picker_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = selectedLocation.nameBengali,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            if (isGpsActive) {
                                Text(
                                    text = "জিপিএস অটো ট্র্যাক সক্রিয়",
                                    fontSize = 10.sp,
                                    color = Emerald700,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Auto GPS Button
                    FilledTonalIconButton(
                        onClick = {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = if (isGpsActive) Emerald700.copy(alpha = 0.2f) else MaterialTheme.colorScheme.secondaryContainer
                        ),
                        modifier = Modifier.testTag("gps_detect_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "GPS Detect",
                            tint = if (isGpsActive) Emerald700 else MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    FilledTonalButton(
                        onClick = onNavigateToQibla,
                        modifier = Modifier.testTag("qibla_shortcut_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("কিবলা")
                    }

                    IconButton(
                        onClick = { showSettingsSheet = true },
                        modifier = Modifier.testTag("prayer_settings_button")
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = "Settings", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        // --- 2. Multi-Calendar Quick Banner ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToCalendar() },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = "বাংলা, ইংরেজি ও হিজরি ক্যালেন্ডার",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "তিনটি ক্যালেন্ডারের তারিখ ও সময়সূচি একসাথে দেখুন",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // --- 3. Azan & Auto Alarm Control Card ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isAutoAzanEnabled) Emerald700.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isAutoAzanEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                    contentDescription = null,
                                    tint = if (isAutoAzanEnabled) Emerald700 else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "৫ ওয়াক্ত নামাজের আজান ও এলার্ম",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isAutoAzanEnabled) "ওয়াক্ত হলে স্বয়ংক্রিয় আজান বাজবে" else "এলার্ম বন্ধ আছে",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isAutoAzanEnabled) Emerald700 else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = isAutoAzanEnabled,
                            onCheckedChange = { onToggleAutoAzan(it) },
                            modifier = Modifier.testTag("auto_azan_switch")
                        )
                    }

                    AnimatedVisibility(visible = isAutoAzanEnabled) {
                        Column(modifier = Modifier.padding(top = 14.dp)) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "ওয়াক্তভিত্তিক আজান নির্বাচন করুন:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                FilterChip(
                                    selected = isFajrAlarmEnabled,
                                    onClick = { onTogglePrayerAlarm("fajr", !isFajrAlarmEnabled) },
                                    label = { Text("ফজর") }
                                )
                                FilterChip(
                                    selected = isDhuhrAlarmEnabled,
                                    onClick = { onTogglePrayerAlarm("dhuhr", !isDhuhrAlarmEnabled) },
                                    label = { Text("যোহর") }
                                )
                                FilterChip(
                                    selected = isAsrAlarmEnabled,
                                    onClick = { onTogglePrayerAlarm("asr", !isAsrAlarmEnabled) },
                                    label = { Text("আসর") }
                                )
                                FilterChip(
                                    selected = isMaghribAlarmEnabled,
                                    onClick = { onTogglePrayerAlarm("maghrib", !isMaghribAlarmEnabled) },
                                    label = { Text("মাগরিব") }
                                )
                                FilterChip(
                                    selected = isIshaAlarmEnabled,
                                    onClick = { onTogglePrayerAlarm("isha", !isIshaAlarmEnabled) },
                                    label = { Text("ইশা") }
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Test Azan Button
                            FilledTonalButton(
                                onClick = onPlayAzanPreview,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("পবিত্র আজান শুনুন (টেস্ট করুন)", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // --- 4. Next Prayer Countdown Hero Card ---
        if (schedule != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Emerald800),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Emerald900, Emerald700)
                                )
                            )
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "পরবর্তী নামাজ: ${schedule.nextPrayerNameBengali}",
                                color = GoldLight,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = formatMillisToCountdown(schedule.remainingMillis),
                                color = GoldLight,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "ওয়াক্ত শুরু: ${schedule.nextPrayerTimeFormatted}",
                                color = Color.White.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // --- 5. Full Timetable ---
            item {
                Text(
                    text = "দৈনিক পাঁচ ওয়াক্ত ও সূর্যোদয়",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PrayerRowItem("ফজর (Fajr)", schedule.fajr, schedule.nextPrayerNameBengali == "ফজর", Icons.Default.WbTwilight)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        PrayerRowItem("সূর্যোদয় (Sunrise)", schedule.sunrise, false, Icons.Default.WbSunny)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        PrayerRowItem("যোহর (Dhuhr)", schedule.dhuhr, schedule.nextPrayerNameBengali == "যোহর", Icons.Default.LightMode)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        PrayerRowItem("আসর (Asr - ${juristicMethod.displayNameBengali.split(" ")[0]})", schedule.asr, schedule.nextPrayerNameBengali == "আসর", Icons.Default.WbCloudy)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        PrayerRowItem("মাগরিব (Maghrib / ইফতার)", schedule.maghrib, schedule.nextPrayerNameBengali == "মাগরিব", Icons.Default.NightsStay)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        PrayerRowItem("ইশা (Isha)", schedule.isha, schedule.nextPrayerNameBengali == "ইশা", Icons.Default.Bedtime)
                    }
                }
            }

            // --- 6. Prohibited Prayer Times Card ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = WarningAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "নামাজের নিষিদ্ধ সময়সমূহ",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "১. সূর্যোদয়ের সময়: ${schedule.sunrise} হতে ১৫ মিনিট পর্যন্ত\n" +
                                    "২. দ্বিপ্রহর (যাওয়াল): যোহরের ঠিক পূর্ববর্তী ১২ মিনিট\n" +
                                    "৩. সূর্যাস্তের সময়: মাগরিবের পূর্ববর্তী ১৫ মিনিট",
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // --- Location Picker BottomSheet ---
    if (showLocationSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLocationSheet = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "জেলা ও শহর নির্বাচন করুন",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                // GPS Auto Location in sheet
                FilledTonalButton(
                    onClick = {
                        showLocationSheet = false
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("জিপিএস থেকে অটোমেটিক অবস্থান ঠিক করুন")
                }

                Spacer(modifier = Modifier.height(12.dp))

                locationPresets.forEach { preset ->
                    Surface(
                        onClick = {
                            onSelectLocation(preset)
                            showLocationSheet = false
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = if (preset.nameBengali == selectedLocation.nameBengali) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = preset.nameBengali, fontWeight = FontWeight.Medium)
                            Text(text = preset.nameEnglish, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // --- Calculation Settings BottomSheet ---
    if (showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "নামাজ হিসাবের সেটিংস ও কনভেনশন",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "আসরের হিসাব পদ্ধতি (মাযহাব):", style = MaterialTheme.typography.labelLarge)
                JuristicMethod.values().forEach { juristic ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = juristic == juristicMethod,
                            onClick = { onSelectJuristicMethod(juristic) }
                        )
                        Text(text = juristic.displayNameBengali, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "হিসাব সংস্থা / কনভেনশন:", style = MaterialTheme.typography.labelLarge)
                CalculationMethod.values().forEach { method ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = method == calculationMethod,
                            onClick = { onSelectCalculationMethod(method) }
                        )
                        Text(text = method.displayNameBengali, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun PrayerRowItem(
    name: String,
    time: String,
    isCurrent: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isCurrent) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = time,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatMillisToCountdown(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
}
