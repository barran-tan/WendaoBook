package com.barran.wendaobook.list

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.barran.wendaobook.R
import com.barran.wendaobook.databinding.ItemHistoryEntryBinding
import com.barran.wendaobook.databinding.ItemSearchEntryBinding
import com.barran.wendaobook.databinding.ItemVersionEntryBinding
import com.barran.wendaobook.model.Entry
import com.barran.wendaobook.model.SearchEntry
import com.barran.wendaobook.tools.ParseUtils
import com.drakeet.multitype.ItemViewDelegate

class HistoryEntryItem : ItemViewDelegate<Entry.HistoryEntry, HistoryEntryHolder>() {
    override fun onBindViewHolder(holder: HistoryEntryHolder, item: Entry.HistoryEntry) {
        holder.update(item)
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): HistoryEntryHolder {
        return HistoryEntryHolder(
            ItemHistoryEntryBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }
}

class HistoryEntryHolder(private val bind: ItemHistoryEntryBinding) :
    RecyclerView.ViewHolder(bind.root) {
    fun update(entry: Entry.HistoryEntry) {
        bind.tvTitle.text = entry.title
    }
}

class VersionEntryItem : ItemViewDelegate<Entry.VersionEntry, VersionEntryHolder>() {
    override fun onBindViewHolder(holder: VersionEntryHolder, item: Entry.VersionEntry) {
        holder.update(item)
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): VersionEntryHolder {
        return VersionEntryHolder(
            ItemVersionEntryBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }
}

class VersionEntryHolder(private val bind: ItemVersionEntryBinding) :
    RecyclerView.ViewHolder(bind.root) {
    fun update(entry: Entry.VersionEntry) {
        bind.tvTitle.text = entry.title

        bind.root.setOnClickListener {
            val intent = Intent(it.context, DetailActivity::class.java)
            intent.putExtra("title", entry.title + "   " + ParseUtils.dateFormat.format(entry.date))
            intent.putExtra("file", entry.file)

            it.context.startActivity(intent)
        }
    }
}

class ContentItem(private val key: LiveData<String>? = null) : ItemViewDelegate<String, ContentHolder>() {
    override fun onBindViewHolder(holder: ContentHolder, item: String) {
        holder.update(item, key?.value)
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ContentHolder {
        val text = TextView(context).apply {
            textSize = 12f
            setTextColor(context.getColor(R.color.gray_3))
            setPadding(0, 20, 0, 20)
        }
        return ContentHolder(text)
    }
}

class ContentHolder(private val text: TextView) : RecyclerView.ViewHolder(text) {
    fun update(content: String, key: String? = null) {

        val span = SpannableString(content)
        span.setSpan(RelativeSizeSpan(1.4f), 0, 3, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)

        if (key != null) {
            val start = content.indexOf(key, 0)
            if (start >= 0) {
                val end = start + key.length

                span.setSpan(
                    ForegroundColorSpan(Color.RED),
                    start,
                    end,
                    SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
        }
        text.text = span
    }
}

class SearchEntryItem(val clickAction: (SearchEntry) -> Unit) : ItemViewDelegate<SearchEntry, SearchEntryHolder>() {
    override fun onBindViewHolder(holder: SearchEntryHolder, item: SearchEntry) {
        holder.update(item, clickAction)
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): SearchEntryHolder {
        return SearchEntryHolder(
            ItemSearchEntryBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }
}

class SearchEntryHolder(private val bind: ItemSearchEntryBinding) :
    RecyclerView.ViewHolder(bind.root) {
    fun update(content: SearchEntry, action: (SearchEntry) -> Unit) {
        bind.tvTitle.text = content.title
        bind.tvCount.text = "本次更新匹配${content.matchCount}条内容"

        bind.root.setOnClickListener {
            action.invoke(content)
        }
    }
}