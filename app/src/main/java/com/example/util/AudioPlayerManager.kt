package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import com.example.data.model.ReciterInfo
import com.example.data.repository.QuranRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val title: String = "",
    val subtitle: String = "",
    val currentAyahNumber: Int = 0,
    val currentSurahNumber: Int = 0,
    val surahNameBengali: String = "",
    val reciterName: String = "মিশারি রাশিদ আল-আফাসী",
    val reciterId: String = "alafasy",
    val isContinuousMode: Boolean = true,
    val durationMs: Int = 0,
    val currentPositionMs: Int = 0,
    val errorMessage: String? = null
)

class AudioPlayerManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var standbyPlayer: MediaPlayer? = null
    private var standbySurahNumber: Int = 0
    private var standbyAyahNumber: Int = 0
    private var standbyReciterId: String = ""
    private var isStandbyPrepared: Boolean = false
    private var shouldStartWhenPrepared: Boolean = false

    private val _playerState = MutableStateFlow(AudioPlayerState())
    val playerState: StateFlow<AudioPlayerState> = _playerState.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    // Listener for synchronous UI highlight and auto-scroll
    var onAyahChangedListener: ((surahNumber: Int, ayahNumber: Int) -> Unit)? = null

    private val audioAttributes = AudioAttributes.Builder()
        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .build()

    private fun getAudioCacheDir(): File {
        val dir = File(context.cacheDir, "quran_audio")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    private fun getCachedFile(reciterId: String, surahNumber: Int, ayahNumber: Int): File {
        val s = String.format(Locale.US, "%03d", surahNumber)
        val a = String.format(Locale.US, "%03d", ayahNumber)
        return File(getAudioCacheDir(), "${reciterId}_${s}_${a}.mp3")
    }

    private fun getAyahAudioUrl(reciter: ReciterInfo, surahNumber: Int, ayahNumber: Int): String {
        val s = String.format(Locale.US, "%03d", surahNumber)
        val a = String.format(Locale.US, "%03d", ayahNumber)
        return "${reciter.audioBaseUrl}$s$a.mp3"
    }

    /**
     * Cache audio file in background for offline use and zero-latency replays.
     */
    private fun cacheAudioInBackground(urlStr: String, destFile: File) {
        coroutineScope.launch {
            if (destFile.exists() && destFile.length() > 2000) return@launch
            try {
                val url = URL(urlStr)
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 8000
                conn.readTimeout = 15000
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Android; Mobile; NoorIslamic/1.0)")
                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    val tempFile = File(destFile.parentFile, "${destFile.name}.tmp")
                    conn.inputStream.use { input ->
                        FileOutputStream(tempFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    if (tempFile.length() > 2000) {
                        tempFile.renameTo(destFile)
                    } else {
                        tempFile.delete()
                    }
                }
            } catch (e: Exception) {
                // Background cache error is non-fatal
            }
        }
    }

    /**
     * Prepares a MediaPlayer for an Ayah.
     * Uses local cached MP3 if available, otherwise streams and triggers background cache.
     */
    private fun preparePlayerForAyah(
        reciter: ReciterInfo,
        surahNumber: Int,
        ayahNumber: Int,
        onPrepared: (MediaPlayer) -> Unit,
        onError: () -> Unit
    ): MediaPlayer {
        val player = MediaPlayer()
        player.setAudioAttributes(audioAttributes)

        val cachedFile = getCachedFile(reciter.id, surahNumber, ayahNumber)
        val audioUrl = getAyahAudioUrl(reciter, surahNumber, ayahNumber)

        try {
            if (cachedFile.exists() && cachedFile.length() > 2000) {
                player.setDataSource(cachedFile.absolutePath)
            } else {
                val uri = Uri.parse(audioUrl)
                val headers = mapOf("User-Agent" to "Mozilla/5.0 (Android; Mobile; NoorIslamic/1.0)")
                player.setDataSource(context, uri, headers)
                // Cache for next time / offline
                cacheAudioInBackground(audioUrl, cachedFile)
            }

            player.setOnPreparedListener { mp ->
                onPrepared(mp)
            }
            player.setOnErrorListener { _, what, extra ->
                Log.e("AudioPlayerManager", "MediaPlayer error: what=$what, extra=$extra for Surah $surahNumber Ayah $ayahNumber")
                onError()
                true
            }
            player.prepareAsync()
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "preparePlayerForAyah exception: ${e.message}")
            onError()
        }

        return player
    }

    /**
     * Preloads the next Ayah on standbyPlayer to guarantee 0ms seamless gapless playback.
     */
    private fun preloadNextAyah(
        surahNumber: Int,
        nextAyahNumber: Int,
        reciter: ReciterInfo,
        totalAyahsInSurah: Int
    ) {
        if (nextAyahNumber > totalAyahsInSurah) {
            // Reached end of Surah
            releaseStandbyPlayer()
            return
        }

        // Release previous standby player if not matching
        if (standbyPlayer != null && (standbySurahNumber != surahNumber || standbyAyahNumber != nextAyahNumber || standbyReciterId != reciter.id)) {
            releaseStandbyPlayer()
        }

        if (standbyPlayer != null && isStandbyPrepared) {
            return // Already prepared
        }

        standbySurahNumber = surahNumber
        standbyAyahNumber = nextAyahNumber
        standbyReciterId = reciter.id
        isStandbyPrepared = false
        shouldStartWhenPrepared = false

        standbyPlayer = preparePlayerForAyah(
            reciter = reciter,
            surahNumber = surahNumber,
            ayahNumber = nextAyahNumber,
            onPrepared = { mp ->
                isStandbyPrepared = true
                if (shouldStartWhenPrepared) {
                    shouldStartWhenPrepared = false
                    switchToStandbyPlayer(surahNumber, nextAyahNumber, reciter, totalAyahsInSurah)
                }
            },
            onError = {
                releaseStandbyPlayer()
            }
        )
    }

    /**
     * Seamlessly transition to the preloaded standby player with ZERO pause.
     */
    private fun switchToStandbyPlayer(
        surahNumber: Int,
        ayahNumber: Int,
        reciter: ReciterInfo,
        totalAyahsInSurah: Int
    ) {
        val next = standbyPlayer ?: return
        standbyPlayer = null
        isStandbyPrepared = false
        shouldStartWhenPrepared = false

        // Release old player safely
        try {
            mediaPlayer?.reset()
            mediaPlayer?.release()
        } catch (e: Exception) {
            // ignore
        }

        mediaPlayer = next
        try {
            next.start()
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Error starting standby player: ${e.message}")
        }

        val surah = QuranRepository.getSurah(surahNumber)
        val surahName = surah?.nameBengali ?: "কুরআন"

        _playerState.value = _playerState.value.copy(
            isPlaying = true,
            isLoading = false,
            title = "সূরা $surahName",
            subtitle = "আয়াত নং $ayahNumber • ${reciter.nameBengali}",
            currentSurahNumber = surahNumber,
            currentAyahNumber = ayahNumber,
            surahNameBengali = surahName,
            reciterName = reciter.nameBengali,
            reciterId = reciter.id,
            durationMs = try { next.duration } catch (e: Exception) { 0 },
            errorMessage = null
        )

        // Inform listener for UI highlight and auto-scroll
        onAyahChangedListener?.invoke(surahNumber, ayahNumber)

        // Set completion listener on the new active player
        next.setOnCompletionListener {
            handleAyahCompletion(surahNumber, ayahNumber, reciter, totalAyahsInSurah)
        }

        // IMMEDIATELY preload the subsequent Ayah (N + 1) in advance!
        if (_playerState.value.isContinuousMode && ayahNumber < totalAyahsInSurah) {
            preloadNextAyah(surahNumber, ayahNumber + 1, reciter, totalAyahsInSurah)
        }
    }

    private fun handleAyahCompletion(
        surahNumber: Int,
        currentAyah: Int,
        reciter: ReciterInfo,
        totalAyahsInSurah: Int
    ) {
        if (!_playerState.value.isContinuousMode || currentAyah >= totalAyahsInSurah) {
            _playerState.value = _playerState.value.copy(isPlaying = false, currentPositionMs = 0)
            releaseStandbyPlayer()
            return
        }

        val nextAyah = currentAyah + 1
        if (isStandbyPrepared && standbyPlayer != null && standbySurahNumber == surahNumber && standbyAyahNumber == nextAyah) {
            // ZERO DELAY INSTANT START!
            switchToStandbyPlayer(surahNumber, nextAyah, reciter, totalAyahsInSurah)
        } else if (standbyPlayer != null) {
            // Standby is still buffering, set flag to auto-start the millisecond it is ready
            shouldStartWhenPrepared = true
            _playerState.value = _playerState.value.copy(
                isLoading = true,
                currentAyahNumber = nextAyah
            )
        } else {
            // Prepare and play directly
            playAyahAudio(
                surahNumber = surahNumber,
                ayahNumberInSurah = nextAyah,
                surahNameBengali = _playerState.value.surahNameBengali,
                reciter = reciter,
                isContinuous = true
            )
        }
    }

    private fun releaseStandbyPlayer() {
        try {
            standbyPlayer?.reset()
            standbyPlayer?.release()
        } catch (e: Exception) {
            // ignore
        }
        standbyPlayer = null
        isStandbyPrepared = false
        shouldStartWhenPrepared = false
    }

    /**
     * Play Ayah audio with seamless prebuffering of the next Ayah.
     */
    fun playAyahAudio(
        surahNumber: Int,
        ayahNumberInSurah: Int,
        surahNameBengali: String,
        reciter: ReciterInfo = QuranRepository.reciters.first(),
        isContinuous: Boolean = true,
        onComplete: (() -> Unit)? = null
    ) {
        stopAudio()
        val surah = QuranRepository.getSurah(surahNumber)
        val totalVerses = surah?.totalVerses ?: 286

        _playerState.value = _playerState.value.copy(
            isLoading = true,
            title = "সূরা $surahNameBengali",
            subtitle = "আয়াত নং $ayahNumberInSurah • ${reciter.nameBengali}",
            currentSurahNumber = surahNumber,
            currentAyahNumber = ayahNumberInSurah,
            surahNameBengali = surahNameBengali,
            reciterName = reciter.nameBengali,
            reciterId = reciter.id,
            isContinuousMode = isContinuous,
            errorMessage = null
        )

        onAyahChangedListener?.invoke(surahNumber, ayahNumberInSurah)

        mediaPlayer = preparePlayerForAyah(
            reciter = reciter,
            surahNumber = surahNumber,
            ayahNumber = ayahNumberInSurah,
            onPrepared = { player ->
                if (mediaPlayer == player) {
                    try {
                        player.start()
                        _playerState.value = _playerState.value.copy(
                            isPlaying = true,
                            isLoading = false,
                            durationMs = player.duration
                        )
                    } catch (e: Exception) {
                        Log.e("AudioPlayerManager", "Error starting active player: ${e.message}")
                    }

                    // PRELOAD NEXT AYAH RIGHT NOW while current ayah is reciting!
                    if (isContinuous && ayahNumberInSurah < totalVerses) {
                        preloadNextAyah(surahNumber, ayahNumberInSurah + 1, reciter, totalVerses)
                    }
                }
            },
            onError = {
                _playerState.value = _playerState.value.copy(
                    isPlaying = false,
                    isLoading = false,
                    errorMessage = "অডিও প্লে করা সম্ভব হয়নি। সংযোগ পরীক্ষা করুন।"
                )
            }
        )

        mediaPlayer?.setOnCompletionListener {
            onComplete?.invoke()
            handleAyahCompletion(surahNumber, ayahNumberInSurah, reciter, totalVerses)
        }
    }

    fun playFullSurahAudio(
        surahNumber: Int,
        surahNameBengali: String,
        reciter: ReciterInfo = QuranRepository.reciters.first(),
        onComplete: (() -> Unit)? = null
    ) {
        playAyahAudio(
            surahNumber = surahNumber,
            ayahNumberInSurah = 1,
            surahNameBengali = surahNameBengali,
            reciter = reciter,
            isContinuous = true,
            onComplete = onComplete
        )
    }

    fun playJuzAudio(
        juzNumber: Int,
        juzNameBengali: String,
        reciter: ReciterInfo = QuranRepository.reciters.first(),
        customUrl: String? = null
    ) {
        val juz = QuranRepository.all30Juz.find { it.number == juzNumber }
        val startSurah = juz?.startingSurahNumber ?: 1
        val startAyah = juz?.startingAyahNumber ?: 1
        val surah = QuranRepository.getSurah(startSurah)

        playAyahAudio(
            surahNumber = startSurah,
            ayahNumberInSurah = startAyah,
            surahNameBengali = surah?.nameBengali ?: "পারা $juzNumber",
            reciter = reciter,
            isContinuous = true
        )
    }

    fun playAzan(onComplete: (() -> Unit)? = null) {
        stopAudio()
        _playerState.value = _playerState.value.copy(
            isLoading = true,
            title = "পবিত্র মক্কার সুললিত আজান",
            subtitle = "হাইয়্যা আলাস সালাহ - নামাজের দিকে এসো",
            currentSurahNumber = 0,
            currentAyahNumber = 0,
            surahNameBengali = "আজান",
            errorMessage = null
        )

        try {
            val player = try {
                context.resources.openRawResourceFd(com.example.R.raw.azan)?.use { afd ->
                    MediaPlayer().apply {
                        setAudioAttributes(audioAttributes)
                        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                        prepare()
                    }
                }
            } catch (e: Exception) {
                Log.e("AudioPlayerManager", "Failed to open raw azan: ${e.message}")
                null
            }

            if (player != null) {
                player.setOnCompletionListener {
                    _playerState.value = _playerState.value.copy(isPlaying = false, currentPositionMs = 0)
                    onComplete?.invoke()
                }
                player.setOnErrorListener { mp, what, extra ->
                    try { mp.reset(); mp.release() } catch (e: Exception) {}
                    if (mediaPlayer == mp) mediaPlayer = null
                    playSystemAlarmFallback(onComplete)
                    true
                }
                player.start()
                _playerState.value = _playerState.value.copy(
                    isPlaying = true,
                    isLoading = false,
                    durationMs = player.duration
                )
                mediaPlayer = player
            } else {
                playSystemAlarmFallback(onComplete)
            }
        } catch (e: Exception) {
            playSystemAlarmFallback(onComplete)
        }
    }

    private fun playSystemAlarmFallback(onComplete: (() -> Unit)?) {
        try {
            val alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            if (alert != null) {
                val player = MediaPlayer().apply {
                    setAudioAttributes(audioAttributes)
                    setDataSource(context, alert)
                    prepare()
                }
                player.setOnCompletionListener {
                    _playerState.value = _playerState.value.copy(isPlaying = false)
                    onComplete?.invoke()
                }
                player.start()
                _playerState.value = _playerState.value.copy(
                    isPlaying = true,
                    isLoading = false
                )
                mediaPlayer = player
            } else {
                _playerState.value = _playerState.value.copy(isPlaying = false, isLoading = false)
            }
        } catch (e: Exception) {
            _playerState.value = _playerState.value.copy(isPlaying = false, isLoading = false)
        }
    }

    fun pauseAudio() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _playerState.value = _playerState.value.copy(isPlaying = false)
            }
        }
    }

    fun resumeAudio() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                _playerState.value = _playerState.value.copy(isPlaying = true)
            }
        }
    }

    fun stopAudio() {
        releaseStandbyPlayer()
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
        } catch (e: Exception) {
            // ignored
        }
        mediaPlayer = null
        _playerState.value = _playerState.value.copy(isPlaying = false, isLoading = false)
    }

    fun release() {
        stopAudio()
    }
}
