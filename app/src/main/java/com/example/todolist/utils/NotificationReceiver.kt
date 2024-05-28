//package com.example.todolist.utils
//
//import android.Manifest
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import androidx.core.app.ActivityCompat
//import androidx.core.app.NotificationCompat
//import androidx.core.app.NotificationManagerCompat
//import com.example.todolist.R
//import android.app.AlertDialog
//import android.net.Uri
//import android.provider.Settings
//
//class NotificationReceiver(private val applicationContext: Context) : BroadcastReceiver() {
//
//    override fun onReceive(context: Context, intent: Intent) {
//        val taskTitle = intent.getStringExtra("taskTitle")
//        val notificationId = intent.getIntExtra("notificationId", 0)
//
//        if (ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.POST_NOTIFICATIONS
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            showPermissionDialog(applicationContext)
//            return
//        }
//
//        val notificationBuilder = NotificationCompat.Builder(applicationContext, "TASK_REMINDER_CHANNEL")
//            .setSmallIcon(R.drawable.notification)
//            .setContentTitle("Task Reminder")
//            .setContentText(taskTitle)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//
//        val notificationManager = NotificationManagerCompat.from(applicationContext)
//        notificationManager.notify(notificationId, notificationBuilder.build())
//    }
//
//    private fun showPermissionDialog(context: Context) {
//        AlertDialog.Builder(context)
//            .setTitle("Permission Required")
//            .setMessage("To send notifications, the app needs permission. Please grant the permission in the app settings.")
//            .setPositiveButton("Go to Settings") { _, _ ->
//                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                val uri = Uri.fromParts("package", context.packageName, null)
//                intent.data = uri
//                context.startActivity(intent)
//            }
//            .setNegativeButton("Cancel") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .show()
//    }
//}