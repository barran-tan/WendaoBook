package com.barran.wendaobook.model

import java.util.Date

class SearchEntry(
    val title: String,
    val date: Date,
    val file: String,
    val key: String,
    val matchCount: Int,
    val matchIndexList: List<Int>
) : Comparable<SearchEntry> {
    override fun compareTo(other: SearchEntry): Int {
        if (date.before(other.date)) {
            return -1
        } else {
            return 1
        }
    }
}