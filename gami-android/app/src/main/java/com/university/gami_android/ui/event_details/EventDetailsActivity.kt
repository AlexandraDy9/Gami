package com.university.gami_android.ui.event_details

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.university.gami_android.model.Event
import com.university.gami_android.ui.profile_preview.ProfilePreviewActivity
import com.university.gami_android.util.getBitmapFromDrawable
import com.university.gami_android.util.mergeBitmaps
import com.university.gami_android.util.scaleBitmap
import com.university.gami_android.ui.write_review.WriteReviewActivity
import com.university.gami_android.R
import com.university.gami_android.preferences.PreferenceHandler
import com.university.gami_android.ui.joined_users.JoinedUsersActivity
import com.university.gami_android.ui.login.LoginActivity
import com.university.gami_android.util.getNavigationBarSize
import java.net.URLDecoder
import java.time.LocalDateTime


class EventDetailsActivity : AppCompatActivity(), EventDetailsContract.View,
    EventDetailsAdapter.ItemClickListener, OnMapReadyCallback {

    private var eventName: String? = null

    private lateinit var presenter: EventDetailsPresenter
    private lateinit var adapter: EventDetailsAdapter

    private val defaultZoom: Int = 16
    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var backBtn: ImageView
    private lateinit var progressBar: RelativeLayout
    private var countSuccessRequest = 0
    private var data: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        progressBar = findViewById(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE

        presenter = EventDetailsPresenter()
        presenter.bindView(this)

        initMap()

        if(PreferenceHandler.getAuthorization() == "") {
            finish()
            startActivity(Intent(appContext(), LoginActivity::class.java))
        }

        if(intent.getStringExtra(EVENT_NAME) != null) {
            eventName = intent.getStringExtra(EVENT_NAME)
            title = eventName
        }

        if(intent.data != null) {
            data = intent.data
            eventName = URLDecoder.decode(data.toString().substringAfterLast("/"), "UTF-8")
            title = eventName
        }

        backBtn = findViewById(R.id.back_to_events)
        backBtn.setOnClickListener { finish() }

        setupRecycler()
    }

    override fun onResume() {
        super.onResume()
        presenter.getReviews(eventName!!, this)
    }

    private fun initMap() {
        mapView = findViewById(R.id.map)
        mapView.apply {
            onCreate(null)
            onResume()
        }

        mapView.getMapAsync(this)
    }

    private fun setupRecycler() {
        val reviewsRecyclerView: RecyclerView = findViewById(R.id.recyclerView_event_details)
        reviewsRecyclerView.apply {
            setPadding(0, 0, 0, getNavigationBarSize(resources))
            layoutManager = LinearLayoutManager(appContext())
        }

        adapter = EventDetailsAdapter(this)
        initObservers()
        reviewsRecyclerView.adapter = adapter
    }

    private fun initObservers() {
        presenter.apply {
            getEvent(eventName!!, appContext())
            getHost(eventName!!, appContext())
            getJoinedUsers(eventName!!, appContext())
        }

        presenter.event.observe(this, Observer { event -> adapter.setEvent(event!!) })
        presenter.host.observe(this, Observer { host -> adapter.setHost(host!!) })
        presenter.joinedUsers.observe(this, Observer { number -> adapter.setJoinedUsers(number!!) })
        presenter.reviews.observe(this, Observer { review -> adapter.setReviewList(review!!) })
    }

    private fun updateEventMapLocation(event: Event) {
        googleMap.addMarker(
            MarkerOptions()
                .title(event.name)
                .position(LatLng(event.latitude, event.longitude))
                .snippet(event.description)
                .icon(BitmapDescriptorFactory.fromBitmap(loadIcon()))
        )

        moveCamera(event.latitude, event.longitude)
    }

    private fun loadIcon(): Bitmap {
        val pinBitmap: Bitmap = getBitmapFromDrawable(
            appContext(),
            R.drawable.ic_pin,
            android.R.color.white
        )

        val scaledPinBitmap: Bitmap = scaleBitmap(pinBitmap, 120, 120)
        val bitmap: Bitmap = getBitmapFromDrawable(
            appContext(),
            R.drawable.ic_dice
        )

        val scaledBitmap: Bitmap = scaleBitmap(bitmap, 70, 70)

        return mergeBitmaps(scaledPinBitmap, scaledBitmap, -15)
    }

    private fun moveCamera(latitude: Double, longitude: Double, zoomLevel: Int = defaultZoom) {
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    latitude,
                    longitude
                ), zoomLevel.toFloat()
            )
        )
    }

    override fun onMapReady(p0: GoogleMap?) {
        MapsInitializer.initialize(this)
        p0?.let { googleMap = p0 }
        p0?.mapType = GoogleMap.MAP_TYPE_HYBRID
        googleMap.setPadding(0, 120, 0, 0)

        presenter.event.observe(this,
            Observer { event ->
                updateEventMapLocation(event!!)
            }
        )
    }

    override fun progressBarVisibility() {
        countSuccessRequest++

        //get host, event details, numberJoinedUsers, reviews
        if(countSuccessRequest >= 4) {
            progressBar.visibility = View.INVISIBLE
        }
    }

    override fun onHostNameClick(hostName: String) {
        startActivity(Intent(this, ProfilePreviewActivity::class.java).putExtra(HOST_NAME, hostName))
    }

    override fun onWriteReviewClick() {
        startActivity(Intent(this, WriteReviewActivity::class.java).putExtra(EVENT_NAME, eventName))
    }

    override fun onJoinedUsersClick() {
        startActivity(Intent(this, JoinedUsersActivity::class.java).putExtra(EVENT_NAME, eventName))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onJoinClick(event: Event) {
        if (LocalDateTime.now().isBefore(LocalDateTime.parse(event.startTime))) {
            presenter.apply {
                joinEvent(eventName!!, appContext())
                getJoinedUsers(eventName!!, appContext())
            }
        } else {
            Toast.makeText(appContext(), R.string.cannot_join, Toast.LENGTH_LONG).show()
        }
    }

    override fun onLeftClick(event: Event) {
        presenter.apply {
            leftEvent(eventName!!, appContext())
            getJoinedUsers(eventName!!, appContext())
        }
    }

    override fun appContext(): Context = applicationContext

    override fun onDestroy() {
        super.onDestroy()
        presenter.unbindView()
    }

    companion object {
        const val HOST_NAME: String = "HOST_NAME"
        const val EVENT_NAME: String = "EVENT_NAME"
    }
}