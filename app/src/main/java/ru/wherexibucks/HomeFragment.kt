package ru.wherexibucks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import ru.wherexibucks.database.Dao

@DelicateCoroutinesApi
class HomeFragment : Fragment() {

    private lateinit var dao: Dao

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onStart() {
        super.onStart()
        dao = (activity as MainActivity).getDatabase().dao()!!
        // кнопка изучения
        view?.findViewById<ConstraintLayout>(R.id.button_learn)?.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_fragment, LearningFragment(), "learn").commit()
        }
        // кнопка повторения
        view?.findViewById<ConstraintLayout>(R.id.button_review)?.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_fragment, ReviewFragment(), "review").commit()
        }
        // кнопка статистики
        view?.findViewById<View>(R.id.button_stats)?.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_fragment, StatsFragment(), "stats").commit()
        }
        // кнопка списка
        view?.findViewById<View>(R.id.button_list)?.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_fragment, ListFragment(), "list").commit()
        }
        // кнопка обновления базы
        view?.findViewById<View>(R.id.button_manage)?.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_fragment, ManagerFragment(), "manager").commit()
        }
        // обновляем количество карточек для изучения/повторения
        GlobalScope.launch(Dispatchers.IO) {
            val readyToLearn = dao.getReadyToBeLearntCount()
            val readyToReview = dao.getReviewCount(System.currentTimeMillis())
            val percent = 100 * dao.getFullyLearntCount() / dao.countAll()
            val locked = dao.getForLevel(-1) - readyToLearn
            withContext(Dispatchers.Main) {
                view?.findViewById<ConstraintLayout>(R.id.button_learn)?.isEnabled = readyToLearn > 0
                view?.findViewById<TextView>(R.id.n_more_left)?.text = String.format(getString(R.string.n_more_left), readyToLearn)
                view?.findViewById<ConstraintLayout>(R.id.button_review)?.isEnabled = readyToReview > 0
                view?.findViewById<TextView>(R.id.n_more_left_review)?.text = String.format(getString(R.string.n_more_left), readyToReview)
                view?.findViewById<ProgressBar>(R.id.progress_bar)?.progress = percent
                view?.findViewById<TextView>(R.id.progress_bar_text)?.text = String.format(getString(R.string.fully_learnt), percent)
                view?.findViewById<TextView>(R.id.n_of_locked)?.text = String.format(getString(R.string.n_cards_locked), locked)
            }
        }
    }
}