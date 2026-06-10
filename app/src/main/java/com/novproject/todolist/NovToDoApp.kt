package com.novproject.todolist

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class NovToDoApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            val taskChannel = NotificationChannel(
                CHANNEL_TASKS,
                "Задачи",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления о задачах"
                enableVibration(true)
            }

            val syncChannel = NotificationChannel(
                CHANNEL_SYNC,
                "Синхронизация",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Уведомления о выполнении задач другими пользователями"
            }

            manager.createNotificationChannels(listOf(taskChannel, syncChannel))
        }
    }

    companion object {
        const val CHANNEL_TASKS = "channel_tasks"
        const val CHANNEL_SYNC = "channel_sync"
    }
}
