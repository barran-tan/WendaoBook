package com.barran.wendaobook.tools

import android.content.Context
import android.widget.Toast
import com.barran.wendaobook.model.Entry
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ParseUtils {

    const val KEY_VERSION_TYPE = "version_type"

    const val VERSION_NATIONAL = 0
    const val VERSION_INTERNATIONAL = 1

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

    private val configJson = mapOf(
        VERSION_NATIONAL to "config.json",
        VERSION_INTERNATIONAL to "haiwai/config.json"
    )

    private val allEntryMap = mutableMapOf<Int, List<Entry>>()
    private val allContentMap = mutableMapOf<Int, MutableMap<Entry.VersionEntry, List<String>>>()

    private var curVersionType = VERSION_NATIONAL

    val curEntries: List<Entry>
        get() = requireNotNull(allEntryMap[curVersionType])

    val curContents: MutableMap<Entry.VersionEntry, List<String>>
        get() = requireNotNull(allContentMap[curVersionType])

    fun parseHistoryEntries(context: Context, versionType: Int = VERSION_NATIONAL): List<Entry> {
        curVersionType = versionType
        if (allEntryMap.contains(versionType)) {
            return requireNotNull(allEntryMap[versionType])
        }
        val list = mutableListOf<Entry>()

        val config = try {
            val inputStream = context.assets.open(requireNotNull(configJson[curVersionType]))
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

        allEntryMap[versionType] = list
        return list
    }

    fun parseContent(context: Context, file: String): List<String> {
        val entry =
            requireNotNull(requireNotNull(allEntryMap[curVersionType]).find { it is Entry.VersionEntry && it.file == file }) as Entry.VersionEntry
        return parseContent(context, entry)
    }

    fun parseContent(context: Context, version: Entry.VersionEntry): List<String> {
        val contents = if (allContentMap.contains(curVersionType)) {
            requireNotNull(allContentMap[curVersionType])
        } else {
            val newMap = mutableMapOf<Entry.VersionEntry, List<String>>()
            allContentMap[curVersionType] = newMap
            newMap
        }
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

}