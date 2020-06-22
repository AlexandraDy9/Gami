package com.university.gami_android.ui.profile_preview

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import com.university.gami_android.util.getNavigationBarSize
import kotlinx.android.synthetic.main.activity_events.*
import java.io.File
import java.net.URL


class ProfilePreviewActivity : AppCompatActivity(), ProfilePreviewContract.View,
    ProfilePreviewAdapter.ItemClickListener {

    private lateinit var backButton: ImageView
    private lateinit var profileImage: ImageView

    private lateinit var progressBar: RelativeLayout
    private var countSuccessRequest = 0

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

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        presenter = ProfilePreviewPresenter()
        presenter.bindView(this)

        val username = intent.getStringExtra("HOST_NAME")
        title = username

        progressBar = findViewById(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE

        carouselView = findViewById(R.id.carousel_view)
        carouselView.pageCount = imageList.size

        backButton = findViewById(R.id.back)
        profileImage = findViewById(R.id.profile_image)

        carouselView.setImageListener(imageListener)
        backButton.setOnClickListener { finish() }

        initObservers(username!!)

        setupRecycler()
    }

    private fun initObservers(username: String) {
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
    }

    private fun setupRecycler() {
        profilePreviewAdapter = ProfilePreviewAdapter(this)

        val profilePreviewRecyclerView: RecyclerView =
            findViewById(R.id.recyclerView_profile_preview)
        profilePreviewRecyclerView.setPadding(0, 0, 0, getNavigationBarSize(resources))
        profilePreviewRecyclerView.layoutManager = LinearLayoutManager(this)
        profilePreviewRecyclerView.adapter = profilePreviewAdapter

    }

    private fun updatePhotos() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        if (userPhotos.isNotEmpty()) {
            profileImage.visibility = View.INVISIBLE
            carouselView.visibility = View.VISIBLE
            userPhotos.forEach {
                val url = Uri.fromFile(File(it.image))
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, url)
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

    override fun progressBarVisibility() {
        countSuccessRequest++

        //get user, photos, hostedEvents
        if(countSuccessRequest >= 3) {
            progressBar.visibility = View.INVISIBLE
        }
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