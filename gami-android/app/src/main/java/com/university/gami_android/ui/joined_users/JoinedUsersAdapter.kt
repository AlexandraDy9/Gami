package com.university.gami_android.ui.joined_users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.university.gami_android.R
import com.university.gami_android.model.User


class JoinedUsersAdapter(val itemClickListener: ItemClickListener) : RecyclerView.Adapter<JoinedUsersAdapter.ViewHolder>() {
    private var users: ArrayList<User> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_joined_users, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun setJoinedUsersList(usersList: List<User>) {
        users = usersList as ArrayList<User>
        notifyDataSetChanged()
    }

    internal fun getItem(id: Int): User {
        return users[id]
    }

    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        fun bindView(user: User) {
            val userName: TextView = itemView.findViewById(R.id.user_name)!!
            userName.text = user.user

            val emailUser: TextView = itemView.findViewById(R.id.email_user)!!
            emailUser.text = user.email

            val descriptionUser: TextView = itemView.findViewById(R.id.description_user)!!
            descriptionUser.text = if(user.description.length > 30) user.description.take(30) + "..." else user.description

            itemView.findViewById<ImageView>(R.id.find_more_button).setOnClickListener {
                itemClickListener.onItemClick(user)
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(user: User)
    }
}