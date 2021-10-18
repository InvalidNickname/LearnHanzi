package ru.wherexibucks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import ru.wherexibucks.database.Card
import ru.wherexibucks.database.Dao

@DelicateCoroutinesApi
class ListFragment : Fragment() {

    private lateinit var dao: Dao
    private lateinit var list: Array<Card>
    private var i = 0
    private var learningLevel: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onStart() {
        super.onStart()
        dao = (activity as MainActivity).getDatabase().dao()!!
        learningLevel = view?.findViewById(R.id.learning_level)
        GlobalScope.launch(Dispatchers.IO) {
            list = dao.getAll()
            if (list.isEmpty()) {
                parentFragmentManager.beginTransaction().replace(R.id.main_fragment, HomeFragment(), "home").commit()
            } else {
                withContext(Dispatchers.Main) {
                    inflate()
                    view?.findViewById<ImageButton>(R.id.button_next)?.visibility = View.VISIBLE
                }
            }
        }
        view?.findViewById<ImageButton>(R.id.button_next)?.setOnClickListener {
            if (++i < list.size) {
                inflate()
            }
            updateVisibility()
        }
        view?.findViewById<ImageButton>(R.id.button_prev)?.setOnClickListener {
            if (--i >= 0) {
                inflate()
            }
            updateVisibility()
        }
    }

    private fun updateVisibility() {
        if (i == list.size - 1) {
            view?.findViewById<ImageButton>(R.id.button_next)?.visibility = View.GONE
        } else {
            view?.findViewById<ImageButton>(R.id.button_next)?.visibility = View.VISIBLE
        }
        if (i == 0) {
            view?.findViewById<ImageButton>(R.id.button_prev)?.visibility = View.GONE
        } else {
            view?.findViewById<ImageButton>(R.id.button_prev)?.visibility = View.VISIBLE
        }
    }

    private fun inflate() {
        val card = list[i]
        view?.findViewById<TextView>(R.id.symbol)?.text = card.symbol
        view?.findViewById<TextView>(R.id.pinyin)?.text = card.pinyin
        view?.findViewById<TextView>(R.id.definition)?.text = card.definition
        view?.findViewById<TextView>(R.id.memo)?.text = card.memo
        if (i > 0) {
            view?.findViewById<TextView>(R.id.prev_symbol)?.text = list[i - 1].symbol
        } else {
            view?.findViewById<TextView>(R.id.prev_symbol)?.text = "⠀"
        }
        view?.findViewById<TextView>(R.id.cur_symbol)?.text = card.symbol
        if (i < list.size - 1) {
            view?.findViewById<TextView>(R.id.next_symbol)?.text = list[i + 1].symbol
        } else {
            view?.findViewById<TextView>(R.id.next_symbol)?.text = "⠀"
        }
        when (card.level) {
            -1 -> {
                learningLevel?.text = getString(R.string.not_learnt)
                learningLevel?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
            }
            in 0..2 -> {
                learningLevel?.text = getString(R.string.started_to_learn)
                learningLevel?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.darker_green))
            }
            in 3..4 -> {
                learningLevel?.text = getString(R.string.learnt_somewhat)
                learningLevel?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dark_green))
            }
            in 5..8 -> {
                learningLevel?.text = getString(R.string.learnt)
                learningLevel?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.darkest_green))
            }
            else -> {
                learningLevel?.text = getString(R.string.burnt)
                learningLevel?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_gray))
            }
        }
    }
}