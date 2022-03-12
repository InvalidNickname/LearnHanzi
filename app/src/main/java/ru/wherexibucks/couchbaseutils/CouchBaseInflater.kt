package ru.wherexibucks.couchbaseutils

import com.couchbase.lite.Database
import com.couchbase.lite.MutableArray
import com.couchbase.lite.MutableDocument

class CouchBaseInflater {
    fun inflate(couchDb: Database) {
        val lessons = arrayOf(
            MutableDocument()
                .setInt("num", 0)
                .setString("lesson", "Lesson 1. Starting Learning")
                .setArray(
                    "paragraphs", MutableArray(
                        mutableListOf<Any>(
                            "Mandarin Chinese is spoken by over a billion people worldwide!",
                            "<big><b>Learning Chinese</b></big>",
                            "Chinese is not as difficult as you think! While the sounds and characters may be intimidating at first, Chinese grammar is actually easier than many other languages.",
                            "<big><b>What is pinyin?</b></big>",
                            "Chinese doesn't have an alphabet like English. Instead of using letters to represent sounds, Chinese characters (like 好, which means good) represent entire words. A word can also have more than one character (for example 高兴, which means happy).",
                            "To make things easier, pinyin represents the sounds of Mandarin Chinese using the Roman alphabet. The common greeting 你好 (hello) is written below using letters you will recognize.",
                            "<big><b>Tones</b></big>",
                            "A big difference between Chinese and English is the use of tones. In Chinese, the inflection you use to pronounce a syllable can create a new word entirely.",
                            "In English, you might be shouting (Mom!), asking a question (Mom?), or complaining (Mah‑ummm!), but the meaning of Mom never changes.",
                            "This is not the case for Chinese! The basic sounds that make up the word for mother can easily mean horse... if you pronounce them with a different tone!"
                        )
                    )
                )
                .setArray("words", MutableArray(mutableListOf<Any>("妈", "麻", "马", "骂")))
                .setBoolean("learnt", false),
            MutableDocument()
                .setInt("num", 1)
                .setString("lesson", "Lesson 2. Numbers")
                .setArray(
                    "paragraphs", MutableArray(
                        mutableListOf<Any>(
                            "Counting in Chinese is as easy as 一 , 二 , 三 ! Next time you walk up a flight of stairs, practice a Chinese number with each step!",
                            "0 ‑ 零 (líng)<br>" + "1 ‑ 一 (yī)<br>" + "2 ‑ 二 (èr)<br>" + "3 ‑ 三 (sān)<br>" + "4 ‑ 四 (sì)<br>" + "5 ‑ 五 (wǔ)<br>" + "6 ‑ 六 (liù)<br>" + "7 ‑ 七 (qī)<br>" + "8 ‑ 八 (bā)<br>" + "9 ‑ 九 (jiǔ)<br>" + "10 ‑ 十 (shí)<br>" + "100 ‑ 百 (bǎi)",
                            "Numbers 11‑100 are formed by combining the numbers above.",
                            "18 ‑ 十八 (shíbā)<br>" + "30 ‑ 三十 (sānshí) <br>" + "56 ‑ 五十六 (wǔshíliù)<br>" + "100 ‑ 一百 (yìbǎi)",
                            "<big><b>Money</b></big>",
                            "Yuan is the main unit of currency in China. To say how much something costs, just add 元 (yuán) after the number. You may also see the yuan sign ¥.",
                            "¥8 ‑ 八元 (bā yuán)<br>" + "¥40 ‑ 四十元 (sìshí yuán)<br>" + "¥83 ‑ 八十三元 (bāshísān yuán)",
                            "<big><b>Tones</b></big>",
                            "Remember that Chinese has four tones, often referred to as the first, second, third, and fourth tone."
                        )
                    )
                )
                .setBoolean("learnt", false)
        )

        for (lesson in lessons) {
            couchDb.save(lesson)
        }
    }
}