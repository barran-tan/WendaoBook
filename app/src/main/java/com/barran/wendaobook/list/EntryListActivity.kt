package com.barran.wendaobook.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.barran.wendaobook.R
import com.barran.wendaobook.databinding.ActivityEntryListBinding
import com.barran.wendaobook.search.SearchActivity
import com.barran.wendaobook.tools.ParseUtils

/**
 * entry list activity
 */
class EntryListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEntryListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEntryListBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val args =
            if (intent.extras == null) {
                Toast.makeText(this, R.string.tip_select_server_version, Toast.LENGTH_SHORT).show()
                finish()
                return
            } else {
                requireNotNull(intent.extras)
            }
        supportActionBar?.title = when (args.getInt(ParseUtils.KEY_VERSION_TYPE)) {
            ParseUtils.VERSION_NATIONAL -> {
                getString(R.string.title_national_server)
            }

            ParseUtils.VERSION_INTERNATIONAL -> {
                getString(R.string.title_international_server)
            }

            else -> {
                return
            }
        }
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportFragmentManager.beginTransaction()
            .add(R.id.container, EntryListFragment::class.java, args)
            .commitAllowingStateLoss()


        initView()
    }

    private fun initView() {
        binding.btnSearch.setImageResource(androidx.appcompat.R.drawable.abc_ic_search_api_material)
        binding.btnSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }
}