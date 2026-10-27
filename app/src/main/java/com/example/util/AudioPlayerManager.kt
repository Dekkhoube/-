package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val title: String = "",
    val subtitle: String = "",
    val currentAyahNumber: Int = 0,
    val currentSurahNumber: Int = 0,
    val surahNameBengali: String = "",
    val reciterName: String = "মিশারি রাশিদ আল-আফাসী",
    val durationMs: Int = 0,
    val currentPositionMs: Int = 0,
    val errorMessage: String? = null
)

class AudioPlayerManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private val _playerState = MutableStateFlow(AudioPlayerState())
    val playerState: StateFlow<AudioPlayerState> = _playerState.asStateFlow()

    fun playAyahAudio(
        surahNumber: Int,
        ayahNumberInSurah: Int,
        surahNameBengali: String,
        reciterId: String = "alafasy"
    ) {
        val surahFormatted = String.format("%03d", surahNumber)
        val ayahFormatted = String.format("%03d", ayahNumberInSurah)
        val audioUrl = "https://everyayah.com/data/Alafasy_128kbps/$surahFormatted$ayahFormatted.mp3"

        playStream(
            url = audioUrl,
            title = "সূরা $surahNameBengali",
            subtitle = "আয়াত নং $ayahNumberInSurah • মিশারি রশিদ",
            surahNumber = surahNumber,
            ayahNumber = ayahNumberInSurah,
            surahName = surahNameBengali
        )
    }

    fun playFullSurahAudio(
        surahNumber: Int,
        surahNameBengali: String,
        customUrl: String? = null
    ) {
        val surahFormatted = String.format("%03d", surahNumber)
        val audioUrl = customUrl ?: "https://download.quranicaudio.com/quran/mishaari_raashid_al_3afaasee/$surahFormatted.mp3"

        playStream(
            url = audioUrl,
            title = "সূরা $surahNameBengali (সম্পূর্ণ তিলাওয়াত)",
            subtitle = "মিশারি রাশিদ আল-আফাসী",
            surahNumber = surahNumber,
            ayahNumber = 0,
            surahName = surahNameBengali
        )
    }

    fun playJuzAudio(
        juzNumber: Int,
        juzNameBengali: String,
        customUrl: String? = null
    ) {
        val defaultUrl = customUrl ?: "https://everyayah.com/data/Alafasy_128kbps/${String.format("%03d", juzNumber)}001.mp3"

        playStream(
            url = defaultUrl,
            title = "পারা $juzNumber - $juzNameBengali",
            subtitle = "কুরআন ৩০ পারা তিলাওয়াত",
            surahNumber = 0,
            ayahNumber = 0,
            surahName = "পারা $juzNumber"
        )
    }

    fun playAzan(onComplete: (() -> Unit)? = null) {
        val azanUrl = "https://media.blubrry.com/muslim_central_audio/podcasts.qurancentral.com/adhan/makkah-adhan.mp3"
        playStream(
            url = azanUrl,
            title = "পবিত্র মক্কার সুললিত আজান",
            subtitle = "হাইয়্যা আলাস সালাহ - নামাজের দিকে এসো",
            surahNumber = 0,
            ayahNumber = 0,
            surahName = "আজান",
            onComplete = onComplete,
            fallbackToAlarmSound = true
        )
    }

    private fun playStream(
        url: String,
        title: String,
        subtitle: String,
        surahNumber: Int,
        ayahNumber: Int,
        surahName: String,
        onComplete: (() -> Unit)? = null,
        fallbackToAlarmSound: Boolean = false
    ) {
        stopAudio()
        _playerState.value = _playerState.value.copy(
            isLoading = true,
            title = title,
            subtitle = subtitle,
            currentSurahNumber = surahNumber,
            currentAyahNumber = ayahNumber,
            surahNameBengali = surahName,
            errorMessage = null
        )

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                setOnPreparedListener { mp ->
                    mp.start()
                    _playerState.value = _playerState.value.copy(
                        isPlaying = true,
                        isLoading = false,
                        durationMs = mp.duration
                    )
                }
                setOnCompletionListener {
                    _playerState.value = _playerState.value.copy(isPlaying = false, currentPositionMs = 0)
                    onComplete?.invoke()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("AudioPlayerManager", "Audio playback error: $what, $extra for $url")
                    if (fallbackToAlarmSound) {
                        playSystemAlarmFallback(onComplete)
                    } else {
                        _playerState.value = _playerState.value.copy(
                            isPlaying = false,
                            isLoading = false,
                            errorMessage = "অডিও প্লে করতে সমস্যা হয়েছে। ইন্টারনেট কানেকশন যাচাই করুন।"
                        )
                    }
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            if (fallbackToAlarmSound) {
                playSystemAlarmFallback(onComplete)
            } else {
                _playerState.value = _playerState.value.copy(
                    isPlaying = false,
                    isLoading = false,
                    errorMessage = "অডিও লোড করা যায়নি: ${e.localizedMessage}"
                )
            }
        }
    }

    private fun playSystemAlarmFallback(onComplete: (() -> Unit)?) {
        try {
            val alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            mediaPlayer = MediaPlayer.create(context, alert)?.apply {
                start()
                _playerState.value = _playerState.value.copy(
                    isPlaying = true,
                    isLoading = false
                )
                setOnCompletionListener {
                    _playerState.value = _playerState.value.copy(isPlaying = false)
                    onComplete?.invoke()
                }
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
