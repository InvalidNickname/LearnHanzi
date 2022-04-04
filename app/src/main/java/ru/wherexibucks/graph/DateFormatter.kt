package ru.wherexibucks.graph

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import ru.wherexibucks.database.Stats
import kotlin.math.floor

class DateFormatter(private val reviews: Array<Stats>) : ValueFormatter() {
    private val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Sep", "Oct", "Nov", "Dec")

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val intVal = reviews.size - floor(value).toInt() - 1
        return if (intVal >= reviews.size || intVal < 0) {
            ""
        } else {
            var date = reviews[floor(value).toInt()].date
            date = date.substring(6, 8) + " " + months[date.substring(4, 6).toInt() - 1]
            date
        }
    }
}