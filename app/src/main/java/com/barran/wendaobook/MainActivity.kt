package com.barran.wendaobook

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.barran.wendaobook.databinding.ActivityMainBinding
import com.barran.wendaobook.list.EntryListActivity
import com.barran.wendaobook.tools.ParseUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.app_name)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        initView()
    }

    private fun initView() {
        binding.tvNational.setOnClickListener {
            switchToNational()
        }

        binding.tvInternational.setOnClickListener {
            switchToInternational()
        }
    }

    private fun switchToNational() {
        val intent = Intent(this, EntryListActivity::class.java)
        intent.putExtra(ParseUtils.KEY_VERSION_TYPE, ParseUtils.VERSION_NATIONAL)
        startActivity(intent)
    }

    private fun switchToInternational() {
        val intent = Intent(this, EntryListActivity::class.java)
        intent.putExtra(ParseUtils.KEY_VERSION_TYPE, ParseUtils.VERSION_INTERNATIONAL)
        startActivity(intent)
    }
}
