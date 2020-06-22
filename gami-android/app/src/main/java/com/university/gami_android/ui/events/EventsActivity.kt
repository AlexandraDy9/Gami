package com.university.gami_android.ui.events

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.university.gami_android.R
import com.university.gami_android.ui.add_event.AddEventActivity


class EventsActivity : AppCompatActivity() {

    private lateinit var extra: Bundle
    private var type: String = ""

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        extra = intent.extras!!
        type = extra.getString("type")!!

        initializeViewPager()

        val toolbar: Toolbar = findViewById(R.id.toolbar_events)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        toolbar.setNavigationOnClickListener { finish() }
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { startActivity(Intent(applicationContext, AddEventActivity::class.java)) }
    }

    private fun getTabNames(): List<String> {
        val list = mutableListOf<String>()
        list.addAll(
            listOf(
                resources.getString(R.string.event_list_tab_title),
                resources.getString(R.string.event_map_tab_title)
            )
        )
        return list
    }

    private fun initializeViewPager() {
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = ViewPagerAdapter(type, supportFragmentManager, getTabNames())

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search ->
                return true
        }
        return false
    }
}
