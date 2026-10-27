package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalculationMethod
import com.example.data.model.JuristicMethod
import com.example.data.repository.QuranRepository
import com.example.ui.components.GoogleSignInCard
import com.example.ui.theme.Emerald700
import com.example.viewmodel.AppUiState
import com.google.firebase.auth.FirebaseUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: AppUiState,
    currentUser: FirebaseUser? = null,
    onAuthSuccess: (FirebaseUser) -> Unit = {},
    onSignOut: () -> Unit = {},
    onToggleDarkMode: () -> Unit,
    onSetArabicFontSize: (Float) -> Unit,
    onSetBengaliFontSize: (Float) -> Unit,
    onSetHijriAdjustment: (Int) -> Unit,
    onSetCalculationMethod: (CalculationMethod) -> Unit,
    onSetJuristicMethod: (JuristicMethod) -> Unit,
    onNavigateToAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Account & Cloud Sync Section
        item {
            GoogleSignInCard(
                currentUser = currentUser,
                onAuthSuccess = onAuthSuccess,
                onSignOut = onSignOut
            )
        }

        // Appearance Section
        item {
            Text(
                text = "অ্যাপের প্রদর্শন ও থিম",
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
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "ডার্ক মোড (Night Mode)", fontWeight = FontWeight.Bold)
                            Text(
                                text = "রাতের আলোয় চোখের সুরক্ষার জন্য",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.isDarkMode,
                            onCheckedChange = { onToggleDarkMode() },
                            modifier = Modifier.testTag("dark_mode_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Arabic Font Size Slider
                    Text(
                        text = "আরবি লেখার ফন্ট সাইজ (${uiState.arabicFontSize.toInt()} sp)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Slider(
                        value = uiState.arabicFontSize,
                        onValueChange = onSetArabicFontSize,
                        valueRange = 18f..36f,
                        steps = 8,
                        modifier = Modifier.testTag("arabic_font_slider")
                    )
                    // Preview
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            fontSize = uiState.arabicFontSize.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald700,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bengali Font Size Slider
                    Text(
                        text = "বাংলা অনুবাদের ফন্ট সাইজ (${uiState.bengaliFontSize.toInt()} sp)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Slider(
                        value = uiState.bengaliFontSize,
                        onValueChange = onSetBengaliFontSize,
                        valueRange = 13f..24f,
                        steps = 10,
                        modifier = Modifier.testTag("bengali_font_slider")
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "পরম করুণাময় অতি দয়ালু আল্লাহর নামে শুরু করছি",
                            fontSize = uiState.bengaliFontSize.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        // Calendar & Moon Sighting Adjustment
        item {
            Text(
                text = "হিজরি ক্যালেন্ডার ও চাঁদ দেখা সমন্বয়",
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
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "স্থানীয় চাঁদ দেখা অনুসারে হিজরি তারিখ সমন্বয় (${if (uiState.hijriAdjustmentDays >= 0) "+${uiState.hijriAdjustmentDays}" else "${uiState.hijriAdjustmentDays}"} দিন)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "বাংলাদেশে চাঁদ দেখার ওপর ভিত্তি করে হিজরি তারিখ ১ বা ২ দিন আগে বা পরে হতে পারে।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(-2, -1, 0, 1, 2).forEach { adj ->
                            FilledTonalButton(
                                onClick = { onSetHijriAdjustment(adj) },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = if (uiState.hijriAdjustmentDays == adj) Emerald700 else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (uiState.hijriAdjustmentDays == adj) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Text(if (adj > 0) "+$adj" else "$adj")
                            }
                        }
                    }
                }
            }
        }

        // Admin Panel Shortcut Button
        item {
            Text(
                text = "প্রশাসনিক প্যানেল ও নিরীক্ষা",
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
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "নিরাপদ অ্যাডমিন পোর্টাল", fontWeight = FontWeight.Bold)
                            Text(
                                text = "ত্রুটি রিপোর্ট যাচাই, কন্টেন্ট অনুমোদন ও নিরীক্ষা লগ",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(
                            onClick = onNavigateToAdmin,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("admin_portal_button")
                        ) {
                            Text("প্রবেশ করুন")
                        }
                    }
                }
            }
        }

        // About & Scholarly Sourcing
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "নূর ইসলামিক (Noor Islamic)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "সংস্করণ: ১.০.০ (প্রোডাকশন সংস্করণ)\n" +
                                "কুরআন পাঠ্য: মদিনা উসমানি লিপি (King Fahd Complex)\n" +
                                "অনুবাদ: মাওলানা মহিউদ্দীন খান ও ইসলামিক ফাউন্ডেশন বাংলাদেশ\n" +
                                "হাদিস: সহীহুল বুখারী ও মুসলিমসহ নির্ভরযোগ্য সিহাহ সিত্তাহ সূত্র\n" +
                                "নামাজের সময়: ইসলামিক ইউনিভার্সিটি করাচি ও উম্মুল কুরা অ্যালগরিদম",
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
