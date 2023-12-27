package com.barran.wendaobook.search

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.barran.wendaobook.R
import com.barran.wendaobook.databinding.ActivitySearchBinding
import com.barran.wendaobook.list.SearchEntryItem
import com.barran.wendaobook.model.Entry
import com.barran.wendaobook.model.SearchEntry
import com.barran.wendaobook.tools.ParseUtils
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private val searchModel: SearchViewModel by viewModels()

    private var adapter: MultiTypeAdapter? = null

    private var dialog: ProgressDialog? = null

    private var job: Job? = null

    private var searchKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        supportActionBar?.title = "search"
        supportActionBar?.setDisplayShowHomeEnabled(true)

        initView()
        loadData()
    }

    override fun onBackPressed() {
        if (searchModel.showDetail.value != null) {
            searchModel.showDetail.value = null
        } else {
            super.onBackPressed()
        }
    }

    fun showProcess() {
        if (dialog == null) {
            dialog = ProgressDialog(this)
            dialog?.setCanceledOnTouchOutside(false)
        }
        dialog?.show()
    }

    fun dismissProcess() {
        dialog?.dismiss()
    }

    private fun initView() {

        binding.btnSearch.setOnClickListener {
            val string = binding.etInput.text.toString()
            if (job?.isActive == true) {
                showProcess()
                searchKey = string
            } else {
                searchModel.searchKey.postValue(string)
            }
            val imm = it.context.getSystemService(InputMethodManager::class.java)
            imm?.hideSoftInputFromWindow(binding.etInput.windowToken, 0)
        }

        binding.rvResultList.layoutManager = LinearLayoutManager(this)
        adapter = MultiTypeAdapter().apply {
            register(SearchEntryItem(){
                searchModel.showDetail.value = it
            })
        }
        binding.rvResultList.adapter = adapter

        searchModel.searchKey.observe(this) {
            if (it.isNullOrEmpty().not()) {
                handleSearchAction(it)
            }
        }
        searchModel.showDetail.observe(this) {
            if (it != null) {
                val args = Bundle()
                args.putString("file", it.file)
                supportFragmentManager.beginTransaction()
                    .add(R.id.container, SearchResultFragment::class.java, args, "detail")
                    .commitAllowingStateLoss()

                binding.rvResultList.isVisible = false
            } else {
                val frag = supportFragmentManager.findFragmentByTag("detail")
                if (frag?.isAdded == true) {
                    supportFragmentManager.beginTransaction().remove(frag).commitAllowingStateLoss()
                }

                binding.rvResultList.isVisible = true
            }
        }
    }

    private fun loadData() {
        job = lifecycleScope.launch(Dispatchers.IO) {
            ParseUtils.entries.filter { it is Entry.VersionEntry }.map { it as Entry.VersionEntry }
                .forEach {
                    ParseUtils.parseContent(this@SearchActivity, it.file)
                }

            if (searchKey.isNullOrEmpty().not()) {
                searchModel.searchKey.postValue(searchKey)
            }
        }
    }

    private fun handleSearchAction(key:String){
        if(searchModel.showDetail.value == null) {
            lifecycleScope.launch(Dispatchers.IO) {
                val list = search(key)
                launch(Dispatchers.Main) {
                    adapter?.items = list
                    adapter?.notifyDataSetChanged()
                    dismissProcess()
                }
            }
        }
    }

    private fun search(key: String): List<SearchEntry> {

        val list = mutableListOf<SearchEntry>()

        for (entry in ParseUtils.contents) {
            val count = entry.value.count { it.contains(key) }
            if (count > 0) {
                list.add(SearchEntry(entry.key.title, entry.key.date, entry.key.file, key, count))
            }
        }

        return list
    }
}