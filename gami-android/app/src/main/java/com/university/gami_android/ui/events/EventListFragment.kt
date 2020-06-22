package com.university.gami_android.ui.events

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.university.gami_android.R
import com.university.gami_android.model.BookmarkEventDto
import com.university.gami_android.model.Event
import com.university.gami_android.ui.event_details.EventDetailsActivity
import java.net.URLEncoder


class EventListFragment : Fragment(), EventListContract.View, EventListAdapter.ItemClickListener {
    override fun appContext(): Context = activity?.applicationContext!!

    private lateinit var adapter: EventListAdapter
    private lateinit var presenter: EventListPresenter
    var type: String = ""
    private lateinit var emptyMessage : TextView
    private lateinit var progressBar: RelativeLayout
    private var countSuccessRequest = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        presenter = EventListPresenter()
        presenter.bindView(this)

        adapter = EventListAdapter(context!!,this)

        progressBar = view.findViewById(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE

        presenter.getBookmarkedEvents(appContext())

        if(type == "ALL") {
            presenter.getEvents(appContext())
        }
        else {
            presenter.getEvents(appContext(), type)
        }

        emptyMessage = activity?.findViewById(R.id.empty_view_list)!!

        val recyclerView = view.findViewById<RecyclerView>(R.id.event_list)
        recyclerView?.layoutManager = LinearLayoutManager(context!!)
        recyclerView?.adapter = adapter
    }

    override fun updateEventList(eventList: List<Event>?) {
        adapter.setEventsList(eventList!!)
        adapter.setModifiedEventsList(eventList)
        if(eventList.isNullOrEmpty())
            emptyMessage.visibility = View.VISIBLE
        else
            emptyMessage.visibility = View.GONE
    }

    override fun updateBookmarkedEventsList(eventList: List<Event>?) {
        adapter.setBookmarkList(eventList!!)
    }

    override fun progressBarVisibility() {
        countSuccessRequest++

        if(countSuccessRequest >= 2) {
            progressBar.visibility = View.INVISIBLE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search ->
                return true
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.imeOptions = EditorInfo.IME_ACTION_DONE

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
    }

    override fun onItemClick(event: Event) {
        startActivity(
            Intent(appContext(), EventDetailsActivity::class.java).putExtra(
                EVENT_NAME,
                event.name
            )
        )
    }

    override fun onBookmarkClick(context: Context, event: BookmarkEventDto) {
        if(!event.isBookmarked)
            presenter.addBookmark(appContext(), event.name)
        else
            presenter.removeBookmark(appContext(), event.name)
        presenter.getBookmarkedEvents(appContext())

    }

    override fun onShareClick(context: Context, event: Event) {
        val shareMessage = StringBuilder()
        shareMessage.append("\nJust take a look at what interesting event I have found!!\n\n")
        shareMessage.append("https://www.gami.com/event/" + URLEncoder.encode(event.name, "UTF-8") + "\n\n")

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareMessage.toString())
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    companion object {
        const val EVENT_NAME: String = "EVENT_NAME"
    }
}
