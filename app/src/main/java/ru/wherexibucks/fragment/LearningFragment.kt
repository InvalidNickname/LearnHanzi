package ru.wherexibucks.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.couchbase.lite.*
import kotlinx.coroutines.*
import ru.wherexibucks.MainActivity
import ru.wherexibucks.R
import ru.wherexibucks.couchbaseutils.Lesson
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
    private lateinit var lessonList: Array<Lesson?>
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
            GlobalScope.launch(Dispatchers.IO) {
                val query: Query = QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database((activity as MainActivity).getCouchbase()))
                    .where(
                        Expression.property("learnt").equalTo(Expression.booleanValue(false))
                            .and(Expression.property("type").equalTo(Expression.string("lesson")))
                    )
                val results = query.execute().allResults()
                if (results.size == 0) {
                    parentFragmentManager.beginTransaction().replace(R.id.main_fragment, AlternativeHomeFragment(), "alternative").commit()
                } else {
                    lessonList = arrayOfNulls(results.size)
                    for (i in 0 until results.size) {
                        val res = results[i].getDictionary("lessons")!!
                        val lesson = Lesson(
                            res.getInt("num"),
                            res.getString("lesson")!!,
                            res.getArray("paragraphs")!!.toList() as List<String>,
                            res.getArray("words")?.toList() as List<String>?
                        )
                        lessonList[i] = lesson
                    }
                    withContext(Dispatchers.Main) {
                        inflate(lessonList, 0)
                        view?.findViewById<Button>(R.id.next)?.isEnabled = true
                    }
                }
            }
            view?.findViewById<Button>(R.id.next)?.setOnClickListener {
                GlobalScope.launch(Dispatchers.IO) {
                    val query: Query = QueryBuilder.select(SelectResult.expression(Meta.id), SelectResult.all())
                        .from(DataSource.database((activity as MainActivity).getCouchbase()))
                        .where(
                            Expression.property("num").equalTo(Expression.intValue(lessonList[i]!!.id))
                                .and(Expression.property("type").equalTo(Expression.string("lesson")))
                        )
                    val result = query.execute().allResults()[0]
                    val doc = (activity as MainActivity).getCouchbase().getDocument(result.getString("id")!!).toMutable()
                    doc.setBoolean("learnt", true)
                    (activity as MainActivity).getCouchbase().save(doc)
                    withContext(Dispatchers.Main) {
                        if (++i < lessonList.size) {
                            inflate(lessonList, i)
                        } else {
                            parentFragmentManager.beginTransaction().replace(R.id.main_fragment, AlternativeHomeFragment(), "alternative").commit()
                        }
                    }
                }
            }
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

    private fun inflate(list: Array<Lesson?>, i: Int) {
        view?.findViewById<TextView>(R.id.title)?.text = list[i]?.lesson
        var body = "" as CharSequence
        for (paragraph in list[i]!!.paragraphs) {
            val span = HtmlCompat.fromHtml("<p>$paragraph</p>", HtmlCompat.FROM_HTML_MODE_LEGACY)
            body = TextUtils.concat(body, span)
        }
        if (list[i]!!.words == null) {
            view?.findViewById<TextView>(R.id.words)?.visibility = View.GONE
            view?.findViewById<TextView>(R.id.words_title)?.visibility = View.GONE
        } else {
            var words = "" as CharSequence
            for (word in list[i]!!.words!!) {
                val span = HtmlCompat.fromHtml("<b>$word</b> ", HtmlCompat.FROM_HTML_MODE_LEGACY)
                words = TextUtils.concat(words, span)
            }
            view?.findViewById<TextView>(R.id.words)?.visibility = View.VISIBLE
            view?.findViewById<TextView>(R.id.words_title)?.visibility = View.VISIBLE
            view?.findViewById<TextView>(R.id.words)?.text = words
        }
        view?.findViewById<TextView>(R.id.lesson)?.text = body
        val id = list[i]!!.id
        if (i > 0) {
            view?.findViewById<TextView>(R.id.prev_symbol)?.text = (id - 1).toString()
        } else {
            view?.findViewById<TextView>(R.id.prev_symbol)?.text = "⠀"
        }
        view?.findViewById<TextView>(R.id.cur_symbol)?.text = "$id"
        if (i < list.size - 1) {
            view?.findViewById<TextView>(R.id.next_symbol)?.text = (id + 1).toString()
        } else {
            view?.findViewById<TextView>(R.id.next_symbol)?.text = "⠀"
        }
    }

    private fun inflate(list: Array<Reviewable>, i: Int) {
        val card = list[i]
        view?.findViewById<TextView>(R.id.symbol)?.text = if (card is Card) card.symbol else (card as Word).word
        view?.findViewById<TextView>(R.id.pinyin)?.text = card.pinyin
        view?.findViewById<TextView>(R.id.definition)?.text = card.definition
        view?.findViewById<TextView>(R.id.memo)?.text = card.memo
        if (i > 0) {
            val item = list[i - 1]
            view?.findViewById<TextView>(R.id.prev_symbol)?.text = if (item is Card) item.symbol else (item as Word).word
        } else {
            view?.findViewById<TextView>(R.id.prev_symbol)?.text = "⠀"
        }
        view?.findViewById<TextView>(R.id.cur_symbol)?.text = if (card is Card) card.symbol else (card as Word).word
        if (i < list.size - 1) {
            val item = list[i + 1]
            view?.findViewById<TextView>(R.id.next_symbol)?.text = if (item is Card) item.symbol else (item as Word).word
        } else {
            view?.findViewById<TextView>(R.id.next_symbol)?.text = "⠀"
        }
    }
}