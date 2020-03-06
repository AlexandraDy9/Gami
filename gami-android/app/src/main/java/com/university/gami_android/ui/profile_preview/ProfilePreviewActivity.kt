package com.university.gami_android.ui.profile_preview

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageListener
import com.university.gami_android.R
import com.university.gami_android.model.Event
import com.university.gami_android.model.Photo
import com.university.gami_android.ui.event_details.EventDetailsActivity
import kotlinx.android.synthetic.main.activity_events.*
import java.net.URL


class ProfilePreviewActivity : AppCompatActivity(), ProfilePreviewContract.View,
    ProfilePreviewAdapter.ItemClickListener {

    private lateinit var backButton: ImageView
    private lateinit var profileImage: ImageView

    private lateinit var presenter: ProfilePreviewPresenter

    private var userPhotos: List<Photo> = arrayListOf()
    private lateinit var profilePreviewAdapter: ProfilePreviewAdapter

    private lateinit var carouselView: CarouselView
    private var imageList: MutableList<Bitmap> = mutableListOf()

    private var imageListener: ImageListener = ImageListener { position, imageView ->
        imageView.setImageBitmap(
            imageList[position]
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_preview)
//        setSupportActionBar(toolbar_events)

        presenter = ProfilePreviewPresenter()
        presenter.bindView(this)

        val username = intent.getStringExtra("HOST_NAME")
        title = username

        carouselView = findViewById(R.id.carousel_view)
        carouselView.pageCount = imageList.size
        carouselView.setImageListener(imageListener)

        backButton = findViewById(R.id.back)
        backButton.setOnClickListener { finish() }
        profileImage = findViewById(R.id.profile_image)

        profilePreviewAdapter = ProfilePreviewAdapter(this)

        presenter.getHostedEvents(appContext(), username)
        presenter.hostedEvents.observe(this, Observer { list ->
            list?.let {
                profilePreviewAdapter.setEventsList(it)
            }
        })

        presenter.getUser(appContext(), username)
        presenter.userDetails.observe(this, Observer {
            it?.let {
                profilePreviewAdapter.setUser(it)
            }
        })

        presenter.getPhotos(appContext(), username)
        presenter.userPhotos.observe(this, Observer { list ->
            list?.let {
                userPhotos = it
                updatePhotos()
            }
        })

        val profilePreviewRecyclerView: RecyclerView =
            findViewById(R.id.recyclerView_profile_preview)
        profilePreviewRecyclerView.setPadding(0, 0, 0, getNavigationBarSize())
        profilePreviewRecyclerView.layoutManager = LinearLayoutManager(this)
        profilePreviewRecyclerView.adapter = profilePreviewAdapter

    }

    private fun updatePhotos() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        if (userPhotos!!.isNotEmpty()) {
            profileImage.visibility = View.INVISIBLE
            carouselView.visibility = View.VISIBLE
            userPhotos?.forEach {
                val url = URL(it.image)
                val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                imageList.add(bitmap)

                val imageView = ImageView(this)
                Glide.with(this)
                    .load(it.image)
                    .into(imageView)

                imageListener.setImageForPosition(imageList.size - 1, imageView)
                carouselView.pageCount = carouselView.pageCount + 1
            }
        }
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

    private fun hasNavBar(): Boolean {
        val id = resources.getIdentifier("config_showNavigationBar", "bool", "android")
        return id > 0 && resources.getBoolean(id)
    }

    override fun onItemClick(event: Event) {
        val intent = Intent(this, EventDetailsActivity::class.java)
        intent.putExtra("EVENT_NAME", event.name)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unbindView()
    }

    override fun appContext(): Context = applicationContext
}