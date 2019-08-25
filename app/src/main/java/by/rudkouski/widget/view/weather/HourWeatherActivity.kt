package by.rudkouski.widget.view.weather

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import by.rudkouski.widget.R
import by.rudkouski.widget.entity.Weather
import by.rudkouski.widget.provider.WidgetProvider
import by.rudkouski.widget.repository.WeatherRepository.getWeatherById
import by.rudkouski.widget.view.BaseActivity
import by.rudkouski.widget.view.forecast.ForecastActivity
import by.rudkouski.widget.view.forecast.ForecastItemView.Companion.DATE_WITH_DAY_SHORT_FORMAT
import by.rudkouski.widget.view.weather.WeatherUtils.getDegreeText
import by.rudkouski.widget.view.weather.WeatherUtils.getIconWeatherImageResource
import by.rudkouski.widget.view.weather.WeatherUtils.getSpannableStringDescription
import by.rudkouski.widget.view.weather.WeatherUtils.getSpannableStringValue
import by.rudkouski.widget.view.weather.WeatherUtils.setCloudCoverText
import by.rudkouski.widget.view.weather.WeatherUtils.setDataToView
import by.rudkouski.widget.view.weather.WeatherUtils.setDewPointText
import by.rudkouski.widget.view.weather.WeatherUtils.setHumidityText
import by.rudkouski.widget.view.weather.WeatherUtils.setPrecipitationText
import by.rudkouski.widget.view.weather.WeatherUtils.setPressureText
import by.rudkouski.widget.view.weather.WeatherUtils.setUvIndexText
import by.rudkouski.widget.view.weather.WeatherUtils.setVisibilityText
import by.rudkouski.widget.view.weather.WeatherUtils.setWindText
import java.text.SimpleDateFormat
import java.util.*

class HourWeatherActivity : BaseActivity() {

    companion object {
        private const val EXTRA_WEATHER_ID = "weatherId"

        fun start(context: Context, widgetId: Int, weatherId: Int) {
            val intent = Intent(context, HourWeatherActivity::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            intent.putExtra(EXTRA_WEATHER_ID, weatherId)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val weatherId = intent?.extras?.getInt(EXTRA_WEATHER_ID) ?: 0
        setContentView(R.layout.hour_weather_activity)
        updateActivity(weatherId)
    }

    private fun updateActivity(weatherId: Int) {
        val weather = getWeatherById(weatherId)
        val view = findViewById<View>(R.id.hour_weather)
        setDescriptionText(view, weather)
        if (weather != null) {
            view.visibility = LinearLayout.VISIBLE
            updateTitle(weather)
            setImage(view, weather)
            setDegreeText(view, weather)
            setPrecipitationText(view, weather.precipitationProbability, weather.precipitationType, R.id.precipitation_hour_weather)
            setFeelText(view, weather)
            setHumidityText(view, weather.humidity, R.id.humidity_hour_weather)
            setPressureText(view, weather.pressure, R.id.pressure_hour_weather)
            setWindText(view, weather.windSpeed, weather.windDirection, weather.windGust, R.id.wind_hour_weather)
            setVisibilityText(view, weather.visibility, R.id.visibility_hour_weather)
            setCloudCoverText(view, weather.cloudCover, R.id.cloud_cover_hour_weather)
            setDewPointText(view, weather.dewPoint, R.id.dew_point_hour_weather)
            setUvIndexText(view, weather.uvIndex, R.id.uv_index_hour_weather)
        } else {
            view.visibility = LinearLayout.INVISIBLE
            onStop()
        }
    }

    private fun setDescriptionText(view: View, weather: Weather?) {
        val descriptionTextView = view.findViewById<TextView>(R.id.description_hour_weather)
        descriptionTextView.text = weather?.description?.toLowerCase()?.capitalize() ?: getString(R.string.default_weather)
    }

    private fun updateTitle(weather: Weather) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar_hour_weather)
        toolbar.title = getFormatDateTime(weather.date)
        setSupportActionBar(toolbar)
    }

    private fun getFormatDateTime(date: Calendar): String {
        val timeFormat = WidgetProvider.chooseSystemTimeFormat(this, WeatherItemView.FULL_TIME_FORMAT_12, WeatherItemView.TIME_FORMAT_24)
        val dateFormat = SimpleDateFormat("$DATE_WITH_DAY_SHORT_FORMAT, $timeFormat", Locale.getDefault())
        dateFormat.timeZone = date.timeZone
        return dateFormat.format(date.time)
    }

    private fun setImage(view: View, weather: Weather) {
        val imageView = view.findViewById<ImageView>(R.id.image_hour_weather)
        imageView.setImageResource(getIconWeatherImageResource(this, weather.iconName, weather.cloudCover, weather.precipitationProbability))
    }

    private fun setDegreeText(view: View, weather: Weather) {
        val degreeTextView = view.findViewById<TextView>(R.id.degrees_hour_weather)
        degreeTextView.text = getDegreeText(weather.temperature)
    }

    private fun setFeelText(view: View, weather: Weather) {
        val description = getSpannableStringDescription(this, R.string.feel)
        val value = getSpannableStringValue(this, getDegreeText(weather.apparentTemperature))
        setDataToView(view, R.id.feel_hour_weather, description, value)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            callPreviousActivity()
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun callPreviousActivity() {
        val intent = ForecastActivity.startIntent(this, widgetId)
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        finish()
    }
}