package com.university.gami_android.ui.hobby_selection

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.university.gami_android.R


class HobbySelectionAdapter(context: Context) :
    RecyclerView.Adapter<HobbySelectionAdapter.ViewHolder>() {

    var selections = linkedSetOf<Int>()
    var selectMedia: (Media, Int) -> Unit = { _, _ -> }

    private var list: List<Media> = arrayListOf()
    private var inflater: LayoutInflater? = LayoutInflater.from(context)

    fun setMediaList(list: List<Media>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater!!.inflate(R.layout.recyclerview_item, parent, false)
        return ViewHolder(view, selectMedia)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(view: View, val selectMedia: (Media, Int) -> Unit) :
        RecyclerView.ViewHolder(view) {

        fun bindView(item: Media) {
            val imageView: ImageView = itemView.findViewById(R.id.image)
            val textView: TextView = itemView.findViewById(R.id.title)
            val selected: ImageView = itemView.findViewById(R.id.selected)
            val notSelected: ImageView = itemView.findViewById((R.id.not_selected))
            val isSelected = item.photo in selections

            selected.invisibleUnless(isSelected)
            notSelected.invisibleUnless(!isSelected)

            imageView.setImageResource(item.photo)
            textView.text = item.title

            imageView.setOnClickListener {

                selectMedia(item, adapterPosition)
                Log.e("viewclick", "true")
            }
        }

        private fun View.invisibleUnless(isSelected: Boolean) {
            visibility = if (isSelected) VISIBLE else INVISIBLE
        }
    }
}
