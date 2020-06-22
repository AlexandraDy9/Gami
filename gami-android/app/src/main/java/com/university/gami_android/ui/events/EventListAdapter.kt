package com.university.gami_android.ui.events

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.university.gami_android.R
import com.university.gami_android.model.BookmarkEventDto
import com.university.gami_android.model.Event
import com.university.gami_android.util.formatDate
import com.university.gami_android.util.load
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log


class EventListAdapter(val context: Context, val itemClickListener: ItemClickListener) : Filterable,
    RecyclerView.Adapter<EventListAdapter.ViewHolder>() {

    private var events: MutableList<Event> = mutableListOf()
    private var modifiedEvents: MutableList<Event> = mutableListOf()
    private var inflater: LayoutInflater? = LayoutInflater.from(context)
    private var bookmarks : List<Event> = listOf()

    fun setEventsList(list: List<Event>) {
        this.events = list as MutableList<Event>
        notifyDataSetChanged()
    }

    fun setModifiedEventsList(list: List<Event>) {
        this.modifiedEvents.clear()
        this.modifiedEvents.addAll(list)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return exampleFilter
    }

    private val exampleFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = ArrayList<Event>()
            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(modifiedEvents)
            } else {
                val filterPattern = constraint.toString().toLowerCase(Locale.US).trim { it <= ' ' }

                for (item in modifiedEvents) {
                    if (item.name.toLowerCase(Locale.US).contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            events.clear()
            events.addAll(results.values as Collection<Event>)
            notifyDataSetChanged()
        }
    }

    fun setBookmarkList(bookmarkList: List<Event>){
        this.bookmarks = bookmarkList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater!!.inflate(R.layout.item_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(events[position])
    }

    override fun getItemCount(): Int {
        return events.size
    }

    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        fun bindView(item: Event) {
            val eventName: TextView = itemView.findViewById(R.id.event_name)
            eventName.text = item.name
            val eventCategory: TextView = itemView.findViewById(R.id.event_category)!!
            eventCategory.text = item.categoryName
            val eventStart: TextView = itemView.findViewById(R.id.event_start)!!
            eventStart.text = item.startTime.formatDate(itemView.resources.getString(R.string
                    .source_date_format), itemView.resources.getString(R.string.result_date_format))
            val eventFinish: TextView = itemView.findViewById(R.id.event_finish)!!
            eventFinish.text = item.endTime.formatDate(itemView.resources.getString(R.string
                    .source_date_format), itemView.resources.getString(R.string.result_date_format))

            val bookmarkIcon: ImageView = itemView.findViewById(R.id.bookmark_button)

            val shareIcon: ImageView = itemView.findViewById(R.id.share_button)

            val isBookmarked = bookmarks.contains(item)
            if(isBookmarked){
                bookmarkIcon.load(context, R.drawable.ic_five_pointed_star)
            } else{
                bookmarkIcon.load(context, R.drawable.ic_five_pointed_star_outline)
            }

            itemView.setOnClickListener {
                itemClickListener.onItemClick(item)
            }

            bookmarkIcon.setOnClickListener {
                itemClickListener.onBookmarkClick(context, BookmarkEventDto(item.name, isBookmarked))
            }

            shareIcon.setOnClickListener {
                itemClickListener.onShareClick(context, item)
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(event: Event)
        fun onBookmarkClick(context: Context, event: BookmarkEventDto)
        fun onShareClick(context: Context, event: Event)
    }
}
