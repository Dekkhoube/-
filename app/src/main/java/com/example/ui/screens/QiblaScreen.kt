package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    qiblaBearing: Float,
    deviceHeading: Float,
    qiblaRelativeAngle: Float,
    distanceKm: Double,
    accuracy: String,
    cityName: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val animatedHeading by animateFloatAsState(
        targetValue = -deviceHeading,
        animationSpec = tween(durationMillis = 200),
        label = "compass_rotation"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("কিবলা কম্পাস", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("qibla_back_button")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .testTag("qibla_screen_content"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Location and Qibla target info
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "আপনার অবস্থান: $cityName",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "কিবলার দিক: ${qiblaBearing.roundToInt()}° উত্তর-পশ্চিম",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "দূরত্ব",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${distanceKm.roundToInt()} কিমি",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = GoldDark
                        )
                    }
                }
            }

            // Compass Dial Box
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .testTag("compass_dial"),
                contentAlignment = Alignment.Center
            ) {
                // Outer ring
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(animatedHeading)
                ) {
                    val radius = size.minDimension / 2
                    val center = Offset(size.width / 2, size.height / 2)

                    // Draw outer border circle
                    drawCircle(
                        color = GoldAccent,
                        radius = radius - 4.dp.toPx(),
                        center = center,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
                    )

                    // Graduations
                    for (i in 0 until 360 step 30) {
                        val angleRad = Math.toRadians((i - 90).toDouble())
                        val isCardinal = i % 90 == 0
                        val tickLength = if (isCardinal) 16.dp.toPx() else 8.dp.toPx()
                        val startX = center.x + (radius - 8.dp.toPx() - tickLength) * kotlin.math.cos(angleRad).toFloat()
                        val startY = center.y + (radius - 8.dp.toPx() - tickLength) * kotlin.math.sin(angleRad).toFloat()
                        val endX = center.x + (radius - 8.dp.toPx()) * kotlin.math.cos(angleRad).toFloat()
                        val endY = center.y + (radius - 8.dp.toPx()) * kotlin.math.sin(angleRad).toFloat()

                        drawLine(
                            color = if (isCardinal) Emerald700 else Color.Gray.copy(alpha = 0.5f),
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = if (isCardinal) 3.dp.toPx() else 1.5.dp.toPx()
                        )
                    }
                }

                // Kaaba Direction Marker Pointer
                val qiblaRelative = (animatedHeading + qiblaBearing)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(qiblaRelative),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mosque,
                            contentDescription = "Kaaba Direction",
                            tint = GoldAccent,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "কা'বা",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald700
                        )
                    }
                }

                // Center Compass Hub
                Surface(
                    modifier = Modifier.size(46.dp),
                    shape = CircleShape,
                    color = Emerald900,
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                            tint = GoldLight,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Accuracy & Calibration Advice
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "সেন্সর সঠিকতা: $accuracy",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "সঠিক কিবলার দিক পেতে ফোনটি অনুভূমিকভাবে সোজা রাখুন এবং প্রয়োজনে শূন্যে '৮' আকৃতিতে কয়েকবার ঘুরিয়ে ক্যালিব্রেট করুন। ধাতব বস্তু ও চুম্বক থেকে দূরে রাখুন।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
