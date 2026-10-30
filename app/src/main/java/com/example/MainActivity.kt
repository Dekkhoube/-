package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AudioMiniPlayer
import com.example.ui.components.AuthScreen
import com.example.ui.components.ErrorReportDialog
import com.example.ui.screens.*
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NoorIslamicTheme
import com.example.util.configureAppCheck
import com.example.viewmodel.MainViewModel

enum class MainNavTab(
    val titleBengali: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME("হোম", Icons.Filled.Home, Icons.Outlined.Home, "nav_tab_home"),
    QURAN("কুরআন", Icons.AutoMirrored.Filled.MenuBook, Icons.AutoMirrored.Outlined.MenuBook, "nav_tab_quran"),
    PRAYER("নামাজ", Icons.Filled.AccessTime, Icons.Outlined.AccessTime, "nav_tab_prayer"),
    HADITH("হাদিস", Icons.Filled.AutoStories, Icons.Outlined.AutoStories, "nav_tab_hadith"),
    DUAS("দোয়া", Icons.Filled.VolunteerActivism, Icons.Outlined.VolunteerActivism, "nav_tab_duas"),
    MORE("আরও", Icons.Filled.Widgets, Icons.Outlined.Widgets, "nav_tab_more")
}

enum class SubScreen {
    NONE,
    SURAH_DETAIL,
    HAFEZI_PAGE_VIEW,
    NAMAZ_SHIKKHA,
    QIBLA,
    TASBIH,
    RAMADAN,
    ZAKAT,
    WORSHIP,
    MOSQUE,
    ADMIN,
    SETTINGS,
    CALENDAR,
    ISLAMIC_AI
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase App Check with synchronous debug token attestation
        configureAppCheck(this, intent)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val audioState by viewModel.audioPlayer.playerState.collectAsStateWithLifecycle()
            val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle(initialValue = emptyList())
            val tasbihList by viewModel.tasbihList.collectAsStateWithLifecycle(initialValue = emptyList())
            val errorReports by viewModel.errorReports.collectAsStateWithLifecycle(initialValue = emptyList())
            val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle(initialValue = emptyList())
            val correctionProposals by viewModel.correctionProposals.collectAsStateWithLifecycle(initialValue = emptyList())
            val adminContent by viewModel.adminContent.collectAsStateWithLifecycle(initialValue = emptyList())

            var currentUser by remember { mutableStateOf(com.google.firebase.auth.FirebaseAuth.getInstance().currentUser) }

            DisposableEffect(Unit) {
                val listener = com.google.firebase.auth.FirebaseAuth.AuthStateListener { auth ->
                    currentUser = auth.currentUser
                }
                com.google.firebase.auth.FirebaseAuth.getInstance().addAuthStateListener(listener)
                onDispose {
                    com.google.firebase.auth.FirebaseAuth.getInstance().removeAuthStateListener(listener)
                }
            }

            var currentTab by remember { mutableStateOf(MainNavTab.HOME) }
            var currentSubScreen by remember { mutableStateOf(SubScreen.NONE) }

            // Back handler for secondary screens
            if (currentSubScreen != SubScreen.NONE) {
                BackHandler {
                    currentSubScreen = SubScreen.NONE
                }
            } else if (currentTab != MainNavTab.HOME) {
                BackHandler {
                    currentTab = MainNavTab.HOME
                }
            }

            NoorIslamicTheme(darkTheme = uiState.isDarkMode) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Column {
                                    Text(
                                        text = "নূর ইসলামিক",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    uiState.currentHijriDate?.let {
                                        Text(
                                            text = "${it.day} ${it.monthNameBengali} ${it.year} হিজরি",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = { currentSubScreen = SubScreen.ISLAMIC_AI },
                                    modifier = Modifier.testTag("app_gemini_ai_icon")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "ইসলামিক এআই সহকারী (Gemini)",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(
                                    onClick = { currentSubScreen = SubScreen.SETTINGS },
                                    modifier = Modifier.testTag("app_settings_icon")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Settings",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    bottomBar = {
                        Column {
                            // Audio Mini Player directly above BottomBar
                            AudioMiniPlayer(
                                state = audioState,
                                onPlayPause = {
                                    if (audioState.isPlaying) viewModel.audioPlayer.pauseAudio()
                                    else viewModel.audioPlayer.resumeAudio()
                                },
                                onNext = { viewModel.playNextAyah() },
                                onPrevious = { viewModel.playPreviousAyah() },
                                onClose = { viewModel.audioPlayer.stopAudio() }
                            )

                            // Main Navigation Bar with safe area insets
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                windowInsets = WindowInsets.navigationBars,
                                modifier = Modifier.testTag("main_bottom_nav_bar")
                            ) {
                                MainNavTab.values().forEach { tab ->
                                    val isSelected = currentTab == tab && currentSubScreen == SubScreen.NONE
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = {
                                            currentTab = tab
                                            currentSubScreen = SubScreen.NONE
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                                contentDescription = tab.titleBengali
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = tab.titleBengali,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.primary,
                                            selectedTextColor = MaterialTheme.colorScheme.primary,
                                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                        ),
                                        modifier = Modifier.testTag(tab.testTag)
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (currentSubScreen != SubScreen.NONE) {
                            when (currentSubScreen) {
                                SubScreen.SURAH_DETAIL -> {
                                    val surah = uiState.selectedSurah
                                    if (surah != null) {
                                        SurahDetailScreen(
                                            surah = surah,
                                            ayahs = uiState.currentSurahAyahs,
                                            arabicFontSize = uiState.arabicFontSize,
                                            bengaliFontSize = uiState.bengaliFontSize,
                                            currentPlayingAyahNumber = audioState.currentAyahNumber,
                                            currentPlayingSurahNumber = audioState.currentSurahNumber,
                                            isAudioPlaying = audioState.isPlaying,
                                            selectedReciter = uiState.selectedReciter,
                                            recitersList = com.example.data.repository.QuranRepository.reciters,
                                            isContinuousPlayback = uiState.isContinuousQuranAudio,
                                            onSelectReciter = { reciter -> viewModel.selectReciter(reciter) },
                                            onToggleContinuous = { viewModel.toggleContinuousPlayback() },
                                            onBack = { currentSubScreen = SubScreen.NONE },
                                            onPlayAyah = { ayah ->
                                                viewModel.playAyah(ayah, surah.nameBengali)
                                            },
                                            onPlayFullSurah = {
                                                viewModel.playFullSurah(surah)
                                            },
                                            onBookmarkAyah = { ayah ->
                                                viewModel.toggleBookmark(
                                                    type = "QURAN",
                                                    refId = "${surah.number}:${ayah.numberInSurah}",
                                                    title = "সূরা ${surah.nameBengali} - আয়াত ${ayah.numberInSurah}",
                                                    arabic = ayah.textArabic,
                                                    bengali = ayah.textBengali
                                                )
                                            },
                                            onReportAyah = { ayah ->
                                                viewModel.openErrorReport("QURAN", "সূরা ${surah.nameBengali}:${ayah.numberInSurah}")
                                            },
                                            onSwitchToMushafPage = { targetPage ->
                                                viewModel.openHafeziMushafPage(targetPage)
                                                currentSubScreen = SubScreen.HAFEZI_PAGE_VIEW
                                            }
                                        )
                                    }
                                }
                                SubScreen.HAFEZI_PAGE_VIEW -> {
                                    HafeziQuranPageView(
                                        initialPage = uiState.selectedMushafPage,
                                        currentPlayingSurah = audioState.currentSurahNumber,
                                        currentPlayingAyah = audioState.currentAyahNumber,
                                        isAudioPlaying = audioState.isPlaying,
                                        selectedReciter = uiState.selectedReciter,
                                        onPlayAyah = { ayah ->
                                            val s = com.example.data.repository.QuranRepository.getSurah(ayah.surahNumber)
                                            viewModel.playAyah(ayah, s?.nameBengali ?: "কুরআন")
                                        },
                                        onBookmarkPage = { pageNum ->
                                            viewModel.toggleBookmark(
                                                type = "QURAN_PAGE",
                                                refId = "PAGE_$pageNum",
                                                title = "হাফেজী কুরআন পৃষ্ঠা $pageNum",
                                                arabic = "صفحة $pageNum",
                                                bengali = "পৃষ্ঠা $pageNum"
                                            )
                                        },
                                        onSwitchToListView = {
                                            val surah = com.example.data.repository.QuranRepository.getSurah(uiState.lastReadSurahNumber)
                                                ?: com.example.data.repository.QuranRepository.all114Surahs.first()
                                            viewModel.selectSurah(surah)
                                            currentSubScreen = SubScreen.SURAH_DETAIL
                                        },
                                        onBack = { currentSubScreen = SubScreen.NONE }
                                    )
                                }
                                SubScreen.NAMAZ_SHIKKHA -> {
                                    NamazShikkhaScreen(onBack = { currentSubScreen = SubScreen.NONE })
                                }
                                SubScreen.CALENDAR -> {
                                    MultiCalendarScreen(
                                        location = uiState.selectedLocation,
                                        calculationMethod = uiState.calculationMethod,
                                        juristicMethod = uiState.juristicMethod,
                                        hijriAdjustment = uiState.hijriAdjustmentDays,
                                        onBack = { currentSubScreen = SubScreen.NONE }
                                    )
                                }
                                SubScreen.QIBLA -> {
                                    QiblaScreen(
                                        qiblaBearing = uiState.qiblaBearing,
                                        deviceHeading = uiState.deviceCompassHeading,
                                        qiblaRelativeAngle = uiState.qiblaRelativeAngle,
                                        distanceKm = uiState.distanceToKaabaKm,
                                        accuracy = uiState.compassAccuracy,
                                        cityName = uiState.selectedLocation.nameBengali,
                                        hasCompassSensor = uiState.hasCompassSensor,
                                        isFacingQibla = uiState.isFacingQibla,
                                        onStartCompass = { viewModel.startCompass() },
                                        onStopCompass = { viewModel.stopCompass() },
                                        onBack = { currentSubScreen = SubScreen.NONE }
                                    )
                                }
                                SubScreen.TASBIH -> {
                                    TasbihScreen(
                                        tasbihList = tasbihList,
                                        onIncrement = { viewModel.incrementTasbih(it) },
                                        onUndo = { viewModel.undoTasbih(it) },
                                        onReset = { viewModel.resetTasbih(it) }
                                    )
                                }
                                SubScreen.RAMADAN -> {
                                    RamadanScreen(
                                        schedule = uiState.prayerSchedule,
                                        cityName = uiState.selectedLocation.nameBengali
                                    )
                                }
                                SubScreen.ZAKAT -> {
                                    ZakatScreen(
                                        featureRepository = viewModel.featureRepository
                                    )
                                }
                                SubScreen.WORSHIP -> {
                                    WorshipTrackerScreen(
                                        viewModel = viewModel
                                    )
                                }
                                SubScreen.MOSQUE -> {
                                    MosqueScreen(
                                        featureRepository = viewModel.featureRepository,
                                        currentLocation = uiState.selectedLocation
                                    )
                                }
                                SubScreen.ADMIN -> {
                                    AdminScreen(
                                        currentAdminUser = uiState.currentAdminUser,
                                        currentAdminRole = uiState.currentAdminRole,
                                        errorReports = errorReports,
                                        correctionProposals = correctionProposals,
                                        auditLogs = auditLogs,
                                        adminContent = adminContent,
                                        onUpdateReportStatus = { id, status, notes ->
                                            viewModel.updateReportStatus(id, status, notes)
                                        },
                                        onSubmitCorrectionProposal = { type, recId, field, oldV, newV, src ->
                                            viewModel.submitCorrectionProposal(type, recId, field, oldV, newV, src)
                                        },
                                        onReviewProposal = { id, status, reason ->
                                            viewModel.reviewCorrectionProposal(id, status, reason)
                                        },
                                        onAddContent = { type, title, arabic, bengali, extra ->
                                            viewModel.addAdminContent(type, title, arabic, bengali, extra)
                                        },
                                        onDeleteContent = { id ->
                                            viewModel.deleteAdminContent(id)
                                        }
                                    )
                                }
                                SubScreen.SETTINGS -> {
                                    SettingsScreen(
                                        uiState = uiState,
                                        currentUser = currentUser,
                                        onAuthSuccess = { user ->
                                            currentUser = user
                                            viewModel.syncUserProfileOnSignIn(user)
                                        },
                                        onSignOut = {
                                            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                                            currentUser = null
                                        },
                                        onToggleDarkMode = { viewModel.toggleDarkMode() },
                                        onSetArabicFontSize = { viewModel.setArabicFontSize(it) },
                                        onSetBengaliFontSize = { viewModel.setBengaliFontSize(it) },
                                        onSetHijriAdjustment = { viewModel.setHijriAdjustment(it) },
                                        onSetCalculationMethod = { viewModel.setCalculationMethod(it) },
                                        onSetJuristicMethod = { viewModel.setJuristicMethod(it) },
                                        onNavigateToAdmin = { currentSubScreen = SubScreen.ADMIN }
                                    )
                                }
                                SubScreen.ISLAMIC_AI -> {
                                    IslamicAiScreen(
                                        onBack = { currentSubScreen = SubScreen.NONE }
                                    )
                                }
                                else -> {}
                            }
                        } else {
                            // Top-level Tabs
                            when (currentTab) {
                                MainNavTab.HOME -> {
                                    HomeScreen(
                                        uiState = uiState,
                                        adminNotices = adminContent.filter { it.contentType == "NOTICE" },
                                        lastReadSurahName = uiState.lastReadSurahNameBengali,
                                        lastReadAyahNumber = uiState.lastReadAyahNumber,
                                        lastReadPageNumber = uiState.lastReadPageNumber,
                                        onResumeLastRead = {
                                            viewModel.resumeLastRead { surah, ayahNumber ->
                                                currentSubScreen = SubScreen.SURAH_DETAIL
                                            }
                                        },
                                        onNavigateToNamazShikkha = { currentSubScreen = SubScreen.NAMAZ_SHIKKHA },
                                        onNavigateToHafeziQuran = { pageNum ->
                                            viewModel.openHafeziMushafPage(pageNum)
                                            currentSubScreen = SubScreen.HAFEZI_PAGE_VIEW
                                        },
                                        onNavigateToQuran = { currentTab = MainNavTab.QURAN },
                                        onNavigateToPrayer = { currentTab = MainNavTab.PRAYER },
                                        onNavigateToQibla = { currentSubScreen = SubScreen.QIBLA },
                                        onNavigateToTasbih = { currentSubScreen = SubScreen.TASBIH },
                                        onNavigateToRamadan = { currentSubScreen = SubScreen.RAMADAN },
                                        onNavigateToZakat = { currentSubScreen = SubScreen.ZAKAT },
                                        onNavigateToWorship = { currentSubScreen = SubScreen.WORSHIP },
                                        onNavigateToMosque = { currentSubScreen = SubScreen.MOSQUE },
                                        onNavigateToCalendar = { currentSubScreen = SubScreen.CALENDAR },
                                        onNavigateToAi = { currentSubScreen = SubScreen.ISLAMIC_AI },
                                        onPlayAyah = { ayah, surahName ->
                                            viewModel.playAyah(ayah, surahName)
                                        }
                                    )
                                }
                                MainNavTab.QURAN -> {
                                    QuranScreen(
                                        bookmarks = bookmarks,
                                        lastReadSurahName = uiState.lastReadSurahNameBengali,
                                        lastReadAyahNumber = uiState.lastReadAyahNumber,
                                        lastReadPageNumber = uiState.lastReadPageNumber,
                                        onResumeLastRead = {
                                            viewModel.resumeLastRead { surah, ayahNumber ->
                                                currentSubScreen = SubScreen.SURAH_DETAIL
                                            }
                                        },
                                        onOpenHafeziPage = { pageNum ->
                                            viewModel.openHafeziMushafPage(pageNum)
                                            currentSubScreen = SubScreen.HAFEZI_PAGE_VIEW
                                        },
                                        onSelectSurah = { surah ->
                                            viewModel.selectSurah(surah)
                                            currentSubScreen = SubScreen.SURAH_DETAIL
                                        },
                                        onSelectJuz = { juz ->
                                            viewModel.selectJuz(juz)
                                            currentSubScreen = SubScreen.SURAH_DETAIL
                                        },
                                        onRemoveBookmark = { id ->
                                            viewModel.removeBookmark(id)
                                        },
                                        onPlaySurah = { surah ->
                                            viewModel.playFullSurah(surah)
                                        },
                                        onPlayJuz = { juz ->
                                            viewModel.playJuz(juz)
                                        }
                                    )
                                }
                                MainNavTab.PRAYER -> {
                                    PrayerScreen(
                                        schedule = uiState.prayerSchedule,
                                        selectedLocation = uiState.selectedLocation,
                                        locationPresets = viewModel.featureRepository.locationPresets,
                                        calculationMethod = uiState.calculationMethod,
                                        juristicMethod = uiState.juristicMethod,
                                        isAutoAzanEnabled = uiState.isAutoAzanEnabled,
                                        isFajrAlarmEnabled = uiState.isFajrAlarmEnabled,
                                        isDhuhrAlarmEnabled = uiState.isDhuhrAlarmEnabled,
                                        isAsrAlarmEnabled = uiState.isAsrAlarmEnabled,
                                        isMaghribAlarmEnabled = uiState.isMaghribAlarmEnabled,
                                        isIshaAlarmEnabled = uiState.isIshaAlarmEnabled,
                                        isGpsActive = uiState.isGpsLocationActive,
                                        onSelectLocation = { viewModel.setLocation(it) },
                                        onSelectCalculationMethod = { viewModel.setCalculationMethod(it) },
                                        onSelectJuristicMethod = { viewModel.setJuristicMethod(it) },
                                        onToggleAutoAzan = { viewModel.toggleAutoAzan(it) },
                                        onTogglePrayerAlarm = { prayer, enabled ->
                                            viewModel.togglePrayerAlarm(prayer, enabled)
                                        },
                                        onPlayAzanPreview = { viewModel.playAzanPreview() },
                                        onDetectGpsLocation = { viewModel.refreshLocationFromGps() },
                                        onNavigateToQibla = { currentSubScreen = SubScreen.QIBLA },
                                        onNavigateToCalendar = { currentSubScreen = SubScreen.CALENDAR }
                                    )
                                }
                                MainNavTab.HADITH -> {
                                    HadithScreen(
                                        selectedCollectionId = uiState.selectedHadithCollectionId,
                                        onSelectCollection = { viewModel.selectHadithCollection(it) },
                                        onBookmarkHadith = { hadith ->
                                            viewModel.toggleBookmark(
                                                type = "HADITH",
                                                refId = hadith.reference,
                                                title = "${hadith.reference} - ${hadith.narratorBengali}",
                                                arabic = hadith.textArabic,
                                                bengali = hadith.textBengali
                                            )
                                        },
                                        onReportHadith = { hadith ->
                                            viewModel.openErrorReport("HADITH", hadith.reference)
                                        }
                                    )
                                }
                                MainNavTab.DUAS -> {
                                    DuaScreen(
                                        selectedCategory = uiState.selectedDuaCategory,
                                        onSelectCategory = { viewModel.selectDuaCategory(it) },
                                        onBookmarkDua = { dua ->
                                            viewModel.toggleBookmark(
                                                type = "DUA",
                                                refId = "${dua.id}",
                                                title = dua.titleBengali,
                                                arabic = dua.textArabic,
                                                bengali = dua.meaningBengali
                                            )
                                        },
                                        onReportDua = { dua ->
                                            viewModel.openErrorReport("DUA", dua.titleBengali)
                                        }
                                    )
                                }
                                MainNavTab.MORE -> {
                                    MoreMenuScreen(
                                        onNavigateToNamazShikkha = { currentSubScreen = SubScreen.NAMAZ_SHIKKHA },
                                        onNavigateToHafeziQuran = {
                                            viewModel.openHafeziMushafPage(uiState.lastReadPageNumber)
                                            currentSubScreen = SubScreen.HAFEZI_PAGE_VIEW
                                        },
                                        onNavigateToTasbih = { currentSubScreen = SubScreen.TASBIH },
                                        onNavigateToRamadan = { currentSubScreen = SubScreen.RAMADAN },
                                        onNavigateToZakat = { currentSubScreen = SubScreen.ZAKAT },
                                        onNavigateToWorship = { currentSubScreen = SubScreen.WORSHIP },
                                        onNavigateToMosque = { currentSubScreen = SubScreen.MOSQUE },
                                        onNavigateToQibla = { currentSubScreen = SubScreen.QIBLA },
                                        onNavigateToCalendar = { currentSubScreen = SubScreen.CALENDAR },
                                        onNavigateToAdmin = { currentSubScreen = SubScreen.ADMIN },
                                        onNavigateToSettings = { currentSubScreen = SubScreen.SETTINGS },
                                        onNavigateToAi = { currentSubScreen = SubScreen.ISLAMIC_AI }
                                    )
                                }
                            }
                        }

                        // Error Reporting Dialog Modal
                        if (uiState.showErrorReportDialog && uiState.reportingTarget != null) {
                            val target = uiState.reportingTarget!!
                            ErrorReportDialog(
                                contentType = target.first,
                                recordId = target.second,
                                onDismiss = { viewModel.closeErrorReport() },
                                onSubmit = { cat, exp, ref ->
                                    viewModel.submitErrorReport(cat, exp, ref)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MoreMenuScreen(
    onNavigateToNamazShikkha: () -> Unit = {},
    onNavigateToHafeziQuran: () -> Unit = {},
    onNavigateToTasbih: () -> Unit,
    onNavigateToRamadan: () -> Unit,
    onNavigateToZakat: () -> Unit,
    onNavigateToWorship: () -> Unit,
    onNavigateToMosque: () -> Unit,
    onNavigateToQibla: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAi: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.lazy.LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "অন্যান্য ইসলামিক সেবা ও টুলস",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            MoreMenuItem(
                "ইসলামিক এআই সহকারী (Gemini)",
                "কুরআন, হাদিস ও ইসলামিক জরুরি প্রশ্নোত্তর",
                Icons.Default.AutoAwesome,
                onNavigateToAi
            )
        }
        item {
            MoreMenuItem("নামাজ ও অযু শিক্ষা", "৫ ওয়াক্ত নামাজ, রাকাত সংখ্যা ও পূর্ণাঙ্গ অযু", Icons.Default.SelfImprovement, onNavigateToNamazShikkha)
        }
        item {
            MoreMenuItem("হাফেজী কুরআন (মুসহাফ)", "ঐতিহ্যবাহী ১৫ লাইনের মুদ্রিত কুরআন ৬০৪ পৃষ্ঠা", Icons.Default.AutoStories, onNavigateToHafeziQuran)
        }
        item {
            MoreMenuItem("বাংলা, ইংরেজি ও হিজরি ক্যালেন্ডার", "সমন্বিত বহু-ভাষিক বর্ষপঞ্জি ও নামাজের সময়", Icons.Default.CalendarMonth, onNavigateToCalendar)
        }
        item {
            MoreMenuItem("ডিজিটাল তাসবীহ", "দৈনিক জিকির ও তাসবীহ গণনা", Icons.Default.TouchApp, onNavigateToTasbih)
        }
        item {
            MoreMenuItem("মাহে রমজান মোড", "সেহরি-ইফতারের সময়সূচি ও রোজা ট্র্যাকার", Icons.Default.WbTwilight, onNavigateToRamadan)
        }
        item {
            MoreMenuItem("যাকাত ক্যালকুলেটর", "স্বর্ণ ও রূপার নেসাব ভিত্তিক যাকাত হিসাব", Icons.Default.Calculate, onNavigateToZakat)
        }
        item {
            MoreMenuItem("দৈনিক আমল ট্র্যাকার", "৫ ওয়াক্ত নামাজ ও আমল ট্র্যাকিং", Icons.Default.CheckCircle, onNavigateToWorship)
        }
        item {
            MoreMenuItem("কাছের মসজিদ সন্ধান", "নিকটবর্তী মসজিদের দূরত্ব ও Google Maps ডিরেকশন", Icons.Default.Mosque, onNavigateToMosque)
        }
        item {
            MoreMenuItem("কিবলা কম্পাস", "কা'বা শরীফের সঠিক দিক ও দূরত্ব", Icons.Default.Explore, onNavigateToQibla)
        }
        item {
            MoreMenuItem("প্রশাসনিক পোর্টাল", "ত্রুটি রিপোর্ট নিরীক্ষা ও কন্টেন্ট অনুমোদন", Icons.Default.AdminPanelSettings, onNavigateToAdmin)
        }
        item {
            MoreMenuItem("অ্যাপ সেটিংস", "ফন্ট সাইজ, থিম ও চাঁদ দেখা সমন্বয়", Icons.Default.Settings, onNavigateToSettings)
        }
    }
}

@Composable
fun MoreMenuItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
