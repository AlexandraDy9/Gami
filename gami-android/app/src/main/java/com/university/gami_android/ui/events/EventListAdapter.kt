package com.university.gami_android.ui.events

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.university.gami_android.R
import com.university.gami_android.model.BookmarkEventDto
import com.university.gami_android.model.Event
import com.university.gami_android.util.formatDate
import com.university.gami_android.util.load


class EventListAdapter(val context: Context, val itemClickListener: ItemClickListener, var bookmarks : List<Event>) :
    RecyclerView.Adapter<EventListAdapter.ViewHolder>() {

    private var list: List<Event> = listOf()
    private var inflater: LayoutInflater? = LayoutInflater.from(context)
    private var viewHolder: ViewHolder? = null

    fun setMediaList(list: List<Event>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun setBookmarkList(bookmarkList: List<Event>){
        bookmarks = bookmarkList
        viewHolder?.setBookmarkList(bookmarks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater!!.inflate(R.layout.item_event, parent, false)
        viewHolder = ViewHolder(view, this.bookmarks)
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(view: View, var bookmarks: List<Event>) :
        RecyclerView.ViewHolder(view) {


        private var bookmarkDtoList = mutableListOf<BookmarkEventDto>()

        fun setBookmarkList(bookmarkList : List<Event>){
            this.bookmarks = bookmarkList
            bookmarkDtoList = mutableListOf()
            bookmarks.forEach{bookmarkDtoList.add(BookmarkEventDto(it.name, true))}
        }

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

            val bookmarkIcon = itemView.findViewById<ImageView>(R.id.bookmark_button)

            val isBookmarked = bookmarks.contains(item)
            if(isBookmarked){
                bookmarkIcon.load(context, R.drawable.ic_five_pointed_star)
            } else{
                bookmarkIcon.load(context, R.drawable.ic_five_pointed_star_outline)
            }

            itemView.setOnClickListener {
                itemClickListener.onItemClick(item)
            }
            itemView.findViewById<ImageView>(R.id.bookmark_button).setOnClickListener {
                itemClickListener.onBookmarkClick(bookmarkIcon, context, BookmarkEventDto(item.name, isBookmarked))
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(event: Event)
        fun onBookmarkClick(bookmarkIcon : ImageView, context: Context, event: BookmarkEventDto)
    }
}
