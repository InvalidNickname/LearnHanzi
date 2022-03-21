package ru.wherexibucks.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import ru.wherexibucks.MainActivity
import ru.wherexibucks.R
import ru.wherexibucks.database.Dao

@DelicateCoroutinesApi
class HomeFragment : Fragment() {

    private lateinit var dao: Dao
    private lateinit var preferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onStart() {
        super.onStart()
        dao = (activity as MainActivity).getDatabase().dao()!!
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
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
        val button = view?.findViewById<View>(R.id.button_manage)!!
        if (preferences.getBoolean("mode_edit", false)) {
            button.visibility = View.VISIBLE
            button.setOnClickListener {
                parentFragmentManager.beginTransaction().replace(R.id.main_fragment, ManagerFragment(), "manager").commit()
            }
        } else {
            button.visibility = View.GONE
        }
        // кнопка перехода на альтернативный экран
        view?.findViewById<View>(R.id.navigate_next)?.setOnClickListener {
            (activity as MainActivity).setAlternativeHome(true)
            parentFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_from_left, R.anim.exit_to_right)
                .replace(R.id.main_fragment, AlternativeHomeFragment(), "alternative")
                .commit()
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