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

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pi = PendingIntent.getActivity(
            applicationContext, taskId.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notif = NotificationCompat.Builder(applicationContext, CHANNEL_TASKS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("⏰ Напоминание")
            .setContentText(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText("Задача: $title"))
            .setAutoCancel(true)
            .setContentIntent(pi)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        (applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(taskId.hashCode(), notif)

        return Result.success()
    }

    companion object {
        const val KEY_TITLE   = "task_title"
        const val KEY_TASK_ID = "task_id"

        fun schedule(context: Context, taskId: String, title: String, dueDateMillis: Long) {
            val notifyAt = dueDateMillis - 30 * 60 * 1000L
            val delayMs  = notifyAt - System.currentTimeMillis()
            if (delayMs <= 0) return

            val data = workDataOf(KEY_TITLE to title, KEY_TASK_ID to taskId)
            val request = OneTimeWorkRequestBuilder<TaskReminderWorker>()
                .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork("reminder:$taskId", ExistingWorkPolicy.REPLACE, request)
        }

        fun cancel(context: Context, taskId: String) {
            WorkManager.getInstance(context).cancelUniqueWork("reminder:$taskId")
        }
    }
}
