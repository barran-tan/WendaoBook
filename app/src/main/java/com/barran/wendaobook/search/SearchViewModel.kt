package com.barran.wendaobook.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.barran.wendaobook.model.SearchEntry

class SearchViewModel : ViewModel() {

    val showDetail = MutableLiveData<SearchEntry>()

    val searchKey = MutableLiveData<String>()
}