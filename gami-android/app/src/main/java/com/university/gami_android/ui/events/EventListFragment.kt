package com.university.gami_android.ui.events

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.university.gami_android.R
import com.university.gami_android.model.BookmarkEventDto
import com.university.gami_android.model.Event
import com.university.gami_android.ui.event_details.EventDetailsActivity


class EventListFragment : Fragment(), EventListContract.View, EventListAdapter.ItemClickListener {
    override fun appContext(): Context = activity?.applicationContext!!

    private var adapter: EventListAdapter? = null
    private lateinit var presenter: EventListPresenter
    var type: String = ""
    private lateinit var emptyMessage : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = EventListPresenter()
        presenter.bindView(this)

        adapter = EventListAdapter(context!!,this)

        presenter.getBookmarkedEvents(appContext())

        if(type == "ALL") {
            presenter.getEvents(appContext())
        }
        else {
            presenter.getEventsByCategory(appContext(), type)
        }

        emptyMessage = activity?.findViewById(R.id.empty_view_list)!!

        val recyclerView = view.findViewById<RecyclerView>(R.id.event_list)
        recyclerView?.layoutManager = LinearLayoutManager(context!!)
        recyclerView?.adapter = adapter
    }

    override fun updateEventList(eventList: List<Event>?) {
        adapter?.setMediaList(eventList!!)
        if(eventList.isNullOrEmpty())
            emptyMessage.visibility = View.VISIBLE
        else
            emptyMessage.visibility = View.GONE
    }

    override fun updateBookmarkedEventsList(eventList: List<Event>?) {
        adapter?.setBookmarkList(eventList!!)
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

    companion object {
        const val EVENT_NAME: String = "EVENT_NAME"
    }
}
