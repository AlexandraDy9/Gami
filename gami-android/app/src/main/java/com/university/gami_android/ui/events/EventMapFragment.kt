package com.university.gami_android.ui.events

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.university.gami_android.model.Event
import com.university.gami_android.ui.event_details.EventDetailsActivity
import com.university.gami_android.util.getBitmapFromDrawable
import com.university.gami_android.util.mergeBitmaps
import com.university.gami_android.util.scaleBitmap
import com.university.gami_android.R


class EventMapFragment : Fragment(), EventMapContract.View, OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnInfoWindowClickListener,
    GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    var type: Boolean = false
    private lateinit var placeAutoComplete: PlacesSearchFragment

    private val defaultZoom: Int = 17
    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: MapView
    private var locationPermission: Boolean = false
    private var lastKnownLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var presenter: EventMapPresenter
    private var eventList: List<Event> = listOf()
    private var parentContext: Context? = null
    private lateinit var toolbar: View

    override fun onMarkerClick(p0: Marker?): Boolean {
        googleMap.uiSettings.isMapToolbarEnabled = true
        animateCamera(p0?.position!!)
        return false
    }

    override fun onMapClick(p0: LatLng?) {
        googleMap.uiSettings.isMapToolbarEnabled = false
    }

    override fun updateEventList(eventList: List<Event>?) {
        this.eventList = eventList!!
        eventList.forEach {
            val iconId: Int? = parentContext?.resources?.getIdentifier(
                "ic_" + it.categoryName,
                "drawable",
                parentContext?.packageName
            )
            val bitmap: Bitmap
            val pinBitmap = getBitmapFromDrawable(
                parentContext!!,
                R.drawable.ic_pin,
                android.R.color.holo_green_dark
            )
            val scaledPinBitmap = scaleBitmap(pinBitmap, 120, 120)

            bitmap = if (iconId == null || iconId == 0) {
                getBitmapFromDrawable(
                    parentContext!!,
                    R.drawable.ic_running,
                    android.R.color.white
                )
            } else {
                getBitmapFromDrawable(parentContext!!, iconId, android.R.color.white)
            }

            val scaledBitmap: Bitmap = scaleBitmap(bitmap, 70, 70)
            val mergedBitmap = mergeBitmaps(scaledPinBitmap, scaledBitmap, 0, -15)
            googleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(it.latitude, it.longitude))
                    .title(it.name)
                    .snippet(it.description)
                    .icon(BitmapDescriptorFactory.fromBitmap(mergedBitmap))
            )
        }
    }

    private fun getLocationPermission() {
        if (ContextCompat
                .checkSelfPermission(
                    context!!,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermission = true
        } else {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }
    }

    private fun updateLocationUI() {
        try {
            if (locationPermission) {
                googleMap.apply {
                    isMyLocationEnabled = true
                    uiSettings.isMyLocationButtonEnabled = true
                }
            } else {
                googleMap.apply {
                    isMyLocationEnabled = false
                    uiSettings.isMyLocationButtonEnabled = false
                }

                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermission = true
        when (requestCode) {
            0 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermission = true
            }
        }
        updateLocationUI()
    }

    override fun onMapReady(p0: GoogleMap?) {
        MapsInitializer.initialize(context)
        p0?.let { googleMap = p0 }
        p0?.mapType = GoogleMap.MAP_TYPE_HYBRID

        googleMap.setPadding(0, 120, 0, 0)
        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnInfoWindowClickListener(this)
        googleMap.setOnMapClickListener(this)
        googleMap.setOnMarkerClickListener(this)
        googleMap.uiSettings.isMapToolbarEnabled = false
        toolbar = mapView.findViewWithTag<View>("GoogleMapToolbar")

        val rlp: RelativeLayout.LayoutParams = toolbar.layoutParams as RelativeLayout.LayoutParams
        rlp.apply {
            addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
            setMargins(0, 0, 0, 80)
        }

        getLocationPermission()
        getDeviceLocation()

        presenter.getEvents(appContext(), type)
    }

    private fun getDeviceLocation() {
        try {
            if (locationPermission) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(
                    activity!!
                ) { task ->
                    if (task.isSuccessful) {
                        lastKnownLocation = task.result
                        moveCamera(
                            lastKnownLocation?.latitude!!,
                            lastKnownLocation?.longitude!!
                        )
                        updateLocationUI()
                    } else {
                        Log.e("Exception: %s", "Current location is null.")
                        Log.e("Exception: %s", task.exception?.message)
                        moveCamera(35.234324, 23.2342)
                        googleMap.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        parentContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)

        if (!Places.isInitialized()) {
            Places.initialize(context!!, "AIzaSyDq1vRTyjxbOqwP8IHDtrXTZH1nPdcdFxI")
        }

        presenter = EventMapPresenter()
        presenter.bindView(this)
        mapView = view.findViewById(R.id.map)
        mapView.apply {
            onCreate(null)
            onResume()
        }
        mapView.getMapAsync(this)

        childFragmentManager.apply {
            beginTransaction()
                .add(R.id.autocomplete_fragment, PlacesSearchFragment(), "placesSearchFragment")
                .commit()
            executePendingTransactions()
        }

        val fragment = childFragmentManager.findFragmentByTag("placesSearchFragment")
        placeAutoComplete = fragment as PlacesSearchFragment
        placeAutoComplete.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                moveCamera(place.latLng!!)
            }

            override fun onError(status: Status) {
                Toast.makeText(activity, "An error occurred: $status", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onInfoWindowClick(p0: Marker?) {
        if (p0 == null)
            return
        startActivity(Intent(appContext(), EventDetailsActivity::class.java).putExtra("EVENT_NAME", p0.title))
    }

    override fun onMyLocationButtonClick(): Boolean {
        if (lastKnownLocation == null)
            return false
        googleMap.uiSettings.isMapToolbarEnabled = false
        animateCamera(lastKnownLocation?.latitude!!, lastKnownLocation?.longitude!!)
        return true
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

    private fun moveCamera(latLng: LatLng, zoomLevel: Int = defaultZoom) {
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng, zoomLevel.toFloat()
            )
        )
    }

    private fun animateCamera(latitude: Double, longitude: Double, zoomLevel: Int = defaultZoom) {
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    latitude,
                    longitude
                ), zoomLevel.toFloat()
            )
        )
    }

    private fun animateCamera(latLng: LatLng, zoomLevel: Int = defaultZoom) {
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng, zoomLevel.toFloat()
            )
        )
    }

    override fun appContext(): Context {
        return context!!
    }
}
