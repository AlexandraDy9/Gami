package com.university.gami_android.ui.event_details

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.university.gami_android.R
import com.university.gami_android.model.Event
import com.university.gami_android.model.Host
import com.university.gami_android.model.ReviewDao
import com.university.gami_android.model.User
import com.university.gami_android.preferences.PreferenceHandler
import com.university.gami_android.util.formatOnlyDate
import com.university.gami_android.util.formatOnlyHour
import kotlin.math.roundToInt


class EventDetailsAdapter(val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listReviewDao: List<ReviewDao> = arrayListOf()
    private var event: Event? = null
    private var host: Host? = null
    private var joinedUsers: List<User> = arrayListOf()

    fun setReviewList(listReviewDao: List<ReviewDao>) {
        this.listReviewDao = listReviewDao
        notifyDataSetChanged()
    }

    fun setEvent(event: Event) {
        this.event = event
        notifyDataSetChanged()
    }

    fun setJoinedUsers(joinedUsers: List<User>) {
        this.joinedUsers = joinedUsers
        notifyDataSetChanged()
    }

    fun setHost(host: Host) {
        this.host = host
        notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.recyclerview_event_details -> (holder as EventDetailsViewHolder).bindView()
            R.layout.recyclerview_reviews -> (holder as ReviewsViewHolder).bindView(listReviewDao[position - 1])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.recyclerview_event_details -> EventDetailsViewHolder(
                LayoutInflater
                    .from(parent.context).inflate(
                        R.layout.recyclerview_event_details,
                        parent,
                        false
                    )
            )
            R.layout.recyclerview_reviews -> ReviewsViewHolder(
                LayoutInflater
                    .from(parent.context).inflate(
                        R.layout.recyclerview_reviews,
                        parent,
                        false
                    )
            )
            else -> error("there is no type that matches the type $viewType + make sure you're using types correctly")
        }
    }

    override fun getItemCount(): Int {
        return listReviewDao.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> R.layout.recyclerview_event_details
            else -> R.layout.recyclerview_reviews
        }
    }

    inner class EventDetailsViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bindView() {
            val hostImage: ImageView = itemView.findViewById(R.id.host_image)
            val hostName: TextView = itemView.findViewById(R.id.host_name)

            val eventDescription: TextView = itemView.findViewById(R.id.event_description)
            val startDate: TextView = itemView.findViewById(R.id.startTime_date)
            val startHour: TextView = itemView.findViewById(R.id.startTime_hour)
            val endDate: TextView = itemView.findViewById(R.id.endTime_date)
            val endHour: TextView = itemView.findViewById(R.id.endTime_hour)
            val limitAge: TextView = itemView.findViewById((R.id.limit_age_text))
            val attendees: ProgressBar = itemView.findViewById(R.id.progress_bar_attendees)
            val numberOfAttendees: TextView = itemView.findViewById(R.id.number_of_attendees)

            val average: TextView = itemView.findViewById(R.id.average)
            val ratingAverage: RatingBar = itemView.findViewById(R.id.rating_bar_average)

            val writeBtn: TextView = itemView.findViewById(R.id.write_review)
            val joinBtn: Button = itemView.findViewById(R.id.join_btn)

            if (listReviewDao.firstOrNull { it.username == PreferenceHandler.getUserName() } != null) {
                writeBtn.isEnabled = false
                writeBtn.setTextColor(itemView.resources.getColor(R.color.unavailable_color))
            }

            host?.let { host ->
                if (host.name == PreferenceHandler.getUserName()) {
                    joinBtn.apply {
                        text = itemView.resources?.getString(R.string.joined_users)!!
                        setOnClickListener {
                            itemClickListener.onJoinedUsersClick()
                        }
                    }

                    writeBtn.apply {
                        isEnabled = false
                        setTextColor(itemView.resources.getColor(R.color.unavailable_color))
                    }
                }

                else {
                    if(joinedUsers.any{it.user == PreferenceHandler.getUserName()}) {
                        joinBtn.apply {
                            text = itemView.resources?.getString(R.string.left_event)!!
                            setOnClickListener {
                                itemClickListener.onLeftClick(event!!)
                            }
                        }
                    }
                    else {
                        joinBtn.apply {
                            text = itemView.resources?.getString(R.string.join)!!
                            setOnClickListener {
                                itemClickListener.onJoinClick(event!!)
                            }
                        }
                    }
                }

                if (host.image != null) {
                    Glide.with(itemView)
                        .load(host.image)
                        .into(hostImage)
                }

                hostName.text = host.name

                hostName.setOnClickListener {
                    itemClickListener.onHostNameClick(host.name)
                }
            }

            event?.let { event ->
                eventDescription.text = event.description

                startDate.text = formatOnlyDate(event.startTime)
                startHour.text = formatOnlyHour(event.startTime)

                endDate.text = formatOnlyDate(event.endTime)
                endHour.text = formatOnlyHour(event.endTime)

                limitAge.text = String.format(
                    " %s to %s years old",
                    event.ageMin.toString(),
                    event.ageMax.toString()
                )

                attendees.max = event.numberOfAttendees

                attendees.progress = joinedUsers.size
                numberOfAttendees.text =
                    String.format(" %s / %s", joinedUsers.size.toString(), event.numberOfAttendees)

                if (attendees.progress == event.numberOfAttendees)
                    numberOfAttendees.text =
                        itemView.resources.getString(R.string.attendees_full_message)
            }

            writeBtn.setOnClickListener {
                itemClickListener.onWriteReviewClick()
            }

            if (listReviewDao.isNotEmpty()) {
                val averageText =
                    (listReviewDao.map { it.rating!! }.average() * 10.0).roundToInt() / 10.0

                average.text = String.format("%s / 5", averageText.toString())
                ratingAverage.rating = averageText.toFloat()
            } else {
                average.text = "0 / 5"
            }
        }
    }

    inner class ReviewsViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        fun bindView(item: ReviewDao) {
            val image = itemView.findViewById<ImageView>(R.id.reviewer_image)
            val name: TextView = itemView.findViewById(R.id.reviewer_name)
            val rating: RatingBar = itemView.findViewById(R.id.reviewer_rating)
            val description: TextView = itemView.findViewById((R.id.reviewer_description))

            if (item.imageForUser != null) {
                Glide.with(itemView)
                    .load(item.imageForUser)
                    .into(image)
            }

            name.text = item.username
            rating.rating = item.rating?.toFloat()!!
            description.text = item.review
        }
    }


    interface ItemClickListener {
        fun onWriteReviewClick()
        fun onJoinClick(event: Event)
        fun onLeftClick(event: Event)
        fun onJoinedUsersClick()
        fun onHostNameClick(hostName: String)
    }
}
