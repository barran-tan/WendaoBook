package com.barran.wendaobook.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.barran.wendaobook.list.ContentItem
import com.barran.wendaobook.list.UpdateContentListFragment
import com.drakeet.multitype.MultiTypeAdapter

class SearchResultFragment : UpdateContentListFragment() {

    private val searchModel: SearchViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchModel.searchKey.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }
    }

    override fun createAdapter(): MultiTypeAdapter {
        return MultiTypeAdapter().apply {
            register(ContentItem(searchModel.searchKey))
        }
    }
}