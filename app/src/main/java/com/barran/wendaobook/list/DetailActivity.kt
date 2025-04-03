package com.barran.wendaobook.list

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.barran.wendaobook.R

/**
 * detail activity
 */
class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_empty)

        val args =
            if (intent.extras == null) {
                Toast.makeText(this, R.string.tip_select_version, Toast.LENGTH_SHORT).show()
                finish()
                return
            } else {
                requireNotNull(intent.extras)
            }
        supportActionBar?.title = args.getString("title")
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportFragmentManager.beginTransaction()
            .add(R.id.container, DetailFragment::class.java, args)
            .commitAllowingStateLoss()
    }

}