package com.example.todolist.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.todolist.MainActivity
import com.example.todolist.R
import com.example.todolist.entities.Task
import java.time.LocalDateTime
import java.time.ZoneId

object NotificationUtils {

    private const val channelName = "todo"
    const val id = "NotificationChannel"


    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(id, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager =
            context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    fun setNotification(context: Context, task: Task) {
        if (task.notificationEnabled) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("task_id", task.id)
                putExtra("task_title", task.title)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                task.id.toInt(),
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val sharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
            var notificationTime = sharedPreferences.getInt("notificationTime", 1)
            notificationTime = (notificationTime * 60 * 1000L).toInt()

            val executionTime = task.dueDate
//            val notifyTime = executionTime - notificationTime
            val notifyTime = LocalDateTime.now().plusSeconds(4).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, notifyTime, pendingIntent)
            }
        }
    }



    class AlarmReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            context?.let {
                val taskId = intent?.getLongExtra("task_id", 0L) ?: 0L
                val taskTitle = intent?.getStringExtra("task_title")
                if (taskTitle != null) {
                    showNotification(it, taskId, taskTitle)
                }
            }
        }

        private fun showNotification(context: Context, taskId: Long, taskTitle: String) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("task_id", taskId)
                putExtra("navigate_to_task", true)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                taskId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, id)
                .setContentTitle(taskTitle)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.notification)
                .setAutoCancel(true)
            notificationManager.notify(taskId.toInt(), builder.build())
        }

    }
}