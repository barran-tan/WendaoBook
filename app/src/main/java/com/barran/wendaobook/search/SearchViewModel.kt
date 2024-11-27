package com.barran.wendaobook.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.barran.wendaobook.model.SearchEntry

class SearchViewModel : ViewModel() {

    val isSearchMode = MutableLiveData(false)

    val showDetail = MutableLiveData<SearchEntry?>()

    val searchKey = MutableLiveData<String>()

    private var searchIndex = -1
    val searchContentIndex = MutableLiveData(-1)

    fun setShowDetail(entry: SearchEntry?) {
        showDetail.postValue(entry)
        isSearchMode.postValue(entry != null)
    }

    fun showNext(limit:Int) {
        if (limit <= 0) {
            return
        }
        val detail = showDetail.value ?: return

        if (searchIndex + 1 < limit) {
            searchIndex++
            searchContentIndex.postValue(detail.matchIndexList[searchIndex])
        } else {
            searchIndex = 0
            searchContentIndex.postValue(detail.matchIndexList[0])
        }
    }
}