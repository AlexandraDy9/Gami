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
import com.university.gami_android.util.getNavigationBarSize
import com.university.gami_android.util.goneUnless

class BookmarkActivity : AppCompatActivity(), BookmarkContract.View, JoinedEventAdapter.ItemClickListener {

    private lateinit var presenter: BookmarkPresenter
    private lateinit var events: ArrayList<Event>
    private lateinit var adapterJoined: JoinedEventAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton: ImageView
    private lateinit var progressBar: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_events)

        presenter = BookmarkPresenter()
        presenter.bindView(this)

        backButton = findViewById(R.id.back_btn_favorites)
        backButton.setOnClickListener { navigateToMainActivity(appContext()) }

        setupRecycler()
    }

    private fun setupRecycler() {
        events = ArrayList()

        adapterJoined = JoinedEventAdapter(this)
        adapterJoined.setEventsList(events)

        recyclerView = findViewById(R.id.favorites_list)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(appContext())
            adapter = adapterJoined
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
                val textView: TextView = findViewById(R.id.no_favorite_events)
                textView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        }
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel") { _, _ -> }
        alertDialog.show()
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