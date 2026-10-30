package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity

class PrayerAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "prayer_azan_channel"
        const val CHANNEL_NAME = "নামাজের আজান ও সতর্কবার্তা"
        const val EXTRA_PRAYER_NAME = "extra_prayer_name"
        const val EXTRA_PRAYER_TIME = "extra_prayer_time"
        const val ACTION_PRAYER_ALARM = "com.example.ACTION_PRAYER_ALARM"

        private var activeAzanPlayer: MediaPlayer? = null

        fun stopActiveAzan() {
            try {
                activeAzanPlayer?.apply {
                    if (isPlaying) stop()
                    release()
                }
            } catch (e: Exception) {
                // ignore
            }
            activeAzanPlayer = null
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME) ?: "নামাজ"
        val prayerTime = intent.getStringExtra(EXTRA_PRAYER_TIME) ?: ""

        Log.d("PrayerAlarmReceiver", "Received prayer alarm for $prayerName at $prayerTime")

        // 1. Show Heads-up Notification
        showPrayerNotification(context, prayerName, prayerTime)

        // 2. Play Azan sound
        playAzanAudio(context, prayerName)

        // 3. Vibrate device
        vibrateDevice(context)
    }

    private fun showPrayerNotification(context: Context, prayerName: String, prayerTime: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "ওয়াক্তমতো ৫ ওয়াক্ত নামাজের আজান ও সতর্কবার্তা"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("ওয়াক্ত হয়েছে: $prayerName নামাজের সময়")
            .setContentText("হাইয়্যা আলাস সালাহ — সালাতের দিকে এসো। সময়: $prayerTime")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))
            .build()

        notificationManager.notify(prayerName.hashCode(), notification)
    }

    private fun playAzanAudio(context: Context, prayerName: String) {
        stopActiveAzan()

        try {
            activeAzanPlayer = MediaPlayer.create(context, com.example.R.raw.azan)?.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
                setOnCompletionListener {
                    stopActiveAzan()
                }
                setOnErrorListener { mp, what, extra ->
                    Log.e("PrayerAlarmReceiver", "Azan raw playback error: what=$what, extra=$extra")
                    try {
                        mp.reset()
                        mp.release()
                    } catch (e: Exception) {
                        // ignore
                    }
                    if (activeAzanPlayer == mp) activeAzanPlayer = null
                    playFallbackAlarm(context)
                    true
                }
                start()
            }

            if (activeAzanPlayer == null) {
                playFallbackAlarm(context)
            }
        } catch (e: Exception) {
            Log.e("PrayerAlarmReceiver", "Failed to play azan: ${e.message}")
            playFallbackAlarm(context)
        }
    }

    private fun playFallbackAlarm(context: Context) {
        try {
            val alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            activeAzanPlayer = MediaPlayer.create(context, alert)?.apply {
                start()
                setOnCompletionListener {
                    stopActiveAzan()
                }
            }
        } catch (e: Exception) {
            Log.e("PrayerAlarmReceiver", "Failed to play fallback alarm: ${e.message}")
        }
    }

    private fun vibrateDevice(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    android.os.VibrationEffect.createWaveform(longArrayOf(0, 400, 200, 400, 200, 400), -1)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(longArrayOf(0, 400, 200, 400, 200, 400), -1)
            }
        } catch (e: Exception) {
            // ignore
        }
    }
}
