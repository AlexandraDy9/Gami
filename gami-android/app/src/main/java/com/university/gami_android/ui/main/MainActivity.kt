package com.university.gami_android.ui.main

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.core.view.GravityCompat
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import com.university.gami_android.util.load


class MainActivity : AppCompatActivity(), MainContract.View,
    NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private var presenter: MainPresenter? = null
    private var drawer: DrawerLayout? = null
    private var bottomMenuGroup: LinearLayout? = null
    private var userPhotos: List<Photo> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = MainPresenter()
        presenter?.bindView(this)

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
        var params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        bottomMenuGroup?.setPadding(0, 0, 0, getNavigationBarSize())

        val logoutNavView = findViewById<NavigationView>(R.id.logout_nav_view)
        logoutNavView.setNavigationItemSelectedListener(this)

        val menuNavView = findViewById<NavigationView>(R.id.nav_view)
        menuNavView.setNavigationItemSelectedListener(this)
        loadImages()

        val header: View = findViewById<NavigationView>(R.id.nav_view).getHeaderView(0)
        header.findViewById<TextView>(R.id.header_text_view).text = PreferenceHandler.getUserName()

        val userIcon = header.findViewById<ImageView>(R.id.sidebar_profile_picture)
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

    override fun onBackPressed() {
        if (drawer!!.isDrawerOpen(GravityCompat.START)) {
            drawer?.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        finish()
        startActivity(intent)
    }

    private fun getNavigationBarSize(): Int {
        if (!hasNavBar())
            return 0
        val id: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (id > 0) {
            return resources.getDimensionPixelSize(id)
        }
        return 0
    }

    private fun isNavigationBarVisible(): Boolean {
        val hasMenuKey: Boolean = ViewConfiguration.get(appContext()).hasPermanentMenuKey();
        val hasBackKey: Boolean = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (hasMenuKey || hasBackKey) {
            return false
        }
        return true
    }

    private fun hasNavBar(): Boolean {
        val id = resources.getIdentifier("config_showNavigationBar", "bool", "android")
        return id > 0 && resources.getBoolean(id)
    }

    override fun appContext(): Context = applicationContext

    override fun navigateToLoginActivity(context: Context) {
        finish()
        startActivity(Intent(context, LoginActivity::class.java))
    }

    override fun navigateToProfileActivity(context: Context) {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    private fun loadImages() {
        val outdoorActivity: ImageView = findViewById(R.id.outdoor_activity)
        outdoorActivity.load(appContext(), R.drawable.outdoor_activity)

        outdoorActivity.setOnClickListener {
            startActivity(Intent(this, EventsActivity::class.java).putExtra("type", false))
        }

        val indoorActivity: ImageView = findViewById(R.id.indoor_activity)
        indoorActivity.load(appContext(), R.drawable.indoor_activity)

        indoorActivity.setOnClickListener {
            startActivity(Intent(this, EventsActivity::class.java).putExtra("type", true))
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.unbindView()
    }
}


