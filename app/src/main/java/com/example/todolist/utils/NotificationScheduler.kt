//package com.example.todolist.utils
//
//import android.app.AlarmManager
//import android.app.Application
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.Observer
//import com.example.todolist.entities.Task
//
//class NotificationScheduler(private val application: Application) {
//
//    private val sharedPreferences = application.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
//
//    fun scheduleNotification(task: Task) {
//        val notificationTime = sharedPreferences.getInt("notificationTime", 0) // Domyślnie 0 minut
//
//        val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(application, NotificationReceiver::class.java).apply {
//            putExtra("taskTitle", task.title)
//            putExtra("notificationId", task.id.toInt())
//        }
//
//        val pendingIntent = PendingIntent.getBroadcast(application, task.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
//
//        val triggerTime = task.dueDate - notificationTime * 60 * 1000 // odjąć czas powiadomienia
//        if (alarmManager.canScheduleExactAlarms()) {
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
//        }
//    }
//
//    fun scheduleAllNotifications(tasks: LiveData<List<Task>>) {
//        tasks.observeForever { taskList ->
//            taskList?.forEach { task ->
//                scheduleNotification(task)
//            }
//        }
//    }
//}
