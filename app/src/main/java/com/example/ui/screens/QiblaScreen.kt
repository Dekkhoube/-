package com.example.ui.screens

import android.graphics.Paint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald900
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    qiblaBearing: Float,
    deviceHeading: Float,
    qiblaRelativeAngle: Float,
    distanceKm: Double,
    accuracy: String,
    cityName: String,
    hasCompassSensor: Boolean = true,
    isFacingQibla: Boolean = false,
    onStartCompass: () -> Unit = {},
    onStopCompass: () -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    // Start compass on enter, stop on exit
    DisposableEffect(Unit) {
        onStartCompass()
        onDispose { onStopCompass() }
    }

    // Smooth continuous dial rotation (shortest angular path)
    var smoothedTargetHeading by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(deviceHeading) {
        val diff = (deviceHeading - smoothedTargetHeading + 540f) % 360f - 180f
        smoothedTargetHeading += diff
    }

    val animatedDialRotation by animateFloatAsState(
        targetValue = -smoothedTargetHeading,
        animationSpec = tween(durationMillis = 150),
        label = "compass_dial_rotation"
    )

    // Calculate angle deviation from Qibla
    val rawDiff = ((qiblaBearing - deviceHeading + 540f) % 360f) - 180f
    val deviation = abs(rawDiff).roundToInt()
    val isAligned = isFacingQibla || deviation <= 4

    val dialBorderColor by animateColorAsState(
        targetValue = if (isAligned) GoldLight else GoldAccent.copy(alpha = 0.6f),
        animationSpec = tween(durationMillis = 300),
        label = "dial_border"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "কিবলা কম্পাস",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "কা'বা শরীফের সঠিক দিক ও দূরত্ব",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("qibla_back_button")) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .testTag("qibla_screen_content"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // Top Status & Navigation Cards
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
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
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Emerald700,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "অবস্থান: $cityName",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mosque,
                                contentDescription = null,
                                tint = GoldDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "দূরত্ব: ${distanceKm.roundToInt()} কিমি",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = GoldDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Realtime Alignment Feedback
                    if (isAligned) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Emerald800
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = GoldLight,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "মাশাআল্লাহ! আপনি এখন কিবলামুখী (কা'বার মুখোমুখি)",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "কিবলার কোণ: ${qiblaBearing.roundToInt()}° উত্তর-পশ্চিম",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                val directionHint = if (rawDiff > 0) "ডানে $deviation° ঘোরান" else "বামে $deviation° ঘোরান"
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primary
                                ) {
                                    Text(
                                        text = directionHint,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- THE LUXURY ASTROLABE COMPASS DIAL ---
            Box(
                modifier = Modifier
                    .size(310.dp)
                    .shadow(12.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Emerald900,
                                Color(0xFF022C22),
                                Color(0xFF011A14)
                            )
                        )
                    )
                    .border(4.dp, dialBorderColor, CircleShape)
                    .testTag("compass_dial"),
                contentAlignment = Alignment.Center
            ) {
                // 1. Rotating Dial Canvas (Degrees, Cardinal Points & Marks)
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(animatedDialRotation)
                ) {
                    val radius = size.minDimension / 2f
                    val center = Offset(size.width / 2f, size.height / 2f)

                    // Outer Astrolabe Concentric Ring
                    drawCircle(
                        color = GoldAccent.copy(alpha = 0.35f),
                        radius = radius - 8.dp.toPx(),
                        center = center,
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    drawCircle(
                        color = GoldAccent.copy(alpha = 0.18f),
                        radius = radius - 40.dp.toPx(),
                        center = center,
                        style = Stroke(width = 1.dp.toPx())
                    )

                    val paintDegree = Paint().apply {
                        color = android.graphics.Color.parseColor("#E2E8F0")
                        textSize = 9.dp.toPx()
                        textAlign = Paint.Align.CENTER
                        isAntiAlias = true
                    }

                    val paintCardinal = Paint().apply {
                        color = android.graphics.Color.parseColor("#FDE68A")
                        textSize = 14.dp.toPx()
                        textAlign = Paint.Align.CENTER
                        isFakeBoldText = true
                        isAntiAlias = true
                    }

                    val paintNorth = Paint().apply {
                        color = android.graphics.Color.parseColor("#EF4444")
                        textSize = 15.dp.toPx()
                        textAlign = Paint.Align.CENTER
                        isFakeBoldText = true
                        isAntiAlias = true
                    }

                    // Draw 360-degree ticks and labels
                    for (deg in 0 until 360 step 5) {
                        val angleRad = Math.toRadians((deg - 90).toDouble())
                        val isCardinal = deg % 90 == 0
                        val isMajor = deg % 30 == 0
                        val isMedium = deg % 10 == 0

                        val tickLength = when {
                            isCardinal -> 16.dp.toPx()
                            isMajor -> 12.dp.toPx()
                            isMedium -> 8.dp.toPx()
                            else -> 4.dp.toPx()
                        }

                        val strokeW = when {
                            isCardinal -> 2.5.dp.toPx()
                            isMajor -> 1.8.dp.toPx()
                            isMedium -> 1.2.dp.toPx()
                            else -> 0.8.dp.toPx()
                        }

                        val tickColor = when {
                            isCardinal -> if (deg == 0) Color(0xFFEF4444) else GoldLight
                            isMajor -> GoldAccent.copy(alpha = 0.8f)
                            isMedium -> Color.White.copy(alpha = 0.6f)
                            else -> Color.White.copy(alpha = 0.25f)
                        }

                        val startDist = radius - 10.dp.toPx() - tickLength
                        val endDist = radius - 10.dp.toPx()

                        val startX = center.x + startDist * cos(angleRad).toFloat()
                        val startY = center.y + startDist * sin(angleRad).toFloat()
                        val endX = center.x + endDist * cos(angleRad).toFloat()
                        val endY = center.y + endDist * sin(angleRad).toFloat()

                        drawLine(
                            color = tickColor,
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = strokeW
                        )

                        // Draw Cardinal Directions & Degree Numbers
                        if (isCardinal) {
                            val labelDist = radius - 26.dp.toPx()
                            val lx = center.x + labelDist * cos(angleRad).toFloat()
                            val ly = center.y + labelDist * sin(angleRad).toFloat() + 5.dp.toPx()

                            val (labelBn, p) = when (deg) {
                                0 -> Pair("উ (N)", paintNorth)
                                90 -> Pair("পূ (E)", paintCardinal)
                                180 -> Pair("দ (S)", paintCardinal)
                                else -> Pair("প (W)", paintCardinal)
                            }
                            drawContext.canvas.nativeCanvas.drawText(labelBn, lx, ly, p)
                        } else if (isMajor) {
                            val degDist = radius - 25.dp.toPx()
                            val dx = center.x + degDist * cos(angleRad).toFloat()
                            val dy = center.y + degDist * sin(angleRad).toFloat() + 3.dp.toPx()
                            drawContext.canvas.nativeCanvas.drawText("$deg°", dx, dy, paintDegree)
                        }
                    }
                }

                // 2. Qibla Target Beam & Kaaba Icon (Rotates with Qibla bearing)
                val qiblaMarkerAngle = animatedDialRotation + qiblaBearing
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(qiblaMarkerAngle),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isAligned) Emerald700 else GoldDark,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldLight),
                            shadowElevation = 6.dp
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mosque,
                                    contentDescription = "Kaaba Direction",
                                    tint = GoldLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "কা'বা",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Alignment Arrow pointer to dial edge
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = if (isAligned) GoldLight else GoldAccent,
                            modifier = Modifier
                                .size(24.dp)
                                .offset(y = (-4).dp)
                        )
                    }
                }

                // 3. Central Magnetic 3D Needle
                Canvas(
                    modifier = Modifier.size(170.dp)
                ) {
                    val w = size.width
                    val h = size.height
                    val cx = w / 2f
                    val cy = h / 2f
                    val needleWidth = 12.dp.toPx()

                    // North Needle (Red & Gold)
                    val northPathLeft = Path().apply {
                        moveTo(cx, 10.dp.toPx())
                        lineTo(cx - needleWidth / 2f, cy)
                        lineTo(cx, cy)
                        close()
                    }
                    drawPath(northPathLeft, Color(0xFFEF4444))

                    val northPathRight = Path().apply {
                        moveTo(cx, 10.dp.toPx())
                        lineTo(cx + needleWidth / 2f, cy)
                        lineTo(cx, cy)
                        close()
                    }
                    drawPath(northPathRight, Color(0xFFDC2626))

                    // South Needle (Silver & Dark Charcoal)
                    val southPathLeft = Path().apply {
                        moveTo(cx, h - 10.dp.toPx())
                        lineTo(cx - needleWidth / 2f, cy)
                        lineTo(cx, cy)
                        close()
                    }
                    drawPath(southPathLeft, Color(0xFF94A3B8))

                    val southPathRight = Path().apply {
                        moveTo(cx, h - 10.dp.toPx())
                        lineTo(cx + needleWidth / 2f, cy)
                        lineTo(cx, cy)
                        close()
                    }
                    drawPath(southPathRight, Color(0xFF64748B))

                    // Inner decorative guideline
                    drawLine(
                        color = GoldAccent.copy(alpha = 0.5f),
                        start = Offset(cx, 16.dp.toPx()),
                        end = Offset(cx, h - 16.dp.toPx()),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // 4. Center Golden Jewel Pivot
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color = GoldDark,
                    border = androidx.compose.foundation.BorderStroke(2.dp, GoldLight),
                    shadowElevation = 8.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.background(
                            Brush.radialGradient(
                                colors = listOf(GoldLight, GoldDark, Emerald900)
                            )
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Real-time Heading Numbers
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "বর্তমান কোণ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${deviceHeading.roundToInt()}°",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    VerticalDivider(modifier = Modifier.height(30.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "কিবলার দিক",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${qiblaBearing.roundToInt()}°",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldDark
                        )
                    }

                    VerticalDivider(modifier = Modifier.height(30.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "দিক পার্থক্য",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$deviation°",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAligned) Emerald700 else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Missing Sensor Warning (if phone hardware lacks compass/magnetometer)
            if (!hasCompassSensor) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "ডিজিটাল কম্পাস সেন্সর সনাক্ত হয়নি",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "আপনার ডিভাইসে ম্যাগনেটোমিটার সেন্সর অনুপস্থিত থাকায় কম্পাস স্বয়ংক্রিয়ভাবে ঘুরতে পারছে না। উপরের নির্দেশিকা অনুযায়ী কা'বার প্রকৃত ভৌগোলিক দিক ${qiblaBearing.roundToInt()}° উত্তর-পশ্চিম ধরে নামাজ আদায় করুন।",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            // Accuracy & Calibration Advice
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Emerald700,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "সেন্সর অবস্থা: $accuracy",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Emerald700
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "নির্ভুল দিক পেতে ফোনটি হাতের তালুতে অনুভূমিকভাবে সমতলে (Flat) রাখুন। ধাতব বস্তু বা ইলেকট্রনিক্স থেকে দূরে থাকুন এবং প্রয়োজনে বাতাসে ফোনটিকে '৮' (Figure-8) আকৃতিতে কয়েকবার ঘুরিয়ে নিন।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
