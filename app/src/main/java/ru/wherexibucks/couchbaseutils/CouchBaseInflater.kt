package ru.wherexibucks.couchbaseutils

import android.content.Context
import com.couchbase.lite.Database
import com.couchbase.lite.MutableArray
import com.couchbase.lite.MutableDocument
import ru.wherexibucks.R

class CouchBaseInflater {
    fun inflate(couchDb: Database, context: Context) {
        val lessons = arrayOf(
            MutableDocument()
                .setInt("num", 0)
                .setString("type", "lesson")
                .setString("lesson", context.getString(R.string.lesson_1_title))
                .setArray("paragraphs", MutableArray(context.resources.getStringArray(R.array.lesson_1_text).toMutableList<Any>()))
                .setArray("words", MutableArray(context.resources.getStringArray(R.array.lesson_1_words).toMutableList<Any>()))
                .setBoolean("learnt", false),
            MutableDocument()
                .setInt("num", 1)
                .setString("type", "lesson")
                .setString("lesson", context.getString(R.string.lesson_2_title))
                .setArray("paragraphs", MutableArray(context.resources.getStringArray(R.array.lesson_2_text).toMutableList<Any>()))
                .setBoolean("learnt", false),
            MutableDocument()
                .setInt("num", 2)
                .setString("type", "lesson")
                .setString("lesson", context.getString(R.string.lesson_3_title))
                .setArray("paragraphs", MutableArray(context.resources.getStringArray(R.array.lesson_3_text).toMutableList<Any>()))
                .setBoolean("learnt", false),
            MutableDocument()
                .setInt("num", 3)
                .setString("type", "lesson")
                .setString("lesson", context.getString(R.string.lesson_4_title))
                .setArray("paragraphs", MutableArray(context.resources.getStringArray(R.array.lesson_4_text).toMutableList<Any>()))
                .setBoolean("learnt", false),
            MutableDocument()
                .setInt("num", 4)
                .setString("type", "lesson")
                .setString("lesson", context.getString(R.string.lesson_5_title))
                .setArray("paragraphs", MutableArray(context.resources.getStringArray(R.array.lesson_5_text).toMutableList<Any>()))
                .setBoolean("learnt", false),
            MutableDocument().setInt("num", 1).setString("type", "rule").setString("rule", context.getString(R.string.rule_1)),
            MutableDocument().setInt("num", 2).setString("type", "rule").setString("rule", context.getString(R.string.rule_2)),
            MutableDocument().setInt("num", 3).setString("type", "rule").setString("rule", context.getString(R.string.rule_3)),
            MutableDocument().setInt("num", 4).setString("type", "rule").setString("rule", context.getString(R.string.rule_4)),
        )
        for (lesson in lessons) {
            couchDb.save(lesson)
        }
    }
}