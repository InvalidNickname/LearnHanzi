package ru.wherexibucks.couchbaseutils

data class Lesson(
    val id: Int,
    val lesson: String,
    val paragraphs: List<String>,
    val words: List<String>?
)