package com.university.gami_android.ui.joined_events

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.university.gami_android.R
import com.university.gami_android.model.Event
import com.university.gami_android.ui.event_details.EventDetailsActivity
import com.university.gami_android.util.getNavigationBarSize
import com.university.gami_android.util.goneUnless

class JoinedEventsActivity : AppCompatActivity(), JoinedEventsContract.View, JoinedEventAdapter.ItemClickListener {

    private lateinit var presenter: JoinedEventsPresenter
    private lateinit var events: ArrayList<Event>
    private lateinit var adapterJoined: JoinedEventAdapter
    private lateinit var listview: RecyclerView
    private lateinit var progressBar: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joined_events)

        presenter = JoinedEventsPresenter()
        presenter.bindView(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar_joined_events)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        toolbar.setNavigationOnClickListener { finish() }

        setupRecycler()
    }

    private fun setupRecycler() {
        events = ArrayList()
        adapterJoined = JoinedEventAdapter(this)
        adapterJoined.setEventsList(events)
        adapterJoined.setModifiedEventsList(events)
        listview = findViewById(R.id.joined_list)

        listview.apply {
            layoutManager = LinearLayoutManager(appContext())
            adapter = adapterJoined
            setPadding(0, 0, 0, getNavigationBarSize(resources))
        }

        progressBar = findViewById(R.id.progress_bar_join)
        progressBar.visibility = View.VISIBLE
        presenter.showEvents(this)
    }

    override fun navigateToMainActivity(context: Context) {
        finish()
    }

    override fun updateEventsList(events: List<Event>?) {
        val textView: TextView = findViewById(R.id.no_events_text)
        textView.goneUnless(events?.isEmpty()!!)
        listview.goneUnless(events.isNotEmpty())
        adapterJoined.setEventsList(events)
        adapterJoined.setModifiedEventsList(events)
        progressBar.visibility = View.INVISIBLE
    }

    override fun onItemClick(event: Event) {
        startActivity(
            Intent(appContext(), EventDetailsActivity::class.java).putExtra(
                EVENT_NAME,
                event.name
            )
        )
    }

    override fun onDeleteClick(event: Event) {
        val alertDialog: AlertDialog = AlertDialog.Builder(this).create()
        alertDialog.setMessage(getString(R.string.left_events_string))
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK") { _, _ ->
            presenter.removeEvent(appContext(), event)
            adapterJoined.removeEvent(event)
            if (adapterJoined.itemCount == 0) {
                val textView: TextView = findViewById(R.id.no_events_text)
                textView.visibility = View.VISIBLE
                listview.visibility = View.GONE
            }
        }
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel") { _, _ -> }
        alertDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_bar, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.imeOptions = EditorInfo.IME_ACTION_DONE

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapterJoined.filter.filter(newText)
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search ->
                return true
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unbindView()
    }

    override fun appContext(): Context = applicationContext

    companion object {
        const val EVENT_NAME: String = "EVENT_NAME"
    }
}