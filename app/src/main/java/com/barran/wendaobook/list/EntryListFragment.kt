package com.barran.wendaobook.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.barran.wendaobook.databinding.FragmentListBinding
import com.barran.wendaobook.tools.ParseUtils
import com.drakeet.multitype.MultiTypeAdapter

/**
 * entry fragment
 */
class EntryListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding

    private var versionType = ParseUtils.VERSION_NATIONAL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val type = arguments?.getInt(ParseUtils.KEY_VERSION_TYPE, -1) ?: -1
        if (type != -1) {
            versionType = type
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = ParseUtils.parseHistoryEntries(requireContext(), versionType)
        binding.rvContentList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvContentList.adapter = MultiTypeAdapter().apply {
            register(HistoryEntryItem())
            register(VersionEntryItem())

            items = list
            notifyDataSetChanged()
        }
    }
}