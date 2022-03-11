package ru.wherexibucks.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import kotlinx.coroutines.DelicateCoroutinesApi
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
        // кнопка перехода на обычный экран
        view?.findViewById<View>(R.id.navigate_previous)?.setOnClickListener {
            (activity as MainActivity).setAlternativeHome(false)
            parentFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_from_right, R.anim.exit_to_left)
                .replace(R.id.main_fragment, HomeFragment(), "home")
                .commit()
        }
    }
}