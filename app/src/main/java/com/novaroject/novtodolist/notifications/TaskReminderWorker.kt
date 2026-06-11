package com.novaroject.novtodolist.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.novaroject.novtodolist.MainActivity
import com.novaroject.novtodolist.NovToDoApp.Companion.CHANNEL_TASKS
import com.novaroject.novtodolist.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class TaskReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val title  = inputData.getString(KEY_TITLE)  ?: return Result.success()
        val taskId = inputData.getString(KEY_TASK_ID) ?: return Result.success()
        val isAtTime = inputData.getBoolean(KEY_IS_AT_TIME, false)

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pi = PendingIntent.getActivity(
            applicationContext, taskId.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Fix #4 — разные тексты для напоминания и момента
        val (notifTitle, notifText) = if (isAtTime) {
            "✅ Задача: $title" to "Время выполнить задачу!"
        } else {
            "⏰ Напоминание" to "Скоро: $title"
        }

        val notif = NotificationCompat.Builder(applicationContext, CHANNEL_TASKS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(notifTitle)
            .setContentText(notifText)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$notifText\n📌 $title"))
            .setAutoCancel(true)
            .setContentIntent(pi)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        (applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify("${taskId}_${if (isAtTime) "at" else "pre"}".hashCode(), notif)

        return Result.success()
    }

    companion object {
        const val KEY_TITLE       = "task_title"
        const val KEY_TASK_ID     = "task_id"
        const val KEY_IS_AT_TIME  = "is_at_time"

        /**
         * Fix #4 — планируем ДВА уведомления:
         *  1) за reminderOffsetMinutes до dueDate (предупреждение)
         *  2) ровно в момент dueDate (напоминание в установленное время)
         */
        fun schedule(
            context: Context,
            taskId: String,
            title: String,
            dueDateMillis: Long,
            reminderOffsetMinutes: Int = 30
        ) {
            val now = System.currentTimeMillis()

            // Уведомление 1 — заблаговременное (за N минут до)
            if (reminderOffsetMinutes > 0) {
                val notifyAtMillis = dueDateMillis - reminderOffsetMinutes * 60_000L
                val delayMs = notifyAtMillis - now
                if (delayMs > 0) {
                    val data = workDataOf(
                        KEY_TITLE      to title,
                        KEY_TASK_ID    to taskId,
                        KEY_IS_AT_TIME to false
                    )
                    val request = OneTimeWorkRequestBuilder<TaskReminderWorker>()
                        .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                        .setInputData(data)
                        .addTag("reminder_$taskId")
                        .build()
                    WorkManager.getInstance(context)
                        .enqueueUniqueWork("reminder_pre:$taskId", ExistingWorkPolicy.REPLACE, request)
                }
            }

            // Уведомление 2 — ровно в момент задачи (Fix #4)
            val delayAtMs = dueDateMillis - now
            if (delayAtMs > 0) {
                val dataAt = workDataOf(
                    KEY_TITLE      to title,
                    KEY_TASK_ID    to taskId,
                    KEY_IS_AT_TIME to true
                )
                val requestAt = OneTimeWorkRequestBuilder<TaskReminderWorker>()
                    .setInitialDelay(delayAtMs, TimeUnit.MILLISECONDS)
                    .setInputData(dataAt)
                    .addTag("reminder_$taskId")
                    .build()
                WorkManager.getInstance(context)
                    .enqueueUniqueWork("reminder_at:$taskId", ExistingWorkPolicy.REPLACE, requestAt)
            }
        }

        fun cancel(context: Context, taskId: String) {
            val wm = WorkManager.getInstance(context)
            wm.cancelUniqueWork("reminder_pre:$taskId")
            wm.cancelUniqueWork("reminder_at:$taskId")
        }
    }
}
