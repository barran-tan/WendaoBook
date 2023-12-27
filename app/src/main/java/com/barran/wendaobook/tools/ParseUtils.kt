package com.barran.wendaobook.tools

import android.content.Context
import android.widget.Toast
import com.barran.wendaobook.model.Entry
import com.barran.wendaobook.model.toJson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ParseUtils {

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

    var entries: List<Entry> = emptyList()
        private set

    val contents = mutableMapOf<Entry.VersionEntry, List<String>>()

    fun parseHistoryEntries(context: Context): List<Entry> {
        if (entries.isNotEmpty()) {
            return entries
        }
        val list = mutableListOf<Entry>()

        val config = try {
            val inputStream = context.assets.open("config.json")
            val reader = BufferedReader(inputStream.reader())
            val temp = reader.readText()
            inputStream.close()
            reader.close()
            temp
        } catch (e: IOException) {
            Toast.makeText(context, "read config failed ${e.message}", Toast.LENGTH_SHORT).show()
            return list
        }

        try {
            val configJson = JSONObject(config)
            val entries = configJson.optJSONArray("entries")
            for (i in 0 until entries.length()) {
                val entry = entries.optJSONObject(i)
                val year = entry.optInt("year")
                list.add(Entry.HistoryEntry(year.toString(), year))

                val versions = entry.optJSONArray("versions")
                for (j in 0 until versions.length()) {
                    val version = versions.optJSONObject(j)
                    list.add(
                        Entry.VersionEntry(
                            version.optString("title"),
                            dateFormat.parse(version.optString("date")) ?: Date(),
                            version.optString("file")
                        )
                    )
                }
            }
        } catch (e: JSONException) {
            Toast.makeText(context, "parse config failed ${e.message}", Toast.LENGTH_SHORT).show()
        }

        entries = list
        return list
    }

    fun parseContent(context: Context, file: String): List<String> {
        val entry =
            requireNotNull(entries.find { it is Entry.VersionEntry && it.file == file }) as Entry.VersionEntry
        return parseContent(context, entry)
    }

    fun parseContent(context: Context, version: Entry.VersionEntry): List<String> {

        if (contents.contains(version)) {
            return requireNotNull(contents[version])
        }
        val file = version.file
        val list = mutableListOf<String>()
        val regex = Regex("^\\d+.*")

        val inputStream = context.assets.open(file)
        val reader = BufferedReader(inputStream.reader())
        var line: String?
        var builder = StringBuilder()
        while (true) {
            line = reader.readLine()
            if (line == null) {
                list.add(builder.toString())
                builder.clear()
                break
            }
            if (line.matches(regex)) {
                if (builder.isNotEmpty()) {
                    list.add(builder.toString())
                    builder.clear()
                }
                builder.append(line)
            } else {
                builder.append("\n")
                builder.append(line)
            }
        }

        inputStream.close()
        contents[version] = list
        return list
    }

    fun genConfig(context: Context): String {

        val maps = mutableMapOf<Entry.HistoryEntry, List<Entry.VersionEntry>>()

        val list = mutableListOf<Entry.VersionEntry>()

        // region gen config data
        list.add(Entry.VersionEntry("1.36", "2007-01-01", ""))
        maps.put(Entry.HistoryEntry("2007", 2007), list)
        list.clear()

        // endregion

        try {
            val json = JSONObject()
            val entries = JSONArray()

            for (entry in maps) {
                val entryJson = JSONObject()
                entryJson.put("year", entry.key.dateYear)
                var versions = JSONArray()
                for (item: Entry.VersionEntry in entry.value) {
                    versions.put(item.toJson())
                }
                entryJson.put("versions", versions)
            }

            json.put("entries", entries)

            return json.toString()
        } catch (e: JSONException) {
            Toast.makeText(context, "gen config failed ${e.message}", Toast.LENGTH_SHORT).show()
            return ""
        }
    }
}