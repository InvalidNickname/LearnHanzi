package ru.wherexibucks.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import ru.wherexibucks.MainActivity
import ru.wherexibucks.R
import ru.wherexibucks.database.Card
import ru.wherexibucks.database.Dao
import ru.wherexibucks.database.Reviewable
import ru.wherexibucks.database.Word

@DelicateCoroutinesApi
@Suppress("UNCHECKED_CAST")
class LearningFragment : Fragment() {

    private lateinit var dao: Dao
    private lateinit var wordList: Array<Word>
    private lateinit var hanjiList: Array<Card>
    private var i = 0
    private var alternative = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        alternative = (activity as MainActivity).isHomeAlternative()
        return if (alternative) {
            inflater.inflate(R.layout.fragment_alternative_learn, container, false)
        } else {
            inflater.inflate(R.layout.fragment_learn, container, false)
        }
    }

    override fun onStart() {
        super.onStart()
        dao = (activity as MainActivity).getDatabase().dao()!!
        if (alternative) {

        } else {
            GlobalScope.launch(Dispatchers.IO) {
                hanjiList = dao.getReadyToBeLearnt()
                wordList = dao.getWordsReadyToBeLearnt()
                if (hanjiList.isEmpty() && wordList.isEmpty()) {
                    parentFragmentManager.beginTransaction().replace(R.id.main_fragment, HomeFragment(), "home").commit()
                } else {
                    withContext(Dispatchers.Main) {
                        if (hanjiList.isNotEmpty()) {
                            inflate(hanjiList as Array<Reviewable>, 0)
                        } else {
                            inflate(wordList as Array<Reviewable>, 0)
                        }
                        view?.findViewById<Button>(R.id.next)?.isEnabled = true
                    }
                }
            }
            view?.findViewById<Button>(R.id.next)?.setOnClickListener {
                GlobalScope.launch(Dispatchers.IO) {
                    val card = hanjiList[i].apply { level = 0 }
                    dao.updateCards(card)
                    withContext(Dispatchers.Main) {
                        when {
                            ++i < hanjiList.size -> {
                                inflate(hanjiList as Array<Reviewable>, i)
                            }
                            ++i < hanjiList.size + wordList.size -> {
                                inflate(wordList as Array<Reviewable>, i - hanjiList.size)
                            }
                            else -> {
                                parentFragmentManager.beginTransaction().replace(R.id.main_fragment, HomeFragment(), "home").commit()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun inflate(list: Array<Reviewable>, i: Int) {
        val card = list[i]
        view?.findViewById<TextView>(R.id.symbol)?.text = if (card is Card) card.symbol else (card as Word).word
        view?.findViewById<TextView>(R.id.pinyin)?.text = card.pinyin
        view?.findViewById<TextView>(R.id.definition)?.text = card.definition
        view?.findViewById<TextView>(R.id.memo)?.text = card.memo
        if (i > 0) {
            view?.findViewById<TextView>(R.id.prev_symbol)?.text = hanjiList[i - 1].symbol
        } else {
            view?.findViewById<TextView>(R.id.prev_symbol)?.text = "⠀"
        }
        view?.findViewById<TextView>(R.id.cur_symbol)?.text = if (card is Card) card.symbol else (card as Word).word
        if (i < hanjiList.size - 1) {
            view?.findViewById<TextView>(R.id.next_symbol)?.text = hanjiList[i + 1].symbol
        } else {
            view?.findViewById<TextView>(R.id.next_symbol)?.text = "⠀"
        }
    }
}