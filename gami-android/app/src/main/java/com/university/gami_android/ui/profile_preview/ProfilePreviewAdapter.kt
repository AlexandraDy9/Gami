package com.university.gami_android.ui.profile_preview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.university.gami_android.R
import com.university.gami_android.model.Event
import com.university.gami_android.model.User
import java.text.SimpleDateFormat


class ProfilePreviewAdapter(val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var hostedEvents: List<Event> = arrayListOf()
    private var user: User? = null

    fun setEventsList(listEventDao: List<Event>) {
        this.hostedEvents = listEventDao
        notifyDataSetChanged()
    }

    fun setUser(user: User) {
        this.user = user
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.recyclerview_profile_preview -> (holder as ProfilePreviewViewHolder).bindView()
            R.layout.recyclerview_item_profile_preview -> (holder as EventsViewHolder).bindView(
                hostedEvents[position - 1]
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.recyclerview_profile_preview -> ProfilePreviewViewHolder(
                LayoutInflater
                    .from(parent.context).inflate(
                        R.layout.recyclerview_profile_preview,
                        parent,
                        false
                    )
            )
            R.layout.recyclerview_item_profile_preview -> EventsViewHolder(
                LayoutInflater
                    .from(parent.context).inflate(
                        R.layout.recyclerview_item_profile_preview,
                        parent,
                        false
                    )
            )
            else -> error("There is no type that matches the type $viewType + make sure you're using types correctly.")
        }
    }

    override fun getItemCount(): Int {
        return hostedEvents.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> R.layout.recyclerview_profile_preview
            else -> R.layout.recyclerview_item_profile_preview
        }
    }

    inner class EventsViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        fun bindView(item: Event) {
            val name: TextView = itemView.findViewById(R.id.event_name)
            val category: TextView = itemView.findViewById(R.id.event_category)
            val startTime: TextView = itemView.findViewById(R.id.event_start)
            val endTime: TextView = itemView.findViewById(R.id.event_finish)

            name.text = item.name
            category.text = item.categoryName
            startTime.text = formatDate(item.startTime)
            endTime.text = formatDate(item.endTime)

            itemView.setOnClickListener {
                itemClickListener.onItemClick(item)
            }
        }

        private fun formatDate(date: String): String {
            val parser = SimpleDateFormat(itemView.resources.getString(R.string.source_date_format))
            val formatter =
                SimpleDateFormat(itemView.resources.getString(R.string.result_date_format))
            return formatter.format(parser.parse(date))
        }
    }

    inner class ProfilePreviewViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        fun bindView() {
            val email: TextView = itemView.findViewById(R.id.email)
            val firstName: TextView = itemView.findViewById(R.id.first_name)
            val lastName: TextView = itemView.findViewById(R.id.last_name)
            val birthday: TextView = itemView.findViewById(R.id.birthday)
            val description: TextView = itemView.findViewById(R.id.description)
            val hostedEventsText: TextView = itemView.findViewById(R.id.hosted_events)

            if (user != null) {
                email.text = user?.email
                firstName.text = user?.firstname
                lastName.text = user?.lastname
                birthday.text = user?.birthdate
                description.text = user?.description
            }

            hostedEventsText.invisibleUnless(hostedEvents.isEmpty())
        }

        private fun View.invisibleUnless(isEmpty: Boolean) {
            visibility = if (isEmpty) View.INVISIBLE else View.VISIBLE
        }
    }

    interface ItemClickListener {
        fun onItemClick(event: Event)
    }
}