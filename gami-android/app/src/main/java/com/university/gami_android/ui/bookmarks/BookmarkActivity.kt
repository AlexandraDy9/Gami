package com.university.gami_android.ui.bookmarks

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.university.gami_android.R
import com.university.gami_android.model.Event
import com.university.gami_android.ui.event_details.EventDetailsActivity
import com.university.gami_android.ui.joined_events.JoinedEventAdapter
import com.university.gami_android.util.goneUnless

class BookmarkActivity : AppCompatActivity(), BookmarkContract.View, JoinedEventAdapter.ItemClickListener {

    private lateinit var presenter: BookmarkPresenter
    private lateinit var events: ArrayList<Event>
    private lateinit var adapterJoined: JoinedEventAdapter
    private lateinit var listview: RecyclerView
    private lateinit var backButton: ImageView
    private lateinit var progressBar: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_events)
        backButton = findViewById(R.id.back_btn_favorites)
        backButton.setOnClickListener { navigateToMainActivity(appContext()) }
        presenter = BookmarkPresenter()
        presenter.bindView(this)
        setupRecycler()
    }

    private fun setupRecycler() {
        events = ArrayList()
        adapterJoined = JoinedEventAdapter(this)
        adapterJoined.setEventsList(events)

        listview = findViewById(R.id.favorites_list)
        listview.apply {
            layoutManager = LinearLayoutManager(appContext())
            adapter = adapterJoined
            setPadding(0, 0, 0, getNavigationBarSize())
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
        listview.goneUnless(events.isNotEmpty())
        adapterJoined.setEventsList(events)
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

    private fun getNavigationBarSize(): Int {
        if (!hasNavBar())
            return 0
        val id: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (id > 0) {
            return resources.getDimensionPixelSize(id)
        }
        return 0
    }

    private fun hasNavBar(): Boolean {
        val id = resources.getIdentifier("config_showNavigationBar", "bool", "android")
        return id > 0 && resources.getBoolean(id)
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