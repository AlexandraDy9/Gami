package com.university.gami_android.ui.bookmarks

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
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
import com.university.gami_android.ui.joined_events.JoinedEventAdapter
import com.university.gami_android.util.getNavigationBarSize
import com.university.gami_android.util.goneUnless

class BookmarkActivity : AppCompatActivity(), BookmarkContract.View, JoinedEventAdapter.ItemClickListener {

    private lateinit var presenter: BookmarkPresenter
    private lateinit var events: ArrayList<Event>
    private lateinit var adapterFavorites: JoinedEventAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_events)

        presenter = BookmarkPresenter()
        presenter.bindView(this)


        val toolbar: Toolbar = findViewById(R.id.toolbar_favorite_events)
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

        adapterFavorites = JoinedEventAdapter(this)
        adapterFavorites.setEventsList(events)
        adapterFavorites.setModifiedEventsList(events)

        recyclerView = findViewById(R.id.favorites_list)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(appContext())
            adapter = adapterFavorites
            setPadding(0, 0, 0, getNavigationBarSize(resources))
        }

        progressBar = findViewById(R.id.progress_bar_favorites)
        progressBar.visibility = View.VISIBLE

        presenter.showBookmarks(this)
    }

    override fun navigateToMainActivity(context: Context) {
        finish()
    }

    override fun updateEventsList(events: List<Event>?) {
        val textView: TextView = findViewById(R.id.no_favorite_events)
        textView.goneUnless(events?.isEmpty()!!)
        recyclerView.goneUnless(events.isNotEmpty())
        adapterFavorites.setEventsList(events)
        adapterFavorites.setModifiedEventsList(events)
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
        alertDialog.setMessage(getString(R.string.remove_events_string))
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK") { _, _ ->
            presenter.removeBookmark(appContext(), event)
            adapterFavorites.removeEvent(event)
            if (adapterFavorites.itemCount == 0) {
                val textView: TextView = findViewById(R.id.no_favorite_events)
                textView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
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
                adapterFavorites.filter.filter(newText)
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