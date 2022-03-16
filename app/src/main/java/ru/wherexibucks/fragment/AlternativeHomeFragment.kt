package ru.wherexibucks.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.couchbase.lite.*
import kotlinx.coroutines.*
import ru.wherexibucks.MainActivity
import ru.wherexibucks.R
import ru.wherexibucks.database.Dao

@DelicateCoroutinesApi
class AlternativeHomeFragment : Fragment() {

    private lateinit var dao: Dao

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return inflater.inflate(R.layout.fragment_alternative_home, container, false)
    }

    override fun onStart() {
        super.onStart()
        dao = (activity as MainActivity).getDatabase().dao()!!
        // кнопка изучения
        view?.findViewById<ConstraintLayout>(R.id.button_learn)?.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_fragment, LearningFragment(), "learn").commit()
        }
        // кнопка просмотра правил
        view?.findViewById<View>(R.id.button_rules)?.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_fragment, RulebookFragment(), "rulebook").commit()
        }
        // кнопка перехода на обычный экран
        view?.findViewById<View>(R.id.navigate_previous)?.setOnClickListener {
            (activity as MainActivity).setAlternativeHome(false)
            parentFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_from_right, R.anim.exit_to_left)
                .replace(R.id.main_fragment, HomeFragment(), "home")
                .commit()
        }
        // обновляем количество уроков для обучения
        GlobalScope.launch(Dispatchers.IO) {
            val query: Query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database((activity as MainActivity).getCouchbase()))
                .where(Expression.property("learnt").equalTo(Expression.booleanValue(false)))
            val readyToLearn = query.execute().allResults().size
            val percent = 100 * dao.getFullyLearntCount() / dao.countAll()
            val readyToLearnCards = dao.getReadyToBeLearntCount()
            val locked = dao.getForLevel(-1) - readyToLearnCards
            withContext(Dispatchers.Main) {
                view?.findViewById<ConstraintLayout>(R.id.button_learn)?.isEnabled = readyToLearn > 0
                view?.findViewById<TextView>(R.id.n_more_left)?.text = String.format(getString(R.string.n_lessons_left), readyToLearn)
                view?.findViewById<ProgressBar>(R.id.progress_bar)?.progress = percent
                view?.findViewById<TextView>(R.id.progress_bar_text)?.text = String.format(getString(R.string.fully_learnt), percent)
                view?.findViewById<TextView>(R.id.n_of_locked)?.text = String.format(getString(R.string.n_cards_locked), locked)
            }
        }
    }
}