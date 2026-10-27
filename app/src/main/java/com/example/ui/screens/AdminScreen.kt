package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdminContentEntity
import com.example.data.model.AuditLogEntity
import com.example.data.model.ContentCorrectionEntity
import com.example.data.model.ErrorReportEntity
import com.example.ui.theme.Emerald700
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.WarningAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    currentAdminUser: String,
    currentAdminRole: String,
    errorReports: List<ErrorReportEntity>,
    correctionProposals: List<ContentCorrectionEntity>,
    auditLogs: List<AuditLogEntity>,
    adminContent: List<AdminContentEntity> = emptyList(),
    onUpdateReportStatus: (Long, String, String) -> Unit,
    onSubmitCorrectionProposal: (String, String, String, String, String, String) -> Unit,
    onReviewProposal: (Long, String, String) -> Unit,
    onAddContent: (String, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onDeleteContent: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Content Manager (Add/Delete), 1: Error Reports, 2: Correction Proposals, 3: Audit Logs
    var showNewContentDialog by remember { mutableStateOf(false) }
    var showNewProposalDialog by remember { mutableStateOf(false) }
    var selectedContentTypeFilter by remember { mutableStateOf("ALL") }

    val filteredContent = remember(adminContent, selectedContentTypeFilter) {
        if (selectedContentTypeFilter == "ALL") adminContent
        else adminContent.filter { it.contentType == selectedContentTypeFilter }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_screen")
    ) {
        // Admin Header Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = "অ্যাডমিন ও কন্টেন্ট কন্ট্রোল প্যানেল",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentAdminUser,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = currentAdminRole,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Tab Row (Scrollable to comfortably fit all 4 tabs)
        PrimaryScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 16.dp
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("কন্টেন্ট যোগ/মুছুন (${adminContent.size})", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("admin_tab_content_mgr")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("ত্রুটি রিপোর্ট (${errorReports.size})", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("admin_tab_reports")
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("সংশোধন প্রস্তাব (${correctionProposals.size})", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("admin_tab_corrections")
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("নিরীক্ষা লগ (${auditLogs.size})", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("admin_tab_audit")
            )
        }

        when (selectedTab) {
            0 -> {
                // Tab 0: Content Manager (Add and Delete directly)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "অ্যাপের কন্টেন্ট নিয়ন্ত্রণ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "চাইলেই নতুন কিছু যোগ করুন বা মুছে ফেলুন",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { showNewContentDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("admin_add_content_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("যোগ করুন", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Type filter chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedContentTypeFilter == "ALL",
                            onClick = { selectedContentTypeFilter = "ALL" },
                            label = { Text("সকল") }
                        )
                        FilterChip(
                            selected = selectedContentTypeFilter == "NOTICE",
                            onClick = { selectedContentTypeFilter = "NOTICE" },
                            label = { Text("নোটিশ") }
                        )
                        FilterChip(
                            selected = selectedContentTypeFilter == "DUA",
                            onClick = { selectedContentTypeFilter = "DUA" },
                            label = { Text("দোয়া") }
                        )
                        FilterChip(
                            selected = selectedContentTypeFilter == "HADITH",
                            onClick = { selectedContentTypeFilter = "HADITH" },
                            label = { Text("হাদিস") }
                        )
                        FilterChip(
                            selected = selectedContentTypeFilter == "MOSQUE",
                            onClick = { selectedContentTypeFilter = "MOSQUE" },
                            label = { Text("মসজিদ") }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (filteredContent.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.PostAdd,
                                    contentDescription = null,
                                    modifier = Modifier.size(54.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "কোনো কাস্টম কন্টেন্ট পাওয়া যায়নি।",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "উপরের 'যোগ করুন' বাটনে ক্লিক করে নোটিশ, দোয়া, হাদিস ইত্যাদি যোগ করতে পারেন।",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredContent, key = { it.id }) { item ->
                                AdminContentCard(
                                    item = item,
                                    onDelete = { onDeleteContent(item.id) }
                                )
                            }
                        }
                    }
                }
            }

            1 -> {
                // Tab 1: User Error Reports
                if (errorReports.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "কোনো অমীমাংসিত ব্যবহারকারী ত্রুটি রিপোর্ট নেই।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(errorReports, key = { it.id }) { report ->
                            ErrorReportAdminCard(
                                report = report,
                                onUpdateStatus = { newStatus, notes ->
                                    onUpdateReportStatus(report.id, newStatus, notes)
                                }
                            )
                        }
                    }
                }
            }

            2 -> {
                // Tab 2: Correction Proposals
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "পদ্ধতিগত সংশোধন প্রস্তাবসমূহ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = { showNewProposalDialog = true },
                            modifier = Modifier.testTag("admin_propose_correction_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("নতুন প্রস্তাব")
                        }
                    }

                    if (correctionProposals.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "কোনো কন্টেন্ট সংশোধন প্রস্তাব জমা পড়েনি।",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(correctionProposals, key = { it.id }) { proposal ->
                                CorrectionProposalCard(
                                    proposal = proposal,
                                    canReview = currentAdminRole == "SUPER_ADMIN",
                                    onReview = { status, reason ->
                                        onReviewProposal(proposal.id, status, reason)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            3 -> {
                // Tab 3: Audit Logs
                if (auditLogs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "কোনো নিরীক্ষা কার্যক্রম এখনও রেকর্ড হয়নি।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(auditLogs, key = { it.id }) { log ->
                            AuditLogCard(log = log)
                        }
                    }
                }
            }
        }
    }

    // Dialog for Adding New Content
    if (showNewContentDialog) {
        AddNewContentDialog(
            onDismiss = { showNewContentDialog = false },
            onSubmit = { type, title, arabic, bengali, source ->
                onAddContent(type, title, arabic, bengali, source)
                showNewContentDialog = false
            }
        )
    }

    // Dialog for Proposing Corrections
    if (showNewProposalDialog) {
        NewProposalDialog(
            onDismiss = { showNewProposalDialog = false },
            onSubmit = { type, recId, field, oldV, newV, src ->
                onSubmitCorrectionProposal(type, recId, field, oldV, newV, src)
                showNewProposalDialog = false
            }
        )
    }
}

@Composable
fun AdminContentCard(
    item: AdminContentEntity,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = when (item.contentType) {
                        "NOTICE" -> MaterialTheme.colorScheme.primaryContainer
                        "DUA" -> Emerald700.copy(alpha = 0.15f)
                        "HADITH" -> GoldAccent.copy(alpha = 0.2f)
                        "MOSQUE" -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = when (item.contentType) {
                            "NOTICE" -> "জরুরি নোটিশ"
                            "DUA" -> "দোয়া"
                            "HADITH" -> "হাদিস"
                            "MOSQUE" -> "মসজিদ"
                            "EVENT" -> "ইভেন্ট"
                            else -> item.contentType
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Item",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.titleBengali,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            if (item.contentArabic.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.contentArabic,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Emerald700,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (item.contentBengali.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.contentBengali,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (item.categoryOrSource.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "সূত্র/উৎস: ${item.categoryOrSource}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("কন্টেন্ট মুছে ফেলুন") },
            text = { Text("আপনি কি নিশ্চিতভাবে '${item.titleBengali}' কন্টেন্টটি মুছে ফেলতে চান? এটি তাৎক্ষণিকভাবে অ্যাপ থেকে ডিলিট হয়ে যাবে।") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("মুছে ফেলুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
fun AddNewContentDialog(
    onDismiss: () -> Unit,
    onSubmit: (type: String, title: String, arabic: String, bengali: String, source: String) -> Unit
) {
    var selectedType by remember { mutableStateOf("NOTICE") }
    var title by remember { mutableStateOf("") }
    var arabic by remember { mutableStateOf("") }
    var bengali by remember { mutableStateOf("") }
    var source by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন কন্টেন্ট যোগ করুন", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "কন্টেন্টের ধরন:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilterChip(
                        selected = selectedType == "NOTICE",
                        onClick = { selectedType = "NOTICE" },
                        label = { Text("নোটিশ") }
                    )
                    FilterChip(
                        selected = selectedType == "DUA",
                        onClick = { selectedType = "DUA" },
                        label = { Text("দোয়া") }
                    )
                    FilterChip(
                        selected = selectedType == "HADITH",
                        onClick = { selectedType = "HADITH" },
                        label = { Text("হাদিস") }
                    )
                    FilterChip(
                        selected = selectedType == "MOSQUE",
                        onClick = { selectedType = "MOSQUE" },
                        label = { Text("মসজিদ") }
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("শিরোনাম *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (selectedType != "NOTICE" && selectedType != "MOSQUE") {
                    OutlinedTextField(
                        value = arabic,
                        onValueChange = { arabic = it },
                        label = { Text("আরবি পাঠ (ঐচ্ছিক)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = bengali,
                    onValueChange = { bengali = it },
                    label = { Text("বাংলা বিবরণ / অর্থ *") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                OutlinedTextField(
                    value = source,
                    onValueChange = { source = it },
                    label = { Text("উৎস / লেখক / ঠিকানা (ঐচ্ছিক)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSubmit(selectedType, title, arabic, bengali, source)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("যোগ করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}

@Composable
fun ErrorReportAdminCard(
    report: ErrorReportEntity,
    onUpdateStatus: (String, String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
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
                    text = "${report.contentType} • ${report.recordId}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (report.status) {
                        "PENDING" -> WarningAmber.copy(alpha = 0.2f)
                        "RESOLVED" -> Emerald700.copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = report.status,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (report.status) {
                            "PENDING" -> WarningAmber
                            "RESOLVED" -> Emerald700
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "সমস্যার ধরন: ${report.issueCategory}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            Text(text = report.userExplanation, style = MaterialTheme.typography.bodyMedium)
            if (report.referenceSource.isNotBlank()) {
                Text(text = "সূত্র: ${report.referenceSource}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("স্ট্যাটাস পরিবর্তন")
            }
        }
    }

    if (showDialog) {
        var status by remember { mutableStateOf(report.status) }
        var notes by remember { mutableStateOf(report.reviewerNotes) }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("রিপোর্ট পর্যালোচনা") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("বর্তমান স্ট্যাটাস: $status")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = status == "RESOLVED", onClick = { status = "RESOLVED" }, label = { Text("RESOLVED") })
                        FilterChip(selected = status == "REJECTED", onClick = { status = "REJECTED" }, label = { Text("REJECTED") })
                    }
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("পর্যালোচক মন্তব্য") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onUpdateStatus(status, notes)
                    showDialog = false
                }) {
                    Text("সংরক্ষণ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
fun CorrectionProposalCard(
    proposal: ContentCorrectionEntity,
    canReview: Boolean,
    onReview: (status: String, reason: String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "${proposal.contentType} [${proposal.recordId}]", fontWeight = FontWeight.Bold)
            Text(text = "ফিল্ড: ${proposal.fieldName}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "আগের মান: ${proposal.originalValue}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            Text(text = "প্রস্তাবিত: ${proposal.proposedValue}", style = MaterialTheme.typography.bodySmall, color = Emerald700)
            Text(text = "সূত্র: ${proposal.authoritativeSource}", style = MaterialTheme.typography.bodySmall)

            if (canReview && proposal.status != "APPROVED") {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { onReview("APPROVED", "প্রামাণ্য সূত্র যাচাইকৃত") }) {
                        Text("অনুমোদন")
                    }
                    OutlinedButton(onClick = { onReview("REJECTED", "তথ্য সঠিক নয়") }) {
                        Text("প্রত্যাখ্যান")
                    }
                }
            }
        }
    }
}

@Composable
fun AuditLogCard(log: AuditLogEntity) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.US).format(Date(log.timestamp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "${log.actionType} • ${log.contentType}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = "কর্তা: ${log.adminEmail}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = log.reason, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
            }
            Text(text = dateStr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun NewProposalDialog(
    onDismiss: () -> Unit,
    onSubmit: (contentType: String, recordId: String, fieldName: String, oldVal: String, proposedVal: String, source: String) -> Unit
) {
    var contentType by remember { mutableStateOf("HADITH") }
    var recordId by remember { mutableStateOf("") }
    var fieldName by remember { mutableStateOf("textBengali") }
    var oldValue by remember { mutableStateOf("") }
    var proposedValue by remember { mutableStateOf("") }
    var authoritativeSource by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন সংশোধন প্রস্তাব") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = contentType,
                    onValueChange = { contentType = it },
                    label = { Text("কন্টেন্ট ধরন (QURAN, HADITH, DUA)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = recordId,
                    onValueChange = { recordId = it },
                    label = { Text("রেকর্ড আইডি") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = fieldName,
                    onValueChange = { fieldName = it },
                    label = { Text("ফিল্ডের নাম") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = oldValue,
                    onValueChange = { oldValue = it },
                    label = { Text("বর্তমান মান") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = proposedValue,
                    onValueChange = { proposedValue = it },
                    label = { Text("প্রস্তাবিত নতুন মান") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = authoritativeSource,
                    onValueChange = { authoritativeSource = it },
                    label = { Text("মুসহাফ/গ্রন্থের প্রামাণ্য সূত্র") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (recordId.isNotBlank() && proposedValue.isNotBlank()) {
                        onSubmit(contentType, recordId, fieldName, oldValue, proposedValue, authoritativeSource)
                    }
                }
            ) {
                Text("জমা দিন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}
