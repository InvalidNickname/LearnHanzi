package ru.wherexibucks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.*
import ru.wherexibucks.database.Dao

@DelicateCoroutinesApi
class StatsFragment : Fragment() {

    private lateinit var dao: Dao
    private lateinit var reviewsChart: BarChart

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onStart() {
        super.onStart()
        dao = (activity as MainActivity).getDatabase().dao()!!
        reviewsChart = view?.findViewById(R.id.reviews_chart)!!
        reviewsChart.description.isEnabled = false
        reviewsChart.setDrawGridBackground(false)
        reviewsChart.setDrawBorders(false)
        val legend = reviewsChart.legend
        val right = LegendEntry(getString(R.string.right), Legend.LegendForm.DEFAULT, Float.NaN, Float.NaN, null, resources.getColor(R.color.green))
        val wrong = LegendEntry(getString(R.string.wrong), Legend.LegendForm.DEFAULT, Float.NaN, Float.NaN, null, resources.getColor(R.color.red))
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
                dataSet.setColors(resources.getColor(R.color.green), resources.getColor(R.color.red))
                val barData = BarData(dataSet)
                reviewsChart.data = barData
                reviewsChart.invalidate()
            }
        }
    }

}