package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TasbihDhikrEntity
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbihScreen(
    tasbihList: List<TasbihDhikrEntity>,
    onIncrement: (TasbihDhikrEntity) -> Unit,
    onUndo: (TasbihDhikrEntity) -> Unit,
    onReset: (TasbihDhikrEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDhikrId by remember { mutableStateOf("subhanallah") }
    var showResetDialog by remember { mutableStateOf(false) }

    val currentDhikr = tasbihList.find { it.id == selectedDhikrId }
        ?: tasbihList.firstOrNull()
        ?: TasbihDhikrEntity("subhanallah", "سُبْحَانَ اللَّهِ", "সুবহানাল্লাহ", "SubhanAllah", 0, 33, 0)

    val progress = remember(currentDhikr.currentCount, currentDhikr.targetCount) {
        if (currentDhikr.targetCount > 0) {
            (currentDhikr.currentCount.toFloat() / currentDhikr.targetCount).coerceIn(0f, 1f)
        } else 0f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 150),
        label = "tasbih_progress"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("tasbih_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Dhikr Selection Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasbihList) { dhikr ->
                val isSelected = dhikr.id == currentDhikr.id
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedDhikrId = dhikr.id },
                    label = { Text(dhikr.bengaliText, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        // Active Dhikr Title & Arabic
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = currentDhikr.arabicText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Emerald700,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = currentDhikr.transliteration,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = "লক্ষ্যমাত্রা: ${currentDhikr.targetCount} বার",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }

        // Giant Circular Tap Counter Button
        Box(
            modifier = Modifier
                .size(260.dp)
                .testTag("tasbih_tap_button")
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onIncrement(currentDhikr)
                },
            contentAlignment = Alignment.Center
        ) {
            // Circular Progress Ring
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 14.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val center = Offset(size.width / 2, size.height / 2)

                // Track Background
                drawCircle(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = strokeWidth)
                )

                // Progress Arc
                drawArc(
                    brush = Brush.sweepGradient(listOf(Emerald700, GoldAccent, Emerald700)),
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // Inner Tap Surface
            Surface(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape),
                shape = CircleShape,
                color = Emerald800,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Emerald700, Emerald900)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${currentDhikr.currentCount}",
                            fontSize = 56.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldLight
                        )
                        Text(
                            text = "ট্যাপ করুন",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Total count & Reset/Undo Action Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "মোট জিকির সংখ্যা",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${currentDhikr.totalCount} বার",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Undo Button
                    FilledTonalIconButton(
                        onClick = { onUndo(currentDhikr) },
                        modifier = Modifier.testTag("tasbih_undo_button")
                    ) {
                        Icon(Icons.Default.Undo, contentDescription = "Undo")
                    }

                    // Reset Button
                    FilledTonalIconButton(
                        onClick = { showResetDialog = true },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.testTag("tasbih_reset_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    }
                }
            }
        }
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("গণনা রিসেট করবেন?") },
            text = { Text("আপনি কি '${currentDhikr.bengaliText}' এর বর্তমান গণনা শূন্য (০) করতে চান?") },
            confirmButton = {
                Button(
                    onClick = {
                        onReset(currentDhikr)
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("হ্যাঁ, রিসেট করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}
