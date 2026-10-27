package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorReportDialog(
    contentType: String,
    recordId: String,
    onDismiss: () -> Unit,
    onSubmit: (issueCategory: String, explanation: String, referenceSource: String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("ARABIC_TEXT") }
    var explanation by remember { mutableStateOf("") }
    var referenceSource by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    val categories = listOf(
        Pair("ARABIC_TEXT", "আরবি হরকত বা বানানের ভুল"),
        Pair("BENGALI_TRANSLATION", "বাংলা অনুবাদে অস্পষ্টতা বা ভুল"),
        Pair("REFERENCE", "রেফারেন্স বা হাদিস নম্বরের অসঙ্গতি"),
        Pair("AUDIO_LINK", "অডিও তেলাওয়াত বা লিঙ্ক ত্রুটি")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "ভুল রিপোর্ট করুন",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "বিষয়: $contentType (আইডি: $recordId)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "সমস্যার ধরন নির্বাচন করুন:",
                    style = MaterialTheme.typography.labelMedium
                )

                categories.forEach { (key, label) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCategory == key,
                            onClick = { selectedCategory = key }
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = explanation,
                    onValueChange = { explanation = it },
                    label = { Text("সমস্যার বিবরণ লিখুন") },
                    placeholder = { Text("কোথায় কী ভুল লক্ষ্য করেছেন...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("report_explanation_input"),
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = referenceSource,
                    onValueChange = { referenceSource = it },
                    label = { Text("প্রামাণ্য রেফারেন্স (ঐচ্ছিক)") },
                    placeholder = { Text("মুসহাফের পৃষ্ঠা, কিতাবের নাম...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("report_reference_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (explanation.isNotBlank()) {
                        isSubmitting = true
                        onSubmit(selectedCategory, explanation, referenceSource)
                    }
                },
                enabled = explanation.isNotBlank() && !isSubmitting,
                modifier = Modifier.testTag("submit_report_button")
            ) {
                Text("জমা দিন")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_report_button")
            ) {
                Text("বাতিল")
            }
        }
    )
}
