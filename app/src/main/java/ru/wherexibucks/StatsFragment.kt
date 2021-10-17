package ru.wherexibucks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.Legend.LegendForm.DEFAULT
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.*
import kotlinx.coroutines.*
import ru.wherexibucks.database.Dao

@DelicateCoroutinesApi
class StatsFragment : Fragment() {

    private lateinit var dao: Dao

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onStart() {
        super.onStart()
        // получаем объект доступа к БД
        dao = (activity as MainActivity).getDatabase().dao()!!
        setUpReviewsChart()
        setUpLevelsChart()
    }

    private fun setUpReviewsChart() {
        // заполняем график проверок
        val reviewsChart = view?.findViewById<BarChart>(R.id.reviews_chart)
        if (reviewsChart != null) {
            reviewsChart.description.isEnabled = false
            reviewsChart.setDrawGridBackground(false)
            reviewsChart.setDrawBorders(false)
            reviewsChart.isHighlightPerDragEnabled = false
            reviewsChart.isHighlightPerTapEnabled = false
            val legend = reviewsChart.legend
            val right = LegendEntry(getString(R.string.right), DEFAULT, Float.NaN, Float.NaN, null, resources.getColor(R.color.green))
            val wrong = LegendEntry(getString(R.string.wrong), DEFAULT, Float.NaN, Float.NaN, null, resources.getColor(R.color.red))
            legend.setCustom(arrayOf(right, wrong))
            GlobalScope.launch(Dispatchers.IO) {
                val reviews = dao.getReviewStats(30)
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

    private fun setUpLevelsChart() {
        // заполняем график уровней изучения
        val levelsChart = view?.findViewById<PieChart>(R.id.levels_chart)
        if (levelsChart != null) {
            levelsChart.description.isEnabled = false
            levelsChart.isHighlightPerTapEnabled = false
            levelsChart.setDrawEntryLabels(false)
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
                        entries.add(PieEntry(locked.toFloat(), "Locked"))
                        colors.add(ContextCompat.getColor(requireContext(), R.color.gray))
                    }
                    if (readyToLearn > 0) {
                        entries.add(PieEntry(readyToLearn.toFloat(), "Not learnt"))
                        colors.add(ContextCompat.getColor(requireContext(), R.color.green))
                    }
                    if (startedToLearn > 0) {
                        entries.add(PieEntry(startedToLearn.toFloat(), "Started to learn"))
                        colors.add(ContextCompat.getColor(requireContext(), R.color.darker_green))
                    }
                    if (semiLearnt > 0) {
                        entries.add(PieEntry(semiLearnt.toFloat(), "Learnt somewhat"))
                        colors.add(ContextCompat.getColor(requireContext(), R.color.dark_green))
                    }
                    if (learnt > 0) {
                        entries.add(PieEntry(learnt.toFloat(), "Learnt"))
                        colors.add(ContextCompat.getColor(requireContext(), R.color.darkest_green))
                    }
                    if (burnt > 0) {
                        entries.add(PieEntry(burnt.toFloat(), "Burnt"))
                        colors.add(ContextCompat.getColor(requireContext(), R.color.black))
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