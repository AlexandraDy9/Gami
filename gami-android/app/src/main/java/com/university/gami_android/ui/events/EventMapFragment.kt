package com.university.gami_android.ui.events

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
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


class EventMapFragment : Fragment(), EventMapContract.View, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
    GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    var type: String = ""
    private lateinit var placeAutoComplete: PlacesSearchFragment

    private val defaultZoom: Int = 17
    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: MapView
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
            val bitmap: Bitmap = getBitmapFromDrawable(
                parentContext!!,
                R.drawable.ic_dice
            )

            val pinBitmap = getBitmapFromDrawable(
                parentContext!!,
                R.drawable.ic_pin,
                android.R.color.white
            )

            val scaledPinBitmap = scaleBitmap(pinBitmap, 120, 120)
            val scaledBitmap: Bitmap = scaleBitmap(bitmap, 70, 70)

            val mergedBitmap = mergeBitmaps(scaledPinBitmap, scaledBitmap, -15)
            googleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(it.latitude, it.longitude))
                    .title(it.name)
                    .snippet( if(it.description.length > 10) it.description.take(10) + "..." else it.description )
                    .icon(BitmapDescriptorFactory.fromBitmap(mergedBitmap))
            )
        }
    }

    override fun onMapReady(p0: GoogleMap?) {
        MapsInitializer.initialize(context)
        p0?.let { googleMap = p0 }
        p0?.mapType = GoogleMap.MAP_TYPE_HYBRID

        googleMap.setPadding(0, 120, 0, 0)
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

        if(type == "ALL") {
            presenter.getEvents(appContext())
        }
        else {
            presenter.getEventsByCategory(appContext(), type)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        parentContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)

        if (!Places.isInitialized()) {
            Places.initialize(context!!, R.string.places.toString())
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
        startActivity(Intent(appContext(), EventDetailsActivity::class.java).putExtra("EVENT_NAME", p0?.title))
    }

    private fun moveCamera(latLng: LatLng, zoomLevel: Int = defaultZoom) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                latLng, zoomLevel.toFloat()
            )
        )
    }

    private fun animateCamera(latLng: LatLng, zoomLevel: Int = defaultZoom) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                latLng, zoomLevel.toFloat()
            )
        )
    }

    override fun appContext(): Context {
        return context!!
    }
}