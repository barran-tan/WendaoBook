package com.barran.wendaobook

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.barran.wendaobook.databinding.ActivityMainBinding
import com.barran.wendaobook.list.EntryListFragment
import com.barran.wendaobook.search.SearchActivity
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.zip.ZipInputStream

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
        binding.btnSearch.setImageResource(androidx.appcompat.R.drawable.abc_ic_search_api_material)
        binding.btnSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.container, EntryListFragment::class.java, null)
            .commitAllowingStateLoss()
    }
}
