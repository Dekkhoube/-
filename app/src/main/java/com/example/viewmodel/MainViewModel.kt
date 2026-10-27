package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.AppFeatureRepository
import com.example.data.repository.HadithAndDuaRepository
import com.example.data.repository.QuranRepository
import com.example.util.AudioPlayerManager
import com.example.util.HijriCalendarHelper
import com.example.util.PrayerCalculator
import com.example.util.QiblaCalculator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max

data class AppUiState(
    // Theme & Preferences
    val isDarkMode: Boolean = false,
    val currentLanguage: String = "bn", // "bn", "en", "ar"
    val arabicFontSize: Float = 24f,
    val bengaliFontSize: Float = 16f,

    // Location & Prayer
    val selectedLocation: LocationPreset = LocationPreset("ঢাকা", "Dhaka", 23.8103, 90.4125, 6.0),
    val isGpsLocationActive: Boolean = false,
    val gpsStatusMessage: String? = null,
    val calculationMethod: CalculationMethod = CalculationMethod.KARACHI,
    val juristicMethod: JuristicMethod = JuristicMethod.HANAFI,
    val prayerSchedule: PrayerSchedule? = null,
    val hijriAdjustmentDays: Int = 0,
    val currentHijriDate: HijriDate? = null,
    val currentBengaliDate: com.example.util.BengaliDate? = null,

    // Azan Alarm System
    val isAutoAzanEnabled: Boolean = false,
    val isFajrAlarmEnabled: Boolean = true,
    val isDhuhrAlarmEnabled: Boolean = true,
    val isAsrAlarmEnabled: Boolean = true,
    val isMaghribAlarmEnabled: Boolean = true,
    val isIshaAlarmEnabled: Boolean = true,

    // Qibla
    val qiblaBearing: Float = 0f,
    val deviceCompassHeading: Float = 0f,
    val qiblaRelativeAngle: Float = 0f,
    val distanceToKaabaKm: Double = 0.0,
    val compassAccuracy: String = "স্বাভাবিক",

    // Active Navigation Subscreen
    val selectedSurah: Surah? = null,
    val currentSurahAyahs: List<Ayah> = emptyList(),
    val quranSearchQuery: String = "",
    val quranSearchResults: List<Ayah> = emptyList(),
    val selectedJuz: JuzInfo? = null,

    // Hadith & Dua
    val selectedHadithCollectionId: String = "bukhari",
    val hadithSearchQuery: String = "",
    val selectedDuaCategory: DuaCategory = DuaCategory.MORNING_EVENING,
    val duaSearchQuery: String = "",

    // Active Tasbih Dhikr
    val activeTasbihDhikr: TasbihDhikrEntity? = null,

    // Ramadan
    val ramadanFastCompletedCount: Int = 0,

    // Admin
    val currentAdminUser: String = "abirkhan33893@gmail.com",
    val currentAdminRole: String = "SUPER_ADMIN",
    val showErrorReportDialog: Boolean = false,
    val reportingTarget: Pair<String, String>? = null // Type, RecordId
)

class MainViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {

    private val database = AppDatabase.getInstance(application)
    val featureRepository = AppFeatureRepository(database)
    val firebaseSyncRepository = com.example.data.repository.FirebaseSyncRepository(application)
    val audioPlayer = AudioPlayerManager(application)

    private val _currentUser = MutableStateFlow<com.google.firebase.auth.FirebaseUser?>(com.google.firebase.auth.FirebaseAuth.getInstance().currentUser)
    val currentUser: StateFlow<com.google.firebase.auth.FirebaseUser?> = _currentUser.asStateFlow()

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    // Persistent Database Flows
    val bookmarks = featureRepository.bookmarks
    val tasbihList = featureRepository.tasbihRecords
    val errorReports = featureRepository.errorReports
    val auditLogs = featureRepository.auditLogs
    val correctionProposals = featureRepository.correctionProposals
    val adminContent = featureRepository.adminContent

    private var countdownJob: Job? = null
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null
    private val gravityValues = FloatArray(3)
    private val geomagneticValues = FloatArray(3)
    private var hasGravity = false
    private var hasGeomagnetic = false

    init {
        // Seed default tasbih list if needed
        viewModelScope.launch {
            database.tasbihDao().insertInitialRecords(featureRepository.defaultTasbihList)
        }

        // Auth state listener
        com.google.firebase.auth.FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val user = auth.currentUser
            _currentUser.value = user
            if (user != null) {
                val email = user.email ?: ""
                val isAdmin = email == "abirkhan33893@gmail.com"
                _uiState.value = _uiState.value.copy(
                    currentAdminUser = email,
                    currentAdminRole = if (isAdmin) "SUPER_ADMIN" else "CONTENT_EDITOR"
                )
            }
        }

        // Initialize prayer times and Hijri date
        updatePrayerScheduleAndHijri()
        startCountdownTimer()
        initCompassSensors()

        // Set default selected surah to Surah Al-Fatiha
        selectSurah(QuranRepository.all114Surahs.first())
    }

    private fun initCompassSensors() {
        try {
            sensorManager = getApplication<Application>().getSystemService(Context.SENSOR_SERVICE) as? SensorManager
            accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

            sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            sensorManager?.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
        } catch (e: Exception) {
            // fallback
        }
    }

    fun updatePrayerScheduleAndHijri() {
        val loc = _uiState.value.selectedLocation
        val method = _uiState.value.calculationMethod
        val juristic = _uiState.value.juristicMethod
        val adj = _uiState.value.hijriAdjustmentDays

        val schedule = PrayerCalculator.calculateTimes(
            calendar = Calendar.getInstance(),
            latitude = loc.latitude,
            longitude = loc.longitude,
            timezoneOffset = loc.timezoneOffsetHours,
            method = method,
            juristic = juristic
        )

        val hijri = HijriCalendarHelper.getHijriDate(Calendar.getInstance(), adj)
        val bengali = com.example.util.BengaliCalendarHelper.getBengaliDate(Calendar.getInstance())
        val qibla = QiblaCalculator.calculateQiblaBearing(loc.latitude, loc.longitude)
        val distance = QiblaCalculator.calculateDistanceToKaabaKm(loc.latitude, loc.longitude)

        _uiState.value = _uiState.value.copy(
            prayerSchedule = schedule,
            currentHijriDate = hijri,
            currentBengaliDate = bengali,
            qiblaBearing = qibla,
            distanceToKaabaKm = distance
        )
    }

    private fun startCountdownTimer() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                updatePrayerScheduleAndHijri()
            }
        }
    }

    fun setLocation(preset: LocationPreset) {
        _uiState.value = _uiState.value.copy(selectedLocation = preset)
        updatePrayerScheduleAndHijri()
    }

    fun setCalculationMethod(method: CalculationMethod) {
        _uiState.value = _uiState.value.copy(calculationMethod = method)
        updatePrayerScheduleAndHijri()
    }

    fun setJuristicMethod(juristic: JuristicMethod) {
        _uiState.value = _uiState.value.copy(juristicMethod = juristic)
        updatePrayerScheduleAndHijri()
    }

    fun setHijriAdjustment(adjustment: Int) {
        _uiState.value = _uiState.value.copy(hijriAdjustmentDays = adjustment)
        updatePrayerScheduleAndHijri()
    }

    fun toggleDarkMode() {
        _uiState.value = _uiState.value.copy(isDarkMode = !_uiState.value.isDarkMode)
    }

    fun setArabicFontSize(size: Float) {
        _uiState.value = _uiState.value.copy(arabicFontSize = size)
    }

    fun setBengaliFontSize(size: Float) {
        _uiState.value = _uiState.value.copy(bengaliFontSize = size)
    }

    fun setLanguage(lang: String) {
        _uiState.value = _uiState.value.copy(currentLanguage = lang)
    }

    // --- Quran Functions ---
    fun selectSurah(surah: Surah) {
        val ayahs = QuranRepository.getAyahsForSurah(surah.number)
        _uiState.value = _uiState.value.copy(
            selectedSurah = surah,
            currentSurahAyahs = ayahs
        )
    }

    fun searchQuran(query: String) {
        _uiState.value = _uiState.value.copy(
            quranSearchQuery = query,
            quranSearchResults = QuranRepository.searchQuran(query)
        )
    }

    fun playAyah(ayah: Ayah, surahNameBengali: String) {
        audioPlayer.playAyahAudio(
            surahNumber = ayah.surahNumber,
            ayahNumberInSurah = ayah.numberInSurah,
            surahNameBengali = surahNameBengali
        )
    }

    fun toggleBookmark(type: String, refId: String, title: String, arabic: String, bengali: String) {
        viewModelScope.launch {
            featureRepository.addBookmark(type, refId, title, arabic, bengali)
        }
    }

    fun removeBookmark(id: Long) {
        viewModelScope.launch {
            featureRepository.removeBookmark(id)
        }
    }

    // --- Hadith & Dua Functions ---
    fun selectHadithCollection(id: String) {
        _uiState.value = _uiState.value.copy(selectedHadithCollectionId = id)
    }

    fun searchHadith(query: String) {
        _uiState.value = _uiState.value.copy(hadithSearchQuery = query)
    }

    fun selectDuaCategory(category: DuaCategory) {
        _uiState.value = _uiState.value.copy(selectedDuaCategory = category)
    }

    fun searchDua(query: String) {
        _uiState.value = _uiState.value.copy(duaSearchQuery = query)
    }

    // --- Tasbih Functions ---
    fun incrementTasbih(dhikr: TasbihDhikrEntity) {
        triggerHapticFeedback()
        val newCount = dhikr.currentCount + 1
        val updated = dhikr.copy(
            currentCount = newCount,
            totalCount = dhikr.totalCount + 1
        )
        viewModelScope.launch {
            featureRepository.updateTasbih(updated)
        }
    }

    fun undoTasbih(dhikr: TasbihDhikrEntity) {
        if (dhikr.currentCount > 0) {
            val updated = dhikr.copy(
                currentCount = dhikr.currentCount - 1,
                totalCount = max(0, dhikr.totalCount - 1)
            )
            viewModelScope.launch {
                featureRepository.updateTasbih(updated)
            }
        }
    }

    fun resetTasbih(dhikr: TasbihDhikrEntity) {
        val updated = dhikr.copy(currentCount = 0)
        viewModelScope.launch {
            featureRepository.updateTasbih(updated)
        }
    }

    private fun triggerHapticFeedback() {
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(30)
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    // --- Worship Tracker Functions ---
    fun getTodayDateStr(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    fun toggleWorshipPrayer(current: DailyWorshipEntity?, prayerName: String) {
        val todayStr = getTodayDateStr()
        val base = current ?: DailyWorshipEntity(dateStr = todayStr)
        val updated = when (prayerName) {
            "fajr" -> base.copy(fajr = !base.fajr)
            "dhuhr" -> base.copy(dhuhr = !base.dhuhr)
            "asr" -> base.copy(asr = !base.asr)
            "maghrib" -> base.copy(maghrib = !base.maghrib)
            "isha" -> base.copy(isha = !base.isha)
            "tahajjud" -> base.copy(tahajjud = !base.tahajjud)
            "fasting" -> base.copy(fasting = !base.fasting)
            "quran" -> base.copy(quranRecited = !base.quranRecited)
            "sadaqah" -> base.copy(sadaqah = !base.sadaqah)
            else -> base
        }
        viewModelScope.launch {
            featureRepository.saveWorship(updated)
        }
    }

    // --- Error Reporting & Admin ---
    fun openErrorReport(contentType: String, recordId: String) {
        _uiState.value = _uiState.value.copy(
            showErrorReportDialog = true,
            reportingTarget = Pair(contentType, recordId)
        )
    }

    fun closeErrorReport() {
        _uiState.value = _uiState.value.copy(
            showErrorReportDialog = false,
            reportingTarget = null
        )
    }

    fun submitErrorReport(
        issueCategory: String,
        explanation: String,
        referenceSource: String
    ) {
        val target = _uiState.value.reportingTarget ?: return
        viewModelScope.launch {
            featureRepository.submitErrorReport(
                contentType = target.first,
                recordId = target.second,
                issueCategory = issueCategory,
                explanation = explanation,
                referenceSource = referenceSource
            )
            closeErrorReport()
        }
    }

    fun updateReportStatus(reportId: Long, status: String, notes: String) {
        viewModelScope.launch {
            featureRepository.updateErrorReportStatus(reportId, status, notes)
        }
    }

    fun submitCorrectionProposal(
        contentType: String,
        recordId: String,
        fieldName: String,
        oldValue: String,
        proposedValue: String,
        authoritativeSource: String
    ) {
        viewModelScope.launch {
            featureRepository.createCorrectionProposal(
                contentType = contentType,
                recordId = recordId,
                fieldName = fieldName,
                oldValue = oldValue,
                proposedValue = proposedValue,
                authoritativeSource = authoritativeSource,
                submittedBy = _uiState.value.currentAdminUser
            )
        }
    }

    fun reviewCorrectionProposal(
        proposalId: Long,
        newStatus: String,
        reason: String
    ) {
        viewModelScope.launch {
            featureRepository.reviewCorrectionProposal(
                proposalId = proposalId,
                newStatus = newStatus,
                reviewerEmail = _uiState.value.currentAdminUser,
                reviewerRole = _uiState.value.currentAdminRole,
                reason = reason
            )
        }
    }

    fun syncUserProfileOnSignIn(user: com.google.firebase.auth.FirebaseUser) {
        viewModelScope.launch {
            firebaseSyncRepository.syncUserProfile(
                displayName = user.displayName ?: "মুসলিম বান্দা",
                currentLanguage = _uiState.value.currentLanguage,
                selectedCity = _uiState.value.selectedLocation.nameBengali,
                arabicFontSize = _uiState.value.arabicFontSize,
                bengaliFontSize = _uiState.value.bengaliFontSize,
                isDarkMode = _uiState.value.isDarkMode
            )
        }
    }

    // --- Azan Alarm Controls ---
    fun toggleAutoAzan(enabled: Boolean, context: Context = getApplication()) {
        _uiState.value = _uiState.value.copy(isAutoAzanEnabled = enabled)
        schedulePrayerAlarms(context)
    }

    fun togglePrayerAlarm(prayerKey: String, enabled: Boolean, context: Context = getApplication()) {
        val current = _uiState.value
        val updated = when (prayerKey.lowercase()) {
            "fajr", "ফজর" -> current.copy(isFajrAlarmEnabled = enabled)
            "dhuhr", "যোহর" -> current.copy(isDhuhrAlarmEnabled = enabled)
            "asr", "আসর" -> current.copy(isAsrAlarmEnabled = enabled)
            "maghrib", "মাগরিব" -> current.copy(isMaghribAlarmEnabled = enabled)
            "isha", "ইশা" -> current.copy(isIshaAlarmEnabled = enabled)
            else -> current
        }
        _uiState.value = updated
        schedulePrayerAlarms(context)
    }

    private fun schedulePrayerAlarms(context: Context = getApplication()) {
        val state = _uiState.value
        val schedule = state.prayerSchedule ?: return
        com.example.util.PrayerAlarmScheduler.scheduleAllPrayerAlarms(
            context = context,
            schedule = schedule,
            isAutoAzanEnabled = state.isAutoAzanEnabled,
            fajrEnabled = state.isFajrAlarmEnabled,
            dhuhrEnabled = state.isDhuhrAlarmEnabled,
            asrEnabled = state.isAsrAlarmEnabled,
            maghribEnabled = state.isMaghribAlarmEnabled,
            ishaEnabled = state.isIshaAlarmEnabled
        )
    }

    fun playAzanPreview() {
        audioPlayer.playAzan()
    }

    // --- Audio Recitation Helpers ---
    fun playFullSurah(surah: Surah) {
        audioPlayer.playFullSurahAudio(surah.number, surah.nameBengali, surah.fullAudioUrl)
    }

    fun playJuz(juz: JuzInfo) {
        audioPlayer.playJuzAudio(juz.number, juz.nameBengali, juz.audioUrl)
    }

    // --- GPS Location Tracking ---
    fun refreshLocationFromGps(context: Context = getApplication(), onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            val detected = com.example.util.GpsLocationHelper.getCurrentDeviceLocation(context)
            if (detected != null) {
                _uiState.value = _uiState.value.copy(
                    selectedLocation = detected,
                    isGpsLocationActive = true,
                    gpsStatusMessage = "জিপিএস অবস্থান সফলভাবে সনাক্ত হয়েছে: ${detected.nameBengali}"
                )
                updatePrayerScheduleAndHijri()
                schedulePrayerAlarms(context)
                onResult(true, "অবস্থান সনাক্ত হয়েছে: ${detected.nameBengali}")
            } else {
                val hasPerm = com.example.util.GpsLocationHelper.hasLocationPermission(context)
                val msg = if (!hasPerm) {
                    "জিপিএস লোকেশন পারমিশন প্রয়োজন।"
                } else {
                    "জিপিএস সিগন্যাল পাওয়া যায়নি। ফোনের লোকেশন অন রাখুন।"
                }
                _uiState.value = _uiState.value.copy(gpsStatusMessage = msg)
                onResult(false, msg)
            }
        }
    }

    // --- Admin Content Management (Add & Delete) ---
    fun addAdminContent(
        type: String,
        title: String,
        arabic: String,
        bengali: String,
        source: String
    ) {
        viewModelScope.launch {
            featureRepository.addAdminContent(
                type = type,
                title = title,
                arabic = arabic,
                bengali = bengali,
                source = source,
                addedBy = _uiState.value.currentAdminUser
            )
        }
    }

    fun deleteAdminContent(id: Long) {
        viewModelScope.launch {
            featureRepository.deleteAdminContent(id)
        }
    }

    // --- Compass SensorEventListener ---
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, gravityValues, 0, 3)
            hasGravity = true
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, geomagneticValues, 0, 3)
            hasGeomagnetic = true
        }

        if (hasGravity && hasGeomagnetic) {
            val r = FloatArray(9)
            val i = FloatArray(9)
            if (SensorManager.getRotationMatrix(r, i, gravityValues, geomagneticValues)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(r, orientation)
                var azimuthDeg = Math.toDegrees(orientation[0].toDouble()).toFloat()
                if (azimuthDeg < 0) azimuthDeg += 360f

                val qibla = _uiState.value.qiblaBearing
                var relative = qibla - azimuthDeg
                if (relative < 0) relative += 360f

                _uiState.value = _uiState.value.copy(
                    deviceCompassHeading = azimuthDeg,
                    qiblaRelativeAngle = relative
                )
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        val accText = when (accuracy) {
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> "উচ্চ (নির্ভুল)"
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> "মাঝারি"
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> "নিম্ন (ফোনটি ৮ আকৃতিতে ঘোরান)"
            else -> "ক্যালিব্রেট করা প্রয়োজন"
        }
        _uiState.value = _uiState.value.copy(compassAccuracy = accText)
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
        audioPlayer.release()
        try {
            sensorManager?.unregisterListener(this)
        } catch (e: Exception) {
            // ignore
        }
    }
}
