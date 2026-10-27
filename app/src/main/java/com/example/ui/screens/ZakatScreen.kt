package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ZakatCalculation
import com.example.data.repository.AppFeatureRepository
import com.example.ui.theme.Emerald700
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZakatScreen(
    featureRepository: AppFeatureRepository,
    modifier: Modifier = Modifier
) {
    var cashInHand by remember { mutableStateOf("") }
    var cashInBank by remember { mutableStateOf("") }
    var goldGrams by remember { mutableStateOf("") }
    var silverGrams by remember { mutableStateOf("") }
    var businessStock by remember { mutableStateOf("") }
    var receivables by remember { mutableStateOf("") }
    var liabilities by remember { mutableStateOf("") }

    // Standard baseline prices in BDT per gram (editable)
    var goldPricePerGram by remember { mutableStateOf("11500") } // ~1,34,000 BDT per Bhori/Tola (11.664g)
    var silverPricePerGram by remember { mutableStateOf("180") }  // ~2,100 BDT per Bhori/Tola
    var useSilverNisab by remember { mutableStateOf(true) }

    val calculation: ZakatCalculation = remember(
        cashInHand, cashInBank, goldGrams, silverGrams, businessStock, receivables, liabilities,
        goldPricePerGram, silverPricePerGram, useSilverNisab
    ) {
        featureRepository.calculateZakat(
            cashInHand = cashInHand.toDoubleOrNull() ?: 0.0,
            cashInBank = cashInBank.toDoubleOrNull() ?: 0.0,
            goldGrams = goldGrams.toDoubleOrNull() ?: 0.0,
            goldPricePerGram = goldPricePerGram.toDoubleOrNull() ?: 11500.0,
            silverGrams = silverGrams.toDoubleOrNull() ?: 0.0,
            silverPricePerGram = silverPricePerGram.toDoubleOrNull() ?: 180.0,
            businessStock = businessStock.toDoubleOrNull() ?: 0.0,
            receivables = receivables.toDoubleOrNull() ?: 0.0,
            debts = liabilities.toDoubleOrNull() ?: 0.0,
            useSilverNisab = useSilverNisab
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("zakat_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Calculation Result Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (calculation.isZakatObligatory) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (calculation.isZakatObligatory) "আপনার ওপর যাকাত ফরজ হয়েছে" else "নেসাব পরিমাণ সম্পদ পূর্ণ হয়নি",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (calculation.isZakatObligatory) Emerald700 else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "প্রদেয় যাকাত (২.৫%):",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "৳ ${String.format(Locale.US, "%,.2f", calculation.zakatPayableBdt)}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (calculation.isZakatObligatory) Emerald700 else MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "মোট যাকাতযোগ্য সম্পদ:", style = MaterialTheme.typography.bodySmall)
                        Text(text = "৳ ${String.format(Locale.US, "%,.0f", calculation.netZakatableBdt)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "বর্তমান নেসাব সীমা:", style = MaterialTheme.typography.bodySmall)
                        Text(text = "৳ ${String.format(Locale.US, "%,.0f", calculation.nisabThresholdBdt)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // Nisab Standard Selector
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "নেসাবের ভিত্তি নির্বাচন:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = useSilverNisab,
                            onClick = { useSilverNisab = true }
                        )
                        Text(text = "রূপার নেসাব (৫২.৫ তোলা / ৬১২.৩৬ গ্রাম - দরিদ্রদের জন্য অধিক উপকারী)", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = !useSilverNisab,
                            onClick = { useSilverNisab = false }
                        )
                        Text(text = "স্বর্ণের নেসাব (৭.৫ তোলা / ৮৭.৪৮ গ্রাম)", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // Asset Input Fields
        item {
            Text(
                text = "সম্পদের বিবরণ (টাকায়)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            OutlinedTextField(
                value = cashInHand,
                onValueChange = { cashInHand = it },
                label = { Text("হাতে থাকা নগদ টাকা") },
                placeholder = { Text("০.০০") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("zakat_cash_hand_input"),
                shape = RoundedCornerShape(10.dp)
            )
        }

        item {
            OutlinedTextField(
                value = cashInBank,
                onValueChange = { cashInBank = it },
                label = { Text("ব্যাংক বা সঞ্চয়ে থাকা টাকা") },
                placeholder = { Text("০.০০") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("zakat_cash_bank_input"),
                shape = RoundedCornerShape(10.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = goldGrams,
                    onValueChange = { goldGrams = it },
                    label = { Text("স্বর্ণ (গ্রাম)") },
                    placeholder = { Text("০ গ্রাম") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = silverGrams,
                    onValueChange = { silverGrams = it },
                    label = { Text("রূপা (গ্রাম)") },
                    placeholder = { Text("০ গ্রাম") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        item {
            OutlinedTextField(
                value = businessStock,
                onValueChange = { businessStock = it },
                label = { Text("ব্যবসায়িক পণ্য ও স্টক মূল্য") },
                placeholder = { Text("০.০০") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
        }

        item {
            OutlinedTextField(
                value = receivables,
                onValueChange = { receivables = it },
                label = { Text("প্রাপ্য ঋণ (অন্যের কাছে পাওনা যা আদায়যোগ্য)") },
                placeholder = { Text("০.০০") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
        }

        item {
            OutlinedTextField(
                value = liabilities,
                onValueChange = { liabilities = it },
                label = { Text("বকেয়া ঋণ ও দায় (বাদ যাবে)") },
                placeholder = { Text("০.০০") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
        }

        // Fiqh note
        item {
            Text(
                text = "শরী'আহ দ্রষ্টব্য: নেসাব পরিমাণ সম্পদ এক চন্দ্রবছর পূর্ণ মালিকানায় থাকলে তার ওপর ২.৫% হারে যাকাত প্রদান করা ফরজ। ব্যক্তিগত ব্যবহারের বাসস্থান, পরিধেয় বস্ত্র ও ঘরের নিত্যপ্রয়োজনীয় আসবাবপত্রে যাকাত নেই।",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }
    }
}
