package com.example.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.model.PrayerSchedule
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object PrayerAlarmScheduler {

    private const val REQUEST_CODE_FAJR = 1001
    private const val REQUEST_CODE_DHUHR = 1002
    private const val REQUEST_CODE_ASR = 1003
    private const val REQUEST_CODE_MAGHRIB = 1004
    private const val REQUEST_CODE_ISHA = 1005

    fun scheduleAllPrayerAlarms(
        context: Context,
        schedule: PrayerSchedule,
        isAutoAzanEnabled: Boolean,
        fajrEnabled: Boolean = true,
        dhuhrEnabled: Boolean = true,
        asrEnabled: Boolean = true,
        maghribEnabled: Boolean = true,
        ishaEnabled: Boolean = true
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        if (!isAutoAzanEnabled) {
            cancelAllAlarms(context)
            Log.d("PrayerAlarmScheduler", "Auto Azan disabled. Cancelled all prayer alarms.")
            return
        }

        val prayerEntries = listOf(
            Triple("ফজর", schedule.fajr, Pair(REQUEST_CODE_FAJR, fajrEnabled)),
            Triple("যোহর", schedule.dhuhr, Pair(REQUEST_CODE_DHUHR, dhuhrEnabled)),
            Triple("আসর", schedule.asr, Pair(REQUEST_CODE_ASR, asrEnabled)),
            Triple("মাগরিব", schedule.maghrib, Pair(REQUEST_CODE_MAGHRIB, maghribEnabled)),
            Triple("ইশা", schedule.isha, Pair(REQUEST_CODE_ISHA, ishaEnabled))
        )

        val now = System.currentTimeMillis()

        for ((name, timeStr, config) in prayerEntries) {
            val (reqCode, isEnabled) = config
            if (!isEnabled) {
                cancelAlarm(context, reqCode)
                continue
            }

            val triggerTime = parseTimeToMillis(timeStr)
            val finalTrigger = if (triggerTime <= now) {
                // If today's prayer time has already passed, schedule for tomorrow
                triggerTime + 24 * 60 * 60 * 1000
            } else {
                triggerTime
            }

            scheduleAlarm(context, alarmManager, reqCode, name, timeStr, finalTrigger)
        }
    }

    private fun scheduleAlarm(
        context: Context,
        alarmManager: AlarmManager,
        requestCode: Int,
        prayerName: String,
        timeFormatted: String,
        triggerAtMillis: Long
    ) {
        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = PrayerAlarmReceiver.ACTION_PRAYER_ALARM
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME, prayerName)
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_TIME, timeFormatted)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
            Log.d("PrayerAlarmScheduler", "Scheduled $prayerName Azan alarm at timestamp $triggerAtMillis")
        } catch (e: SecurityException) {
            Log.w("PrayerAlarmScheduler", "Exact alarm permission not granted: ${e.message}")
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } catch (e: Exception) {
            Log.e("PrayerAlarmScheduler", "Failed to schedule $prayerName: ${e.message}")
        }
    }

    fun cancelAllAlarms(context: Context) {
        listOf(
            REQUEST_CODE_FAJR,
            REQUEST_CODE_DHUHR,
            REQUEST_CODE_ASR,
            REQUEST_CODE_MAGHRIB,
            REQUEST_CODE_ISHA
        ).forEach { cancelAlarm(context, it) }
    }

    private fun cancelAlarm(context: Context, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, PrayerAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

    private fun parseTimeToMillis(timeStr: String): Long {
        val formats = listOf(
            SimpleDateFormat("hh:mm a", Locale.US),
            SimpleDateFormat("HH:mm", Locale.US),
            SimpleDateFormat("h:mm a", Locale.US)
        )

        val calNow = Calendar.getInstance()

        for (format in formats) {
            try {
                val parsed = format.parse(timeStr.trim())
                if (parsed != null) {
                    val calParsed = Calendar.getInstance().apply { time = parsed }
                    return calNow.apply {
                        set(Calendar.HOUR_OF_DAY, calParsed.get(Calendar.HOUR_OF_DAY))
                        set(Calendar.MINUTE, calParsed.get(Calendar.MINUTE))
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                }
            } catch (e: Exception) {
                // Try next format
            }
        }
        return calNow.timeInMillis
    }
}
