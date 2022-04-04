package ru.wherexibucks.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import ru.wherexibucks.MainActivity
import ru.wherexibucks.R
import ru.wherexibucks.database.Card
import ru.wherexibucks.database.Dao
import ru.wherexibucks.database.Stats
import java.text.SimpleDateFormat
import java.util.*

@DelicateCoroutinesApi
class ReviewFragment : Fragment() {

    private lateinit var dao: Dao
    private var answered = false
    private var correct = false
    private lateinit var button: Button
    private lateinit var easyButton: Button
    private lateinit var normalButton: Button
    private lateinit var hardButton: Button
    private lateinit var answer: EditText
    private lateinit var result: ConstraintLayout
    private lateinit var list: Array<Card>
    private var i = 0
    private val nextTimeMatrix = intArrayOf(0, 4, 8, 24, 48, 168, 336, 672, 2688)
    private val format = SimpleDateFormat("yyyyMMdd", Locale.US)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return inflater.inflate(R.layout.fragment_review, container, false)
    }

    override fun onStart() {
        super.onStart()

        dao = (activity as MainActivity).getDatabase().dao()!!
        button = view?.findViewById(R.id.next)!!
        answer = view?.findViewById(R.id.definition_input)!!
        result = view?.findViewById(R.id.review_answered)!!
        easyButton = view?.findViewById(R.id.easy)!!
        normalButton = view?.findViewById(R.id.normal)!!
        hardButton = view?.findViewById(R.id.hard)!!

        GlobalScope.launch(Dispatchers.IO) {
            list = dao.getReview(System.currentTimeMillis())
            if (list.isEmpty()) {
                parentFragmentManager.beginTransaction().replace(R.id.main_fragment, HomeFragment(), "home").commit()
            } else {
                withContext(Dispatchers.Main) {
                    inflate()
                    button.isEnabled = true
                }
            }
        }

        button.setOnClickListener {
            if (answered) {
                checkAndUpdate()
            } else {
                if (answer.text.toString() == "") {
                    answer.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
                } else {
                    answered = true
                    answer.visibility = View.GONE
                    result.visibility = View.VISIBLE
                    val definitions = list[i].definition.split(',')
                    correct = false
                    for (def in definitions) {
                        if (normalizeText(answer.text.toString()) == normalizeText(def)) {
                            correct = true
                            break
                        }
                    }
                    if (correct) {
                        view?.findViewById<TextView>(R.id.definition)?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                        setVisibilityForDiff(View.VISIBLE)
                        button.visibility = View.GONE
                    } else {
                        view?.findViewById<TextView>(R.id.definition)?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                        button.text = getString(R.string.next)
                        updateDatabase(i = i, correct = false)
                    }
                    answer.text.clear()
                }
            }
        }
        easyButton.setOnClickListener {
            checkAndUpdate(2.0f)
        }
        normalButton.setOnClickListener {
            checkAndUpdate()
        }
        hardButton.setOnClickListener {
            checkAndUpdate(0.5f)
        }
    }

    private fun checkAndUpdate(modifier: Float = 1.0f) {
        answered = false
        if (correct) {
            updateDatabase(modifier, i, true)
            correct = false
        }
        if (++i < list.size) {
            inflate()
            answer.visibility = View.VISIBLE
            result.visibility = View.GONE
            button.visibility = View.VISIBLE
            button.text = getString(R.string.check)
            setVisibilityForDiff(View.GONE)
        } else {
            parentFragmentManager.beginTransaction().replace(R.id.main_fragment, HomeFragment(), "home").commit()
        }
    }

    private fun setVisibilityForDiff(visibility: Int) {
        easyButton.visibility = visibility
        normalButton.visibility = visibility
        hardButton.visibility = visibility
    }

    private fun updateDatabase(modifier: Float = 1.0f, i: Int, correct: Boolean) {
        GlobalScope.launch(Dispatchers.IO) {
            val card = list[i]
            val currentDate = format.format(Date())
            var stats = dao.getStatsForDay(currentDate)
            if (stats == null) stats = Stats(currentDate, 0, 0)
            if (correct) {
                // ответ правильный
                if (card.level < 8) {
                    card.level++
                    card.time = System.currentTimeMillis() + (nextTimeMatrix[card.level] * 3600000 * modifier).toLong()
                } else {
                    card.time = Long.MAX_VALUE
                }
                stats.revRight++
            } else {
                // ответ неправильный
                if (card.level >= 5) {
                    card.level -= 2
                } else {
                    card.level--
                }
                card.time = 0
                stats.revWrong++
            }
            dao.updateCards(card)
            dao.insertOrReplaceStats(stats)
        }
    }

    private fun normalizeText(string: String): String {
        return string.toLowerCase(Locale.ROOT).trim().replace(" ", "")
    }

    private fun inflate() {
        val card = list[i]
        view?.findViewById<TextView>(R.id.symbol)?.text = card.symbol
        view?.findViewById<TextView>(R.id.pinyin)?.text = card.pinyin
        view?.findViewById<TextView>(R.id.definition)?.text = card.definition
        view?.findViewById<TextView>(R.id.memo)?.text = card.memo
    }
}