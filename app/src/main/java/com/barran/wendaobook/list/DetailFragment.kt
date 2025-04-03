package com.barran.wendaobook.list

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
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
import com.barran.wendaobook.tts.TTSHelper
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * content detail fragment
 */
open class DetailFragment : Fragment() {

    private val TAG = "DetailFragment"

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

            initTTS(list)
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

    // region tts

    private var tts: TTSHelper? = null

    private var speakIndex = 0

    private val list = mutableListOf<String>()

    private var id: String? = null

    private var start = 0L

    private fun initTTS(list: List<String>) {

        val ctx = context ?: return

        id = arguments?.getString("title")
        this.list.addAll(list)

        tts = TTSHelper(onInit = {
            start = System.currentTimeMillis()
            speak(this.list[speakIndex++])
        }, onSpeakStart = {

        }, onSpeakFinish = {
            if (speakIndex < list.size) {
                speak(this.list[speakIndex++])
            } else {
                Log.v(TAG, "tts play finished cost ${System.currentTimeMillis() - start}")
            }
        })

        tts?.startTTS(ctx)
    }

    private fun speak(text: String) {
        if (id == null) {
            tts?.speak(text)
        } else {
            tts?.speak(text, requireNotNull(id))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        
        tts?.shutdown()
    }

    // endregion
}