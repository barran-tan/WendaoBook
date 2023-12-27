package com.barran.wendaobook.list

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.barran.wendaobook.R
import com.barran.wendaobook.databinding.FragmentListBinding
import com.barran.wendaobook.tools.ParseUtils
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class UpdateContentListFragment : Fragment() {

    protected lateinit var binding: FragmentListBinding

    protected var title: String? = null
    protected lateinit var file: String

    protected lateinit var adapter: MultiTypeAdapter

    protected var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        file = arguments?.getString("file") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentListBinding.inflate(inflater, container, false)
        val context = requireContext()
        binding.rvContentList.layoutManager = LinearLayoutManager(context)
        adapter = createAdapter()
        binding.rvContentList.adapter = adapter
        val dividerItemDecoration = DividerItemDecoration(context, RecyclerView.VERTICAL)
        val drawable = context.resources.getDrawable(R.drawable.list_divider_horizontal)
        drawable?.let {
            dividerItemDecoration.setDrawable(it)
        }
        binding.rvContentList.addItemDecoration(dividerItemDecoration)
        return binding.root
    }

    open fun createAdapter():MultiTypeAdapter{
        return MultiTypeAdapter().apply {
            register(ContentItem())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showContent()
    }

    open fun showContent() {

        showProcess()
        lifecycleScope.launch(Dispatchers.IO) {
            val list = ParseUtils.parseContent(requireContext(), file)

            launch(Dispatchers.Main) {
                adapter.items = list
                adapter.notifyDataSetChanged()
                dismissProcess()
            }
        }
    }

    fun showProcess(){
        if (dialog == null) {
            dialog = ProgressDialog(requireContext())
            dialog?.setCanceledOnTouchOutside(false)
        }
        dialog?.show()
    }

    fun dismissProcess(){
        dialog?.dismiss()
    }
}