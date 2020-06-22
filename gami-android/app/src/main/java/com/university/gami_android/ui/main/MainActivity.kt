package com.university.gami_android.ui.main

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import androidx.core.view.GravityCompat
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.university.gami_android.R
import com.university.gami_android.model.Photo
import com.university.gami_android.preferences.PreferenceHandler
import com.university.gami_android.ui.bookmarks.BookmarkActivity
import com.university.gami_android.ui.events.EventsActivity
import com.university.gami_android.ui.joined_events.JoinedEventsActivity
import com.university.gami_android.ui.login.LoginActivity
import com.university.gami_android.ui.profile.ProfileActivity
import com.university.gami_android.util.getNavigationBarSize

class MainActivity : AppCompatActivity(), MainContract.View,
    NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private var presenter: MainPresenter? = null
    private var drawer: DrawerLayout? = null
    private var bottomMenuGroup: LinearLayout? = null
    private var userPhotos: List<Photo> = arrayListOf()
    private var storagePermission: Boolean = false

    private lateinit var userIcon: ImageView

    override fun onResume() {
        super.onResume()
        presenter?.getPhotos(appContext())
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = MainPresenter()
        presenter?.bindView(this)

        getStoragePermission()

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.drawer_open, R.string.drawer_close
        )

        drawer?.addDrawerListener(toggle)
        toggle.syncState()

        bottomMenuGroup = findViewById(R.id.bottom_menu_group)
        bottomMenuGroup?.setPadding(0, 0, 0, getNavigationBarSize(resources))

        val logoutNavView = findViewById<NavigationView>(R.id.logout_nav_view)
        logoutNavView.setNavigationItemSelectedListener(this)

        val menuNavView = findViewById<NavigationView>(R.id.nav_view)
        menuNavView.setNavigationItemSelectedListener(this)

        val header: View = findViewById<NavigationView>(R.id.nav_view).getHeaderView(0)
        header.findViewById<TextView>(R.id.header_text_view).text = PreferenceHandler.getUserName()

        userIcon = header.findViewById(R.id.sidebar_profile_picture)
        userIcon.setOnClickListener(this)

        presenter?.getPhotos(appContext())
        presenter?.userPhotos?.observe(this, Observer { list ->
            list?.let {
                userPhotos = it
                if (userPhotos.isNotEmpty()) {
                    Glide.with(this)
                        .load(userPhotos[0].image)
                        .into(userIcon)
                }
            }
        })

        loadCategories()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sidebar_profile_picture -> {
                navigateToProfileActivity(this)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                presenter?.doLogout(appContext())!!
            }
            R.id.events -> {
                startActivity(Intent(applicationContext, JoinedEventsActivity::class.java))
            }
            R.id.favorites -> {
                startActivity(Intent(applicationContext, BookmarkActivity::class.java))
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getStoragePermission() {
        if (ContextCompat
                .checkSelfPermission(
                    appContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            storagePermission = true
        } else {
            requestPermissions(
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                0
            )
        }
    }

    private fun loadCategories() {
        val boardgamesActivity: ImageView = findViewById(R.id.boardgames_activity)
        boardgamesActivity.setOnClickListener {
            startActivity(Intent(this, EventsActivity::class.java).putExtra("type", "Boardgames"))
        }

        val cardgamesActivity: ImageView = findViewById(R.id.cardgames_activity)
        cardgamesActivity.setOnClickListener {
            startActivity(Intent(this, EventsActivity::class.java).putExtra("type", "Card Games"))
        }

        val dicegamesActivity: ImageView = findViewById(R.id.dicegames_activity)
        dicegamesActivity.setOnClickListener {
            startActivity(Intent(this, EventsActivity::class.java).putExtra("type", "Dice Games"))
        }

        val allEvents: TextView = findViewById(R.id.view_all_events)
        allEvents.setOnClickListener {
            startActivity(Intent(this, EventsActivity::class.java).putExtra("type", "ALL"))
        }
    }

    override fun appContext(): Context = applicationContext

    override fun navigateToLoginActivity(context: Context) {
        finish()
        startActivity(Intent(context, LoginActivity::class.java))
    }

    override fun navigateToProfileActivity(context: Context) {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    override fun onBackPressed() {
        if (drawer!!.isDrawerOpen(GravityCompat.START)) {
            drawer?.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        finish()
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.unbindView()
    }
}


