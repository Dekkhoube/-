package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyWorshipEntity
import com.example.ui.theme.Emerald700
import com.example.viewmodel.MainViewModel

@Composable
fun WorshipTrackerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val todayDateStr = viewModel.getTodayDateStr()
    val todayWorship by viewModel.featureRepository.getWorshipForDate(todayDateStr).collectAsState(initial = null)
    val recentRecords by viewModel.featureRepository.getRecentWorshipRecords().collectAsState(initial = emptyList())

    val currentWorship = todayWorship ?: DailyWorshipEntity(dateStr = todayDateStr)

    // Calculate completed prayers
    var completedCount = 0
    if (currentWorship.fajr) completedCount++
    if (currentWorship.dhuhr) completedCount++
    if (currentWorship.asr) completedCount++
    if (currentWorship.maghrib) completedCount++
    if (currentWorship.isha) completedCount++
    if (currentWorship.tahajjud) completedCount++
    if (currentWorship.quranRecited) completedCount++
    if (currentWorship.fasting) completedCount++
    if (currentWorship.sadaqah) completedCount++

    val totalGoals = 9
    val percentage = (completedCount.toFloat() / totalGoals).coerceIn(0f, 1f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("worship_tracker_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Today's Progress Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "আজকের আমল ও ইবাদত",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = todayDateStr,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "সম্পন্ন: $completedCount / $totalGoals টি (${(percentage * 100).toInt()}%)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Emerald700
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { percentage },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = Emerald700,
                        trackColor = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }

        // Daily Salah Checkbox Section
        item {
            Text(
                text = "পাঁচ ওয়াক্ত ফরজ সালাত",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    WorshipCheckRow("ফজরের সালাত", currentWorship.fajr) { viewModel.toggleWorshipPrayer(todayWorship, "fajr") }
                    WorshipCheckRow("যোহরের সালাত", currentWorship.dhuhr) { viewModel.toggleWorshipPrayer(todayWorship, "dhuhr") }
                    WorshipCheckRow("আসরের সালাত", currentWorship.asr) { viewModel.toggleWorshipPrayer(todayWorship, "asr") }
                    WorshipCheckRow("মাগরিবের সালাত", currentWorship.maghrib) { viewModel.toggleWorshipPrayer(todayWorship, "maghrib") }
                    WorshipCheckRow("ইশার সালাত", currentWorship.isha) { viewModel.toggleWorshipPrayer(todayWorship, "isha") }
                }
            }
        }

        // Additional Sunnah & Daily Deeds
        item {
            Text(
                text = "নফল ইবাদত ও নেক আমল",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    WorshipCheckRow("তাহাজ্জুদ সালাত", currentWorship.tahajjud) { viewModel.toggleWorshipPrayer(todayWorship, "tahajjud") }
                    WorshipCheckRow("কুরআন তিলাওয়াত", currentWorship.quranRecited) { viewModel.toggleWorshipPrayer(todayWorship, "quran") }
                    WorshipCheckRow("আজকের রোজা", currentWorship.fasting) { viewModel.toggleWorshipPrayer(todayWorship, "fasting") }
                    WorshipCheckRow("সাদাকাহ / দান", currentWorship.sadaqah) { viewModel.toggleWorshipPrayer(todayWorship, "sadaqah") }
                }
            }
        }

        // History of Recent Days
        if (recentRecords.isNotEmpty()) {
            item {
                Text(
                    text = "বিগত দিনসমূহের রেকর্ড",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(recentRecords, key = { it.dateStr }) { record ->
                var dayScore = 0
                if (record.fajr) dayScore++
                if (record.dhuhr) dayScore++
                if (record.asr) dayScore++
                if (record.maghrib) dayScore++
                if (record.isha) dayScore++
                if (record.tahajjud) dayScore++
                if (record.quranRecited) dayScore++
                if (record.fasting) dayScore++
                if (record.sadaqah) dayScore++

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = record.dateStr, fontWeight = FontWeight.Medium)
                        Text(
                            text = "$dayScore / ৯ টি সম্পন্ন",
                            fontWeight = FontWeight.Bold,
                            color = if (dayScore >= 5) Emerald700 else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WorshipCheckRow(
    title: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (checked) FontWeight.Bold else FontWeight.Normal,
            color = if (checked) Emerald700 else MaterialTheme.colorScheme.onSurface
        )
        Checkbox(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(checkedColor = Emerald700)
        )
    }
}
