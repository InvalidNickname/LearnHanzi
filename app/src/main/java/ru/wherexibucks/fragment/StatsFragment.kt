package ru.wherexibucks.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Switch
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.Legend.LegendForm.DEFAULT
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import kotlinx.coroutines.*
import ru.wherexibucks.MainActivity
import ru.wherexibucks.R
import ru.wherexibucks.database.Dao
import ru.wherexibucks.graph.DateFormatter
import ru.wherexibucks.graph.DecimalFormatter

@DelicateCoroutinesApi
class StatsFragment : Fragment() {

    private lateinit var dao: Dao
    private lateinit var preferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onStart() {
        super.onStart()
        // получаем объект доступа к БД
        dao = (activity as MainActivity).getDatabase().dao()!!
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        setUpReviewsChart()
        setUpLevelsChart()
        val switch = requireView().findViewById<Switch>(R.id.mode_edit_switch)
        switch.isChecked = preferences.getBoolean("mode_edit", false)
        switch.setOnCheckedChangeListener { _, isChecked ->
            preferences.edit().putBoolean("mode_edit", isChecked).apply()
        }
    }

    private fun setUpReviewsChart() {
        // заполняем график проверок
        val reviewsChart = view?.findViewById<BarChart>(R.id.reviews_chart)
        if (reviewsChart != null) {
            val axisLeft = reviewsChart.axisLeft
            val axisRight = reviewsChart.axisRight
            val xAxis = reviewsChart.xAxis
            // убираем отрисовку сетки
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)
            axisLeft.setDrawGridLines(false)
            axisLeft.setDrawAxisLine(false)
            axisRight.setDrawGridLines(false)
            axisRight.setDrawAxisLine(false)
            reviewsChart.setDrawGridBackground(false)
            // задаем форматирование значений осей
            axisLeft.valueFormatter = DecimalFormatter()
            axisRight.valueFormatter = DecimalFormatter()
            // отрисовываем полоску нуля
            axisRight.setDrawZeroLine(true)
            axisLeft.setDrawZeroLine(true)
            // даты отображаются снизу
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            // убираем отображение описания
            reviewsChart.description.isEnabled = false
            // убираем отрисовку границ графика
            reviewsChart.setDrawBorders(false)
            // убираем возможность выбора значений
            reviewsChart.setTouchEnabled(false)
            reviewsChart.isHighlightPerDragEnabled = false
            reviewsChart.isHighlightPerTapEnabled = false
            // задаем кастомную легенду
            val legend = reviewsChart.legend
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            legend.setCustom(
                arrayOf(
                    LegendEntry(getString(R.string.right), DEFAULT, Float.NaN, Float.NaN, null, getColor(R.color.green)),
                    LegendEntry(getString(R.string.wrong), DEFAULT, Float.NaN, Float.NaN, null, getColor(R.color.red))
                )
            )
            // заполняем и отрисовываем
            GlobalScope.launch(Dispatchers.IO) {
                val reviews = dao.getReviewStats(30)
                xAxis.valueFormatter = DateFormatter(reviews)
                withContext(Dispatchers.Main) {
                    val entries = ArrayList<BarEntry>()
                    for (i in reviews.indices) {
                        entries.add(BarEntry(i.toFloat(), floatArrayOf(reviews[i].revRight.toFloat(), reviews[i].revWrong.toFloat())))
                    }
                    val dataSet = BarDataSet(entries.toList(), "")
                    dataSet.setDrawValues(false)
                    dataSet.setColors(
                        ContextCompat.getColor(requireContext(), R.color.green),
                        ContextCompat.getColor(requireContext(), R.color.red)
                    )
                    val barData = BarData(dataSet)
                    reviewsChart.data = barData
                    reviewsChart.invalidate()
                }
            }
        }
    }

    private fun getColor(id: Int): Int {
        return ContextCompat.getColor(requireContext(), id)
    }

    private fun setUpLevelsChart() {
        // заполняем график уровней изучения
        val levelsChart = view?.findViewById<PieChart>(R.id.levels_chart)
        if (levelsChart != null) {
            levelsChart.description.isEnabled = false
            levelsChart.isHighlightPerTapEnabled = false
            levelsChart.setDrawEntryLabels(false)
            levelsChart.setTouchEnabled(false)
            GlobalScope.launch(Dispatchers.IO) {
                val readyToLearn = dao.getReadyToBeLearntCount()
                val locked = dao.getForLevel(-1) - readyToLearn
                val learnt = dao.getForLevelInterval(5, 8)
                val semiLearnt = dao.getForLevelInterval(3, 4)
                val startedToLearn = dao.getForLevelInterval(0, 2)
                val burnt = dao.getForLevel(9)
                withContext(Dispatchers.Main) {
                    val entries = mutableListOf<PieEntry>()
                    val colors = mutableListOf<Int>()
                    if (locked > 0) {
                        entries.add(PieEntry(locked.toFloat(), getString(R.string.locked)))
                        colors.add(getColor(R.color.gray))
                    }
                    if (readyToLearn > 0) {
                        entries.add(PieEntry(readyToLearn.toFloat(), getString(R.string.not_learnt)))
                        colors.add(getColor(R.color.green))
                    }
                    if (startedToLearn > 0) {
                        entries.add(PieEntry(startedToLearn.toFloat(), getString(R.string.started_to_learn)))
                        colors.add(getColor(R.color.darker_green))
                    }
                    if (semiLearnt > 0) {
                        entries.add(PieEntry(semiLearnt.toFloat(), getString(R.string.learnt_somewhat)))
                        colors.add(getColor(R.color.dark_green))
                    }
                    if (learnt > 0) {
                        entries.add(PieEntry(learnt.toFloat(), getString(R.string.learnt)))
                        colors.add(getColor(R.color.darkest_green))
                    }
                    if (burnt > 0) {
                        entries.add(PieEntry(burnt.toFloat(), getString(R.string.burnt)))
                        colors.add(getColor(R.color.black))
                    }
                    val dataSet = PieDataSet(entries, "")
                    dataSet.colors = colors
                    dataSet.sliceSpace = 4f
                    val chartData = PieData(dataSet)
                    val legend = levelsChart.legend
                    legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                    legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
                    legend.orientation = Legend.LegendOrientation.VERTICAL
                    levelsChart.data = chartData
                    levelsChart.invalidate()
                }
            }
        }
    }
}