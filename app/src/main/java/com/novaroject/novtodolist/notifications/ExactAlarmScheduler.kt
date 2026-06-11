package com.novaroject.novtodolist.notifications

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.novaroject.novtodolist.MainActivity
import com.novaroject.novtodolist.NovToDoApp.Companion.CHANNEL_TASKS
import com.novaroject.novtodolist.R

/**
 * ExactAlarmScheduler — точные уведомления через AlarmManager.
 *
 * Почему AlarmManager вместо WorkManager:
 *  - WorkManager откладывается на 5–15 мин в режиме Doze (Батарея экономится).
 *  - AlarmManager.setExactAndAllowWhileIdle() срабатывает точно, даже в Doze.
 *  - Требует разрешения SCHEDULE_EXACT_ALARM (Android 12+) — уже в манифесте.
 */
object ExactAlarmScheduler {

    private const val EXTRA_TASK_ID   = "task_id"
    private const val EXTRA_TITLE     = "task_title"
    private const val EXTRA_IS_AT     = "is_at_time"
    private const val REQ_PRE_OFFSET  = 100_000  // offset для уникального requestCode "pre"
    private const val REQ_AT_OFFSET   = 200_000  // offset для уникального requestCode "at"

    /**
     * Планирует два точных будильника:
     *  1. За [reminderOffsetMinutes] минут до дедлайна (предупреждение)
     *  2. Ровно в момент [dueDateMillis]                (точное напоминание)
     */
    fun schedule(
        context: Context,
        taskId: String,
        title: String,
        dueDateMillis: Long,
        reminderOffsetMinutes: Int = 30
    ) {
        val am  = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val now = System.currentTimeMillis()

        // 1. Заблаговременное уведомление
        if (reminderOffsetMinutes > 0) {
            val preMs = dueDateMillis - reminderOffsetMinutes * 60_000L
            if (preMs > now) {
                val pi = buildIntent(context, taskId, title, isAtTime = false, reqCode = reqCode(taskId, pre = true))
                setExact(am, preMs, pi)
            }
        }

        // 2. Точное уведомление в момент задачи
        if (dueDateMillis > now) {
            val pi = buildIntent(context, taskId, title, isAtTime = true, reqCode = reqCode(taskId, pre = false))
            setExact(am, dueDateMillis, pi)
        }
    }

    /** Отменяет оба будильника для задачи */
    fun cancel(context: Context, taskId: String) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(buildIntent(context, taskId, "", isAtTime = false, reqCode = reqCode(taskId, true)))
        am.cancel(buildIntent(context, taskId, "", isAtTime = true, reqCode = reqCode(taskId, false)))
    }

    private fun setExact(am: AlarmManager, triggerMs: Long, pi: PendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+: проверяем разрешение
            if (am.canScheduleExactAlarms()) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMs, pi)
            } else {
                // Fallback — неточный, но работает без разрешения
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMs, pi)
            }
        } else {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMs, pi)
        }
    }

    private fun buildIntent(
        context: Context,
        taskId: String,
        title: String,
        isAtTime: Boolean,
        reqCode: Int
    ): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_TASK_ID, taskId)
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_IS_AT, isAtTime)
        }
        return PendingIntent.getBroadcast(
            context, reqCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /** Уникальный requestCode из первых 5 цифр хэша taskId + offset */
    private fun reqCode(taskId: String, pre: Boolean): Int {
        val base = Math.abs(taskId.hashCode() % 90_000)
        return base + if (pre) REQ_PRE_OFFSET else REQ_AT_OFFSET
    }
}

/**
 * BroadcastReceiver — принимает сигнал будильника и показывает уведомление.
 * Должен быть зарегистрирован в AndroidManifest.xml.
 */
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId  = intent.getStringExtra("task_id")  ?: return
        val title   = intent.getStringExtra("task_title") ?: return
        val isAtTime = intent.getBooleanExtra("is_at_time", false)

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pi = PendingIntent.getActivity(
            context, taskId.hashCode(), openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val (notifTitle, notifText) = if (isAtTime)
            "✅ Задача: $title" to "Время выполнить задачу!"
        else
            "⏰ Напоминание" to "Скоро: $title"

        val notif = NotificationCompat.Builder(context, CHANNEL_TASKS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(notifTitle)
            .setContentText(notifText)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$notifText\n📌 $title"))
            .setAutoCancel(true)
            .setContentIntent(pi)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()

        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify("${taskId}_${if (isAtTime) "at" else "pre"}".hashCode(), notif)
    }
}

/**
 * BroadcastReceiver для восстановления будильников после перезагрузки устройства.
 * Требует разрешения RECEIVE_BOOT_COMPLETED.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        // TODO: загрузить задачи из локальной БД и переназначить будильники
        // Это потребует Room DB для хранения дат локально без обращения к Firestore
    }
}
