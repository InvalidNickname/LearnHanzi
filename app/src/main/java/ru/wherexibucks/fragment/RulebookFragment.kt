package ru.wherexibucks.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.couchbase.lite.*
import kotlinx.coroutines.*
import ru.wherexibucks.MainActivity
import ru.wherexibucks.R
import ru.wherexibucks.rules.Adapter
import ru.wherexibucks.rules.Rule

@DelicateCoroutinesApi
class RulebookFragment : Fragment() {

    private lateinit var rules: Array<Rule?>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return inflater.inflate(R.layout.fragment_rules, container, false)
    }

    override fun onStart() {
        super.onStart()
        GlobalScope.launch(Dispatchers.IO) {
            val query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database((activity as MainActivity).getCouchbase()))
                .where(Expression.property("type").equalTo(Expression.string("rule")))
                .orderBy(Ordering.property("num").ascending())
            val results = query.execute().allResults()
            if (results.size == 0) {
                parentFragmentManager.beginTransaction().replace(R.id.main_fragment, AlternativeHomeFragment(), "alternative").commit()
            } else {
                rules = arrayOfNulls(results.size)
                for (i in 0 until results.size) {
                    val res = results[i].getDictionary("lessons")!!
                    val rule = Rule(res.getInt("num"), res.getString("rule")!!)
                    rules[i] = rule
                }
                withContext(Dispatchers.Main) {
                    val recyclerView = requireView().findViewById<RecyclerView>(R.id.recycler_rules)
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.adapter = Adapter(rules)
                }
            }
        }
    }

}