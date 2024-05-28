//// BootReceiver.kt
//package com.example.todolist.utils
//
//import android.app.Application
//import android.app.PendingIntent
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import com.example.todolist.TaskViewModel
//
//class BootReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
//            val application = context.applicationContext as Application
//            val taskViewModel = TaskViewModel(application)
//            val notificationScheduler = NotificationScheduler(application)
//
//            val notificationIntent = Intent(context, NotificationReceiver::class.java)
//            val pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, 0)
//            pendingIntent.send()
//
//            notificationScheduler.scheduleAllNotifications(taskViewModel.allTasks)
//        }
//    }
//}
