package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.RisaleRepository

data class AcademyQuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RisaleAcademyScreen(
    onNavigateToLibrary: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val readingGoal by RisaleRepository.readingGoal.collectAsState()
    var dailyTarget by remember { mutableFloatStateOf(readingGoal.dailyTargetPages.toFloat()) }

    val quizzes = remember {
        listOf(
            AcademyQuizQuestion(
                id = 1,
                question = "রিসালে-ই নূরের প্রথম বাণী অনুসারে 'বিসমিল্লাহ' শব্দের তাৎপর্য কী?",
                options = listOf(
                    "একটি সাধারণ প্রথা",
                    "গোটা সৃষ্টিজগতের মৌন ভাষার অবিরাম জিকির ও অসীম শক্তির চাবি",
                    "শুধুমাত্র খাদ্য গ্রহণের পূর্বের বাক্য",
                    "একটি ঐতিহাসিক প্রবাদ"
                ),
                correctIndex = 1,
                explanation = "বিসমিল্লাহ এমন এক সুমহান চাবি যা দিয়ে একজন দুর্বল সৃষ্টি আল্লাহর অসীম রহমতের শক্তির সাথে সংযুক্ত হয়।"
            ),
            AcademyQuizQuestion(
                id = 2,
                question = "হাসতালার রিসালেসি (অসুস্থদের সান্ত্বনা) অনুসারে রোগব্যাধির প্রধান আধ্যাত্মিক সুফল কী?",
                options = listOf(
                    "শুধুমাত্র শারীরিক ক্ষতি",
                    "পাপ মার্জনা ও আল্লাহর দরবারে আত্মিক বিনয় অর্জন",
                    "পৃথিবী থেকে বিচ্ছিন্নতা",
                    "ভাগ্যের বিরুদ্ধে ক্ষোভ"
                ),
                correctIndex = 1,
                explanation = "রোগব্যাধি মানুষের অন্তরে অহংকার চূর্ণ করে, অতীত গুনাহ মার্জনা করে এবং আত্মিক পরিশুদ্ধি ঘটায়।"
            ),
            AcademyQuizQuestion(
                id = 3,
                question = "দ্বীনি কর্মে 'ইখলাস' রক্ষার মৌলিক শর্ত কী?",
                options = listOf(
                    "মানুষের প্রশংসা ও সংখ্যাধিক্য অর্জন",
                    "শুধুমাত্র এবং একচ্ছত্রভাবে আল্লাহর সন্তুষ্টির সন্ধান করা",
                    "অন্যান্য ইসলামি কর্মীদের সাথে প্রতিযোগিতায় লিপ্ত হওয়া",
                    "দুনিয়াবি খ্যাতি বৃদ্ধি করা"
                ),
                correctIndex = 1,
                explanation = "আমলে একমাত্র লক্ষ্য হতে হবে আল্লাহর সন্তুষ্টি। যদি তিনি সন্তুষ্ট হন, তবে সারা পৃথিবী অসন্তুষ্ট হলেও কোনো ক্ষতি নেই।"
            )
        )
    }

    var selectedAnswers by remember { mutableStateOf(mutableMapOf<Int, Int>()) }
    var showResults by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ইসলামিক পাঠশালা ও একাডেমি",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "নিয়মিত অধ্যয়ন ট্র্যাকার ও জ্ঞান যাচাই কুইজ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_academy_back")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "পিছনে যান")
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Daily Goal & Streak Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "দৈনিক অধ্যয়ন লক্ষ্য",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = Color(0xFFFF6D00)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "${readingGoal.currentStreakDays} দিন",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "প্রতিদিন লক্ষ্য: ${dailyTarget.toInt()} পৃষ্ঠা",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )

                        Slider(
                            value = dailyTarget,
                            onValueChange = { dailyTarget = it },
                            valueRange = 5f..50f,
                            steps = 8
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("মোট পঠিত: ${readingGoal.totalPagesRead} পৃষ্ঠা", style = MaterialTheme.typography.bodySmall)
                            Text("সম্পূর্ণ গ্রন্থ: ${readingGoal.completedBooksCount}টি", style = MaterialTheme.typography.bodySmall)
                        }

                        Spacer(Modifier.height(10.dp))

                        Button(
                            onClick = onNavigateToLibrary,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("লাইব্রেরিতে অধ্যয়ন শুরু করুন")
                        }
                    }
                }
            }

            // Knowledge Quiz Section
            item {
                Text(
                    text = "জ্ঞান যাচাই ও পর্যালোচনা কুইজ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(quizzes.size) { index ->
                val quiz = quizzes[index]
                val selectedOption = selectedAnswers[quiz.id]

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "প্রশ্ন ${index + 1}: ${quiz.question}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp
                        )

                        Spacer(Modifier.height(10.dp))

                        quiz.options.forEachIndexed { optIdx, optText ->
                            val isSelected = selectedOption == optIdx
                            val isCorrect = optIdx == quiz.correctIndex
                            val showFeedback = showResults && isSelected

                            val bgColor = when {
                                showResults && isCorrect -> Color(0xFFE8F5E9)
                                showResults && isSelected && !isCorrect -> Color(0xFFFFEBEE)
                                isSelected -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            }

                            Surface(
                                onClick = {
                                    if (!showResults) {
                                        val newMap = selectedAnswers.toMutableMap()
                                        newMap[quiz.id] = optIdx
                                        selectedAnswers = newMap
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = bgColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = null
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = optText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }

                        if (showResults) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "ব্যাখ্যা: ${quiz.explanation}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Submit / Reset Quiz Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { showResults = !showResults },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (showResults) "পুনরায় চেষ্টা করুন" else "ফলাফল দেখুন")
                    }

                    if (showResults) {
                        OutlinedButton(
                            onClick = {
                                selectedAnswers = mutableMapOf()
                                showResults = false
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("রিসেট")
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
