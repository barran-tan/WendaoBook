package com.barran.wendaobook.model

import com.barran.wendaobook.tools.ParseUtils
import org.json.JSONObject
import java.util.Date


sealed class Entry {

    class HistoryEntry(
        val title: String,
        val dateYear: Int,
        var fold: Boolean = true
    ) : Entry(), Comparable<HistoryEntry> {
        override fun compareTo(other: HistoryEntry): Int {
            if (dateYear < other.dateYear) {
                return -1
            } else {
                return 1
            }
        }
    }

    class VersionEntry(
        val title: String,
        val date: Date,
        val file:String
    ) : Entry(), Comparable<VersionEntry> {

        var dateStr: String = ParseUtils.dateFormat.format(date)

        constructor(
            title: String,
            date: String,
            file: String
        ) : this(title, ParseUtils.dateFormat.parse(date) ?: Date(), file) {
            dateStr = date
        }

        override fun compareTo(other: VersionEntry): Int {
            if (date.before(other.date)) {
                return -1
            } else {
                return 1
            }
        }
    }
}

fun Entry.VersionEntry.toJson(): JSONObject {
    val itemJson = JSONObject()
    itemJson.put("title", title)
    itemJson.put("date", dateStr)
    itemJson.put("file", file)
    return itemJson
}
