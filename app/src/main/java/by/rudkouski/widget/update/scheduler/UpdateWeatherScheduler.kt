package by.rudkouski.widget.update.scheduler

import android.app.AlarmManager
import android.app.AlarmManager.*
import android.content.Context.ALARM_SERVICE
import android.text.format.DateUtils.MINUTE_IN_MILLIS
import android.text.format.DateUtils.SECOND_IN_MILLIS
import by.rudkouski.widget.app.App
import by.rudkouski.widget.update.receiver.WeatherUpdateBroadcastReceiver
import java.util.Calendar.*

object UpdateWeatherScheduler {

    fun startUpdateWeatherScheduler() {
        val alarmManager = App.appContext.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(RTC, System.currentTimeMillis() + getUpdateStart(), INTERVAL_HALF_HOUR,
            WeatherUpdateBroadcastReceiver.getUpdateWeatherPendingIntent(App.appContext))
    }

    private fun getUpdateStart(): Long {
        val currentTime = getInstance()
        val millisInCurrentHour =
            currentTime.get(MINUTE) * MINUTE_IN_MILLIS + (currentTime.get(SECOND) * SECOND_IN_MILLIS - currentTime.get(MILLISECOND))
        return when {
            millisInCurrentHour < INTERVAL_FIFTEEN_MINUTES -> INTERVAL_FIFTEEN_MINUTES - millisInCurrentHour
            millisInCurrentHour < INTERVAL_FIFTEEN_MINUTES * 3 -> INTERVAL_FIFTEEN_MINUTES * 3 - millisInCurrentHour
            else -> INTERVAL_HOUR - millisInCurrentHour + INTERVAL_FIFTEEN_MINUTES
        }
    }

    fun stopUpdateWeatherScheduler() {
        val alarmManager = App.appContext.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(WeatherUpdateBroadcastReceiver.getUpdateWeatherPendingIntent(App.appContext))
    }
}