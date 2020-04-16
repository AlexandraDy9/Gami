package com.university.gami_android.ui.joined_events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.university.gami_android.R
import com.university.gami_android.model.Event
import com.university.gami_android.util.formatDate


class JoinedEventAdapter(val itemClickListener: ItemClickListener) : RecyclerView.Adapter<JoinedEventAdapter.ViewHolder>() {
    private var events: ArrayList<Event> = ArrayList()

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