package com.idn.myalarmapp

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val TYPE_ONE_TIME: Int = 0
        const val TYPE_REPEATING: Int = 1

        const val EXTRA_MESSAGE: String = "message"
        const val EXTRA_TYPE: String = "type"

        private const val ID_ONETIME: Int = 100
        private const val ID_REPEATING: Int = 101

        private const val DATE_FORMAT: String = "dd-MM-yyyy"
        private const val TIME_FORMAT: String = "HH:mm"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getIntExtra(EXTRA_TYPE,0)
        val message = intent.getStringExtra(EXTRA_MESSAGE)

        val title = if (type == TYPE_ONE_TIME) "One Time Alarm" else "Repeating Alarm"
        val notifId = if(type == TYPE_ONE_TIME) ID_ONETIME else ID_REPEATING

        if (message != null) {
            showAlarmNotification(context,title,message,notifId)
        }
    }

    private fun showAlarmNotification(context: Context, title: String, message: String, notifId: Int) {
        val channelId = "Channel_1"
        val channelName = "AlarmManager channel1"

        val notificationManagerCompat = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val vibrationPattern = longArrayOf(1000,1000,1000,1000,1000)
        val notifIcon = R.drawable.ic_one_time

        val builder = NotificationCompat.Builder(context,channelId)
            .setSmallIcon(notifIcon)
            .setContentTitle(title)
            .setContentText(message)
            .setColor(ContextCompat.getColor(context,android.R.color.transparent))
            .setVibrate(vibrationPattern)
            .setSound(alarmSound)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            channel.enableVibration(true)
            channel.vibrationPattern = vibrationPattern
            builder.setChannelId(channelId)

            notificationManagerCompat.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManagerCompat.notify(notifId,notification)
    }

    fun setOneTimeAlarm(context: Context, type : Int, date : String, time : String, message : String, requestCode: Int) {
        if (isDateInvalid (date, DATE_FORMAT) || isDateInvalid(time, TIME_FORMAT)) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        intent.putExtra(EXTRA_MESSAGE,message)
        intent.putExtra(EXTRA_TYPE,type)

        Log.e("ONE TIME","$date $time")
        val dateArray = date.split("-").toTypedArray()
        val timeArray = time.split(":").toTypedArray()

        val calendar = Calendar.getInstance()

        calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[2]))
        calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[1]) - 1)
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[0]))
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calendar.set(Calendar.SECOND,0)

        val pendingIntent = PendingIntent.getBroadcast(context, requestCode , intent, 0)
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Toast.makeText(context,"Success Set Up One Time Alarm",Toast.LENGTH_SHORT).show()
    }

    fun setRepeatingAlarm(context: Context, type : Int, time : String, message : String, requestCode: Int) {
        if (isDateInvalid(time, TIME_FORMAT)) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        intent.putExtra(EXTRA_MESSAGE,message)
        intent.putExtra(EXTRA_TYPE,type)

        Log.e("REPEATING",time)
        val timeArray = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calendar.set(Calendar.SECOND,0)

        val pendingIntent = PendingIntent.getBroadcast(context, requestCode , intent, 0)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        Toast.makeText(context,"Success Set Up Repeating Alarm",Toast.LENGTH_SHORT).show()
    }

    fun cancelAlarm(context: Context, type: Int, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context,AlarmReceiver::class.java)

//        val requestCode = when(type) {
//            TYPE_ONE_TIME -> ID_ONETIME
//            TYPE_REPEATING -> ID_REPEATING
//            else -> Log.i("CancelAlarm","cancelAlarm : Unknown type of Alarm")
//        }

        val pendingIntent = PendingIntent.getBroadcast(context,requestCode,intent,0)
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)

        if (type == TYPE_ONE_TIME) {
            Toast.makeText(context,"Cancelled One Time Alarm",Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context,"Cancelled Repeating Alarm",Toast.LENGTH_SHORT).show()
        }
        Log.i("CancelAlarm","Cancelled Alarm with request code $requestCode")
    }

    private fun isDateInvalid(date: String, format: String): Boolean {
        return try {
            val df = SimpleDateFormat(format, Locale.getDefault())
            df.isLenient = false
            df.parse(date)
            false
        } catch (e : ParseException) {
            true
        }
    }
}