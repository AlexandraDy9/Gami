package com.university.gami_android.ui.joined_events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.university.gami_android.R
import com.university.gami_android.model.Event
import com.university.gami_android.util.formatDate
import java.util.*
import kotlin.collections.ArrayList


class JoinedEventAdapter(val itemClickListener: ItemClickListener) : Filterable, RecyclerView.Adapter<JoinedEventAdapter.ViewHolder>() {
    private var events: MutableList<Event> = mutableListOf()
    private var modifiedEvents: MutableList<Event> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(events[position])
    }

    override fun getItemCount(): Int {
        return events.size
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


    fun setEventsList(eventsList: List<Event>) {
        events = eventsList as ArrayList<Event>
        notifyDataSetChanged()
    }

    fun removeEvent(event: Event) {
        val position: Int = events.indexOf(event)
        events.remove(event)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    internal fun getItem(id: Int): Event {
        return events[id]
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(event: Event) {
            val eventName: TextView = itemView.findViewById(R.id.event_name)!!
            eventName.text = event.name

            val eventCategory: TextView = itemView.findViewById(R.id.event_category)!!
            eventCategory.text = event.categoryName

            val eventStart: TextView = itemView.findViewById(R.id.event_start)!!
            eventStart.text = event.startTime.formatDate(itemView.resources.getString(R.string.source_date_format), itemView.resources.getString(R.string.result_date_format))

            val eventFinish: TextView = itemView.findViewById(R.id.event_finish)!!
            eventFinish.text = event.endTime.formatDate(itemView.resources.getString(R.string.source_date_format), itemView.resources.getString(R.string.result_date_format))

            itemView.setOnClickListener {
                itemClickListener.onItemClick(event)
            }

            itemView.findViewById<ImageView>(R.id.delete_button).setOnClickListener {
                itemClickListener.onDeleteClick(event)
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(event: Event)
        fun onDeleteClick(event: Event)
    }

}