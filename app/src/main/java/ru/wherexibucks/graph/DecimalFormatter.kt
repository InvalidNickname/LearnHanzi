package ru.wherexibucks.graph

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.floor

class DecimalFormatter : ValueFormatter() {
    override fun getBarLabel(barEntry: BarEntry?): String {
        val y = barEntry?.y
        return if (y != null && floor(y) == y) y.toString() else ""
    }

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return if (floor(value) == value) value.toInt().toString() else ""
    }
}