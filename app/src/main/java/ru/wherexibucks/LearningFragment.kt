package ru.wherexibucks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import ru.wherexibucks.database.Card
import ru.wherexibucks.database.Dao

@DelicateCoroutinesApi
class LearningFragment : Fragment() {

    private lateinit var dao: Dao
    private lateinit var list: Array<Card>
    private var i = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return inflater.inflate(R.layout.fragment_learn, container, false)
    }

    override fun onStart() {
        super.onStart()
        dao = (activity as MainActivity).getDatabase().dao()!!
        GlobalScope.launch(Dispatchers.IO) {
            list = dao.getReadyToBeLearnt()
            if (list.isEmpty()) {
                parentFragmentManager.beginTransaction().replace(R.id.main_fragment, HomeFragment(), "home").commit()
            } else {
                withContext(Dispatchers.Main) {
                    inflate()
                    view?.findViewById<Button>(R.id.next)?.isEnabled = true
                }
            }
        }
        view?.findViewById<Button>(R.id.next)?.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val card = list[i].apply { level = 0 }
                dao.updateCards(card)
                withContext(Dispatchers.Main) {
                    if (++i < list.size) {
                        inflate()
                    } else {
                        parentFragmentManager.beginTransaction().replace(R.id.main_fragment, HomeFragment(), "home").commit()
                    }
                }
            }
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
    }
}