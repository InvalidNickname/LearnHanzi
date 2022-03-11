package ru.wherexibucks.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import ru.wherexibucks.MainActivity
import ru.wherexibucks.R
import ru.wherexibucks.database.Card
import ru.wherexibucks.database.Dao
import ru.wherexibucks.database.RadicalLink


@DelicateCoroutinesApi
class ManagerFragment : Fragment() {

    private lateinit var dao: Dao
    private lateinit var symbolEdit: EditText
    private lateinit var pinyinEdit: EditText
    private lateinit var definitionEdit: EditText
    private lateinit var memoEdit: EditText
    private lateinit var radicalsEdit: EditText
    private lateinit var addButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return inflater.inflate(R.layout.fragment_manager, container, false)
    }

    override fun onStart() {
        super.onStart()
        // получаем объект доступа к БД
        dao = (activity as MainActivity).getDatabase().dao()!!
        initialize()
        setSymbolListener()
        setAddListener()
    }

    private fun initialize() {
        symbolEdit = view?.findViewById(R.id.symbol_input) as EditText
        pinyinEdit = view?.findViewById(R.id.pinyin_input) as EditText
        definitionEdit = view?.findViewById(R.id.definition) as EditText
        radicalsEdit = view?.findViewById(R.id.radicals) as EditText
        memoEdit = view?.findViewById(R.id.memo) as EditText
        addButton = view?.findViewById(R.id.add) as Button
    }

    private fun setSymbolListener() {
        symbolEdit.doOnTextChanged { text, _, _, _ ->
            GlobalScope.launch(Dispatchers.IO) {
                if (!text.isNullOrEmpty()) {
                    val symbol = symbolEdit.text.toString()
                    val selectedCard = dao.getCardOrGetNull(symbol)
                    val radicals = dao.getRadicals(symbol).joinToString("")
                    withContext(Dispatchers.Main) {
                        if (selectedCard != null) {
                            // карточка существует, обновляем данные
                            pinyinEdit.setText(selectedCard.pinyin)
                            definitionEdit.setText(selectedCard.definition)
                            memoEdit.setText(selectedCard.memo)
                            radicalsEdit.setText(radicals)
                            addButton.setText(R.string.modify)
                        } else {
                            pinyinEdit.setText("")
                            definitionEdit.setText("")
                            memoEdit.setText("")
                            radicalsEdit.setText("")
                            addButton.setText(R.string.add)
                        }
                        view?.findViewById<ConstraintLayout>(R.id.additional_info)?.visibility = View.VISIBLE
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        view?.findViewById<ConstraintLayout>(R.id.additional_info)?.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setAddListener() {
        addButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val text = symbolEdit.text.toString()
                if (text.isNotEmpty()) {
                    val selectedCard = dao.getCardOrGetNull(symbolEdit.text.toString())
                    val symbol = symbolEdit.text.toString()
                    val card = Card(
                        symbol,
                        pinyinEdit.text.toString(),
                        definitionEdit.text.toString(),
                        memoEdit.text.toString(),
                        selectedCard?.level ?: -1,
                        selectedCard?.time ?: 0
                    )
                    val radicals = radicalsEdit.text.toString()
                    if (selectedCard != null) {
                        // карточка существует, обновляем данные
                        dao.updateCards(card)
                        dao.clearLinks(symbol)
                    } else {
                        // карточки не существует, добавляем
                        dao.insertAll(card)
                    }
                    // добавляем новые связки
                    for (radical in radicals) {
                        dao.insertLinks(RadicalLink(symbol, radical.toString()))
                    }
                    withContext(Dispatchers.Main) {
                        symbolEdit.setText("")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        symbolEdit.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
                    }
                }
            }
        }
    }
}