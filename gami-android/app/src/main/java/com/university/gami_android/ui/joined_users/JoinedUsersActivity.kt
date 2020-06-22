package com.university.gami_android.ui.joined_users

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.university.gami_android.R
import com.university.gami_android.model.User
import com.university.gami_android.ui.profile_preview.ProfilePreviewActivity
import com.university.gami_android.util.getNavigationBarSize
import com.university.gami_android.util.goneUnless


class JoinedUsersActivity : AppCompatActivity(), JoinedUsersContract.View, JoinedUsersAdapter.ItemClickListener {
    private lateinit var presenter: JoinedUsersPresenter
    private lateinit var eventName: String

    private lateinit var users: ArrayList<User>
    private lateinit var adapterJoined: JoinedUsersAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton: ImageView
    private lateinit var progressBar: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joined_users)

        eventName = intent.getStringExtra(EVENT_NAME)!!
        title = eventName

        presenter = JoinedUsersPresenter()
        presenter.bindView(this)

        backButton = findViewById(R.id.back_btn_joined_users)
        backButton.setOnClickListener { finish() }

        setupRecycler()
    }

    private fun setupRecycler() {
        users = ArrayList()

        adapterJoined = JoinedUsersAdapter(this)
        adapterJoined.setJoinedUsersList(users)

        recyclerView = findViewById(R.id.joined_users_list)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(appContext())
            adapter = adapterJoined
            setPadding(0, 0, 0, getNavigationBarSize(resources))
        }

        progressBar = findViewById(R.id.progress_bar_joined_users)
        progressBar.visibility = View.VISIBLE

        presenter.showJoinedUsers(eventName, this)
    }

    override fun updateJoinedUsersList(usersList: List<User>?) {
        val textView: TextView = findViewById(R.id.no_joined_users_events)
        textView.goneUnless(usersList?.isEmpty()!!)
        recyclerView.goneUnless(usersList.isNotEmpty())
        adapterJoined.setJoinedUsersList(usersList)
        progressBar.visibility = View.INVISIBLE
    }

    override fun onItemClick(user: User) {
        startActivity(
            Intent(this, ProfilePreviewActivity::class.java)
                .putExtra(HOST_NAME, user.user)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unbindView()
    }

    override fun appContext(): Context = applicationContext

    companion object {
        const val HOST_NAME: String = "HOST_NAME"
        const val EVENT_NAME: String = "EVENT_NAME"
    }
}