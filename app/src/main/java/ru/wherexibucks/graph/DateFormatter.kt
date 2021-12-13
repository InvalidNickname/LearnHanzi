package ru.wherexibucks.graph

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import ru.wherexibucks.database.Stats

class DateFormatter(private val reviews: Array<Stats>) : ValueFormatter() {
    private val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Sep", "Oct", "Nov", "Dec")

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        var date = reviews[value.toInt()].date
        date = date.substring(0, 2) + " " + months[date.substring(2, 4).toInt() - 2]
        return date
    }
}