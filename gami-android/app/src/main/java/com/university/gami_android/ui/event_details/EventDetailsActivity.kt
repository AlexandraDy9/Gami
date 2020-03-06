package com.university.gami_android.ui.event_details

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
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



class EventDetailsActivity : AppCompatActivity(), EventDetailsContract.View,
    EventDetailsAdapter.ItemClickListener, OnMapReadyCallback {

    private lateinit var eventName: String

    private lateinit var presenter: EventDetailsPresenter
    private lateinit var eventDetailsAdapter: EventDetailsAdapter

    private val defaultZoom: Int = 16
    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var backBtn: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)
//        setSupportActionBar(toolbar)

        mapView = findViewById(R.id.map)
        mapView.apply {
            onCreate(null)
            onResume()
        }
        mapView.getMapAsync(this)

        eventName = intent.getStringExtra("EVENT_NAME")

        title = eventName

        presenter = EventDetailsPresenter()
        presenter.bindView(this)

        backBtn = findViewById(R.id.back_to_events)
        backBtn.setOnClickListener { finish() }

        presenter.apply {
            getEvent(eventName, appContext())
            getHost(eventName, appContext())
            getNumberOfJoinedUsers(eventName, appContext())
        }

        val reviewsRecyclerView: RecyclerView = findViewById(R.id.recyclerView_event_details)
        reviewsRecyclerView.apply {
            setPadding(0, 0, 0, getNavigationBarSize())
            layoutManager = LinearLayoutManager(appContext())
        }

        eventDetailsAdapter = EventDetailsAdapter(this)
        initObservers()
        reviewsRecyclerView.adapter = eventDetailsAdapter
    }

    private fun initObservers() {
        presenter.event.observe(this, Observer { event -> eventDetailsAdapter.setEvent(event!!) })
        presenter.host.observe(this, Observer { host -> eventDetailsAdapter.setHost(host!!) })
        presenter.numberJoinedUsers.observe(
            this,
            Observer { number -> eventDetailsAdapter.setJoinedUsers(number!!) })
        presenter.reviews.observe(
            this,
            Observer { review -> eventDetailsAdapter.setReviewList(review!!) })
    }

    override fun onResume() {
        super.onResume()
        presenter.getReviews(eventName, this)
    }

    private fun updateEventMapLocation(event: Event) {
        googleMap.addMarker(
            MarkerOptions()
                .title(event.name)
                .position(LatLng(event.latitude, event.longitude))
                .snippet(event.description)
                .icon(BitmapDescriptorFactory.fromBitmap(loadIcon(event)))
        )

        moveCamera(event.latitude, event.longitude)
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

    private fun loadIcon(event: Event): Bitmap {
        val iconId: Int? = resources?.getIdentifier(
            "ic_" + event.categoryName,
            "drawable",
            packageName
        )

        val pinBitmap: Bitmap = getBitmapFromDrawable(
            appContext(),
            R.drawable.ic_pin,
            android.R.color.holo_green_dark
        )

        val scaledPinBitmap: Bitmap = scaleBitmap(pinBitmap, 120, 120)
        val bitmap: Bitmap = if (iconId == null || iconId == 0) {
            getBitmapFromDrawable(
                appContext(),
                R.drawable.ic_running,
                android.R.color.white
            )
        } else {
            getBitmapFromDrawable(appContext(), iconId, android.R.color.white)
        }

        val scaledBitmap: Bitmap = scaleBitmap(bitmap, 70, 70)

        return mergeBitmaps(scaledPinBitmap, scaledBitmap, 0, -15)
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

    override fun onHostNameClick(hostName: String) {
        startActivity(Intent(this, ProfilePreviewActivity::class.java).putExtra(HOST_NAME, hostName))
    }

    override fun onWriteReviewClick() {
        startActivity(Intent(this, WriteReviewActivity::class.java).putExtra(EVENT_NAME, eventName))
    }

    override fun onJoinClick() {
        presenter.apply {
            joinEvent(eventName, appContext())
            getNumberOfJoinedUsers(eventName, appContext())
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