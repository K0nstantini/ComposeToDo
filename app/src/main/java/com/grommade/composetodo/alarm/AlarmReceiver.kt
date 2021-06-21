package com.grommade.composetodo.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.grommade.composetodo.add_classes.MyCalendar

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val timeInMillis = intent.getLongExtra(EXTRA_EXACT_ALARM_TIME, 0L)
        when (intent.action) {
            ACTION_SET_TIME_SINGLE_TASK -> {
                buildNotification(context, MyCalendar(timeInMillis).toString())
            }
        }
    }

    private fun buildNotification(context: Context, message: String) {
        // TODO: Notification
    }

}